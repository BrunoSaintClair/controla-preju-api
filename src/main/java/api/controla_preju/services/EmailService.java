package api.controla_preju.services;

import api.controla_preju.entities.Email;
import api.controla_preju.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${api.email.from}")
    private String from;
    @Value("${api.confirm.email.route}")
    private String confirmEmailRoute;
    @Value("${api.reject.email.route}")
    private String rejectEmailRoute;
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
        String confirmationToken = tokenService.generateToken(newUser);
        String confirmationLink = confirmEmailRoute + confirmationToken;
        String rejectionLink = rejectEmailRoute + confirmationToken;
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

}

