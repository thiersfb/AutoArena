// src/main/java/br/com/autoarena/controller/CidadeController.java
package br.com.autoarena.controller;

import br.com.autoarena.model.Cidade;
import br.com.autoarena.model.Estado;
import br.com.autoarena.model.Pais;
import br.com.autoarena.service.CidadeService;
import br.com.autoarena.service.EstadoService; // Para buscar estados por país
import br.com.autoarena.service.PaisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity; // Para endpoints REST
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Para PathVariable em endpoints REST
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // Para retornar JSON
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CidadeController {

    private final CidadeService cidadeService;
    private final EstadoService estadoService; // Para buscar estados para o select
    private final PaisService paisService;     // Para buscar países para o select

    @Autowired
    public CidadeController(CidadeService cidadeService, EstadoService estadoService, PaisService paisService) {
        this.cidadeService = cidadeService;
        this.estadoService = estadoService;
        this.paisService = paisService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_CIDADE')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping("/private/cadastro/cidade")
    public String showPage(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "paisId", required = false) Long paisId,
            @RequestParam(value = "estadoId", required = false) Long estadoId,
            @RequestParam(value = "sort", required = false, defaultValue = "nome,asc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {

        model.addAttribute("pageTitle", "Cadastro de Cidade");
        model.addAttribute("blockTitle", "Lista de Cidades");
        model.addAttribute("blockSubtitle", "Todos os registros");
        model.addAttribute("blockText", "Visualize e gerencie as cidades cadastradas no sistema.");

        model.addAttribute("currentUri", request.getRequestURI());

        // Adiciona a lista de todos os países para popular o primeiro select
        List<Pais> paises = paisService.findAll();
        model.addAttribute("paises", paises);

        // A lista de estados será carregada via AJAX no frontend

        // Adiciona a lista de todas as cidades para a tabela
        //List<Cidade> cidades = cidadeService.findAll();
        //model.addAttribute("cidades", cidades);



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

        Page<Cidade> cidadesPage = cidadeService.findAllFiltered(nome, paisId, estadoId, pageable);
        model.addAttribute("cidades", cidadesPage);
        model.addAttribute("page", cidadesPage);
        model.addAttribute("totalPages", cidadesPage.getTotalPages());

        model.addAttribute("paises", paisService.findAll());


        // CORREÇÃO: Lógica para popular a lista de estados do filtro
        if (paisId != null) {
            model.addAttribute("estadosFiltrar", estadoService.findByPaisId(paisId));
        } else {
            // Se nenhum país for selecionado, carrega todos os estados
            model.addAttribute("estadosFiltrar", estadoService.findAll());
        }

        // Adiciona os valores dos filtros para manter na página após a submissão
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroPaisId", paisId);
        model.addAttribute("filtroEstadoId", estadoId);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);


        return "private/cadastro/cidade"; // Nome da sua view Thymeleaf (cidade.html)
    }

    // NOVO ENDPOINT REST para retornar estados por país (usado pelo AJAX)
    @GetMapping("/api/estados-por-pais/{paisId}")
    @ResponseBody // Indica que o retorno será o corpo da resposta (JSON por padrão com Spring Boot)
    public ResponseEntity<List<Estado>> getEstadosByPais(@PathVariable Long paisId) {
        List<Estado> estados = estadoService.findByPaisId(paisId); // Você precisará criar findByPaisId no EstadoService
        return ResponseEntity.ok(estados);
    }

    @PostMapping("/private/cadastro/cidade/registro")
    public String saveOrUpdateCidade(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("txtNome") String nome,
            @RequestParam("selectEstado") Long estadoId, // Captura o ID do Estado selecionado
            RedirectAttributes redirectAttributes) {

        try {
            // Busca o objeto Estado completo pelo ID
            Estado estado = estadoService.findById(estadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Estado selecionado não encontrado."));

            Cidade cidade;
            String operacao;

            if (id != null && id > 0) {
                // É uma edição
                cidade = cidadeService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada para edição com ID: " + id));
                cidade.setNome(nome);
                cidade.setEstado(estado); // Atualiza o estado associado
                operacao = "atualizada";
            } else {
                // É um novo cadastro
                cidade = new Cidade();
                cidade.setNome(nome);
                cidade.setEstado(estado); // Define o estado associado
                operacao = "cadastrada";
            }

            cidadeService.saveOrUpdate(cidade);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Cidade '" + nome + "' " + operacao + " com sucesso!");

            return "redirect:/private/cadastro/cidade";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro: " + e.getMessage());
            return "redirect:/private/cadastro/cidade";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao processar cidade: " + e.getMessage());
            return "redirect:/private/cadastro/cidade";
        }
    }

    @PostMapping("/private/cadastro/cidade/excluir")
    public String excluirCidade(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            cidadeService.deleteById(id);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Cidade excluída com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir a cidade: " + e.getMessage());
        }
        return "redirect:/private/cadastro/cidade";
    }


    // NOVO ENDPOINT: Para buscar todos os estados
    @GetMapping("/api/estados/todos")
    @ResponseBody
    public List<Estado> getAllEstados() {
        return estadoService.findAll();
    }

}