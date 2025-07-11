package com.linktic.prueba.inventario.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.linktic.prueba.inventario.client.ProductoFeign;
import com.linktic.prueba.inventario.dto.CompraRequest;
import com.linktic.prueba.inventario.dto.CompraResponse;
import com.linktic.prueba.inventario.mapper.CompraMapper;
import com.linktic.prueba.inventario.model.Compra;
import com.linktic.prueba.inventario.model.CompraXProducto;
import com.linktic.prueba.inventario.model.EstadoCompra;
import com.linktic.prueba.inventario.model.Producto;
import com.linktic.prueba.inventario.repository.CompraRepository;
import com.linktic.prueba.inventario.service.CompraService;

import jakarta.transaction.Transactional;

@Service
public class CompraServiceImpl implements CompraService{
	
	
	@Autowired
	private CompraRepository repository;
	
	@Autowired
	private ProductoFeign productoClient;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private CompraMapper compraMapper;

	@Override
	public CompraResponse crear() {
		Compra compra = new Compra();
		compra = repository.save(compra);
		return mapper.map(compra, CompraResponse.class);
	}

	@Override
	@Transactional
    public CompraResponse agregarProducto(UUID compraId, CompraRequest request) {
		Compra compra = validarCompra(compraId, "agregar productos a una compra pendiente");

        Producto producto = productoClient.verificar(request.getCodigoBarras(), request.getCantidad());        
        productoClient.separaInventario(producto.getId(), false, request.getCantidad());
        
        boolean existe = false;
        for(CompraXProducto compraProducto : compra.getProductos()) {
        	if(compraProducto.getProducto().getId().equals(producto.getId())) {
        		compraProducto.setCantidad(compraProducto.getCantidad() + request.getCantidad());
        		existe = true;
        		break;
        	}
        }
        
        if(!existe) {
	        CompraXProducto agregarProducto = new CompraXProducto();
	        agregarProducto.setCompra(compra);
	        agregarProducto.setProducto(producto);
	        agregarProducto.setCantidad(request.getCantidad());
	        agregarProducto.setPrecioUnitario(producto.getPrecio());
	        compra.getProductos().add(agregarProducto);
        }
        
        double subtotal = request.getCantidad() * producto.getPrecio();
        compra.setTotal(compra.getTotal() + subtotal);

        return compraMapper.toDto(compra);
    }

	@Override
	@Transactional
	public CompraResponse buscar(UUID compraId){
		 Compra compra = validarCompra(compraId, "retomar una compra si está en estado pendiente");
		 return compraMapper.toDto(compra);
	}
	
	 @Override
	 @Transactional
	 public CompraResponse cancelar(UUID compraId) {
		 Compra compra = validarCompra(compraId, "cancelar una compra si está en estado pendiente");
		 compra.setEstado(EstadoCompra.CANCELADA);
		 compra.setFinalizacion(LocalDateTime.now());
		 
		 for (CompraXProducto cxp : compra.getProductos()) {
			 productoClient.separaInventario(cxp.getProducto().getId(), true, cxp.getCantidad());
		 }
		 return compraMapper.toDto(repository.save(compra));
	 }

	@Override
	@Transactional
	public CompraResponse finalizar(UUID compraId) {
		Compra compra = validarCompra(compraId, "finalizar una compra si está en estado pendiente");
		 compra.setEstado(EstadoCompra.FINALIZADA);
		 compra.setFinalizacion(LocalDateTime.now());
		 
		 for (CompraXProducto cxp : compra.getProductos()) {
			 productoClient.modificaInventario(cxp.getProducto().getId(), cxp.getCantidad());
		 }
		 return compraMapper.toDto(repository.save(compra));
	 }

	private Compra validarCompra(UUID compraId, String accion) {
		Compra compra = repository.findById(compraId).orElseThrow(() -> new IllegalArgumentException("La compra no existe"));
        if (compra.getEstado() != EstadoCompra.PENDIENTE) throw new IllegalStateException("Solo se puede "+accion);
        return compra;
	}

	@Override
	@Transactional
	public Page<CompraResponse> listar(String nombre, String codigoBarras, Pageable pageable) {
		var page = repository.findAllByProductoNombreOCodigo(nombre != null && !nombre.isBlank() ? nombre : null, codigoBarras != null && !codigoBarras.isBlank() ? codigoBarras : null, pageable);
		return page.map(compraMapper::toDto);
	}
}
