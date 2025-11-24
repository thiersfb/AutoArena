package br.com.autoarena.controller;

import br.com.autoarena.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Exibe o formulário para solicitar a redefinição de senha
    @GetMapping("/forgot")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("pageTitle", "Esqueci Minha Senha");
        return "forgot";
    }


    // Processa a solicitação de redefinição de senha
    @PostMapping("/forgot")
    public String processForgotPasswordForm(@RequestParam("email") String email,
                                            RedirectAttributes redirectAttributes,
                                            HttpServletRequest request) {

        // Chamada do serviço. A lógica interna do serviço decide se envia o e-mail.
        passwordResetService.createPasswordResetTokenForUser(email, request.getRequestURL().toString().replace("/forgot", ""));
        //passwordResetService.createPasswordResetTokenForUser(email, "http://localhost:8080");

        // CORREÇÃO: Sempre redireciona com uma mensagem de sucesso genérica para evitar que um atacante descubra e-mails de usuários válidos.
        redirectAttributes.addFlashAttribute("successMessage", "Se o e-mail informado estiver registrado, um link para redefinição será enviado.");

        return "redirect:/login";

        // NÃO RECOMENDADO: para evitar a enumeração de usuários
        // A chamada agora captura o valor de retorno
        //        boolean emailExists = passwordResetService.createPasswordResetTokenForUser(email, request.getRequestURL().toString().replace("/forgot", ""));
        //        if (emailExists) {
        //            redirectAttributes.addFlashAttribute("successMessage", "Link para redefinir senha será enviado para o e-mail inserido.");
        //            return "redirect:/login";
        //        } else {
        //            redirectAttributes.addFlashAttribute("errorMessage", "O e-mail inserido não foi encontrado na base de dados.");
        //            return "redirect:/forgot";
        //        }

    }

    // Exibe o formulário para redefinir a senha com o token
    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Validação básica do token pode ser feita aqui, mas a validação completa fica no serviço
        model.addAttribute("token", token);
        model.addAttribute("pageTitle", "Redefinir Senha");
        return "reset";
    }

    // Processa a nova senha e o token
    @PostMapping("/reset")
    public String processResetPasswordForm(@RequestParam("token") String token,
                                           @RequestParam("j_newpassword") String newPassword,
                                           @RequestParam("j_newpasswordconf") String passwordConfirm,
                                           RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("errorMessage", "As senhas não conferem.");
            return "redirect:/reset?token=" + token;
        }

        boolean success = passwordResetService.resetPassword(token, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Sua senha foi redefinida com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "O link de redefinição é inválido ou expirou.");
        }
        return "redirect:/login";
    }
}