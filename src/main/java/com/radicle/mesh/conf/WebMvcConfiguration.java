package com.radicle.mesh.conf;

import com.radicle.mesh.conf.token.GaiaInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	@Autowired
	JWTHandlerInterceptor jwtInjectedInterceptor;
    @Autowired GaiaInterceptor gaiaInterceptor;

	@Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(true);
    }
    
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInjectedInterceptor).addPathPatterns("/**");
        registry.addInterceptor(gaiaInterceptor).addPathPatterns("/**");
	}
}
