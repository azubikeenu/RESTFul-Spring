package com.azubike.ellpisis.app.ws.ui.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azubike.ellpisis.app.ws.ui.model.request.LoginRequestModel;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class AuthenticationController {
	@ApiOperation("userLogin")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Response headers", responseHeaders = {
			@ResponseHeader(name = "authorization", description = "Bearer:JWT token here", response = String.class),
			@ResponseHeader(name = "userId", description = "Public userId here", response = String.class) }) })
	@PostMapping("/users/login")
	public void fakeLogin(@RequestBody LoginRequestModel loginRequestModel) {
		throw new IllegalStateException("This method is implemented by spring security and should not be called");
	}

}
