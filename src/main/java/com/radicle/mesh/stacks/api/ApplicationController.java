package com.radicle.mesh.stacks.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class ApplicationController {

	@GetMapping(value = "/api/server/time")
	public Long servertime() {
		return System.currentTimeMillis();
	}

	@GetMapping(value = "/error")
	public String get(HttpServletRequest request) {
		return "Please use the assets url provided";
	}
}
