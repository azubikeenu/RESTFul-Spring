package com.azubike.ellpisis.app.ws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.service.UserService;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.shared.utils.Utils;

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

}
