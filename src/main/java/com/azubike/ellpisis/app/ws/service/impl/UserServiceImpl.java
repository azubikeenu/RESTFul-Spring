package com.azubike.ellpisis.app.ws.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.azubike.ellpisis.app.ws.exceptions.UserServiceException;
import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.service.UserService;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.shared.utils.Utils;
import com.azubike.ellpisis.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Utils utils;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessages());
		// generate random addressId for each addresses

		for (int i = 0; i < user.getAddresses().size(); i++) {
			// get the address from the userDto object
			AddressDto address = user.getAddresses().get(i);
			// set the addressId of the returned address to a random string
			address.setAddressId(utils.generateRadomAddressId(30));
			// set the userDetails of the returned address to the userDto
			address.setUserDetails(user);
			// set the returned address back to the userDto
			user.getAddresses().set(i, address);
		}
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(utils.generateRandomUserId(30));
		// set emailVerificationToken
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(userEntity.getUserId()));
		userEntity.setEmailVerificationStatus(false);
		UserEntity savedUserDetails = userRepository.save(userEntity);
		UserDto userDto = modelMapper.map(savedUserDetails, UserDto.class);
		return userDto;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// load users from the database using username as email and password
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException("user not found");
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
		// return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new
		// ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		UserDto returnedValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnedValue);
		return returnedValue;
	}

	@Override
	public UserDto getUserById(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("user not found");
		ModelMapper modelMapper = new ModelMapper();
		UserDto returnedValue = modelMapper.map(userEntity, UserDto.class);
		return returnedValue;
	}

	@Override
	public UserDto updateUser(UserDto userDto, String id) {
		UserEntity userEntity = userRepository.findByUserId(id);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		userRepository.save(userEntity);
		UserDto updatedEntities = new UserDto();
		BeanUtils.copyProperties(userEntity, updatedEntities);
		return updatedEntities;
	}

	@Override
	public void deleteUser(String id) {
		UserEntity userEntity = userRepository.findByUserId(id);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		userRepository.delete(userEntity);

	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnedValue = new ArrayList<>();
		Pageable pageable = PageRequest.of(page - 1, limit);
		List<UserEntity> users = userRepository.findAll(pageable).getContent();
		if (users != null && !users.isEmpty()) {
			Type listType = new TypeToken<List<UserDto>>() {
			}.getType();
			returnedValue = new ModelMapper().map(users, listType);
		}
		return returnedValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		if (userEntity != null) {
			boolean hasTokenExpired = utils.hasTokenExpired(token);
			if (!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				return true;
			}
		}
		return false;
	}

}
