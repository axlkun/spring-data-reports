package com.example.springdata.services;

import com.example.springdata.models.CompraProducto;
import com.example.springdata.repositories.CompraProductoRepository;
import com.example.springdata.repositories.CompraProductoSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompraProductoService {

    @Autowired
    private CompraProductoRepository compraproductoRepo;

    @Autowired
    private EntityManager entityManager;

    // Reporte server side default input search
    public Page<CompraProducto> getAllComprasServer(String search, int page, int size) {
        if (search == null || search.isEmpty()) {
            return compraproductoRepo.findAll(PageRequest.of(page, size));
        } else {
            return compraproductoRepo.findBySearchTerm(search, PageRequest.of(page, size));
        }
    }

    // Excel reporte server side default input search
    public List<CompraProducto> getAllCompras(String search) {
        if (search == null || search.isEmpty()) {
            return compraproductoRepo.findAll();
        } else {
            return compraproductoRepo.findBySearchTerm(search);
        }
    }

    // Busqueda general desde el cliente
    public Page<CompraProducto> getAllComprasBusqueda(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (search == null || search.isEmpty()) {
            return compraproductoRepo.findAll(pageable);
        } else {
            Specification<CompraProducto> spec = CompraProductoSpecification.containsTextInAttributes(search);
            return compraproductoRepo.findAll(spec, pageable);
        }
    }

    // Busqueda 3 inputs
    public Page<CompraProducto> getAllComprasCustom(String apellido, String ciudad, String monto, int page, int size) {
        if (apellido == null && ciudad == null && monto == null) {
            return compraproductoRepo.findAll(PageRequest.of(page, size));
        } else {
            // Implementa la lógica para filtrar por apellido, ciudad y monto
            return compraproductoRepo.findBySearchTermsCustom(apellido, ciudad, monto, PageRequest.of(page, size));
        }
    }

    // Reporte funcion de tabla
    public Page<Map<String, Object>> getAllComprasFuncionTabla(String apellido, String ciudad, String monto, int page, int size) {

        Page<Object[]> results = compraproductoRepo.searchComprasFuncionTabla(apellido, ciudad, monto, PageRequest.of(page, size));
        List<Map<String, Object>> mappedResults = results.getContent().stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("cliente_id", record[0]);
            map.put("apellido", record[1]);
            map.put("email", record[2]);
            map.put("ciudad", record[3]);
            map.put("monto", record[4]);
            map.put("fecha", record[5]);
            return map;
        }).collect(Collectors.toList());

        return new PageImpl<>(mappedResults, PageRequest.of(page, size), results.getTotalElements());
    }

    // Excel reporte funcion de tabla
    public List<Map<String, Object>> getAllComprasFuncionTablaSinPaginado(String apellido, String ciudad, String monto) {
        List<Object[]> results = compraproductoRepo.searchComprasFuncionTablaSinPaginado(apellido, ciudad, monto);

        return results.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("cliente_id", record[0]);
            map.put("apellido", record[1]);
            map.put("email", record[2]);
            map.put("ciudad", record[3]);
            map.put("monto", record[4]);
            map.put("fecha", record[5]);
            return map;
        }).collect(Collectors.toList());
    }

    // Reporte procedimiento almacenado
    /*public List<Object[]> getFilteredCompras(String apellido, String ciudad, String monto) {
        // Ejecutar el procedimiento almacenado
        StoredProcedureQuery procedureQuery = entityManager.createStoredProcedureQuery("public.procedure_compras");

        procedureQuery.registerStoredProcedureParameter("p_apellido", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("p_monto", String.class, ParameterMode.IN);

        procedureQuery.setParameter("p_apellido", apellido);
        procedureQuery.setParameter("p_ciudad", ciudad);
        procedureQuery.setParameter("p_monto", monto);

        procedureQuery.execute();

        // Consultar los resultados de la tabla temporal
        Query query = entityManager.createNativeQuery("SELECT * FROM persistent_compras_result");
        return query.getResultList();
    }*/


    public Page<Map<String, Object>> getFilteredCompras(String apellido, String ciudad, String monto, int page, int size) {
        // Ejecutar el procedimiento almacenado
        StoredProcedureQuery procedureQuery = entityManager.createStoredProcedureQuery("public.procedure_compras");

        procedureQuery.registerStoredProcedureParameter("p_apellido", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("p_monto", String.class, ParameterMode.IN);

        procedureQuery.setParameter("p_apellido", apellido);
        procedureQuery.setParameter("p_ciudad", ciudad);
        procedureQuery.setParameter("p_monto", monto);

        procedureQuery.execute();

        // Consultar los resultados de la tabla temporal
        Query query = entityManager.createNativeQuery("SELECT * FROM persistent_compras_result ORDER BY cliente_id DESC");

        // Obtener el total de resultados
        int totalResults = query.getResultList().size();

        // Aplicar paginación
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Object[]> results = query.getResultList();

        // Mapear los resultados
        List<Map<String, Object>> mappedResults = results.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("cliente_id", record[0]);
            map.put("apellido", record[1]);
            map.put("email", record[2]);
            map.put("ciudad", record[3]);
            map.put("monto", record[4]);
            //map.put("fecha", record[5]);
            // Convertir y formatear la fecha
            if (record[5] != null) {
                String fechaOriginal = record[5].toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime dateTime = LocalDateTime.parse(fechaOriginal, formatter);
                String formattedDate = dateTime.toLocalDate().toString();


                map.put("fecha", formattedDate);
            } else {
                map.put("fecha", null);
            }
            return map;
        }).collect(Collectors.toList());

        return new PageImpl<>(mappedResults, PageRequest.of(page, size), totalResults);
    }

    public Map<String, Object> insertarDatos(String apellido, String email, String ciudad, BigDecimal monto, Date fecha) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("insertar_datos")
                .registerStoredProcedureParameter("p_apellido", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_email", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_ciudad", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_monto", BigDecimal.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_fecha", Date.class, ParameterMode.IN)
                .registerStoredProcedureParameter("cliente_id", Integer.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("direccion_id", Integer.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("compra_id", Integer.class, ParameterMode.OUT)
                .setParameter("p_apellido", apellido)
                .setParameter("p_email", email)
                .setParameter("p_ciudad", ciudad)
                .setParameter("p_monto", monto)
                .setParameter("p_fecha", fecha);

        storedProcedureQuery.execute();

        int clienteId = (int) storedProcedureQuery.getOutputParameterValue("cliente_id");
        int direccionId = (int) storedProcedureQuery.getOutputParameterValue("direccion_id");
        int compraId = (int) storedProcedureQuery.getOutputParameterValue("compra_id");

        Map<String, Object> result = new HashMap<>();
        result.put("clienteId", clienteId);
        result.put("direccionId", direccionId);
        result.put("compraId", compraId);

        return result;
    }

}
