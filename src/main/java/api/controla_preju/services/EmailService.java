package api.controla_preju.services;

import api.controla_preju.entities.Email;
import api.controla_preju.entities.Expense;
import api.controla_preju.entities.Transfer;
import api.controla_preju.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

    @Value("${api.email.from}")
    private String from;
    @Value("${api.confirm.email.route}")
    private String confirmEmailRoute;
    @Value("${api.reject.email.route}")
    private String rejectEmailRoute;
    @Value("${api.frontend.url}")
    private String frontendUrl;

    private final JavaMailSender mailSender;
    private final TokenService tokenService;

    public EmailService(JavaMailSender mailSender, TokenService tokenService) {
        this.mailSender = mailSender;
        this.tokenService = tokenService;
    }

    public void sendGenericEmail(Email email){
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email.getTo());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        mailSender.send(message);
    }

    @Async
    public void sendRegisterEmail(User newUser){
        String token = tokenService.generateToken(newUser);
        String confirmationLink = confirmEmailRoute + token;
        String rejectionLink = rejectEmailRoute + token;
        String emailBody = String.format("""
        Olá, %s!

        Seu cadastro no ControlaPreju foi recebido. Por favor, confirme seu e-mail clicando no link abaixo:
        %s

        Se você não fez este cadastro, por favor, rejeite clicando aqui:
        %s
        
        Atenciosamente, equipe ControlaPreju.
        """, newUser.getName(), confirmationLink, rejectionLink);

        var message = new SimpleMailMessage();
        message.setTo(newUser.getEmail());
        message.setSubject("Novo cadastro no ControlaPreju!");
        message.setText(emailBody);
        mailSender.send(message);
    }

    @Async
    public void sendResetPasswordEmail(User user) {
        String token = tokenService.generateShortTimeToken(user);
        String resetLink = frontendUrl + "/recover-password?token=" + token;

        String emailBody = String.format("""
        Olá!

        Recebemos um pedido para recuperar a senha.
        Clique no link abaixo para criar uma nova senha:
        %s

        Se você não pediu isso, apenas ignore este e-mail e sua senha permanecerá a mesma.
        """, resetLink);

        var message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Recuperação de senha - ControlaPreju");
        message.setText(emailBody);
        mailSender.send(message);
    }

    @Async
    public void sendFailedAutomaticDebitsEmail(User user, List<Expense> failedExpenses) {
        StringBuilder expensesList = new StringBuilder();
        for (Expense expense : failedExpenses) {
            double amountInReais = expense.getAmountInCents() / 100.0;
            expensesList.append(String.format("- %s: R$ %.2f\n", expense.getTitle(), amountInReais));
        }

        String emailBody = String.format("""
        Olá, %s!

        Aviso importante: Não foi possível realizar o débito automático das seguintes despesas devido a saldo insuficiente na conta vinculada no momento da cobrança:
        
        %s
        Para evitar atrasos, por favor, acesse o sistema, regularize seu saldo e realize o pagamento manualmente.
        
        O débito automático destas despesas foi desativado.
        
        Atenciosamente, equipe ControlaPreju.
        """, user.getName(), expensesList.toString());

        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Falha no Débito Automático - ControlaPreju");
        message.setText(emailBody);
        mailSender.send(message);
    }

    @Async
    public void sendFailedAutomaticTransfersEmail(User user, List<Transfer> failedTransfers) {
        StringBuilder transfersList = new StringBuilder();
        for (Transfer transfer : failedTransfers) {
            double amountInReais = transfer.getAmountInCents() / 100.0;
            transfersList.append(String.format("- %s (Para: %s): R$ %.2f\n",
                    transfer.getTitle(), transfer.getDestinationAccount().getName(), amountInReais));
        }

        String emailBody = String.format("""
        Olá, %s!

        Aviso importante: Não foi possível realizar as seguintes transferências automáticas devido a saldo insuficiente na conta de origem no momento da execução:
        
        %s
        O processamento automático destas transferências foi desativado. Por favor, acesse o sistema, regularize seu saldo e realize a transferência manualmente.
        
        Atenciosamente, equipe ControlaPreju.
        """, user.getName(), transfersList.toString());

        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Falha na Transferência Automática - ControlaPreju");
        message.setText(emailBody);
        mailSender.send(message);
    }

}
