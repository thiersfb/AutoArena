package br.com.autoarena.controller;

import br.com.autoarena.model.Veiculo;
import br.com.autoarena.service.VeiculoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http.HttpServletRequest for Spring Boot 3+
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EstoqueController {

    @Autowired
    private VeiculoService veiculoService; // Injeção do serviço

    /*
    @GetMapping("/estoque")
    public String showPage(HttpServletRequest request, Model model) {
        // Você pode adicionar atributos ao modelo que serão acessíveis no template Thymeleaf
        model.addAttribute("pageTitle", "Estoque");
        model.addAttribute("welcomeMessage", "Lista de veículos à venda na AutoArena!");
        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida

        //List<Veiculo> veiculosAVenda = veiculoService.findByForSaleTrue(); // Supondo um método no seu Service
        List<Veiculo> veiculosAVenda = veiculoService.findAllForSale(); // Supondo um método no seu Service
        model.addAttribute("veiculosAVenda", veiculosAVenda);
        model.addAttribute("totalVeiculos", veiculosAVenda.size());

        return "public/estoque"; // Retorna o nome do template Thymeleaf (empresa.html)
    }
    */


    @GetMapping("/estoque")
    public String showPage(HttpServletRequest request,
                           Model model,
                           @RequestParam(defaultValue = "0") int page, // Parâmetro para a página
                           @RequestParam(defaultValue = "9") int size, // Parâmetro para tamanho da página
                           @RequestParam(defaultValue = "id") String sort,// Parâmetro para ordenação
                           @RequestParam(required = false) Long brandId, // Parâmetro Marca
                           @RequestParam(required = false) Long modelId, // Parâmetro Modelo
                           @RequestParam(required = false) String color) { // Parâmetro Cor

        // Lógica de ordenação baseada no parâmetro 'sort'
        Sort sortMethod;
        if ("priceAsc".equals(sort)) {
            sortMethod = Sort.by("precoAnunciado").ascending();
        } else if ("priceDesc".equals(sort)) {
            sortMethod = Sort.by("precoAnunciado").descending();
        } else if ("yearDesc".equals(sort)) {
            sortMethod = Sort.by("anoFabricacao").descending();
        } else if ("yearAsc".equals(sort)) {
            sortMethod = Sort.by("anoFabricacao").ascending();
        } else {
            sortMethod = Sort.by("id").ascending(); // Padrão
        }

        //List<Veiculo> veiculosAVenda = veiculoService.findByForSaleTrue(); // Supondo um método no seu Service
        //List<Veiculo> veiculosAVenda = veiculoService.findAllForSale(); // Supondo um método no seu Service

        // Cria o objeto Pageable para a consulta
        //PageRequest pageable = PageRequest.of(0, size, sortMethod);
        PageRequest pageable = PageRequest.of(page, size, sortMethod); // Usando o 'page' aqui

        // Busca os veículos usando o Pageable
        //Page<Veiculo> veiculosPage = veiculoService.findForSaleTrue(pageable);
        //Page<Veiculo> veiculosPage = veiculoService.findByForSaleTrue(pageable);

        Page<Veiculo> veiculosPage = veiculoService.findByFiltros(pageable, brandId, modelId, color);



        // Você pode adicionar atributos ao modelo que serão acessíveis no template Thymeleaf
        model.addAttribute("pageTitle", "Estoque");
        model.addAttribute("welcomeMessage", "Lista de veículos à venda na AutoArena!");
        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida

        //model.addAttribute("veiculosAVenda", veiculosAVenda);
        //model.addAttribute("totalVeiculos", veiculosAVenda.size());

        model.addAttribute("veiculosAVenda", veiculosPage.getContent());
        model.addAttribute("totalVeiculos", veiculosPage.getTotalElements());

        // Adiciona o objeto Page completo para o HTML usar
        model.addAttribute("veiculosPage", veiculosPage);

        // Adiciona os valores de filtro de volta ao modelo para manter a seleção na view
        model.addAttribute("currentSize", size);
        model.addAttribute("currentSort", sort);
        model.addAttribute("selectedBrandId", brandId);
        model.addAttribute("selectedModelId", modelId);
        model.addAttribute("selectedColor", color);

        // Popula as opções dos filtros para o HTML
        model.addAttribute("montadorasDisponiveis", veiculoService.findAllMontadoras());
        model.addAttribute("modelosDisponiveis", veiculoService.findAllModelos());
        model.addAttribute("coresDisponiveis", veiculoService.findAllCores());


        return "public/estoque"; // Retorna o nome do template Thymeleaf (empresa.html)
    }


}
