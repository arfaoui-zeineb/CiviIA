package com.civia.resourcelogisticsservice.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiError> handleConflict(ConflictException ex, WebRequest request) {
		return build(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex, WebRequest request) {
		return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));
		return build(HttpStatus.BAD_REQUEST, message, request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	private ResponseEntity<ApiError> build(HttpStatus status, String message, WebRequest request) {
		String path = request.getDescription(false).replace("uri=", "");
		ApiError error = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path);
		return ResponseEntity.status(status).body(error);
	}
}
