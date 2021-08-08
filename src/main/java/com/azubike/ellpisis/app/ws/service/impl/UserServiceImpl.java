package com.azubike.ellpisis.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

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
			throw new RuntimeException("Record Already Exists");

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setUserId(utils.generateRandomUserId(30));
		UserEntity savedUserDetails = userRepository.save(userEntity);
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(savedUserDetails, userDto);
		return userDto;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// load users from the database using username as email and password
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException("user not found");
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException("user not found");
		UserDto returnedValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnedValue);
		return returnedValue;
	}

	@Override
	public UserDto getUserById(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("user not found");
		UserDto returnedValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnedValue);
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
		for (UserEntity user : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(user, userDto);
			returnedValue.add(userDto);
		}
		return returnedValue;
	}

}
