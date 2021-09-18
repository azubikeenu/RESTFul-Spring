package com.azubike.ellpisis.app.ws.io.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.azubike.ellpisis.app.ws.io.entity.AddressEntity;
import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;

@SpringBootTest
class UserRepositoryTest {
	@Autowired
	UserRepository userRepository;
	UserEntity userEntity;
	UserEntity userEntity2;
	String userId = "xxssyyusuuwiei";
	String encryptedPassword = "xxxxwuuurhhrhhuuxbbgdg_eyrnnr";
	String emailVerificationToken = "xxxeuueuuriixnnxmeuue";
	String addressId = "hdhhhhheuxuuexkkee";

	@BeforeEach
	void setUp() throws Exception {

		// Create an instance of UserEntity Stub for each test case
		userEntity = new UserEntity();
		userEntity.setUserId(userId);
		userEntity.setFirstName("Richard");
		userEntity.setLastName("Enu");
		userEntity.setEmail("enazubike88@gmail.com");
		userEntity.setAddresses(getAddressEntities());
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmailVerificationStatus(Boolean.TRUE);
		userRepository.save(userEntity);

		userEntity2 = new UserEntity();
		userEntity2.setUserId(userId + "x");
		userEntity2.setFirstName("Kingsley");
		userEntity2.setLastName("Sammuel");
		userEntity2.setEmail("kingsley@gmail.con");
		userEntity2.setAddresses(getAddressEntities());
		userEntity2.setEncryptedPassword(encryptedPassword);
		userEntity2.setEmailVerificationStatus(Boolean.TRUE);
		userRepository.save(userEntity2);
	}

	@Test
	void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(1, 1);
		Page<UserEntity> pages = userRepository.findVerifiedUsers(pageableRequest);
		assertNotNull(pages);
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);

	}

	private List<AddressDto> getAddressDto() {
		AddressDto addressOne = new AddressDto();
		addressOne.setType("shipping");
		addressOne.setCity("Lagos");
		addressOne.setCountry("Nigeria");
		addressOne.setStreetName("Festac");
		addressOne.setAddressId(addressId);
		addressOne.setPostalCode("12355");
		AddressDto addressTwo = new AddressDto();
		addressTwo.setType("billing");
		addressTwo.setCity("Lagos");
		addressTwo.setCountry("Nigeria");
		addressTwo.setStreetName("Festac");
		addressTwo.setAddressId(addressId);
		addressTwo.setPostalCode("2345666");
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
