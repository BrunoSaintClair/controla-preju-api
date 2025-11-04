package api.controla_preju.services;

import api.controla_preju.entities.Email;
import api.controla_preju.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${api.email.from}")
    private String from;
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

    public void sendRegisterEmail(User newUser){
        String confirmationToken = tokenService.generateToken(newUser);
        String confirmationLink = "http://localhost:8080/api/v1/auth/confirm?token=" + confirmationToken;
        String rejectionLink = "http://localhost:8080/api/v1/auth/reject?token=" + confirmationToken;
        String emailBody = String.format("""
        Olá, %s!

        Seu cadastro no ControlaPreju foi recebido. Por favor, confirme seu e-mail clicando no link abaixo:
        %s

        Se você não fez este cadastro, por favor, rejeite clicando aqui:
        %s
        """, newUser.getName(), confirmationLink, rejectionLink);

        var message = new SimpleMailMessage();
        message.setTo(newUser.getEmail());
        message.setSubject("Novo cadastro no ControlaPreju!");
        message.setText(emailBody);
        mailSender.send(message);
    }

}

