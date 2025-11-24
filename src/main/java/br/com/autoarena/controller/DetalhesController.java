package br.com.autoarena.controller;

import br.com.autoarena.model.Veiculo;
import br.com.autoarena.model.VeiculoFoto;
import br.com.autoarena.service.VeiculoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest; // Use jakarta.servlet.http.HttpServletRequest for Spring Boot 3+
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.autoarena.repository.VeiculoFotoRepository; // Adicione este import
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

import java.util.Optional;

@Controller
@RequestMapping("/detalhes")
public class DetalhesController {

    @Autowired
    private VeiculoService veiculoService; // Injeção do serviço

    @Autowired
    private VeiculoFotoRepository veiculoFotoRepository; // Adicione esta injeção


    //@GetMapping("/detalhes")
    @GetMapping()
    public String showPage(HttpServletRequest request, Model model) {
        // Você pode adicionar atributos ao modelo que serão acessíveis no template Thymeleaf
        model.addAttribute("pageTitle", "Detalhes");
        model.addAttribute("welcomeMessage", "Detalhes do veículo");
        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida
        return "public/detalhes_bkp"; // Retorna o nome do template Thymeleaf (empresa.html)
    }

    //@GetMapping("/private/admin/users/edit/{id}")
    //@GetMapping("/detalhes/{id}")
    @GetMapping("/{id}")
    public String detalhesVeiculo(@PathVariable("id") Long id, HttpServletRequest request, Model model) {

        model.addAttribute("pageTitle", "Detalhes do Veículo");
        //model.addAttribute("pageTitle", "Detalhes");
        //model.addAttribute("welcomeMessage", "Detalhes do veículo");
        model.addAttribute("currentUri", request.getRequestURI());  //currentUri é o parâmetro para verificar qual página está sendo exibida

        //Veiculo veiculo = veiculoService.findById(id); // Supondo que você tenha um método findById no seu serviço
        Optional<Veiculo> veiculoOptional = veiculoService.findById(id);

        //if (veiculo != null) {
        if (veiculoOptional.isPresent()) {
            Veiculo veiculo = veiculoOptional.get(); // Desempacota o Optional para obter o objeto Veiculo

            model.addAttribute("veiculo", veiculo);
            //model.addAttribute("blockTitle", veiculo.getMontadora() + " " + veiculo.getModelo());

            return "public/detalhes"; // Nome do seu arquivo HTML
        } else {
            // Lidar com o caso de veículo não encontrado, talvez redirecionar para uma página de erro
            return "redirect:/404"; //TO-DO: criar uma página de erro
        }

    }

    // NOVO MÉTODO: Serve a imagem a partir do banco de dados
    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> exibirFoto(@PathVariable("id") Long id) {
        Optional<VeiculoFoto> fotoOptional = veiculoFotoRepository.findById(id);

        if (fotoOptional.isPresent()) {
            VeiculoFoto foto = fotoOptional.get();
            byte[] fotoBytes = foto.getFotoBytes();

            HttpHeaders headers = new HttpHeaders();
            // A menos que você armazene o tipo de conteúdo, 'image/jpeg' é um bom padrão
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(fotoBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
