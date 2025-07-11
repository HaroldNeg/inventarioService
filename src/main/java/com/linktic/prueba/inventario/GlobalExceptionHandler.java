package com.linktic.prueba.inventario;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.linktic.prueba.inventario.dto.ErrorResponse;
import com.linktic.prueba.inventario.dto.JsonApiResponse;
import com.linktic.prueba.inventario.exception.ConflictException;
import com.linktic.prueba.inventario.exception.ConflictoInventarioException;
import com.linktic.prueba.inventario.exception.RecursoNoEncontradoException;
import com.linktic.prueba.inventario.exception.ResourceNotFoundException;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	//400 - Mala petición
	@ExceptionHandler({MissingServletRequestParameterException.class, IllegalArgumentException.class, IllegalStateException.class, ConstraintViolationException.class})
	public ResponseEntity<Map<String, List<ErrorResponse>>> handleBadRequest(Exception ex) {
		return buildError("400", "Bad Request", ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

    // 404 - No encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleNotFound(ResourceNotFoundException ex) {
    	return buildError("404", "Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
 // 404 - No encontrado
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleNoSuchElement(NoSuchElementException ex) {
    	ex.printStackTrace();
        return buildError("404", "Not Found", "El recurso solicitado no fue encontrado.", HttpStatus.NOT_FOUND);
    }

    // 405 - Método no permitido
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
    	return buildError("405", "Method Not Allowed", ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 409 - Conflicto (por ejemplo, registro duplicado)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleConflict(ConflictException ex) {
    	return buildError("409", "Conflict", ex.getMessage(), HttpStatus.CONFLICT);
    }

	// 422
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorResponse("422", "Validation Error",  err.getField() + ": " + err.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.unprocessableEntity().body(Map.of("errors", errors));
    }
	
	@ExceptionHandler(FeignException.class)
	public ResponseEntity<?> handleFeignErrors(FeignException ex) {
		String responseBody = ex.contentUTF8();

		// Si ya contiene "errors", reenviamos el JSON tal como lo recibimos
	    if (responseBody != null && responseBody.trim().startsWith("{") && responseBody.contains("\"errors\"")) {
	        return ResponseEntity
	                .status(ex.status())
	                .header("Content-Type", "application/json")
	                .body(responseBody);
	    }

	    // Si no viene con estructura JSON API, formateamos el error
	    ErrorResponse error = new ErrorResponse(
	            String.valueOf(ex.status()),
	            "Upstream Error",
	            responseBody != null ? responseBody : ex.getMessage()
	    );

	    return ResponseEntity
	            .status(ex.status())
	            .body(Map.of("errors", List.of(error)));
	}

	// 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<ErrorResponse>>> handleGeneralErrors(Exception ex) {
    	ex.printStackTrace();
        return buildError("500", "Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleFaviconRequest(NoResourceFoundException ex) throws NoResourceFoundException {
        if (ex.getResourcePath().equals("favicon.ico")) {
            return ResponseEntity.notFound().build();
        }
        throw ex;
    }
    
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<JsonApiResponse<Void>> handleNotFound(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new JsonApiResponse<>(null, null, null));
    }

    @ExceptionHandler(ConflictoInventarioException.class)
    public ResponseEntity<JsonApiResponse<Void>> handleConflict(ConflictoInventarioException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new JsonApiResponse<>(null, null, null));
    }
    
    private ResponseEntity<Map<String, List<ErrorResponse>>> buildError(String status, String title, String detail, HttpStatus httpStatus) {
        ErrorResponse error = new ErrorResponse(status, title, detail);
        return ResponseEntity.status(httpStatus).body(Map.of("errors", List.of(error)));
    }

}
