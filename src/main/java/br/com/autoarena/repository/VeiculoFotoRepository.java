package br.com.autoarena.repository;

import br.com.autoarena.model.Veiculo;
import br.com.autoarena.model.VeiculoFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoFotoRepository extends JpaRepository<VeiculoFoto, Long> {

    List<VeiculoFoto> findByVeiculoId(Long veiculoId);

    VeiculoFoto findByVeiculoIdAndIsFotoPrincipalTrue(Long veiculoId);

    // NOVO MÉTODO: Encontra todas as fotos de um veículo, exceto a que será excluída
    List<VeiculoFoto> findByVeiculoIdAndIdNot(Long veiculoId, Long fotoId);


}