package br.com.autoarena.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Indica que esta classe é um controlador Spring MVC
public class ViewController {

    /**
     * Mapeia a requisição GET para /logout.html para o template Thymeleaf 'logout.html'.
     * Esta página irá então, via JavaScript, submeter um formulário POST para /logout,
     * que será tratado pelo Spring Security.
     *
     * @return O nome do template Thymeleaf a ser renderizado (logout.html)
     */
    @GetMapping("/logout.html")
    public String showLogoutPage() {
        return "logout"; // Corresponde ao nome do arquivo logout.html em src/main/resources/templates/
    }

    // Você pode adicionar outros mapeamentos de view simples aqui, se necessário.
    // Por exemplo, para sua página de login, se ainda não tiver um controller:

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

}