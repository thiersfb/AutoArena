package br.com.autoarena.service;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import br.com.autoarena.repository.ModeloRepository;
import br.com.autoarena.util.ModeloSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloService {

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MontadoraService montadoraService; // Para buscar a Montadora pelo ID

    @Transactional(readOnly = true)
    public List<Modelo> findAll() {
        return modeloRepository.findAll(Sort.by("nome").ascending());
    }

    @Transactional(readOnly = true)
    public Optional<Modelo> findById(Long id) {
        return modeloRepository.findById(id);
    }

    // Método existente que recebe ID da montadora
    @Transactional(readOnly = true)
    public List<Modelo> findByMontadora(Long montadoraId) {
        Montadora montadora = montadoraService.findById(montadoraId)
                .orElseThrow(() -> new IllegalArgumentException("Montadora não encontrada com ID: " + montadoraId));
        return modeloRepository.findByMontadora(montadora);
    }

    // *** MÉTODO ADICIONADO/CORRIGIDO PARA SER CHAMADO PELO VEICULOCONTROLLER ***
    // Este método recebe um objeto Montadora e delega para o repositório
    @Transactional(readOnly = true)
    public List<Modelo> findByMontadoraAndEnabledTrue(Montadora montadora) {
        if (montadora == null) {
            return List.of(); // Retorna lista vazia ou lança exceção, conforme sua regra de negócio
        }
        return modeloRepository.findByMontadoraAndEnabledTrue(montadora, Sort.by("nome").ascending());
    }

    @Transactional(readOnly = true)
    public List<Modelo> findAllEnabled() {
        return modeloRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    public List<Modelo> findAllDisabled() {
        return modeloRepository.findByEnabledFalse();
    }

    @Transactional
    public Modelo save(Modelo modelo) {
        if (modelo.getMontadora() == null || modelo.getMontadora().getId() == null) {
            throw new IllegalArgumentException("O modelo deve estar associado a uma montadora.");
        }
        if (modelo.getTipoVeiculo() == null || modelo.getTipoVeiculo().getId() == null) {
            throw new IllegalArgumentException("O modelo deve estar associado a um tipo de veículo.");
        }

        // Validação de unicidade do nome DENTRO DA MESMA MONTADORA E TIPO DE VEÍCULO
        // Ajuste para considerar também o TipoVeiculo na validação de unicidade se a regra for essa
        modeloRepository.findByNomeAndMontadora(modelo.getNome(), modelo.getMontadora())
                .ifPresent(existingModelo -> {
                    if (modelo.getId() == null || !existingModelo.getId().equals(modelo.getId())) {
                        throw new IllegalArgumentException("Já existe um modelo '" + modelo.getNome() + "' para a montadora '" + modelo.getMontadora().getNome() + "'.");
                    }
                });

        return modeloRepository.save(modelo);
    }

    @Transactional
    public void deleteById(Long id) {
        modeloRepository.deleteById(id);
    }

    @Transactional
    public Modelo updateStatus(Long id, boolean enabled) {
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modelo não encontrado com ID: " + id));
        modelo.setEnabled(enabled);
        return modeloRepository.save(modelo);
    }


    public List<Modelo> findByMontadoraId(Long montadoraId) {
        return modeloRepository.findByMontadoraId(montadoraId, Sort.by("nome").ascending());
    }

    // NOVO MÉTODO:
    //public List<Modelo> findAllFiltered(Long tipoVeiculoId, Long montadoraId, String nome, Boolean enabled, Sort sort) {
    public Page<Modelo> findAllFiltered(Long tipoVeiculoId, Long montadoraId, String nome, Boolean enabled, Pageable page) {
        Specification<Modelo> specification = Specification.where(null);

        if (tipoVeiculoId != null) {
            specification = specification.and(ModeloSpecifications.hasTipoVeiculo(tipoVeiculoId));
        }

        if (montadoraId != null) {
            specification = specification.and(ModeloSpecifications.hasMontadora(montadoraId));
        }

        if (nome != null && !nome.trim().isEmpty()) {
            specification = specification.and(ModeloSpecifications.hasNomeLike(nome));
        }

        if (enabled != null) {
            specification = specification.and(ModeloSpecifications.hasStatus(enabled));
        }

        // Retorna a lista de modelos usando o repositório com a Specification e o objeto Sort
        ///return modeloRepository.findAll(specification, sort);
        return modeloRepository.findAll(specification, page);
    }


}