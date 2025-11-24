package br.com.autoarena.service;

import br.com.autoarena.model.Montadora;
import br.com.autoarena.model.TipoVeiculo;
import br.com.autoarena.repository.TipoVeiculoRepository;
import br.com.autoarena.util.TipoVeiculoSpecifications;
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
public class TipoVeiculoService {

    @Autowired
    private TipoVeiculoRepository tipoVeiculoRepository;

    @Transactional(readOnly = true)
    public List<TipoVeiculo> findAll() {
        return tipoVeiculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<TipoVeiculo> findById(Long id) {
        return tipoVeiculoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<TipoVeiculo> findAllEnabled() {

        List<TipoVeiculo> tipos = tipoVeiculoRepository.findByEnabledTrue(Sort.by("nome").ascending());
        //return tipoVeiculoRepository.findByEnabledTrue();

        return tipos;
    }

    @Transactional(readOnly = true)
    public List<TipoVeiculo> findAllDisabled() {
        return tipoVeiculoRepository.findByEnabledFalse();
    }

    @Transactional
    public TipoVeiculo save(TipoVeiculo tipoVeiculo) {
        // Validação de unicidade do nome
        tipoVeiculoRepository.findByNome(tipoVeiculo.getNome())
                .ifPresent(tv -> {
                    if (!tv.getId().equals(tipoVeiculo.getId())) { // Permite atualizar o próprio registro
                        throw new IllegalArgumentException("Já existe um tipo de veículo com o nome: " + tipoVeiculo.getNome());
                    }
                });

        return tipoVeiculoRepository.save(tipoVeiculo);
    }

    @Transactional
    public void deleteById(Long id) {
        // Opcional: Adicionar validação se o TipoVeiculo está sendo usado em algum lugar
        // antes de permitir a exclusão física.
        tipoVeiculoRepository.deleteById(id);
    }

    @Transactional
    public TipoVeiculo updateStatus(Long id, boolean enabled) {
        TipoVeiculo tipoVeiculo = tipoVeiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Veículo não encontrado com ID: " + id));
        tipoVeiculo.setEnabled(enabled);
        return tipoVeiculoRepository.save(tipoVeiculo);
    }

    // Método para registrar um novo tipo de veículo (nome mais específico)
    @Transactional
    public TipoVeiculo registerNewTipoVeiculo(TipoVeiculo newTipoVeiculo) {
        // Garante que o nome não é nulo ou vazio e que a flag 'enabled' está setada (se houver default no construtor)
        if (newTipoVeiculo.getNome() == null || newTipoVeiculo.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do tipo de veículo não pode ser vazio.");
        }

        // Validação de unicidade do nome ao registrar
        tipoVeiculoRepository.findByNome(newTipoVeiculo.getNome())
                .ifPresent(tv -> {
                    throw new IllegalArgumentException("Já existe um tipo de veículo com o nome: " + newTipoVeiculo.getNome());
                });

        return tipoVeiculoRepository.save(newTipoVeiculo);
    }

    public Page<TipoVeiculo> findAllFiltered(String nome, Boolean enabled, Pageable page) {
        Specification<TipoVeiculo> specification = Specification.where(null);

        if (nome != null && !nome.trim().isEmpty()) {
            specification = specification.and(TipoVeiculoSpecifications.hasNomeLike(nome));
        }

        if (enabled != null) {
            specification = specification.and(TipoVeiculoSpecifications.hasStatus(enabled));
        }

        return tipoVeiculoRepository.findAll(specification, page);
    }
}