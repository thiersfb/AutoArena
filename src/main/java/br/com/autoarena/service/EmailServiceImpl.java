package br.com.autoarena.service;

import br.com.autoarena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(User user, String token, String appUrl) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("thiersfb@yahoo.com.br");
        //email.setFrom("no-reply@autoarena.com");
        email.setTo(user.getEmail());
        email.setSubject("Redefinição de Senha - AutoArena");

        String resetLink = appUrl + "/reset?token=" + token;

        String emailContent = "Olá " + user.getNome() + ",\n\n"
                + "Você solicitou a redefinição de sua senha. "
                + "Clique no link abaixo para criar uma nova senha:\n\n"
                + resetLink + "\n\n"
                + "Se você não solicitou isso, por favor, ignore este e-mail.\n\n"
                + "Por favor, não responda este e-mail.\n\n"
                + "Atenciosamente,\n"
                + "Equipe AutoArena";

        email.setText(emailContent);

        mailSender.send(email);
    }
}