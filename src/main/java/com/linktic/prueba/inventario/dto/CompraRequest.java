package com.linktic.prueba.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraRequest {
	
	@NotNull
	private String codigoBarras;
	
	@NotNull
	@Min(0)
    private Integer cantidad;
}
