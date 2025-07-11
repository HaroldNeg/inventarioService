package com.linktic.prueba.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.prueba.inventario.dto.CompraRequest;
import com.linktic.prueba.inventario.dto.CompraResponse;
import com.linktic.prueba.inventario.exception.ConflictoInventarioException;
import com.linktic.prueba.inventario.exception.RecursoNoEncontradoException;
import com.linktic.prueba.inventario.service.CompraService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompraService compraService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buscarCompra_devuelve_200() throws Exception {
        UUID id = UUID.randomUUID();
        CompraResponse response = new CompraResponse();
        response.setId(id);

        Mockito.when(compraService.buscar(id)).thenReturn(response);

        mockMvc.perform(get("/api/inventario/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(id.toString()));
    }

    @Test
    void buscarCompra_noEncontrada_devuelve_404() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(compraService.buscar(id)).thenThrow(new RecursoNoEncontradoException("No encontrada"));

        mockMvc.perform(get("/api/inventario/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearCompra_devuelve_200() throws Exception {
        CompraResponse response = new CompraResponse();
        response.setId(UUID.randomUUID());

        Mockito.when(compraService.crear()).thenReturn(response);

        mockMvc.perform(post("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void agregarProducto_devuelve_200() throws Exception {
        UUID id = UUID.randomUUID();
        CompraRequest request = new CompraRequest("ABC123", 2);
        CompraResponse response = new CompraResponse();
        response.setId(id);

        Mockito.when(compraService.agregarProducto(eq(id), any())).thenReturn(response);

        mockMvc.perform(patch("/api/inventario/{id}/agregar-producto", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(id.toString()));
    }

    @Test
    void agregarProducto_conflicto_devuelve_409() throws Exception {
        UUID id = UUID.randomUUID();
        CompraRequest request = new CompraRequest("ABC123", 99);

        Mockito.when(compraService.agregarProducto(eq(id), any()))
                .thenThrow(new ConflictoInventarioException("Sin stock"));

        mockMvc.perform(patch("/api/inventario/{id}/agregar-producto", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void agregarProducto_noEncontrado_devuelve_404() throws Exception {
        UUID id = UUID.randomUUID();
        CompraRequest request = new CompraRequest("ABC123", 1);

        Mockito.when(compraService.agregarProducto(eq(id), any()))
                .thenThrow(new RecursoNoEncontradoException("Compra o producto no existe"));

        mockMvc.perform(patch("/api/inventario/{id}/agregar-producto", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarCompra_devuelve_200() throws Exception {
        UUID id = UUID.randomUUID();
        CompraResponse response = new CompraResponse();
        response.setId(id);

        Mockito.when(compraService.cancelar(id)).thenReturn(response);

        mockMvc.perform(patch("/api/inventario/{id}/cancelar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(id.toString()));
    }

    @Test
    void cancelarCompra_noEncontrada_devuelve_404() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(compraService.cancelar(id)).thenThrow(new RecursoNoEncontradoException("Compra no existe"));

        mockMvc.perform(patch("/api/inventario/{id}/cancelar", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void finalizarCompra_devuelve_200() throws Exception {
        UUID id = UUID.randomUUID();
        CompraResponse response = new CompraResponse();
        response.setId(id);

        Mockito.when(compraService.finalizar(id)).thenReturn(response);

        mockMvc.perform(patch("/api/inventario/{id}/finalizar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(id.toString()));
    }

    @Test
    void finalizarCompra_noEncontrada_devuelve_404() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(compraService.finalizar(id)).thenThrow(new RecursoNoEncontradoException("Compra no existe"));

        mockMvc.perform(patch("/api/inventario/{id}/finalizar", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarCompras_devuelve_200() throws Exception {
        CompraResponse compra = new CompraResponse();
        compra.setId(UUID.randomUUID());

        var page = new PageImpl<>(Collections.singletonList(compra), PageRequest.of(0, 5), 1);

        Mockito.when(compraService.listar(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/inventario?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(compra.getId().toString()))
                .andExpect(jsonPath("$.meta.size").value(5));
    }
}
