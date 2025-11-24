package br.com.autoarena.repository;

import br.com.autoarena.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long>, JpaSpecificationExecutor<Estado> {
    // Spring Data JPA já fornece findAll(), save(), findById(), deleteById()

    // Exemplo de método customizado se precisar:
    Optional<Estado> findByUf(String uf);
    boolean existsByNomeAndPaisId(String nome, Long paisId);
    boolean existsByUfAndPaisId(String uf, Long paisId);

    // NOVO MÉTODO: Para buscar estados por ID do país
    List<Estado> findByPaisId(Long paisId);

    // Método para verificar se existe algum estado associado a um determinado ID de país
    boolean existsByPaisId(Long paisId);
}