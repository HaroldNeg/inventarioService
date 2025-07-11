package com.linktic.prueba.inventario.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.linktic.prueba.inventario.model.EstadoCompra;

import lombok.Data;

@Data
public class CompraResponse {
	private UUID id;
	private LocalDateTime creacion;
    private LocalDateTime finalizacion;
    private EstadoCompra estado;
    private Double total;
    private List<DetalleCompra> productos;

    @Data
    public static class DetalleCompra {
    	private String codigoBarras;
        private String nombre;
        private Integer cantidad;
        private Double precioUnitario;
    }
}
