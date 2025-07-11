package com.linktic.prueba.inventario.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linktic.prueba.inventario.dto.ProductoResponse;
import com.linktic.prueba.inventario.exception.ConflictException;
import com.linktic.prueba.inventario.model.Producto;
import com.linktic.prueba.inventario.repository.ProductoRepository;
import com.linktic.prueba.inventario.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService{
	
	@Autowired
	private ProductoRepository repository;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public ProductoResponse consultar(String codigoBarras, String nombre) {
		Producto producto = repository.findByCodigoBarrasOrNombreContaining(codigoBarras, nombre).orElseThrow(() -> new IllegalArgumentException("El producto no existe"));
		producto.setStock(producto.getStock()-producto.getCompraTemporal());
		return mapper.map(producto, ProductoResponse.class);
	}
	
	public Producto consultarInterno(String codigoBarras, int cantidad) {
		if(cantidad <= 0) throw new IllegalArgumentException("No se puede agregar cantidades iguales o inferiores a 0");
		Producto producto = repository.findByCodigoBarras(codigoBarras).orElseThrow(() -> new IllegalArgumentException("El producto no existe"));
		if ((producto.getStock() - producto.getCompraTemporal()) < cantidad) throw new ConflictException("No hay suficientes unidades del producto");
		return producto;
	}
	
	public void SeparaInventario(Producto producto, boolean cancela, int cantidad) {
		if(cancela) producto.setCompraTemporal(producto.getCompraTemporal() - cantidad);
		else producto.setCompraTemporal(producto.getCompraTemporal() + cantidad);
		repository.save(producto);
	}
	
	public void ModificarInventario(Producto producto, int cantidad) {
		producto.setStock(producto.getStock() - cantidad);
		producto.setCompraTemporal(producto.getCompraTemporal() - cantidad);
		repository.save(producto);
	}
}
