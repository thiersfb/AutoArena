package br.com.autoarena.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http.HttpServletRequest for Spring Boot 3+

@Controller
public class ContatoController {


    @GetMapping("/contato")
    public String showPage(HttpServletRequest request, Model model) {
        // Você pode adicionar atributos ao modelo que serão acessíveis no template Thymeleaf
        model.addAttribute("pageTitle", "Contato");
        model.addAttribute("welcomeMessage", "Entre em contato com a AutoArena!");
        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida
        return "public/contato"; // Retorna o nome do template Thymeleaf
    }

}
