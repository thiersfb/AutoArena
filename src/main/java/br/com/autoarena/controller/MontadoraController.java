package br.com.autoarena.controller;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import br.com.autoarena.service.MontadoraService;
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

import java.util.List;

@Controller
public class MontadoraController {

    @Autowired
    private MontadoraService montadoraService;

    // Exibe a página de cadastro/listagem de montadoras
    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_DADOS_VEICULO')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping("/private/cadastro/montadoras")
    public String showMontadorasPage(
            @RequestParam(value = "tipoVeiculoId", required = false) Long tipoVeiculoId,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestParam(value = "sort", required = false) String sort, // NOVO PARÂMETRO
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("pageTitle", "Cadastro de Montadoras");
        //model.addAttribute("blockTitle", "Gerenciamento de Montadoras");
        //model.addAttribute("blockSubtitle", "Cadastre e gerencie as montadoras disponíveis.");
        model.addAttribute("blockTitle", "Lista de Montadoras");
        model.addAttribute("blockSubtitle", "Todos os registros");

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

        // Adiciona a lista de montadoras para exibição na tabela
        //List<Montadora> montadoras = montadoraService.findAll();
        //model.addAttribute("montadoras", montadoras);


        Page<Montadora> montadorasPage = montadoraService.findAllFiltered(tipoVeiculoId, nome, enabled, pageable);
        //model.addAttribute("modelos", modelosPage);

        model.addAttribute("montadoras", montadorasPage.getContent());



        model.addAttribute("page", montadorasPage);
        model.addAttribute("totalPages", montadorasPage.getTotalPages());


        // Adiciona os valores do filtro ao modelo para manter os campos do formulário preenchidos
        model.addAttribute("filtroTipoVeiculoId", tipoVeiculoId);
//        model.addAttribute("filtroMontadoraId", montadoraId);
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroStatus", enabled);
        model.addAttribute("sort", sort); // Adiciona o sort ao modelo para o Thymeleaf
        model.addAttribute("size", size);


        model.addAttribute("currentUri", request.getRequestURI());

        return "private/cadastro/montadora";
    }

    // Lida com o envio do formulário de registro/edição de uma montadora
    @PostMapping("/private/cadastro/montadoras/registro")
    public String registerMontadora(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("nome") String nome,
            @RequestParam("enabled") boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            Montadora montadora;
            String actionMessage;

            if (id == null) { // Modo de cadastro (ID não presente)
                montadora = new Montadora();
                actionMessage = "cadastrada";
            } else { // Modo de edição (ID presente)
                montadora = montadoraService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Montadora não encontrada para edição com ID: " + id));
                actionMessage = "atualizada";
            }

            montadora.setNome(nome);
            montadora.setEnabled(enabled);

            montadoraService.save(montadora);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Montadora '" + nome + "' " + actionMessage + " com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldEnabled", enabled);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao salvar montadora: " + e.getMessage());
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldEnabled", enabled);
        }

        return "redirect:/private/cadastro/montadoras";
    }

    // Lida com a mudança de status (ativar/desativar) de uma montadora
    @PostMapping("/private/cadastro/montadoras/status")
    public String updateMontadoraStatus(
            @RequestParam("id") Long id,
            @RequestParam("enabled") boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            Montadora updatedMontadora = montadoraService.updateStatus(id, enabled);
            String statusMsg = enabled ? "ativada" : "desativada";
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Montadora '" + updatedMontadora.getNome() + "' foi " + statusMsg + " com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao alterar status da montadora: " + e.getMessage());
        }

        return "redirect:/private/cadastro/montadoras";
    }

    // Lida com a exclusão de uma montadora
    @PostMapping("/private/cadastro/montadoras/excluir")
    public String deleteMontadora(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            montadoraService.deleteById(id);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Montadora excluída com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir montadora: " + e.getMessage());
        }

        return "redirect:/private/cadastro/montadoras";
    }
}