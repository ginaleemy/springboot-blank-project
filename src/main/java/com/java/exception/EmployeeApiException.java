package com.java.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeApiException extends RuntimeException{
	private HttpStatus status;
	private String message;
}
