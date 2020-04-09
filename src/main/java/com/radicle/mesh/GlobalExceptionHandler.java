package com.radicle.mesh;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Object> handleResourceAccessException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		return new ResponseEntity<Object>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

}
