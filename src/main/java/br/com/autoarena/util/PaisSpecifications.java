package br.com.autoarena.util;

import br.com.autoarena.model.Pais;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PaisSpecifications {

    public static Specification<Pais> hasNome(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

}
