// src/main/java/br/com/autoarena/controller/EstadoController.java
package br.com.autoarena.controller;

import br.com.autoarena.model.Cidade;
import br.com.autoarena.model.Estado;
import br.com.autoarena.model.Pais; // Importe a entidade Pais
import br.com.autoarena.service.EstadoService;
import br.com.autoarena.service.PaisService; // Importe o PaisService
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
import java.util.Optional;

@Controller
public class EstadoController {

    private final EstadoService estadoService;
    private final PaisService paisService; // Injetar PaisService para obter a lista de países

    @Autowired
    public EstadoController(EstadoService estadoService, PaisService paisService) {
        this.estadoService = estadoService;
        this.paisService = paisService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_LOCAIS')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping("/private/cadastro/estado")
    public String showPage(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "paisId", required = false) Long paisId,
            //@RequestParam(value = "estadoId", required = false) Long estadoId,
            @RequestParam(value = "sort", required = false, defaultValue = "nome,asc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("pageTitle", "Cadastro de Estado");
        model.addAttribute("blockTitle", "Lista de Estados");
        model.addAttribute("blockSubtitle", "Todos os registros");
        model.addAttribute("blockText", "Visualize e gerencie os estados cadastrados no sistema.");

        model.addAttribute("currentUri", request.getRequestURI());

        // Adiciona a lista de todos os países para popular o select
        List<Pais> paises = paisService.findAll();
        model.addAttribute("paises", paises);

        // Adiciona a lista de todos os estados para a tabela
        //List<Estado> estados = estadoService.findAll();
        //model.addAttribute("estados", estados);


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

        Page<Estado> estadosPage = estadoService.findAllFiltered(nome, paisId, pageable);
        //        model.addAttribute("cidades", cidadesPage);
        model.addAttribute("estados", estadosPage);
        model.addAttribute("page", estadosPage);
        model.addAttribute("totalPages", estadosPage.getTotalPages());



        // Adiciona os valores dos filtros para manter na página após a submissão
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroPaisId", paisId);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);


        return "private/cadastro/estado"; // Nome da sua view Thymeleaf (estado.html)
    }

    @PostMapping("/private/cadastro/estado/registro")
    public String saveOrUpdateEstado(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("txtNome") String nome,
            @RequestParam("txtUf") String uf,
            @RequestParam("selectPais") Long paisId, // Captura o ID do País selecionado
            RedirectAttributes redirectAttributes) {

        try {
            // Busca o objeto Pais completo pelo ID
            Pais pais = paisService.findById(paisId)
                    .orElseThrow(() -> new IllegalArgumentException("País selecionado não encontrado."));

            Estado estado;
            String operacao;

            if (id != null && id > 0) {
                // É uma edição: busca o estado existente
                estado = estadoService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Estado não encontrado para edição com ID: " + id));
                estado.setNome(nome);
                estado.setUf(uf);
                estado.setPais(pais); // Atualiza o país associado
                operacao = "atualizado";
            } else {
                // É um novo cadastro
                estado = new Estado();
                estado.setNome(nome);
                estado.setUf(uf);
                estado.setPais(pais); // Define o país associado
                operacao = "cadastrado";
            }

            estadoService.saveOrUpdate(estado); // Salva ou atualiza no serviço

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Estado '" + nome + "' " + operacao + " com sucesso!");

            return "redirect:/private/cadastro/estado";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro: " + e.getMessage());
            return "redirect:/private/cadastro/estado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao processar estado: " + e.getMessage());
            return "redirect:/private/cadastro/estado";
        }
    }

    @PostMapping("/private/cadastro/estado/excluir")
    public String excluirEstado(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            estadoService.deleteById(id);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Estado excluído com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir o estado: " + e.getMessage());
        }
        return "redirect:/private/cadastro/estado";
    }
}