package com.radicle.mesh.api;

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

	@GetMapping(value = "/")
	public RedirectView bounceBack(HttpServletRequest request) {
		return new RedirectView("http://c1.assets.local/login");
	}
	
    @GetMapping(value = "/lsat")
	public String getHome(HttpServletRequest request) {
		return "API home page - please read the docs";
	}
	
	@GetMapping(value = "/error")
	public String get(HttpServletRequest request) {
		return "Please use the assets url provided";
	}
}
