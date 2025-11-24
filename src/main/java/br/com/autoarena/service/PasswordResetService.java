package br.com.autoarena.service;

import br.com.autoarena.model.PasswordResetToken;
import br.com.autoarena.model.User;
import br.com.autoarena.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserService userService; // Seu serviço de usuário

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService; // Você precisa ter um EmailService

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    //public void createPasswordResetTokenForUser(String email, String appUrl) {
    public boolean createPasswordResetTokenForUser(String email, String appUrl) {
        Optional<User> userOptional = userService.findByEmail(email); // Verifique se seu UserService tem este método

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Primeiro, remove tokens antigos para o mesmo usuário
            // Isso garante que cada token seja de uso único e o mais recente
            tokenRepository.deleteByUser(user);

            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken();
            myToken.setToken(token);
            myToken.setUser(user);
            tokenRepository.save(myToken);

            // Monta e envia o e-mail de redefinição
            emailService.sendPasswordResetEmail(user, token, appUrl);

            return true; // E-mail encontrado, processo iniciado
        }

        return false; // E-mail não encontrado
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {

        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // Opcional: Deletar tokens expirados
            return false;
        }

        User user = resetToken.getUser();

        if (user == null) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        //userRepository.save(user);
        userService.save(user);
        tokenRepository.delete(resetToken);

        return true;
    }
}