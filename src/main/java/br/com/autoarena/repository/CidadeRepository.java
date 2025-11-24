package br.com.autoarena.repository;

import br.com.autoarena.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long>, JpaSpecificationExecutor<Cidade> {
    // Para validação: verifica se já existe uma cidade com o mesmo nome dentro do mesmo estado
    boolean existsByNomeAndEstadoId(String nome, Long estadoId);

    // Opcional: Se precisar buscar cidades por um estado específico
    List<Cidade> findByEstadoId(Long estadoId);

    // Método para verificar se existe alguma cidade associada a um determinado ID de estado
    boolean existsByEstadoId(Long estadoId);
}