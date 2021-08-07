package com.azubike.ellpisis.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.azubike.ellpisis.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);

	UserDto getUser(String email);

	UserDto getUserById(String id);
}
