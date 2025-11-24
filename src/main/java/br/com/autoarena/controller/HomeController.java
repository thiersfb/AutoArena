package br.com.autoarena.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http.HttpServletRequest for Spring Boot 3+

@Controller // Indica que esta classe é um controlador Spring MVC
public class HomeController {

    @GetMapping("/") // Mapeia a URL raiz ("/") para este método
    public String home(HttpServletRequest request, Model model) {
        model.addAttribute("mensagem", "Olá do Spring Boot e Thymeleaf!");
        model.addAttribute("currentUri", request.getRequestURI());

        return "index"; // Retorna o nome do template Thymeleaf (index.html)
    }

    /*
    @GetMapping("/hello") // Mapeia a URL "/hello" para este método
    public String hello(Model model) {
        model.addAttribute("name", "Mundo");
        return "hello"; // Retorna o template hello.html
    }

    // Exemplo de uma página que você quer que seja protegida
    @GetMapping("/private")
    public String privatePage(Model model) {
        model.addAttribute("message", "Bem-vindo à página protegida!");
        return "private"; // Você precisará criar o template private.html
    }
    */
}