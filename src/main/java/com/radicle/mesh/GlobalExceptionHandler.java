package com.radicle.mesh;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceAccessException.class)
	public String handleResourceAccessException(HttpServletRequest request, HttpServletResponse response, Exception e) {
	    response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
		return e.getMessage();
	}

	@ExceptionHandler
	public String handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		// return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
		return e.getMessage();
	}

}
