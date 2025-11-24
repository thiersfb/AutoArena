package br.com.autoarena.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http.HttpServletRequest for Spring Boot 3+

@Controller
public class DashboardController {


    // Exemplo de uma página que você quer que seja protegida
    @GetMapping("/private/dashboard")
    public String privatePage(HttpServletRequest request, Model model) {
        model.addAttribute("pageTitle", "Bem-vindo");
        model.addAttribute("blockTitle", "Veículos À Venda por Montadora");
        //model.addAttribute("blockSubtitle", "Subtitulo");
        model.addAttribute("blockText", "Texto bloco");

        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida

        return "private/dashboard"; // Você precisará criar o template private.html
    }

}
