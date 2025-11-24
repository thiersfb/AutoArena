package br.com.autoarena.controller;

import br.com.autoarena.model.User;
import br.com.autoarena.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class AdminUserController {

    @Autowired
    private UserService userService;


    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    // Protege o acesso à página de gerenciamento de roles de usuários
    @PreAuthorize("hasRole('ADMIN')") // APENAS ADMIN PODE ACESSAR ESTA PÁGINA
    @GetMapping("/private/admin/user_roles")
    public String showPage(HttpServletRequest request, Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Gerenciar Permissões de Usuários");
        model.addAttribute("blockTitle", "Adicionar Permissão a Usuário");
        model.addAttribute("blockSubtitle", "Selecione um usuário e adicione uma permissão");
        model.addAttribute("currentUri", request.getRequestURI());

        // Opcional: Adicionar uma lista de roles conhecidas para um dropdown
        // Se você tiver roles fixas (ex: ADMIN, USER, GERENTE), pode passá-las aqui
        Set<String> availableRoles = Set.of(
                "ADMIN",
                "USER",
                //"GERENTE",
                //"CADASTRAR_PAIS",
                //"CADASTRAR_ESTADO",
                //"CADASTRAR_CIDADE",
                "CADASTRAR_LOCAIS",
                "CADASTRAR_DADOS_VEICULO",
                //"CADASTRAR_TIPO_VEICULO",
                //"CADASTRAR_MONTADORA",
                //"CADASTRAR_MODELO",
                "CADASTRAR_VEICULO_VENDA",
                "VEICULOS_VENDIDOS"
        ); // Ajuste conforme suas roles
        model.addAttribute("availableRoles", availableRoles.stream().sorted());

        return "private/admin/user_roles"; // Caminho para o seu template Thymeleaf
    }

    // Lida com a submissão do formulário para adicionar role
    //@PostMapping("/users/roles/add")
    @PostMapping("/private/admin/users/roles/add")
    // Proteger este endpoint com Spring Security, por exemplo, @PreAuthorize("hasRole('ADMIN')")
    public String addRoleToUser(
            @RequestParam("userId") Long userId,
            @RequestParam("roleName") String roleName,
            RedirectAttributes redirectAttributes) {
        try {
            userService.addRoleToUser(userId, roleName);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Permissão '" + roleName + "' adicionada ao usuário com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao adicionar permissão: " + e.getMessage());
        }
        //return "redirect:/private/admin/users/roles"; // Redireciona de volta para a página de gerenciamento
        return "redirect:/private/admin/user_roles";
    }

    @PreAuthorize("hasRole('ADMIN')") // NOVO ENDPOINT PARA REMOVER ROLE
    @PostMapping("/private/admin/users/roles/remove")
    public String removeRoleFromUser(
            @RequestParam("userId") Long userId,
            @RequestParam("roleName") String roleName,
            RedirectAttributes redirectAttributes) {
        try {
            userService.removeRoleFromUser(userId, roleName);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Role '" + roleName + "' removida do usuário com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao remover role: " + e.getMessage());
        }
        //return "redirect:/private/admin/users/roles";
        return "redirect:/private/admin/user_roles";
    }

    // Opcional: Endpoint para listar todos os usuários (se você não tiver um)
    //@GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/private/admin/users")
    public String showUsersPage(HttpServletRequest request, Model model) {
        //List<User> users = userService.findAll();
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Gerenciar Usuários");
        model.addAttribute("blockTitle", "Todos os Usuários");
        model.addAttribute("currentUri", request.getRequestURI());
        //return "private/admin/user-list"; // Crie este template se necessário
        return "private/admin/users";
        //return "private/admin/user_roles"; // Caminho para o seu template Thymeleaf


    }

    // Método para exibir o formulário de edição de OUTRO usuário
    @GetMapping("/private/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            User userToEdit = userOptional.get();
            model.addAttribute("user", userToEdit); // Passa o objeto user para o template

            // Passa os "old" attributes para consistência com o template, caso o formulário seja re-exibido por erro
            if (!model.containsAttribute("oldId")) {
                model.addAttribute("oldId", userToEdit.getId());
                model.addAttribute("oldNome", userToEdit.getNome());
                model.addAttribute("oldSobrenome", userToEdit.getSobrenome());
                model.addAttribute("oldUsername", userToEdit.getUsername());
                model.addAttribute("oldContatoEmail", userToEdit.getEmail());
                model.addAttribute("oldDataNascimento", userToEdit.getDataNascimento());
                //model.addAttribute("oldFotoUrl", userToEdit.getFotoUrl());
                model.addAttribute("oldEnabled", userToEdit.isEnabled()); // ADMIN pode editar o status enabled
            }

            model.addAttribute("pageTitle", "Editar Usuário");
            model.addAttribute("blockTitle", "Editar Informações de Usuário");
            model.addAttribute("blockSubtitle", "Atualize dados de outros usuários.");
            model.addAttribute("currentUri", "/private/admin/users/edit"); // Para o menu lateral, se aplicável

            model.addAttribute("origin", "admin_users"); // Define a origem como 'admin_users'

            // Adiciona um atributo para o template saber que é uma edição de ADMIN
            model.addAttribute("isAdminEdit", true);

            return "private/admin/perfil"; // REAPROVEITA O MESMO TEMPLATE HTML!

        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Usuário não encontrado.");
            return "redirect:/private/admin/users"; // Redireciona para a lista de usuários
        }
    }


    // Método POST para salvar as alterações feitas pelo ADMIN
    @PostMapping("/private/admin/perfil/saveAdmin")
    public String saveUserAdminProfile(
            @RequestParam("id") Long id,
            @RequestParam("nome") String nome,
            @RequestParam("sobrenome") String sobrenome,
            @RequestParam("username") String username,
            @RequestParam(value = "contatoEmail", required = false) String contatoEmail,
            @RequestParam(value = "senha", required = false) String senha,
            @RequestParam("dataNascimento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile, // Alterado de fotoUrl para fotoFile
            @RequestParam("enabled") boolean enabled,
            @RequestParam("origin") String origin,
            RedirectAttributes redirectAttributes) {


        try {
            User userToUpdate = userService.findUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para atualização."));

            userToUpdate.setNome(nome);
            userToUpdate.setSobrenome(sobrenome);
            userToUpdate.setUsername(username);
            userToUpdate.setEmail(contatoEmail);
            userToUpdate.setDataNascimento(dataNascimento);
            userToUpdate.setEnabled(enabled);

            if (senha != null && !senha.isEmpty()) {
                //userToUpdate.setPassword(senha);
                userToUpdate.setPassword(passwordEncoder.encode(senha));
            }

            // Lidar com o upload do arquivo
            if (fotoFile != null && !fotoFile.isEmpty()) {
                try {
                    userToUpdate.setFotoData(fotoFile.getBytes());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("messageType", "error");
                    redirectAttributes.addFlashAttribute("message", "Erro ao processar a imagem: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("oldId", id);
                    redirectAttributes.addFlashAttribute("oldNome", nome);
                    redirectAttributes.addFlashAttribute("oldSobrenome", sobrenome);
                    redirectAttributes.addFlashAttribute("oldUsername", username);
                    redirectAttributes.addFlashAttribute("oldContatoEmail", contatoEmail);
                    redirectAttributes.addFlashAttribute("oldDataNascimento", dataNascimento);
                    redirectAttributes.addFlashAttribute("oldEnabled", enabled);
                    //return "redirect:/private/admin/users/edit/" + id;
                    return "redirect:/private/admin/perfil";
                }
            }

            userService.save(userToUpdate);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Usuário atualizado com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldSobrenome", sobrenome);
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldContatoEmail", contatoEmail);
            redirectAttributes.addFlashAttribute("oldDataNascimento", dataNascimento);
            redirectAttributes.addFlashAttribute("oldEnabled", enabled);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao atualizar usuário: " + e.getMessage());
        }

        //return "redirect:/private/admin/users/edit/" + id;
        //return "redirect:/private/admin/perfil";

        // Lógica de redirecionamento condicional
        if ("admin_users".equals(origin)) {
            return "redirect:/private/admin/users";
        } else {
            return "redirect:/private/admin/perfil";
        }

    }

    /*
    @PostMapping("/private/admin/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(userId);
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Status do usuário alterado com sucesso!");
        return "redirect:/private/admin/users";
    }
    */

    @PostMapping("/private/admin/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(userId);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Status do usuário alterado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/private/admin/users";
    }


    /*
    // Método POST para salvar as alterações feitas pelo ADMIN
    //@PostMapping("/private/admin/users/save")
    @PostMapping("/private/admin/users/saveAdmin")
    public String adminSaveUser(
            @RequestParam("id") Long id,
            @RequestParam("nome") String nome,
            @RequestParam("sobrenome") String sobrenome,
            @RequestParam("username") String username,
            @RequestParam(value = "contatoEmail", required = false) String contatoEmail,
            @RequestParam(value = "senha", required = false) String senha,
            @RequestParam("dataNascimento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(value = "fotoUrl", required = false) String fotoUrl,
            @RequestParam("enabled") boolean enabled, // Administrador pode alterar o status 'enabled'
            @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile, // Novo parâmetro para o arquivo
            RedirectAttributes redirectAttributes) {

        try {
            User userToUpdate = userService.findUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para atualização."));

            userToUpdate.setNome(nome);
            userToUpdate.setSobrenome(sobrenome);
            userToUpdate.setUsername(username);
            userToUpdate.setEmail(contatoEmail);
            userToUpdate.setDataNascimento(dataNascimento);
            //userToUpdate.setFotoUrl(fotoUrl);
            userToUpdate.setEnabled(enabled); // Define o status enabled

            if (senha != null && !senha.isEmpty()) {
                userToUpdate.setPassword(senha); // O Service fará a criptografia
            }

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

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Usuário atualizado com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            // Repassa os dados para o formulário em caso de erro
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldSobrenome", sobrenome);
            redirectAttributes.addFlashAttribute("oldUsername", username);
            redirectAttributes.addFlashAttribute("oldContatoEmail", contatoEmail);
            redirectAttributes.addFlashAttribute("oldDataNascimento", dataNascimento);
            redirectAttributes.addFlashAttribute("oldFotoUrl", fotoUrl);
            redirectAttributes.addFlashAttribute("oldEnabled", enabled);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao atualizar usuário: " + e.getMessage());
        }

        return "redirect:/private/admin/users/edit/" + id; // Redireciona de volta para a edição
    }

    */

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
