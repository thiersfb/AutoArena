package br.com.autoarena.controller;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import br.com.autoarena.model.TipoVeiculo;
import br.com.autoarena.model.Veiculo;
import br.com.autoarena.service.ModeloService;
import br.com.autoarena.service.MontadoraService;
import br.com.autoarena.service.TipoVeiculoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/private/cadastro/modelos")
public class ModeloController {

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private MontadoraService montadoraService;

    @Autowired
    private TipoVeiculoService tipoVeiculoService;

    /*
    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_MODELO')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping
    public String showPage(HttpServletRequest request, Model model) {
        model.addAttribute("pageTitle", "Cadastro de Modelos");
        model.addAttribute("blockTitle", "Lista de Modelos");
        model.addAttribute("blockSubtitle", "Todos os registros");

        List<Modelo> modelos = modeloService.findAll();
        model.addAttribute("modelos", modelos);

        //List<Montadora> montadoras = montadoraService.findByEnabledTrue();
        List<Montadora> montadoras = montadoraService.findAllEnabled();
        model.addAttribute("montadoras", montadoras);

        List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAllEnabled();
        model.addAttribute("tiposVeiculo", tiposVeiculo);

        // Se o modelo não está presente no flash (vindo de um redirect com erro), cria um novo
        if (!model.containsAttribute("modelo")) {
            model.addAttribute("modelo", new Modelo());
        }

        model.addAttribute("currentUri", request.getRequestURI());
        return "private/cadastro/modelos";
    }
    */


    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_DADOS_VEICULO')")
    @GetMapping
    public String showPage(
            @RequestParam(value = "tipoVeiculoId", required = false) Long tipoVeiculoId,
            @RequestParam(value = "montadoraId", required = false) Long montadoraId,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestParam(value = "sort", required = false) String sort, // NOVO PARÂMETRO
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {

        model.addAttribute("pageTitle", "Cadastro de Modelos");
        model.addAttribute("blockTitle", "Lista de Modelos");
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

        // Passa o objeto Sort para o serviço
        //List<Modelo> modelos = modeloService.findAllFiltered(tipoVeiculoId, montadoraId, nome, enabled, sortObj);
        //model.addAttribute("modelos", modelos);

        Page<Modelo> modelosPage = modeloService.findAllFiltered(tipoVeiculoId, montadoraId, nome, enabled, pageable);
        //model.addAttribute("modelos", modelosPage);

        model.addAttribute("modelos", modelosPage.getContent());



        model.addAttribute("page", modelosPage);
        model.addAttribute("totalPages", modelosPage.getTotalPages());


        List<Montadora> montadoras = montadoraService.findAllEnabled();
        model.addAttribute("montadoras", montadoras);

        List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAllEnabled();
        model.addAttribute("tiposVeiculo", tiposVeiculo);

        // Adiciona os valores do filtro ao modelo para manter os campos do formulário preenchidos
        model.addAttribute("filtroTipoVeiculoId", tipoVeiculoId);
        model.addAttribute("filtroMontadoraId", montadoraId);
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroStatus", enabled);
        model.addAttribute("sort", sort); // Adiciona o sort ao modelo para o Thymeleaf
        model.addAttribute("size", size);

        if (!model.containsAttribute("modelo")) {
            model.addAttribute("modelo", new Modelo());
        }

        model.addAttribute("currentUri", request.getRequestURI());
        return "private/cadastro/modelos";
    }


    @PostMapping
    public String saveModelo(@ModelAttribute("modelo") @Valid Modelo modelo,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        // Se houver erros de validação (ex: nome em branco, montadora/tipoVeiculo nulos)
        if (result.hasErrors()) {
            // Adiciona o objeto 'modelo' com os erros e os dados preenchidos ao flash attributes
            // para que o formulário possa ser repopulado.
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.modelo", result);
            redirectAttributes.addFlashAttribute("modelo", modelo);

            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro de validação! Por favor, verifique os campos.");

            // Redireciona para o GET da mesma página para exibir os erros de validação do Thymeleaf
            return "redirect:/private/cadastro/modelos";
        }

        try {
            // Busca as entidades completas para garantir que são gerenciadas pelo JPA
            // e para validar se os IDs realmente existem no banco de dados.

            // Validação e busca da Montadora
            // As validações @NotNull em Modelo.java já devem ter garantido que montadora e tipoVeiculo não são nulos.
            // Aqui, validamos se o ID existe no banco de dados.
            if (modelo.getMontadora() == null || modelo.getMontadora().getId() == null) {
                throw new IllegalArgumentException("Montadora não selecionada.");
            }
            Optional<Montadora> montadoraOptional = montadoraService.findById(modelo.getMontadora().getId());
            if (montadoraOptional.isEmpty()) {
                throw new IllegalArgumentException("Montadora selecionada não encontrada.");
            }
            modelo.setMontadora(montadoraOptional.get()); // Seta a entidade Montadora completa

            // Validação e busca do Tipo de Veículo
            if (modelo.getTipoVeiculo() == null || modelo.getTipoVeiculo().getId() == null) {
                throw new IllegalArgumentException("Tipo de veículo não selecionado.");
            }
            Optional<TipoVeiculo> tipoVeiculoOptional = tipoVeiculoService.findById(modelo.getTipoVeiculo().getId());
            if (tipoVeiculoOptional.isEmpty()) {
                throw new IllegalArgumentException("Tipo de veículo selecionado não encontrado.");
            }
            modelo.setTipoVeiculo(tipoVeiculoOptional.get()); // Seta a entidade TipoVeiculo completa

            modeloService.save(modelo);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Modelo '" + modelo.getNome() + "' salvo com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("modelo", modelo); // Mantém os dados preenchidos no formulário
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.modelo", result); // Repassa o BindingResult também
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao salvar modelo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("modelo", modelo); // Mantém os dados preenchidos no formulário
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.modelo", result); // Repassa o BindingResult também
        }

        return "redirect:/private/cadastro/modelos";
    }

    @PostMapping("/alterar-status")
    public String changeModeloStatus(
            @RequestParam("id") Long id,
            @RequestParam("enabled") boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            Modelo updatedModelo = modeloService.updateStatus(id, enabled);
            String statusMsg = enabled ? "ativado" : "desativado";
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Modelo '" + updatedModelo.getNome() + "' foi " + statusMsg + " com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao alterar status do modelo: " + e.getMessage());
        }

        return "redirect:/private/cadastro/modelos";
    }

    @PostMapping("/excluir")
    public String deleteModelo(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            modeloService.deleteById(id);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Modelo excluído com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir modelo: " + e.getMessage());
        }

        return "redirect:/private/cadastro/modelos";
    }

    @GetMapping("/por-montadora/{montadoraId}")
    @ResponseBody
    public List<Modelo> getModelosByMontadora(@PathVariable("montadoraId") Long montadoraId) {
        if (montadoraId == null) {
            return List.of();
        }
        Montadora montadora = montadoraService.findById(montadoraId)
                .orElseThrow(() -> new IllegalArgumentException("Montadora não encontrada com ID: " + montadoraId));
        return modeloService.findByMontadoraAndEnabledTrue(montadora);
    }


    @GetMapping("/por-montadora/todos")
    public ResponseEntity<List<Modelo>> findAllEnabled() {
        List<Modelo> modelos = modeloService.findAll().stream()
                .filter(Modelo::isEnabled)
                .collect(Collectors.toList());
        return ResponseEntity.ok(modelos);
    }

}