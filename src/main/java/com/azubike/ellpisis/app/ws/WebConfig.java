package com.azubike.ellpisis.app.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// to configure for all controllers and methods
		registry.addMapping("/**").allowedMethods("*").allowedOrigins("*");
		// registry.addMapping("/**").allowedMethods("GET,POST,PUT,DELETE");
	}

}
