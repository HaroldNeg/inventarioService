package com.linktic.prueba.inventario.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linktic.prueba.inventario.dto.CompraRequest;
import com.linktic.prueba.inventario.dto.CompraResponse;
import com.linktic.prueba.inventario.dto.JsonApiResponse;
import com.linktic.prueba.inventario.dto.ProductoResponse;
import com.linktic.prueba.inventario.service.CompraService;
import com.linktic.prueba.inventario.service.ProductoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private CompraService compraService;
	
	@GetMapping("/buscar")
	public JsonApiResponse<ProductoResponse> buscar(@RequestParam String codigoBarras, @RequestParam String nombre, @RequestParam(defaultValue = "1") int cantidad) {
	    ProductoResponse response = productoService.consultar(codigoBarras, nombre);
        return new JsonApiResponse<>(List.of(response), null, null);
	}
	
	@GetMapping("/{id}")
    public JsonApiResponse<CompraResponse> buscarCompra(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(compraService.buscar(id)), null, null);
    }
	
	@PostMapping
    public JsonApiResponse<CompraResponse> crearCompra() {
        return new JsonApiResponse<>(List.of(compraService.crear()), null, null);
    }

    @PatchMapping("/{id}/agregar-producto")
    public JsonApiResponse<CompraResponse> agregarProducto(@PathVariable UUID id, @RequestBody CompraRequest request) {
        return new JsonApiResponse<>(List.of(compraService.agregarProducto(id, request)), null, null);
    }

    @PatchMapping("/{id}/cancelar")
    public JsonApiResponse<CompraResponse> cancelar(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(compraService.cancelar(id)), null, null);
    }

    @PatchMapping("/{id}/finalizar")
    public JsonApiResponse<CompraResponse> finalizar(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(compraService.finalizar(id)), null, null);
    }
    
    @GetMapping
    public JsonApiResponse<CompraResponse> listar(@RequestParam(required = false) String nombre, @RequestParam(required = false) String codigoBarras, Pageable pageable, HttpServletRequest request) {
        var page = compraService.listar(nombre, codigoBarras, pageable);
        
        String params = "";
	    if (nombre != null) params += "&nombre=" + nombre;
	    if (codigoBarras != null) params += "&codigoBarras=" + codigoBarras;

        String base = request.getRequestURL().toString();
        String self = base + "?page=" + page.getNumber() + "&size=" + page.getSize() + params;
        String next = page.hasNext() ? base + "?page=" + (page.getNumber() + 1) + "&size=" + page.getSize() + params : null;
        String prev = page.hasPrevious() ? base + "?page=" + (page.getNumber() - 1) + "&size=" + page.getSize() + params : null;

        var meta = new JsonApiResponse.Meta(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
        var links = new JsonApiResponse.Links(self, next, prev);

        return new JsonApiResponse<>(page.getContent(), meta, links);
    }
}
