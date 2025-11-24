package br.com.autoarena.controller;

import br.com.autoarena.model.Modelo;
//import br.com.autoarena.model.Montadora;
import br.com.autoarena.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Indica que este Controller retornará dados JSON
@RequestMapping("/api")
public class ModeloRestController {

    @Autowired
    private ModeloService modeloService; // Crie este Service se ainda não existir

    @GetMapping("/modelos/por-montadora/{montadoraId}")
    public List<Modelo> getModelosPorMontadora(@PathVariable Long montadoraId) {
        return modeloService.findByMontadoraId(montadoraId);

        //Montadora montadora = new Montadora();
        //montadora.setId(montadoraId);
        //return modeloService.findByMontadoraAndEnabledTrue(montadora);
    }



}