package br.com.autoarena.util;

import br.com.autoarena.model.Cidade;
import br.com.autoarena.model.Estado;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EstadoSpecifications {

    public static Specification<Estado> hasNome(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Estado> hasPais(Long paisId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("pais").get("id"), paisId);
    }
}
