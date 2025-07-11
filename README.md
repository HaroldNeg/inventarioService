inventario-service
==================

Este repositorio contiene el microservicio `inventario-service` del sistema CompraApp.
Este servicio se encarga de gestionar el stock e inventario de productos.

Descripción del Proyecto
-------------------------
`inventario-service` permite consultar, modificar y validar la disponibilidad de productos en inventario. 
Se comunica con `productos-service` a través de un cliente Feign para verificar y obtener información de productos relacionados.

Tecnologías utilizadas
-----------------------
- Java 21
- Spring Boot 3.5.3
- Spring Cloud 2025.0.0
- Spring Data JPA
- Spring Web
- Eureka Client
- Config Client
- Feign Client

Características
---------------
- Consultar inventario disponible de un producto.
- Actualizar stock después de una compra.
- Comunicación con productos-service vía Feign.
- Registro en Eureka y configuración vía config-server.

Ejecución del Proyecto
-----------------------
1. Clonar el repositorio:
   git clone https://github.com/HaroldNeg/inventarioService.git

2. Entrar al directorio del proyecto:
   cd inventarioService

3. Compilar el proyecto con Maven:
   mvn clean install

4. Ejecutar el microservicio:
   mvn spring-boot:run

Asegúrate de tener corriendo previamente:
- config-server
- eureka-server

Configuración
-------------
El archivo `application.yml` incluye:
- Puerto del servicio
- Nombre de instancia para Eureka
- Configuración del datasource (Base de Datos)
- Feign Client para productos-service

Ejemplo de Feign Client:
-------------------------
@FeignClient(name = "productos-service")
public interface ProductoClient {
    @GetMapping("/api/productos/{id}")
    Producto obtenerProductoPorId(@PathVariable Long id);
}

Base de Datos
-------------
El servicio se conecta a una base de datos relacional (MySQL, PostgreSQL, etc.).
Configura las credenciales en `application.yml` o mediante variables de entorno.

Observabilidad
--------------
Recomendado integrar:
- Spring Boot Actuator
- Sleuth + Zipkin para trazabilidad
- Prometheus para métricas

Licencia
--------
Este proyecto está bajo una licencia de código abierto.

Contacto
--------
Para soporte técnico o sugerencias, abre un issue en el repositorio.
