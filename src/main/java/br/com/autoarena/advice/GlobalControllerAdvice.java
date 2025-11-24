package br.com.autoarena.advice;

import br.com.autoarena.model.User; //
import br.com.autoarena.service.UserService; //
import org.springframework.beans.factory.annotation.Autowired; //
import org.springframework.security.core.Authentication; //
import org.springframework.security.core.context.SecurityContextHolder; //
import org.springframework.ui.Model; //
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute; //

import java.util.Base64; //
import java.util.Optional; //

@ControllerAdvice // Indica que esta classe fornece métodos que aplicam-se globalmente a todos os controladores.
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService; //

    @ModelAttribute // Marca um método que adiciona um atributo ao Model antes que qualquer handler @RequestMapping seja chamado.
    public void addLoggedInUserToModel(Model model) { //
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            // Verifica se o usuário está autenticado e não é um usuário anônimo

            String username = authentication.getName(); //
            Optional<User> userOptional = userService.findByUsername(username); //

            userOptional.ifPresent(user -> { //
                model.addAttribute("loggedInUser", user); // Adiciona o objeto User completo ao modelo


                // Converte a fotoData para Base64 se existir e adiciona ao modelo
                if (user.getFotoData() != null && user.getFotoData().length > 0) { //
                    String base64Image = Base64.getEncoder().encodeToString(user.getFotoData()); //
                    model.addAttribute("loggedInUserPhoto", base64Image); // Atributo para a imagem Base64
                } else {
                    model.addAttribute("loggedInUserPhoto", null); // Indica que não há foto
                }

                // *** NOVO: Adiciona a verificação de role ADMIN ***
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); // Assume que sua role de admin é "ADMIN"
                // Adiciona a verificação de role ADMIN
                //boolean isAdmin = authentication.getAuthorities().stream()
                //        .anyMatch(a -> {
                //            System.out.println("Role encontrada: " + a.getAuthority()); // ADICIONE ESTA LINHA PARA DEBUG
                //            return a.getAuthority().equals("ADMIN"); // Assume que sua role de admin é "ADMIN"
                //        });

                model.addAttribute("isAdmin", isAdmin); // Atributo 'isAdmin' disponível em todas as views
            });
        }
    }
}