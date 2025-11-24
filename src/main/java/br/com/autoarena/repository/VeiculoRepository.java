package br.com.autoarena.repository;

import br.com.autoarena.model.User;
import br.com.autoarena.model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> , JpaSpecificationExecutor<Veiculo> {

    List<Veiculo> findByMontadoraId(Long montadoraId);
    List<Veiculo> findByModeloId(Long modeloId);
    List<Veiculo> findByAnoModelo(Integer anoModelo);

    // Método para buscar todos os veículos que estão à venda
    List<Veiculo> findByForSaleTrue();

    // Método que busca todos os veículos com forSale=true, suportando paginação
    Page<Veiculo> findByForSaleTrue(Pageable pageable);

    // Adicionado para buscar veículos que NÃO estão à venda (vendidos)
    List<Veiculo> findByForSaleFalse();

    // Método para buscar um veículo por ID que está à venda
    Optional<Veiculo> findByIdAndForSaleTrue(Long id);

    // *** REMOVIDO: findByForSaleTrueAndUsuarioCadastro - AGORA O USUÁRIO VÊ TODOS OS SEUS VEÍCULOS ***

    // NOVO MÉTODO: Busca todos os veículos cadastrados por um usuário específico, independente do status 'forSale'
    List<Veiculo> findByUsuarioCadastro(User usuarioCadastro);


    /**
     * Busca todos os veículos que estão marcados como 'à venda' (forSale = true)
     * e que foram cadastrados por um usuário específico.
     *
     * @param usuarioCadastro O objeto User que é o proprietário/registrador do veículo.
     * @return Uma lista de veículos que correspondem aos critérios.
     */
    List<Veiculo> findByForSaleTrueAndUsuarioCadastro(User usuarioCadastro);

    //Pagina Publica - Filtrar Busca de veiculos

    @Query("SELECT DISTINCT v.cor FROM Veiculo v WHERE v.forSale = true")
    List<String> findDistinctCoresByForSaleTrue();

    /*********************************** RELATORIOS ***********************************/

    /**
     * Conta o número de veículos por montadora.
     * @return Uma lista de arrays de objetos, onde cada array contém o nome da montadora (String) e a contagem de veículos (Long).
     */
    @Query("SELECT v.montadora.nome, COUNT(v) " +
            " FROM Veiculo v " +
            "WHERE v.forSale = true " +
            "GROUP BY v.montadora.nome")
    List<Object[]> countVeiculosByMontadora();


}