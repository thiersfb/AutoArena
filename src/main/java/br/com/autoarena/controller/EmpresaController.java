package br.com.autoarena.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http.HttpServletRequest for Spring Boot 3+

@Controller
public class EmpresaController {

    @GetMapping("/empresa")
    public String showPage(HttpServletRequest request, Model model) {
        // Você pode adicionar atributos ao modelo que serão acessíveis no template Thymeleaf
        model.addAttribute("pageTitle", "Nossa Empresa");
        model.addAttribute("welcomeMessage", "Conheça a história e os valores da AutoArena!");
        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida
        return "public/empresa"; // Retorna o nome do template Thymeleaf (empresa.html)
    }

}