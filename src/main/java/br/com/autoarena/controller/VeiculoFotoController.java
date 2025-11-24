package br.com.autoarena.controller;

import br.com.autoarena.enums.TipoCarroceria;
import br.com.autoarena.enums.TipoCombustivel;
import br.com.autoarena.enums.TipoDirecao;
import br.com.autoarena.model.*;
import br.com.autoarena.service.VeiculoService;
import br.com.autoarena.service.VeiculoFotoService; // <-- NOVO IMPORT
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
//@RequestMapping("/private/veiculos/fotos")
@RequestMapping("/private/cadastro/veiculosFotos")
public class VeiculoFotoController {

    private final VeiculoService veiculoService; // Ainda precisamos dele para buscar o Veiculo
    private final VeiculoFotoService veiculoFotoService; // <-- NOVO SERVICE

    @Autowired
    public VeiculoFotoController(VeiculoService veiculoService, VeiculoFotoService veiculoFotoService) {
        this.veiculoService = veiculoService;
        this.veiculoFotoService = veiculoFotoService;
    }

    /**
     * Exibe a página de gerenciamento de fotos de um veículo específico.
     *
     * @param veiculoId O ID do veículo.
     * @param model O modelo do Spring para passar dados para a view.
     * @return O nome da view (template Thymeleaf).
     */
    @GetMapping("/{veiculoId}")
    //public String gerenciarFotos(@PathVariable("veiculoId") Long veiculoId, Model model) {
    public String showFotosPage(@PathVariable("veiculoId") Long veiculoId, Model model) {
        //Veiculo veiculo = veiculoService.findById(veiculoId);
        Optional<Veiculo> veiculoOptional = veiculoService.findById(veiculoId);


        // Verifica se o Optional contém um valor.
        if (veiculoOptional.isPresent()) {
            Veiculo veiculo = veiculoOptional.get(); // Extrai o objeto Veiculo do Optional


            model.addAttribute("pageTitle", "Fotos do Veículo");
            model.addAttribute("blockTitle", "Fotos do Veículo ID " + veiculo.getId());
            model.addAttribute("blockSubtitle", "Todos os registros");


            model.addAttribute("veiculo", veiculo); // Adiciona o objeto Veiculo, não o Optional

            List<VeiculoFoto> veiculosFotos;

            //veiculos = veiculoService.listarVeiculosAVendaDoUsuario(currentUser); // Vendedor vê APENAS seus veículos cadastrados (todos, forSale=true/false)

            veiculosFotos = veiculoFotoService.findAll(veiculo.getId());
            model.addAttribute("fotos", veiculosFotos);


            return "private/cadastro/veiculosFotos";
        } else {
            // Se o veículo não for encontrado, redireciona para a lista.
            // O ideal é adicionar uma mensagem de erro aqui.
            return "redirect:/private/cadastro/veiculos/lista";
        }

    }

    @PostMapping("/salvar/{veiculoId}")
    public String salvarFoto(@PathVariable("veiculoId") Long veiculoId,
                             @RequestParam("foto") MultipartFile foto,
                             RedirectAttributes attributes) {
        try {
            //Veiculo veiculo = veiculoService.findById(veiculoId);
            Optional<Veiculo> veiculo = veiculoService.findById(veiculoId);
            if (veiculo.isEmpty()) {
                throw new IllegalArgumentException("Veículo não encontrado.");
            }
            // Chama o novo serviço de fotos
            veiculoFotoService.salvarFotoDoVeiculo(veiculo.orElse(null), foto);


            //redirectAttributes.addFlashAttribute("messageType", "success");
            //redirectAttributes.addFlashAttribute("message", "Veículo salvo com sucesso!");

            attributes.addFlashAttribute("messageType", "success");
            attributes.addFlashAttribute("message", "Foto salva com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("messageType", "error");
            attributes.addFlashAttribute("message", "Erro ao salvar a foto: " + e.getMessage());
        }
        //return "redirect:/private/veiculos/fotos/" + veiculoId;
        return "redirect:/private/cadastro/veiculosFotos/" + veiculoId;
    }

    @PostMapping("/excluir/{fotoId}")
    public String excluirFoto(@PathVariable("fotoId") Long fotoId,
                              @RequestParam("veiculoId") Long veiculoId,
                              RedirectAttributes attributes) {
        try {
            veiculoFotoService.excluirFoto(fotoId); // Chama o novo serviço de fotos
            //attributes.addFlashAttribute("mensagemSucesso", "Foto excluída com sucesso!");

            attributes.addFlashAttribute("messageType", "success");
            attributes.addFlashAttribute("message", "Foto excluída com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("messageType", "error");
            attributes.addFlashAttribute("message", "Erro ao excluir a foto: " + e.getMessage());
        }
        //return "redirect:/private/veiculos/fotos/" + veiculoId;
        return "redirect:/private/cadastro/veiculosFotos/" + veiculoId;
    }

    @GetMapping("/visualizar/{fotoId}")
    public ResponseEntity<byte[]> visualizarFoto(@PathVariable("fotoId") Long fotoId) {
        //Optional<VeiculoFoto> fotoOptional = veiculoFotoService.findById(fotoId);
        Optional<VeiculoFoto> fotoOptional = veiculoFotoService.findById(fotoId);

        if (fotoOptional.isPresent()) {
            VeiculoFoto foto = fotoOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(foto.getFotoBytes(), headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // NOVO MÉTODO PARA DEFINIR A FOTO PRINCIPAL
    @PostMapping("/definir-principal/{fotoId}")
    public String definirFotoPrincipal(@PathVariable("fotoId") Long fotoId,
                                       @RequestParam("veiculoId") Long veiculoId,
                                       RedirectAttributes attributes) {
        try {
            veiculoFotoService.definirFotoPrincipal(veiculoId, fotoId);
            attributes.addFlashAttribute("messageType", "success");
            attributes.addFlashAttribute("message", "Foto principal atualizada com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("messageType", "error");
            attributes.addFlashAttribute("message", "Erro ao definir a foto principal: " + e.getMessage());
        }
        //return "redirect:/private/veiculos/fotos/" + veiculoId;
        return "redirect:/private/cadastro/veiculosFotos/" + veiculoId;

    }



}