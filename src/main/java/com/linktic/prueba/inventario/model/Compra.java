package com.linktic.prueba.inventario.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "compra")
public class Compra {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
	@Min(0)
    private Double total = 0.0;
    
    @CreationTimestamp
    private LocalDateTime creacion;
    
    private LocalDateTime finalizacion;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private EstadoCompra estado = EstadoCompra.PENDIENTE;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompraXProducto> productos = new ArrayList<>();
}
