package com.linktic.prueba.inventario.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "compra_x_producto")
public class CompraXProducto {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

	@ManyToOne(optional = false)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull
	@Min(0)
    private Integer cantidad;
    
    @Column(nullable = false)
    private Double precioUnitario;
}
