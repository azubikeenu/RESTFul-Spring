package com.azubike.ellpisis.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.azubike.ellpisis.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);

	UserDto getUser(String email);

	UserDto getUserById(String id);

	UserDto updateUser(UserDto userDto, String id);

	void deleteUser(String id);

	List<UserDto> getUsers(int page, int limit);

	boolean verifyEmailToken(String token);

	boolean requestPasswordReset(String email);

}
