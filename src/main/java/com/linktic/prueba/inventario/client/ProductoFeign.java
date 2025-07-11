package com.linktic.prueba.inventario.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.linktic.prueba.inventario.dto.ProductoResponse;
import com.linktic.prueba.inventario.model.Producto;

@FeignClient(name="producto-service", path = "/api/productos", configuration = com.linktic.prueba.inventario.config.FeignConfig.class)
public interface ProductoFeign {
	
	@GetMapping("/buscar")
	ProductoResponse buscar(@RequestParam String codigoBarras, @RequestParam String nombre);
	
	@GetMapping("/verificar")
	Producto verificar(@RequestParam String codigoBarras, @RequestParam int cantidad);
	
	@PutMapping("/{id}/separar")
	ResponseEntity<Void> separaInventario(@PathVariable("id") UUID productoId, @RequestParam boolean cancela, @RequestParam int cantidad);
	
	@PutMapping("/{id}/modificar")
	ResponseEntity<Void> modificaInventario(@PathVariable("id") UUID productoId, @RequestParam int cantidad);
}
