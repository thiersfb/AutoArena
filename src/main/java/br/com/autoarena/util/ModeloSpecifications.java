package br.com.autoarena.util;

import br.com.autoarena.model.Modelo;
import org.springframework.data.jpa.domain.Specification;

public final class ModeloSpecifications {

    private ModeloSpecifications() {
        // Construtor privado para evitar instanciar a classe de utilidade
    }

    // Filtra por ID do Tipo de Veículo
    public static Specification<Modelo> hasTipoVeiculo(Long tipoVeiculoId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tipoVeiculo").get("id"), tipoVeiculoId);
    }

    // Filtra por ID da Montadora
    public static Specification<Modelo> hasMontadora(Long montadoraId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("montadora").get("id"), montadoraId);
    }

    // Filtra por nome (sem distinção entre maiúsculas/minúsculas)
    public static Specification<Modelo> hasNomeLike(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    // Filtra por status (ativo/inativo)
    public static Specification<Modelo> hasStatus(Boolean enabled) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("enabled"), enabled);
    }
}