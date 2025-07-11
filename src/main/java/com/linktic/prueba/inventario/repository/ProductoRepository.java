package com.linktic.prueba.inventario.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linktic.prueba.inventario.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID>{
	Optional<Producto> findByCodigoBarras(String codigoBarras);
	Optional<Producto> findByCodigoBarrasOrNombreContaining(String codigoBarras, String nombre);
}
