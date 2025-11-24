package br.com.autoarena.service;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import br.com.autoarena.repository.MontadoraRepository;
import br.com.autoarena.util.ModeloSpecifications;
import br.com.autoarena.util.MontadoraSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.slf4j.Logger; // Importe o Logger
//import org.slf4j.LoggerFactory; // Importe o LoggerFactory

import java.util.List;
import java.util.Optional;

@Service
public class MontadoraService {

    //private static final Logger logger = LoggerFactory.getLogger(MontadoraService.class); // Instancie o Logger


    @Autowired
    private MontadoraRepository montadoraRepository;

    @Transactional(readOnly = true)
    public List<Montadora> findAll() {
        //return montadoraRepository.findAll();
        List<Montadora> montadoras = montadoraRepository.findAll();
        //logger.info("MontadoraService: Retornando {} montadoras no findAll().", montadoras.size()); // Adicione este log
        return montadoras;
    }


    // NOVO MÉTODO:
    //public List<Montadora> findAllFiltered(Long tipoVeiculoId, Long montadoraId, String nome, Boolean enabled, Sort sort) {
    public Page<Montadora> findAllFiltered(Long tipoVeiculoId, String nome, Boolean enabled, Pageable page) {
        Specification<Montadora> specification = Specification.where(null);

        if (tipoVeiculoId != null) {
            specification = specification.and(MontadoraSpecifications.hasTipoVeiculo(tipoVeiculoId));
        }

        if (nome != null && !nome.trim().isEmpty()) {
            specification = specification.and(MontadoraSpecifications.hasNomeLike(nome));
        }

        if (enabled != null) {
            specification = specification.and(MontadoraSpecifications.hasStatus(enabled));
        }

        // Retorna a lista de montadoras usando o repositório com a Specification e o objeto Sort
        ///return montadoraRepository.findAll(specification, sort);
        return montadoraRepository.findAll(specification, page);
    }

    @Transactional(readOnly = true)
    public Optional<Montadora> findById(Long id) {
        return montadoraRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Montadora> findAllEnabled() {
        //return montadoraRepository.findByEnabledTrue();

        //List<Montadora> montadoras = montadoraRepository.findByEnabledTrue();

        // Retorna a lista de montadoras ativas já ordenadas por nome (alfabeticamente)
        List<Montadora> montadoras = montadoraRepository.findByEnabledTrue(Sort.by("nome").ascending());

        //logger.info("MontadoraService: Retornando {} montadoras no findAllEnabled().", montadoras.size()); // Adicione este log
        return montadoras;
    }

    @Transactional(readOnly = true)
    public List<Montadora> findAllDisabled() {
        //return montadoraRepository.findByEnabledFalse();
        List<Montadora> montadoras = montadoraRepository.findByEnabledFalse(); // Ou findByAtivoFalse, etc.
        //logger.info("MontadoraService: Retornando {} montadoras no findAllEnabled().", montadoras.size()); // Adicione este log
        return montadoras;
    }

    @Transactional
    public Montadora save(Montadora montadora) {
        // Validação de unicidade do nome
        montadoraRepository.findByNome(montadora.getNome())
                .ifPresent(existingMontadora -> {
                    // Se uma montadora com o mesmo nome já existe E não é o próprio objeto que está sendo atualizado
                    if (montadora.getId() == null || !existingMontadora.getId().equals(montadora.getId())) {
                        throw new IllegalArgumentException("Já existe uma montadora com o nome: " + montadora.getNome());
                    }
                });

        // Garante que o nome não é nulo ou vazio
        if (montadora.getNome() == null || montadora.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da montadora não pode ser vazio.");
        }

        // O Spring Data JPA's save() lida com persistência (ID nulo) e merge (ID não nulo)
        return montadoraRepository.save(montadora);
    }

    @Transactional
    public void deleteById(Long id) {
        // Opcional: Adicionar validação se a Montadora está sendo usada em algum lugar (ex: por um Modelo de Veículo)
        // antes de permitir a exclusão física.
        montadoraRepository.deleteById(id);
    }

    @Transactional
    public Montadora updateStatus(Long id, boolean enabled) {
        Montadora montadora = montadoraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Montadora não encontrada com ID: " + id));
        montadora.setEnabled(enabled);
        return montadoraRepository.save(montadora);
    }
}