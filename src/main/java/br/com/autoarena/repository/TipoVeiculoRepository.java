package br.com.autoarena.repository;

import br.com.autoarena.model.TipoVeiculo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoVeiculoRepository extends JpaRepository<TipoVeiculo, Long>, JpaSpecificationExecutor<TipoVeiculo> {

    // Método para buscar um TipoVeiculo pelo nome (útil para validação de unicidade)
    Optional<TipoVeiculo> findByNome(String nome);

    // Método para buscar todos os TiposVeiculo ativos
    List<TipoVeiculo> findByEnabledTrue(Sort sort);

    // Método para buscar todos os TiposVeiculo inativos
    List<TipoVeiculo> findByEnabledFalse();
}