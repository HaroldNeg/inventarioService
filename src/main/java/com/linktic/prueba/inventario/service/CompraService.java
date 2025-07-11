package com.linktic.prueba.inventario.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.linktic.prueba.inventario.dto.CompraRequest;
import com.linktic.prueba.inventario.dto.CompraResponse;

public interface CompraService {
	
	CompraResponse crear();
    CompraResponse agregarProducto(UUID compraId, CompraRequest request);
    CompraResponse buscar(UUID compraId);
    CompraResponse cancelar(UUID compraId);
    CompraResponse finalizar(UUID compraId);
    Page<CompraResponse> listar(String nombre, String codigoBarras, Pageable pageable);
}
