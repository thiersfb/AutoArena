package br.com.autoarena.controller;

import br.com.autoarena.model.User;
import br.com.autoarena.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // Importar para @DateTimeFormat
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate; // Importar para LocalDate
import java.util.Optional;
import java.util.Base64; // Import para Base64

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;


    @GetMapping("/private/admin/perfil")
    public String showUserProfileForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user != null) {

            // ESSENCIAL: Limpe a senha do objeto User ANTES de enviá-lo para o Thymeleaf.
            // Isso garante que o campo de senha no HTML não seja pré-preenchido com o hash.
            user.setPassword(null); // <--- ADICIONE ESTA LINHA!

            model.addAttribute("oldId", user.getId());
            model.addAttribute("oldNome", user.getNome());
            model.addAttribute("oldSobrenome", user.getSobrenome());
            model.addAttribute("oldUsername", user.getUsername());
            model.addAttribute("oldContatoEmail", user.getEmail());
            model.addAttribute("oldDataNascimento", user.getDataNascimento());
            model.addAttribute("oldEnabled", user.isEnabled()); // Para o caso de algum admin editar o status

            // Converte byte[] para String Base64 para exibir a imagem no HTML
            if (user.getFotoData() != null && user.getFotoData().length > 0) {
                String base64Image = Base64.getEncoder().encodeToString(user.getFotoData());
                model.addAttribute("oldFotoBase64", base64Image);
            } else {
                model.addAttribute("oldFotoBase64", null); // Nenhuma imagem para exibir
            }

            model.addAttribute("pageTitle", "Meu Perfil");
            model.addAttribute("blockTitle", "Minhas Informações de Perfil");
            model.addAttribute("blockSubtitle", "Gerencie suas informações pessoais.");
        }
        model.addAttribute("origin", "perfil"); // Define a origem como 'perfil'

        model.addAttribute("currentUri", "/private/admin/perfil");
        //model.addAttribute("currentUri", request.getRequestURI());

        //model.addAttribute("isAdminEdit", false); // Indica que não é uma edição de administrador

        return "private/admin/perfil";
    }

    // Método POST para salvar as alterações feitas pelo NÃO ADMIN
    //@PostMapping("/private/perfil/salvar")
    //@PostMapping("/private/admin/users/save")
    @PostMapping("/private/admin/perfil/save")
    public String saveUserProfile(
            @RequestParam("id") Long id,
            @RequestParam("nome") String nome,
            @RequestParam("sobrenome") String sobrenome,
            @RequestParam("username") String username,
            @RequestParam(value = "contatoEmail", required = false) String contatoEmail,
            @RequestParam(value = "senha", required = false) String senha,
            @RequestParam("dataNascimento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile, // Novo parâmetro para o arquivo
            @RequestParam("origin") String origin,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalUsername = authentication.getName(); // Obtém o username do usuário logado

        try {
            //User userToUpdate = userService.findById(id)
            User userToUpdate = userService.findUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para atualização."));

            // Garante que o usuário que está tentando editar é realmente o usuário logado
            if (!userToUpdate.getUsername().equals(currentPrincipalUsername)) { // Compara com username
                throw new SecurityException("Tentativa de editar perfil de outro usuário.");
            }

            userToUpdate.setNome(nome);
            userToUpdate.setSobrenome(sobrenome); // Define sobrenome
            userToUpdate.setUsername(username); // Define username
            userToUpdate.setEmail(contatoEmail); // Define contatoEmail
            userToUpdate.setDataNascimento(dataNascimento); // Define dataNascimento

            if (senha != null && !senha.isEmpty()) {
                userToUpdate.setPassword(senha);
            }
            //userToUpdate.setFotoUrl(fotoUrl);


            // Lógica para lidar com o arquivo de imagem
            if (fotoFile != null && !fotoFile.isEmpty()) {
                try {
                    userToUpdate.setFotoData(fotoFile.getBytes()); // Define os bytes da imagem
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("messageType", "error");
                    redirectAttributes.addFlashAttribute("message", "Erro ao processar a imagem: " + e.getMessage());
                    // Retorna os dados para que o formulário possa ser preenchido novamente
                    populateModelForError(redirectAttributes, id, nome, sobrenome, username, contatoEmail, dataNascimento, senha, userToUpdate.getFotoData(), userToUpdate.isEnabled());
                    return "redirect:/private/admin/perfil";
                }
            }


            userService.save(userToUpdate);

            // Se o username foi alterado, o usuário precisará fazer login novamente
            if (!currentPrincipalUsername.equals(username)) {
                redirectAttributes.addFlashAttribute("messageType", "warning");
                redirectAttributes.addFlashAttribute("message", "Seu nome de usuário foi alterado. Por favor, faça login novamente com o novo nome de usuário.");
                SecurityContextHolder.clearContext();
                return "redirect:/login";
            }

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Seu perfil foi atualizado com sucesso!");

        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro de segurança: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            // Preenche os old values para re-exibir o formulário com os dados que o usuário digitou
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldSobrenome", sobrenome);
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldContatoEmail", contatoEmail);
            redirectAttributes.addFlashAttribute("oldDataNascimento", dataNascimento);
            redirectAttributes.addFlashAttribute("oldFotoUrl", fotoFile);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao atualizar perfil: " + e.getMessage());
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldSobrenome", sobrenome);
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldContatoEmail", contatoEmail);
            redirectAttributes.addFlashAttribute("oldDataNascimento", dataNascimento);
            redirectAttributes.addFlashAttribute("oldFotoUrl", fotoFile);
        }

        //return "redirect:/private/admin/perfil";

        // Lógica de redirecionamento condicional
        if ("admin_users".equals(origin)) {
            return "redirect:/private/admin/users";
        } else {
            return "redirect:/private/admin/perfil";
        }

    }


    // Helper method to repopulate attributes on error
    private void populateModelForError(RedirectAttributes redirectAttributes, Long id, String nome, String sobrenome, String username, String contatoEmail, LocalDate dataNascimento, String senha, byte[] fotoData, boolean enabled) {
        redirectAttributes.addFlashAttribute("oldId", id);
        redirectAttributes.addFlashAttribute("oldNome", nome);
        redirectAttributes.addFlashAttribute("oldSobrenome", sobrenome);
        redirectAttributes.addFlashAttribute("oldUsername", username);
        redirectAttributes.addFlashAttribute("oldContatoEmail", contatoEmail);
        redirectAttributes.addFlashAttribute("oldDataNascimento", dataNascimento);
        // Não passamos a senha aqui por segurança
        // Se a fotoData for passada, converte para Base64
        if (fotoData != null && fotoData.length > 0) {
            redirectAttributes.addFlashAttribute("oldFotoBase64", Base64.getEncoder().encodeToString(fotoData));
        }
        redirectAttributes.addFlashAttribute("oldEnabled", enabled);
    }

}