package com.linktic.prueba.inventario.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.linktic.prueba.inventario.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, UUID>{
	
	@Query("""
	        SELECT DISTINCT c FROM Compra c
	        JOIN c.productos cxp
	        JOIN cxp.producto p
	        WHERE (:nombre IS NULL OR UPPER(p.nombre) LIKE CONCAT('%', UPPER(CAST(:nombre AS string)), '%'))
	        AND (:codigoBarras IS NULL OR p.codigoBarras = :codigoBarras)
	    """)
	Page<Compra> findAllByProductoNombreOCodigo(@Param("nombre") String nombre, @Param("codigoBarras") String codigoBarras, Pageable pageable);
}
