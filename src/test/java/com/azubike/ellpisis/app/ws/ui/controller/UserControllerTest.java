package com.azubike.ellpisis.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.azubike.ellpisis.app.ws.service.UserService;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	@InjectMocks
	UserController userController;
	@Mock
	UserService userService;
	UserDto userDto;
	final String USER_ID = "xyueuueuuxuuriiiui";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		userDto = new UserDto();
		userDto.setId(1L);
		userDto.setUserId(USER_ID);
		userDto.setFirstName("Richard");
		userDto.setLastName("Enu");
		userDto.setEncryptedPassword("xymnnxeeuux");
		userDto.setEmailVerificationToken("xzuyryuxuurixnnr");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setAddresses(getAddressDtos());

	}

	private List<AddressDto> getAddressDtos() {
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

	@Test
	void testGetUser() {
		when(userService.getUserById(anyString())).thenReturn(userDto);
		ResponseEntity<UserRest> returnedUser = userController.getUser(USER_ID);
		assertNotNull(returnedUser);
		assertEquals(UserRest.class, returnedUser.getBody().getClass());
		assertEquals(userDto.getEmail(), returnedUser.getBody().getEmail());
		assertEquals(userDto.getAddresses().size(), returnedUser.getBody().getAddresses().size());
	}

}
