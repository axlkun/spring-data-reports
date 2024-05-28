package com.example.springdata.controllers;

import com.example.springdata.models.*;
import com.example.springdata.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main")
public class MainController {

    @Autowired
    private ClienteRepository cliente_repo;

    @Autowired
    private DireccionRepository direccion_repo;

    @Autowired
    private CompraRepository compra_repo;

    @Autowired
    private ProductoRepository producto_repo;

    @Autowired
    private CompraProductoRepository compraproducto_repo;

    @GetMapping("/clientes")
    @ResponseBody
    public List<Cliente> getClientes() {
        return cliente_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/direcciones")
    @ResponseBody
    public List<Direccion> getDirecciones() {
        return direccion_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/compras")
    @ResponseBody
    public List<Compra> getCompras() {
        return compra_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/productos")
    @ResponseBody
    public List<Producto> getProductos() {
        return producto_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/compra-producto")
    @ResponseBody
    public List<CompraProducto> getCompraProductos() {
        return compraproducto_repo.findAll(PageRequest.of(0, 50)).getContent();
    }


    // REPORTE CON BUSQUEDA DINAMICA CON TODOS LOS CAMPOS PERO EN EL CLIENTE
    @GetMapping("/reporte-datatable")
    public Object showReporte() {

        return "main/reporte-datatable";

    }

    // REPORTE CON BUSQUEDA DINAMICA CON TODOS LOS CAMPOS PERO EN EL CLIENTE
    @GetMapping("/reporte-datatable-json")
    public Object showResponseReporte(@RequestParam(defaultValue = "0") int draw,
                                      @RequestParam(defaultValue = "0") int start,
                                      @RequestParam(defaultValue = "10000") int length) {

        // Obtener datos según la búsqueda y la paginación
        Page<CompraProducto> pageComprasProductos = compraproducto_repo.findAll(PageRequest.of(start / length, length));
        List<CompraProducto> compras_productos = pageComprasProductos.getContent();

        // Obtener el total de registros sin paginación
        long total = compraproducto_repo.count();

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", total);
        response.put("recordsFiltered", total);
        response.put("data", compras_productos);

        return ResponseEntity.ok(response);

    }

    // REPORTE SERVER SIDE PROCESSING
    @GetMapping("/reporte-busqueda")
    public Object showReporteBusqeuda() {

        return "main/reporte-busqueda";

    }

    // REPORTE SERVER SIDE PROCESSINGn CON BUSQUEDA DINAMICA
    @GetMapping("/reporte-busqueda-dinamica")
    public Object showReporteBusquedaDinamica() {

        return "main/reporte-busqueda-dinamica";

    }

    // REPORTE SERVER SIDE PROCESSINGn CON BUSQUEDA DINAMICA CUSTOM INPUTS
    @GetMapping("/reporte-custom-input")
    public Object showReporteBusquedaCustomInput() {

        return "main/reporte-custom-input";

    }

    // REPORTE SERVER SIDE PROCESSING CON BUSQUEDA DINAMICA CUSTOM INPUTS Y FUNCION DE TABLA
    @GetMapping("/reporte-funcion-tabla")
    public Object showReporteFuncionTabla() {

        return "main/reporte-funcion-tabla";

    }

    // Get procedure
    @GetMapping("/reporte-procedure")
    public Object showReporteProcedure() {

        return "main/reporte-procedure";

    }

    // create view with procedure
    @GetMapping("/insertar")
    public String mostrarFormulario() {
        return "reporte/create";
    }

}
