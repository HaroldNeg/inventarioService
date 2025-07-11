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
import com.linktic.prueba.inventario.service.CompraService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

	@Autowired
	private CompraService service;

	@GetMapping("/{id}")
    public JsonApiResponse<CompraResponse> buscarCompra(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(service.buscar(id)), null, null);
    }
	
	@PostMapping
    public JsonApiResponse<CompraResponse> crearCompra() {
        return new JsonApiResponse<>(List.of(service.crear()), null, null);
    }

    @PatchMapping("/{id}/agregar-producto")
    public JsonApiResponse<CompraResponse> agregarProducto(@PathVariable UUID id, @RequestBody CompraRequest request) {
        return new JsonApiResponse<>(List.of(service.agregarProducto(id, request)), null, null);
    }

    @PatchMapping("/{id}/cancelar")
    public JsonApiResponse<CompraResponse> cancelar(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(service.cancelar(id)), null, null);
    }

    @PatchMapping("/{id}/finalizar")
    public JsonApiResponse<CompraResponse> finalizar(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(service.finalizar(id)), null, null);
    }
    
    @GetMapping
    public JsonApiResponse<CompraResponse> listar(@RequestParam(required = false) String nombre, @RequestParam(required = false) String codigoBarras, Pageable pageable, HttpServletRequest request) {
        var page = service.listar(nombre, codigoBarras, pageable);
        
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
