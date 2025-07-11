package com.linktic.prueba.inventario.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.linktic.prueba.inventario.dto.CompraResponse;
import com.linktic.prueba.inventario.dto.CompraResponse.DetalleCompra;
import com.linktic.prueba.inventario.model.Compra;
import com.linktic.prueba.inventario.model.CompraXProducto;

@Component
public class CompraMapper {

    public CompraResponse toDto(Compra compra) {
        CompraResponse dto = new CompraResponse();
        dto.setId(compra.getId());
        dto.setCreacion(compra.getCreacion());
        dto.setFinalizacion(compra.getFinalizacion());
        dto.setEstado(compra.getEstado());
        dto.setTotal(compra.getTotal());

        List<DetalleCompra> detalles = compra.getProductos().stream()
            .map(this::mapDetalle)
            .collect(Collectors.toList());

        dto.setProductos(detalles);
        return dto;
    }

    private DetalleCompra mapDetalle(CompraXProducto cxp) {
        DetalleCompra d = new DetalleCompra();
        if (cxp.getProducto() != null) {
            d.setCodigoBarras(cxp.getProducto().getCodigoBarras());
            d.setNombre(cxp.getProducto().getNombre());
        }
        d.setCantidad(cxp.getCantidad());
        d.setPrecioUnitario(cxp.getPrecioUnitario());
        return d;
    }
}
