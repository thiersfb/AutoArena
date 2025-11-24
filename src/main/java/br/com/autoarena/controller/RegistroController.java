package br.com.autoarena.controller;

import br.com.autoarena.model.User;
import br.com.autoarena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    @Autowired
    private UserService userService; // Injeta o UserService que criamos

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o PasswordEncoder do SecurityConfig

    // Este método pode ser usado para exibir a página de login/registro
    // (se você quiser que /register também exiba o formulário, caso contrário, /login já faz isso)
    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        // Pode adicionar um objeto User vazio para o formulário se estiver usando th:object
        // model.addAttribute("user", new User());
        return "login"; // Retorna o nome do template Thymeleaf (login.html)
    }

    // Este método lida com o envio do formulário de registro via POST
    @PostMapping("/registro")
    public String registerUser(
            @RequestParam("j_newusername") String username,
            @RequestParam("j_newemail") String email,
            @RequestParam("j_newpassword") String password,
            @RequestParam("j_newpasswordconf") String passwordConfirm,
            RedirectAttributes redirectAttributes,
            Model model) {

        // 1. Validação de senhas (se não forem iguais)
        if (!password.equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("errorMessage", "As senhas não coincidem.");
            // Adiciona os dados inseridos de volta ao modelo para preencher o formulário
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldEmail", email);
            return "redirect:/login?errorRegister"; // Redireciona para a página de login com erro
        }

        try {
            // 2. Criação do objeto User e codificação da senha
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password)); // Codifica a senha antes de salvar!

            // 3. Tenta registrar o usuário usando o UserService
            userService.registerNewUser(newUser);

            // Se o registro for bem-sucedido
            redirectAttributes.addFlashAttribute("successMessage", "Cadastro realizado com sucesso! Faça login.");
            return "redirect:/login?successRegister"; // Redireciona para a página de login com mensagem de sucesso

        } catch (Exception e) {
            // Se ocorrer algum erro durante o registro (ex: email já em uso)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Adiciona os dados inseridos de volta ao modelo para preencher o formulário
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldEmail", email);
            return "redirect:/login?errorRegister"; // Redireciona para a página de login com erro
        }
    }
}