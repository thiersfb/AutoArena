package br.com.autoarena.util; // Ajuste o pacote conforme onde você criou

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Senhas que você quer codificar
        String rawAdminPassword = "admin";
        String rawUserPassword = "cpedro2025";
        String rawThiersPassword = "thiers07";


        // Codificando as senhas
        String encodedAdminPassword = encoder.encode(rawAdminPassword);
        String encodedUserPassword = encoder.encode(rawUserPassword);
        String encodedThiersPassword = encoder.encode(rawThiersPassword);

        // Imprimindo as senhas codificadas
        System.out.println("Senha codificada para '" + rawAdminPassword + "': " + encodedAdminPassword);
        System.out.println("Senha codificada para '" + rawUserPassword + "': " + encodedUserPassword);
        System.out.println("Senha codificada para '" + rawThiersPassword + "': " + encodedThiersPassword);

        // Exemplo de uma nova senha qualquer
        String newRawPassword = "user";
        String newEncodedPassword = encoder.encode(newRawPassword);
        System.out.println("Senha codificada para '" + newRawPassword + "': " + newEncodedPassword);
    }
}