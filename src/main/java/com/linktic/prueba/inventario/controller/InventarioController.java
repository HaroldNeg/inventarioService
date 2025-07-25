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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Operaciones compra de con productos")
public class InventarioController {

	@Autowired
	private CompraService service;

	@GetMapping("/{id}")
	@Operation(summary = "buscar compra", description = "Retorna una compra por su ID")
	@ApiResponse(responseCode = "200", description = "Compra encontrada")
	@ApiResponse(responseCode = "404", description = "Compra no encontrada")
    public JsonApiResponse<CompraResponse> buscarCompra(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(service.buscar(id)), null, null);
    }
	
	@PostMapping
	@Operation(summary = "crear un nueva compra")
	@ApiResponse(responseCode = "200", description = "Compra creada")
    public JsonApiResponse<CompraResponse> crearCompra() {
        return new JsonApiResponse<>(List.of(service.crear()), null, null);
    }

    @PatchMapping("/{id}/agregar-producto")
    @Operation(summary = "agregar producto", description = "Agrega/Modifica un producto a la compra")
	@ApiResponse(responseCode = "200", description = "El Agregó/Modificó un producto de la compra")
	@ApiResponse(responseCode = "404", description = "Compra no encontrada o Producto no Encontrado")
    @ApiResponse(responseCode = "409", description = "El producto no tiene suficientes unidades")
    public JsonApiResponse<CompraResponse> agregarProducto(@PathVariable UUID id, @RequestBody CompraRequest request) {
        return new JsonApiResponse<>(List.of(service.agregarProducto(id, request)), null, null);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "cancelar compra", description = "Cancela la compra y retira los productos separados")
	@ApiResponse(responseCode = "200", description = "La compra cambió de estado a Cancelada")
    @ApiResponse(responseCode = "404", description = "Compra no encontrada o Producto no Encontrado")
    public JsonApiResponse<CompraResponse> cancelar(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(service.cancelar(id)), null, null);
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "finalizar compra", description = "Finaliza la compra y retira los productos separados y afecta la cantidad de productos en stock")
	@ApiResponse(responseCode = "200", description = "La compra cambió de estado a Finalizada")
    @ApiResponse(responseCode = "404", description = "Compra no encontrada o Producto no Encontrado")
    public JsonApiResponse<CompraResponse> finalizar(@PathVariable UUID id) {
        return new JsonApiResponse<>(List.of(service.finalizar(id)), null, null);
    }
    
    @GetMapping
    @Operation(summary = "Listar compras", description = "Retorna una lista paginada de compras con filtros opcionales")
    public JsonApiResponse<CompraResponse> listar(
    		@Parameter(description = "Filtro por nombre") @RequestParam(required = false) String nombre, 
    		@Parameter(description = "Filtro por codigo de barras") @RequestParam(required = false) String codigoBarras
    		, Pageable pageable, HttpServletRequest request
    ) {
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
