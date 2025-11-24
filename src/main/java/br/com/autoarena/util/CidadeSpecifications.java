package br.com.autoarena.util;

import br.com.autoarena.model.Cidade;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CidadeSpecifications {

    public static Specification<Cidade> hasNome(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Cidade> hasPais(Long paisId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado").get("pais").get("id"), paisId);
    }

    public static Specification<Cidade> hasEstado(Long estadoId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado").get("id"), estadoId);
    }
}
