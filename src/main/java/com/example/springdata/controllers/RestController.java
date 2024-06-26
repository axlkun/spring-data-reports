package com.example.springdata.controllers;

import com.example.springdata.models.CompraProducto;
import com.example.springdata.models.User;
import com.example.springdata.repositories.CompraProductoRepository;
import com.example.springdata.services.ClienteService;
import com.example.springdata.services.CompraProductoService;
import com.example.springdata.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private CompraProductoRepository compraproducto_repo;

    @Autowired
    private CompraProductoService compraProductoService;

    @Autowired
    private ClienteService clienteService;

    // REPORTE CON BUSQUEDA DINAMICA CON TODOS LOS CAMPOS PERO EN EL CLIENTE
    @GetMapping("/main/reporte-datatable-json")
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

    // SERVER SIDE PROCESSING
    @PostMapping("/api/compras")
    public Map<String, Object> getCompras(@RequestBody Map<String, Object> params) {
        int draw = (int) params.get("draw");
        int start = (int) params.get("start");
        int length = (int) params.get("length");

        Map<String, Object> search = (Map<String, Object>) params.get("search");
        String searchValue = search != null ? (String) search.get("value") : "";

        int page = start / length;
        Page<CompraProducto> comprasPage = compraProductoService.getAllComprasServer(searchValue, page, length);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", comprasPage.getTotalElements());
        response.put("recordsFiltered", comprasPage.getTotalElements());
        response.put("data", comprasPage.getContent());

        return response;
    }

    // SERVER SIDE PROCESSING CON BUSQUEDA 1 CAMPO
    @PostMapping("/api/compras-busqueda")
    public Map<String, Object> getComprasBusqueda(@RequestBody Map<String, Object> params) {
        int draw = (int) params.get("draw");
        int start = (int) params.get("start");
        int length = (int) params.get("length");

        Map<String, Object> search = (Map<String, Object>) params.get("search");
        String searchValue = search != null ? (String) search.get("value") : "";

        int page = start / length;
        Page<CompraProducto> comprasPage = compraProductoService.getAllComprasBusqueda(searchValue, page, length);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", comprasPage.getTotalElements());
        response.put("recordsFiltered", comprasPage.getTotalElements());
        response.put("data", comprasPage.getContent());

        return response;
    }

    // Busqueda con 3 inputs
    @PostMapping("/api/compras-custom")
    public Map<String, Object> getComprasCustom(@RequestBody Map<String, Object> params) {
        int draw = (int) params.get("draw");
        int start = (int) params.get("start");
        int length = (int) params.get("length");
        String apellido = (String) params.get("apellido");
        String ciudad = (String) params.get("ciudad");
        String monto = (String) params.get("monto");

        int page = start / length;
        Page<CompraProducto> comprasPage = compraProductoService.getAllComprasCustom(apellido, ciudad, monto, page, length);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", comprasPage.getTotalElements());
        response.put("recordsFiltered", comprasPage.getTotalElements());
        response.put("data", comprasPage.getContent());

        return response;
    }

    // Get funcion tabla
    @PostMapping("/api/compras-funcion-tabla")
    public Map<String, Object> getComprasFuncionTabla(@RequestBody Map<String, Object> params) {
        int draw = (int) params.get("draw");
        int start = (int) params.get("start");
        int length = (int) params.get("length");
        String apellido = (String) params.get("apellido");
        String ciudad = (String) params.get("ciudad");
        String monto = (String) params.get("monto");

        int page = start / length;
        Page<Map<String, Object>> comprasPage = compraProductoService.getAllComprasFuncionTabla(apellido, ciudad, monto, page, length);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", comprasPage.getTotalElements());
        response.put("recordsFiltered", comprasPage.getTotalElements());
        response.put("data", comprasPage.getContent());

        return response;
    }

    // Export excel busqueda desde el cliente
    @GetMapping("/api/compras/export")
    public void exportToExcel(HttpServletResponse response, @RequestParam String search) throws IOException {

        List<CompraProducto> comprasList = compraProductoService.getAllCompras(search);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=compras.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Compras");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Cliente ID");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Ciudad");
        headerRow.createCell(4).setCellValue("Monto");
        headerRow.createCell(5).setCellValue("Fecha");

        int rowNum = 1;
        for (CompraProducto compra : comprasList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(compra.getCompra().getCliente().getId());
            row.createCell(1).setCellValue(compra.getCompra().getCliente().getNombre());
            row.createCell(2).setCellValue(compra.getCompra().getCliente().getEmail());
            row.createCell(3).setCellValue(compra.getCompra().getCliente().getDirecciones().get(0).getCiudad());
            row.createCell(4).setCellValue(compra.getCompra().getMonto().doubleValue());
            row.createCell(5).setCellValue(compra.getCompra().getFecha().toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Export excel funcion tabla
    @GetMapping("/api/compras-funcion-tabla/export")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam(required = false) String apellido,
                              @RequestParam(required = false) String ciudad,
                              @RequestParam(required = false) String monto) throws IOException {
        List<Map<String, Object>> comprasList = compraProductoService.getAllComprasFuncionTablaSinPaginado(apellido, ciudad, monto);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=compras.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Compras");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Cliente ID");
        headerRow.createCell(1).setCellValue("Apellido");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Ciudad");
        headerRow.createCell(4).setCellValue("Monto");
        headerRow.createCell(5).setCellValue("Fecha");

        int rowNum = 1;
        for (Map<String, Object> compra : comprasList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(compra.get("cliente_id").toString());
            row.createCell(1).setCellValue(compra.get("apellido").toString());
            row.createCell(2).setCellValue(compra.get("email").toString());
            row.createCell(3).setCellValue(compra.get("ciudad").toString());
            row.createCell(4).setCellValue(compra.get("monto").toString());
            row.createCell(5).setCellValue(compra.get("fecha").toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Procedure update email
    @PutMapping("/api/clientes/{id}/email")
    public Map<String, Object> actualizarEmail(@PathVariable int id, @RequestParam String nuevoEmail) {
        return clienteService.actualizarEmailCliente(id, nuevoEmail);
    }

    @GetMapping("/api/get-procedure")
    public Map<String, Object> getFilteredCompras(
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String monto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Map<String, Object>> comprasPage = compraProductoService.getFilteredCompras(apellido, ciudad, monto, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("recordsTotal", comprasPage.getTotalElements());
        response.put("recordsFiltered", comprasPage.getTotalElements());
        response.put("data", comprasPage.getContent());

        return response;
    }

    @PostMapping("/api/insertar")
    public Map<String, Object> insertarDatos(
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String ciudad,
            @RequestParam BigDecimal monto,
            @RequestParam String fecha) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        java.util.Date utilDate = formatter.parse(fecha);
        Date sqlDate = new Date(utilDate.getTime()); // Convertir java.util.Date a java.sql.Date

        return compraProductoService.insertarDatos(apellido, email, ciudad, monto, sqlDate);
    }

}
