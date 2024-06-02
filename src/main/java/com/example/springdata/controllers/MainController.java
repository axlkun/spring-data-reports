package com.example.springdata.controllers;

import com.example.springdata.models.*;
import com.example.springdata.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
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

    @GetMapping("/main/clientes")
    @ResponseBody
    public List<Cliente> getClientes() {
        return cliente_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/main/direcciones")
    @ResponseBody
    public List<Direccion> getDirecciones() {
        return direccion_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/main/compras")
    @ResponseBody
    public List<Compra> getCompras() {
        return compra_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/main/productos")
    @ResponseBody
    public List<Producto> getProductos() {
        return producto_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    @GetMapping("/main/compra-producto")
    @ResponseBody
    public List<CompraProducto> getCompraProductos() {
        return compraproducto_repo.findAll(PageRequest.of(0, 50)).getContent();
    }

    //menu
    @GetMapping("/main")
    public Object showMenu() {

        return "main/main";

    }

    // REPORTE CON BUSQUEDA DINAMICA CON TODOS LOS CAMPOS PERO EN EL CLIENTE
    @GetMapping("/main/reporte-datatable")
    public Object showReporte() {

        return "main/reporte-datatable";

    }

    // REPORTE SERVER SIDE PROCESSING
    @GetMapping("/main/reporte-busqueda")
    public Object showReporteBusqeuda() {

        return "main/reporte-busqueda";

    }

    // REPORTE SERVER SIDE PROCESSINGn CON BUSQUEDA DINAMICA
    @GetMapping("/main/reporte-busqueda-dinamica")
    public Object showReporteBusquedaDinamica() {

        return "main/reporte-busqueda-dinamica";

    }

    // REPORTE SERVER SIDE PROCESSINGn CON BUSQUEDA DINAMICA CUSTOM INPUTS
    @GetMapping("/main/reporte-custom-input")
    public Object showReporteBusquedaCustomInput() {

        return "main/reporte-custom-input";

    }

    // REPORTE SERVER SIDE PROCESSING CON BUSQUEDA DINAMICA CUSTOM INPUTS Y FUNCION DE TABLA
    @GetMapping("/main/reporte-funcion-tabla")
    public Object showReporteFuncionTabla() {

        return "main/reporte-funcion-tabla";

    }

    // Get procedure
    @GetMapping("/main/reporte-procedure")
    public Object showReporteProcedure() {

        return "reporte/reporte-procedure";

    }

    // create view with procedure
    @GetMapping("/main/insertar")
    public String mostrarFormulario() {
        return "reporte/create";
    }

    // create view usuarios
    @GetMapping("/admin/usuarios")
    public String mostrarUsuarios() {
        return "main/usuarios";
    }

}
