package com.java.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
	// 1. Domain / Business Exception Handler
	@ExceptionHandler(EmployeeApiException.class)
	public ResponseEntity<ErrorDetails> handleEmployeeApiException(EmployeeApiException exp, WebRequest webRequest) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),  exp.getStatus().toString(),
				exp.getMessage(), webRequest.getDescription(false));

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	// 2. Single DTO Validation (@Valid EmployeeRequest)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> handleDTOValidation(MethodArgumentNotValidException ex, WebRequest webRequest) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining("; "));

		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
				errorMessage, webRequest.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	// 3. Collection / List Validation (@Valid List<EmployeeRequest>)
	/*
	 Example: {
		  "timeStamp": "2026-08-29T23:08:15",
		  "status": "400 BAD_REQUEST",
		  "message": "requests[0].gender: Gender is required; requests[1].firstName: First Name is required",
		  "details": "uri=/api/employees/batch"
		}
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException ex,
			WebRequest webRequest) {

		String errorMessage = ex.getConstraintViolations().stream().map(violation -> {
			String path = violation.getPropertyPath().toString();
			String shortPath = path.contains(".") ? path.substring(path.indexOf(".") + 1) : path;
			return shortPath + ": " + violation.getMessage();
		}).collect(Collectors.joining("; "));

		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
				errorMessage, webRequest.getDescription(false));

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	// 4. JSON / Enum Parsing Failures
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorDetails> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			WebRequest webRequest) {

		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
				"Invalid JSON input or enum value format. Please check field types.", webRequest.getDescription(false));

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

	}
	
	@ExceptionHandler(jakarta.validation.UnexpectedTypeException.class)
	public ResponseEntity<ErrorDetails> handleUnexpectedType(jakarta.validation.UnexpectedTypeException ex, WebRequest webRequest) {
	    ErrorDetails errorDetails = new ErrorDetails(
	            LocalDateTime.now(),
	            HttpStatus.BAD_REQUEST.toString(),
	            "Validation configuration error: Check field constraint annotations.",
	            webRequest.getDescription(false)
	    );
	    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
