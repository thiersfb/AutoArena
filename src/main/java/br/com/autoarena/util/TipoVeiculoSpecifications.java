package br.com.autoarena.util;

import br.com.autoarena.model.TipoVeiculo;
import org.springframework.data.jpa.domain.Specification;

public final class TipoVeiculoSpecifications {

    private TipoVeiculoSpecifications() {
        // Construtor privado para evitar instanciar a classe de utilidade
    }

    // Filtra por nome (sem distinção entre maiúsculas/minúsculas)
    public static Specification<TipoVeiculo> hasNomeLike(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    // Filtra por status (ativo/inativo)
    public static Specification<TipoVeiculo> hasStatus(Boolean enabled) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("enabled"), enabled);
    }
}