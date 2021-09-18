package com.azubike.ellpisis.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.azubike.ellpisis.app.ws.exceptions.UserServiceException;
import com.azubike.ellpisis.app.ws.io.entity.AddressEntity;
import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.shared.utils.EmailService;
import com.azubike.ellpisis.app.ws.shared.utils.Utils;

class UserServiceImplTest {
	// this is added because the class contains dependencies
	@InjectMocks
	UserServiceImpl userServiceImpl;
	@Mock
	UserRepository userRepository;
	@Mock
	Utils utils;
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Mock
	EmailService emailService;

	String userId = "xxssyyusuuwiei";
	String encryptedPassword = "xxxxwuuurhhrhhuuxbbgdg_eyrnnr";
	String emailVerificationToken = "xxxeuueuuriixnnxmeuue";
	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		// Create an instance of UserEntity Stub for each test case
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setUserId(userId);
		userEntity.setFirstName("Richard");
		userEntity.setLastName("Enu");
		userEntity.setEmail("enazubike88@gmail.com");
		userEntity.setAddresses(getAddressEntities());
	}

	@Test
	void testGetUsers() {
		userEntity.setEncryptedPassword(encryptedPassword);
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = userServiceImpl.getUser(userEntity.getEmail());
		assertNotNull(userDto);
		assertEquals("Richard", userDto.getFirstName());
		assertEquals("Enu", userDto.getLastName());
	}

	@Test
	void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.getUser(userEntity.getEmail());
		});

	}

	@Test
	final void createUser() throws Exception {
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateRadomAddressId(anyInt())).thenReturn("xsueuuiriieiieioqoooeooroux");
		when(utils.generateRandomUserId(anyInt())).thenReturn(userId);
		when(utils.generateEmailVerificationToken(anyString())).thenReturn(emailVerificationToken);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(emailService).verifyEmail(any(UserDto.class));

		// stub the argument
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressDto());
		userDto.setFirstName("Richard");
		userDto.setLastName("Enu");
		userDto.setEmail("enuazubike88@gmail.com");
		userDto.setPassword("12345");
		UserDto returnedUser = userServiceImpl.createUser(userDto);

		assertNotNull(returnedUser);
		assertEquals(userEntity.getFirstName(), returnedUser.getFirstName());
		assertEquals(userEntity.getLastName(), returnedUser.getLastName());
		assertNotNull(returnedUser.getUserId());
		assertEquals(returnedUser.getAddresses().size(), userEntity.getAddresses().size());
		// verify methods
		verify(utils, times(returnedUser.getAddresses().size())).generateRadomAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("12345");
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	@Test
	final void createUser_UserServiceException() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressDto());
		userDto.setFirstName("Richard");
		userDto.setLastName("Enu");
		userDto.setEmail("enuazubike88@gmail.com");
		userDto.setPassword("12345");
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.createUser(userDto);
		});
	}

	private List<AddressDto> getAddressDto() {
		AddressDto addressOne = new AddressDto();
		addressOne.setType("shipping");
		addressOne.setCity("Lagos");
		addressOne.setCountry("Nigeria");
		addressOne.setStreetName("Festac");
		AddressDto addressTwo = new AddressDto();
		addressTwo.setType("billing");
		addressTwo.setCity("Lagos");
		addressTwo.setCountry("Nigeria");
		addressTwo.setStreetName("Festac");
		List<AddressDto> addresses = Arrays.asList(addressOne, addressTwo);
		return addresses;

	}

	private List<AddressEntity> getAddressEntities() {
		List<AddressDto> addresses = getAddressDto();
		Type listType = new TypeToken<List<AddressEntity>>() {
		}.getType();
		return new ModelMapper().map(addresses, listType);
	}

}
