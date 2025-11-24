package br.com.autoarena.repository;

import br.com.autoarena.model.Montadora;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MontadoraRepository extends JpaRepository<Montadora, Long>, JpaSpecificationExecutor<Montadora> {

    // Método para buscar uma Montadora pelo nome (para validação de unicidade)
    Optional<Montadora> findByNome(String nome);

    // Método para buscar todas as Montadoras ativas/habilitadas
    List<Montadora> findByEnabledTrue();

    // Método para buscar todas as Montadoras inativas/desabilitadas
    List<Montadora> findByEnabledFalse();

    //-----------------------------------------------------------------
    // Método que busca apenas montadoras ativas, ordenando por nome.
    // O nome do método já indica a ordenação.
    ///List<Montadora> findByEnabledTrueOrderByNameAsc();

    // Ou um método mais genérico
    List<Montadora> findByEnabledTrue(Sort sort);

}