package com.example.springdata.services;

import com.example.springdata.models.CompraProducto;
import com.example.springdata.repositories.CompraProductoRepository;
import com.example.springdata.repositories.CompraProductoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompraProductoService {

    @Autowired
    private CompraProductoRepository compraproductoRepo;

    public Page<CompraProducto> getAllComprasServer(String search, int page, int size) {
        if (search == null || search.isEmpty()) {
            return compraproductoRepo.findAll(PageRequest.of(page, size));
        } else {
            return compraproductoRepo.findBySearchTerm(search, PageRequest.of(page, size));
        }
    }

    public List<CompraProducto> getAllCompras(String search) {
        if (search == null || search.isEmpty()) {
            return compraproductoRepo.findAll();
        } else {
            return compraproductoRepo.findBySearchTerm(search);
        }
    }

    public Page<CompraProducto> getAllComprasBusqueda(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (search == null || search.isEmpty()) {
            return compraproductoRepo.findAll(pageable);
        } else {
            Specification<CompraProducto> spec = CompraProductoSpecification.containsTextInAttributes(search);
            return compraproductoRepo.findAll(spec, pageable);
        }
    }

    public Page<CompraProducto> getAllComprasCustom(String apellido, String ciudad, String monto, int page, int size) {
        if (apellido == null && ciudad == null && monto == null) {
            return compraproductoRepo.findAll(PageRequest.of(page, size));
        } else {
            // Implementa la lógica para filtrar por apellido, ciudad y monto
            return compraproductoRepo.findBySearchTermsCustom(apellido, ciudad, monto, PageRequest.of(page, size));
        }
    }

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

}
