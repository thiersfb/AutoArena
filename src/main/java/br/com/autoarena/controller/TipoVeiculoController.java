package br.com.autoarena.controller;

import br.com.autoarena.model.TipoVeiculo;
import br.com.autoarena.service.TipoVeiculoService;
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
public class TipoVeiculoController {

    @Autowired
    private TipoVeiculoService tipoVeiculoService;

    // Exibe a página de cadastro/listagem de tipos de veículo
    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_DADOS_VEICULO')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping("/private/cadastro/tipos_veiculo")
    public String showTiposVeiculoPage(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestParam(value = "sort", required = false) String sort, // NOVO PARÂMETRO
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("pageTitle", "Cadastro de Tipos de Veículo");
        //model.addAttribute("blockTitle", "Gerenciamento de Tipos de Veículo");
        //model.addAttribute("blockSubtitle", "Cadastre e gerencie os tipos de veículos disponíveis.");
        model.addAttribute("blockTitle", "Lista de Tipos de Veículos");
        model.addAttribute("blockSubtitle", "Todos os registros");

        // Adiciona a lista de tipos de veículo para exibição na tabela
        //List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAll();
        //model.addAttribute("tiposVeiculo", tiposVeiculo);


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

        Page<TipoVeiculo> tiposVeiculoPage = tipoVeiculoService.findAllFiltered(nome, enabled, pageable);
        //model.addAttribute("modelos", modelosPage);

        model.addAttribute("tiposVeiculo", tiposVeiculoPage.getContent());



        model.addAttribute("page", tiposVeiculoPage);
        model.addAttribute("totalPages", tiposVeiculoPage.getTotalPages());


        // Adiciona os valores do filtro ao modelo para manter os campos do formulário preenchidos
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroStatus", enabled);
        model.addAttribute("sort", sort); // Adiciona o sort ao modelo para o Thymeleaf
        model.addAttribute("size", size);

        model.addAttribute("currentUri", request.getRequestURI());

        return "private/cadastro/tipos_veiculo";
    }

    // Lida com o envio do formulário de registro/edição de um tipo de veículo
    @PostMapping("/private/cadastro/tipos_veiculo/registro")
    public String registerTipoVeiculo(
            @RequestParam(value = "id", required = false) Long id, // NOVO: ID agora é opcional
            @RequestParam("nome") String nome,
            @RequestParam("enabled") boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            TipoVeiculo tipoVeiculo;
            String actionMessage;

            if (id == null) { // Modo de cadastro (ID não presente)
                tipoVeiculo = new TipoVeiculo();
                actionMessage = "cadastrado";
            } else { // Modo de edição (ID presente)
                tipoVeiculo = tipoVeiculoService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Tipo de Veículo não encontrado para edição com ID: " + id));
                actionMessage = "atualizado";
            }

            tipoVeiculo.setNome(nome);
            tipoVeiculo.setEnabled(enabled);

            // Delega para o serviço que agora lida com create/update baseado no ID
            tipoVeiculoService.save(tipoVeiculo); // Usamos o método save que já valida unicidade e lida com persistência

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Tipo de veículo '" + nome + "' " + actionMessage + " com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("oldId", id); // Manter o ID para o formulário permanecer em modo edição
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldEnabled", enabled);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao salvar tipo de veículo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldNome", nome);
            redirectAttributes.addFlashAttribute("oldEnabled", enabled);
        }

        return "redirect:/private/cadastro/tipos_veiculo";
    }

    // ... (métodos updateTipoVeiculoStatus e deleteTipoVeiculo permanecem inalterados) ...

    @PostMapping("/private/cadastro/tipos_veiculo/status")
    public String updateTipoVeiculoStatus(
            @RequestParam("id") Long id,
            @RequestParam("enabled") boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            TipoVeiculo updatedTipoVeiculo = tipoVeiculoService.updateStatus(id, enabled);
            String statusMsg = enabled ? "ativado" : "desativado";
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Tipo de veículo '" + updatedTipoVeiculo.getNome() + "' foi " + statusMsg + " com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao alterar status do tipo de veículo: " + e.getMessage());
        }

        return "redirect:/private/cadastro/tipos_veiculo";
    }

    @PostMapping("/private/cadastro/tipos_veiculo/excluir")
    public String deleteTipoVeiculo(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            tipoVeiculoService.deleteById(id);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Tipo de veículo excluído com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir tipo de veículo: " + e.getMessage());
        }

        return "redirect:/private/cadastro/tipos_veiculo";
    }
}