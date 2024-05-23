package com.example.springdata.repositories;

import com.example.springdata.models.CompraProducto;
import org.springframework.data.jpa.domain.Specification;

public class CompraProductoSpecification {

    public static Specification<CompraProducto> containsTextInAttributes(String searchText) {
        return (root, query, builder) -> {
            String[] keywords = searchText.toLowerCase().split("\\s+");
            Specification<CompraProducto> spec = Specification.where(null);
            for (String keyword : keywords) {
                String likePattern = "%" + keyword + "%";
                Specification<CompraProducto> keywordSpec = (r, q, b) ->
                        b.or(
                                b.like(b.lower(r.get("compra").get("cliente").get("apellido")), likePattern),
                                b.like(b.lower(r.get("compra").get("cliente").get("direcciones").get("ciudad")), likePattern),
                                b.like(b.lower(r.get("compra").get("monto").as(String.class)), likePattern)
                        );
                spec = spec.and(keywordSpec);
            }
            return spec.toPredicate(root, query, builder);
        };
    }
}
