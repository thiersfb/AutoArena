package br.com.autoarena.repository;

import br.com.autoarena.model.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long>, JpaSpecificationExecutor<Pais> {
    // JpaRepository já fornece métodos como save(), findById(), findAll(), delete(), etc.


    @Override
    List<Pais> findAllById(Iterable<Long> longs);

    Optional<Pais> findByNome(String nome);
}
