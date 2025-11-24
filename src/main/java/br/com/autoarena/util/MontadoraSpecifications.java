package br.com.autoarena.util;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import org.springframework.data.jpa.domain.Specification;

public final class MontadoraSpecifications {

    private MontadoraSpecifications() {
        // Construtor privado para evitar instanciar a classe de utilidade
    }

    // Filtra por ID do Tipo de Veículo
    public static Specification<Montadora> hasTipoVeiculo(Long tipoVeiculoId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tipoVeiculo").get("id"), tipoVeiculoId);
    }

    // Filtra por nome (sem distinção entre maiúsculas/minúsculas)
    public static Specification<Montadora> hasNomeLike(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    // Filtra por status (ativo/inativo)
    public static Specification<Montadora> hasStatus(Boolean enabled) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("enabled"), enabled);
    }
}