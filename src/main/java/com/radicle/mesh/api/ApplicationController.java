package com.radicle.mesh.api;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class ApplicationController {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping(value = "/")
	public RedirectView bounceBack(HttpServletRequest request) {
		return new RedirectView("http://c1.assets.local/login");
	}
	
    @GetMapping(value = "/bounce")
	public String getHome(HttpServletRequest request) {
		return "API home page - please read the docs";
	}
	
	@GetMapping(value = "/error")
	public String get(HttpServletRequest request) {
		return "Please use the assets url provided";
	}
}
