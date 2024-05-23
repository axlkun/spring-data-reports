package com.example.springdata.repositories;

import com.example.springdata.models.CompraProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompraProductoRepository extends JpaRepository<CompraProducto, Integer>, JpaSpecificationExecutor<CompraProducto> {

    @Query("SELECT cp FROM CompraProducto cp WHERE cp.compra.cliente.email LIKE %:search%")
    Page<CompraProducto> findBySearchTerm(@Param("search") String search, Pageable pageable);

    @Query("SELECT cp FROM CompraProducto cp WHERE cp.compra.cliente.email LIKE %:search%")
    List<CompraProducto> findBySearchTerm(@Param("search") String search);

    default Page<CompraProducto> findBySearchTermsCustom(String apellido, String ciudad, String monto, Pageable pageable) {
        Specification<CompraProducto> spec = Specification.where(null);

        if (apellido != null && !apellido.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("compra").get("cliente").get("apellido")), "%" + apellido.toLowerCase() + "%"));
        }

        if (ciudad != null && !ciudad.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("compra").get("cliente").get("direcciones").get("ciudad")), "%" + ciudad.toLowerCase() + "%"));
        }

        if (monto != null && !monto.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("compra").get("monto").as(String.class)), "%" + monto.toLowerCase() + "%"));
        }

        return findAll(spec, pageable);
    }

    @Query(value = "SELECT * FROM search_compras(:search_apellido, :search_ciudad, :search_monto)", nativeQuery = true)
    Page<Object[]> searchComprasFuncionTabla(
            @Param("search_apellido") String searchApellido,
            @Param("search_ciudad") String searchCiudad,
            @Param("search_monto") String searchMonto,
            Pageable pageable);

    @Query(value = "SELECT * FROM search_compras(:search_apellido, :search_ciudad, :search_monto)", nativeQuery = true)
    List<Object[]> searchComprasFuncionTablaSinPaginado(
            @Param("search_apellido") String searchApellido,
            @Param("search_ciudad") String searchCiudad,
            @Param("search_monto") String searchMonto);
}

