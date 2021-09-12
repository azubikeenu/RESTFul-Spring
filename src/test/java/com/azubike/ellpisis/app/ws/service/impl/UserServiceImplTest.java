package com.azubike.ellpisis.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;

class UserServiceImplTest {
	// this is added because the class contains dependencies
	@InjectMocks
	UserServiceImpl userServiceImpl;
	@Mock
	UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetUsers() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setUserId("xxssyyusuuwiei");
		userEntity.setFirstName("Richard enu");
		userEntity.setEncryptedPassword("xxxxwuuurhhrhhuuxbbgdg_eyrnnr");
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = userServiceImpl.getUser("enuazubike@gmail.com");
		assertNotNull(userDto);
		assertEquals("Richard enu", userDto.getFirstName());
	}

}
