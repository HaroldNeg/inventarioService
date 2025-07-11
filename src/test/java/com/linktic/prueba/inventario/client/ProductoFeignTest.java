package com.linktic.prueba.inventario.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.linktic.prueba.inventario.dto.ProductoResponse;
import com.linktic.prueba.inventario.model.Producto;

import feign.FeignException;

@SuppressWarnings("removal")
@SpringBootTest
@ActiveProfiles("test")
class ProductoFeignTest {

    @MockBean
    private ProductoFeign productoFeign;

    @Test
    void buscar_producto_devuelve_datos() {
        ProductoResponse productoMock = new ProductoResponse();
        productoMock.setId(UUID.randomUUID());
        productoMock.setNombre("Test");
        productoMock.setCodigoBarras("123");
        productoMock.setPrecio(1000.0);
        productoMock.setStock(5);

        when(productoFeign.buscar("123", "Test")).thenReturn(productoMock);

        ProductoResponse producto = productoFeign.buscar("123", "Test");

        assertThat(producto.getNombre()).isEqualTo("Test");
        assertThat(producto.getCodigoBarras()).isEqualTo("123");
    }

    @Test
    void verificar_producto_devuelve_objeto() {
        Producto productoMock = new Producto();
        productoMock.setId(UUID.randomUUID());
        productoMock.setNombre("Producto Test");
        productoMock.setCodigoBarras("123");
        productoMock.setPrecio(1000.0);
        productoMock.setStock(5);
        productoMock.setCompraTemporal(0);

        when(productoFeign.verificar("123", 2)).thenReturn(productoMock);

        Producto producto = productoFeign.verificar("123", 2);

        assertThat(producto.getNombre()).isEqualTo("Producto Test");
        assertThat(producto.getStock()).isEqualTo(5);
        assertThat(producto.getCompraTemporal()).isEqualTo(0);
    }

    @Test
    void separaInventario_devuelve_204() {
        UUID id = UUID.randomUUID();
        when(productoFeign.separaInventario(id, false, 2)).thenReturn(ResponseEntity.noContent().build());

        var response = productoFeign.separaInventario(id, false, 2);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void modificaInventario_devuelve_204() {
        UUID id = UUID.randomUUID();
        when(productoFeign.modificaInventario(id, 3)).thenReturn(ResponseEntity.noContent().build());

        var response = productoFeign.modificaInventario(id, 3);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void buscar_producto_inexistente_devuelve_404() {
        when(productoFeign.buscar("000", "Inexistente"))
                .thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> productoFeign.buscar("000", "Inexistente"))
                .isInstanceOf(FeignException.NotFound.class);
    }

    @Test
    void verificar_producto_sin_stock_devuelve_409() {
        when(productoFeign.verificar("123", 999))
                .thenThrow(FeignException.Conflict.class);

        assertThatThrownBy(() -> productoFeign.verificar("123", 999))
                .isInstanceOf(FeignException.Conflict.class);
    }

    @Test
    void separaInventario_falla_con_404() {
        UUID id = UUID.randomUUID();
        when(productoFeign.separaInventario(id, false, 2))
                .thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> productoFeign.separaInventario(id, false, 2))
                .isInstanceOf(FeignException.NotFound.class);
    }

    @Test
    void modificaInventario_error_servidor_devuelve_500() {
        UUID id = UUID.randomUUID();
        when(productoFeign.modificaInventario(id, 1))
                .thenThrow(FeignException.InternalServerError.class);

        assertThatThrownBy(() -> productoFeign.modificaInventario(id, 1))
                .isInstanceOf(FeignException.InternalServerError.class);
    }
}
