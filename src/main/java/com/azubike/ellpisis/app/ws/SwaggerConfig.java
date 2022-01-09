package com.azubike.ellpisis.app.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	/*
	 * String title, String description, String version, String termsOfServiceUrl,
	 * String contactName, String license, String licenseUrl
	 * http://host:port/context_path/swagger-ui
	 */
	Contact contact = new Contact("Enu Azubike", "https://github.com/azubikeenu", "enuazubike88@gmail.com");
	List<VendorExtension> vendorExtensions = new ArrayList<>();
	ApiInfo apiInfo = new ApiInfo("Photo App Restful webservice Api documentation",
			"These pages shows the documentation of the RestFul Api of the photo app webservice", "1.0.0",
			"www.example.com", contact, "Apache 2.0", "www.apache.org/licenses/LICENSE 2.0", vendorExtensions);

	@Bean
	public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2).protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPS")))
				.apiInfo(apiInfo).select().apis(RequestHandlerSelectors.basePackage("com.azubike.ellpisis.app.ws"))
				.paths(PathSelectors.any()).build();
		return docket;
	}

}
