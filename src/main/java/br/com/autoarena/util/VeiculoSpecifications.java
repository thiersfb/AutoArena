package br.com.autoarena.util;


//import br.com.autoarena.enums.StatusVeiculo;
import br.com.autoarena.model.User;
import br.com.autoarena.model.Veiculo;
import org.springframework.data.jpa.domain.Specification;

public final class VeiculoSpecifications {

    private VeiculoSpecifications() {
        // Construtor privado para classe de utilidade
    }

    public static Specification<Veiculo> hasId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<Veiculo> hasTipoVeiculo(Long tipoVeiculoId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tipoVeiculo").get("id"), tipoVeiculoId);
    }

    public static Specification<Veiculo> hasMontadora(Long montadoraId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("montadora").get("id"), montadoraId);
    }

    public static Specification<Veiculo> hasModelo(Long modeloId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("modelo").get("id"), modeloId);
    }

    public static Specification<Veiculo> hasPlaca(String placa) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("placa")), "%" + placa.toLowerCase() + "%");
    }

    /*
    public static Specification<Veiculo> hasStatus(StatusVeiculo status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }
    */

    public static Specification<Veiculo> isForSale(Boolean forSale) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("forSale"), forSale);
    }


    // NOVO MÉTODO: Filtra veículos cadastrados por um usuário específico
    public static Specification<Veiculo> isCadastradoPor(User user) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user);
    }
}
