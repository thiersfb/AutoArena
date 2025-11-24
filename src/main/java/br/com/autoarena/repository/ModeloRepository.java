package br.com.autoarena.repository;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long>, JpaSpecificationExecutor<Modelo> {

    // Método para buscar um Modelo pelo nome E por uma Montadora específica (para validação de unicidade)
    Optional<Modelo> findByNomeAndMontadora(String nome, Montadora montadora);

    // Método para buscar todos os Modelos de uma Montadora específica
    List<Modelo> findByMontadora(Montadora montadora);

    // NOVO MÉTODO (ou ajuste do existente para filtrar por enabled)
    List<Modelo> findByMontadoraAndEnabledTrue(Montadora montadora, Sort sort);

    // Método para buscar todos os Modelos ativos/habilitados
    List<Modelo> findByEnabledTrue();

    // Método para buscar todos os Modelos inativos/desabilitados
    List<Modelo> findByEnabledFalse();

    Optional<Modelo> findByNome(String nome);

    List<Modelo> findByMontadoraId(Long montadoraId, Sort sort);


}