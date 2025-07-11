package com.linktic.prueba.inventario.service;

import com.linktic.prueba.inventario.dto.ProductoResponse;
import com.linktic.prueba.inventario.model.Producto;

public interface ProductoService {

	ProductoResponse consultar(String codigoBarras, String nombre);
	Producto consultarInterno(String codigoBarras, int cantidad);
	void SeparaInventario(Producto producto, boolean cancela, int cantidad);
	void ModificarInventario(Producto producto, int cantidad);
}
