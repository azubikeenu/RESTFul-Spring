package com.azubike.ellpisis.app.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.service.UserService;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDto createUser(UserDto user) {
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		userEntity.setEncryptedPassword("test");
		userEntity.setUserId("testUserId");
		UserEntity savedUserDetails = userRepository.save(userEntity);
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(savedUserDetails, userDto);
		return userDto;
	}

}
