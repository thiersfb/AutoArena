package br.com.autoarena.service;

import br.com.autoarena.model.User;

public interface EmailService {

    /**
     * Envia um e-mail com o link de redefinição de senha.
     * @param user O usuário que solicitou a redefinição.
     * @param token O token gerado para a redefinição.
     * @param appUrl A URL base da aplicação para criar o link.
     */
    void sendPasswordResetEmail(User user, String token, String appUrl);
}