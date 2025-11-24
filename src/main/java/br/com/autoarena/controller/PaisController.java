package br.com.autoarena.controller;

//import br.com.autoarena.model.User;
import br.com.autoarena.model.Estado;
import br.com.autoarena.model.Pais;
import br.com.autoarena.service.PaisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaisController {

    @Autowired
    private PaisService paisService; //Injeta o PaisService

    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_LOCAIS')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping("/private/cadastro/pais")
    public String showPage(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "sort", required = false, defaultValue = "nome,asc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("pageTitle", "Cadastro de País");
        model.addAttribute("blockTitle", "Lista de Países");
        model.addAttribute("blockSubtitle", "Todos os registros");
        model.addAttribute("blockText", "Visualize e gerencie os países cadastrados no sistema.");

        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida

        // AQUI: Adiciona a lista de países ao modelo
        // Supondo que paisService.findAll() retorna uma List<Pais>
        //model.addAttribute("paises", paisService.findAll());


        // Cria um objeto Sort a partir do parâmetro String. Se o parâmetro for nulo, usa uma ordenação padrão.
        Sort sortObj = Sort.by("id").ascending(); // Ordenação padrão, se nenhuma for especificada
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sortObj = Sort.by(direction, property);
            }
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Pais> paisesPage = paisService.findAllFiltered(nome, pageable);
        model.addAttribute("paises", paisesPage.getContent());
        model.addAttribute("page", paisesPage);
        model.addAttribute("totalPages", paisesPage.getTotalPages());



        // Adiciona os valores dos filtros para manter na página após a submissão
        model.addAttribute("filtroNome", nome);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        return "private/cadastro/pais"; // Você precisará criar o template private.html
    }

    // Este método lida com o envio do formulário de registro via POST
    @PostMapping("/private/cadastro/pais/registro")
    public String salvarPais(
            @RequestParam(value = "id", required = false) Long id, // Tornar o ID opcional
            @RequestParam("txtPais") String nome,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {


            Pais pais;
            if (id != null && id > 0) {
                // É uma edição: busca o país existente
                pais = paisService.findById(id) // Você precisará criar findById no PaisService
                        .orElseThrow(() -> new IllegalArgumentException("País não encontrado para edição com ID: " + id));
                pais.setNome(nome);
                redirectAttributes.addFlashAttribute("message", "País '" + nome + "' atualizado com sucesso!");
            } else {
                // É um novo cadastro
                pais = new Pais();
                pais.setNome(nome);
                redirectAttributes.addFlashAttribute("message", "País '" + nome + "' cadastrado com sucesso!");
            }

            // Tenta registrar o país usando o PaisService
            paisService.salvarPais(pais);

            // Se o registro for bem-sucedido
            redirectAttributes.addFlashAttribute("messageType", "success");
            //redirectAttributes.addFlashAttribute("message", "Cadastrado com sucesso!");
            //return "redirect:/login?successRegister"; // Redireciona para a página de login com mensagem de sucesso
            return "redirect:/private/cadastro/pais"; // Redireciona para o GET /pais

        } catch (Exception e) {
            // Se ocorrer algum erro durante o registro
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            // Adiciona os dados inseridos de volta ao modelo para preencher o formulário
            //redirectAttributes.addFlashAttribute("oldUsername", username);
            //redirectAttributes.addFlashAttribute("oldEmail", email);
            //return "redirect:/login?errorRegister"; // Redireciona para a página de login com erro
            return "redirect:/private/cadastro/pais"; // Redireciona para o GET /pais
        }
    }

    @PostMapping("/private/cadastro/pais/excluir") // Endpoint para a exclusão
    public String excluirPais(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {

            String nome = "";
            Pais pais;
            if (id != null && id > 0) {
                // É uma edição: busca o país existente
                pais = paisService.findById(id) // Você precisará criar findById no PaisService
                        .orElseThrow(() -> new IllegalArgumentException("País não encontrado para edição com ID: " + id));
                //pais.setNome(nome);
                nome = pais.getNome();
            }
            paisService.deleteById(id);

                redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "País " + nome.toString() + " excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir o país: " + e.getMessage());
        }
        return "redirect:/private/cadastro/pais"; // Redireciona de volta para a lista
    }



}
