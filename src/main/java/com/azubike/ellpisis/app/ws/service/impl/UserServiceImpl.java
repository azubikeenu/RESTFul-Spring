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
import com.azubike.ellpisis.app.ws.io.entity.PasswordRestTokenEntity;
import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.PasswordResetTokenRepository;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.service.UserService;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.shared.utils.EmailService;
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
	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

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
		try {
			// send email to user
			emailService.verifyEmail(userDto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userDto;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// load users from the database using username as email and password
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			throw new UsernameNotFoundException("user not found");
		}

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

	@Override
	public boolean requestPasswordReset(String email) {
		UserEntity user = userRepository.findByEmail(email);
		boolean returnedValue = false;
		if (user == null) {
			return returnedValue;
		}
		String token = utils.generatePasswordResetToken(user.getUserId());
		PasswordRestTokenEntity passwordResetTokenEntity = new PasswordRestTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(user);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		try {
			returnedValue = emailService.sendPasswordRequestEmail(user.getFirstName(), user.getEmail(), token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnedValue;
	}

	@Override
	public boolean resetPassword(String password, String token) {
		boolean returnedValue = false;
		boolean hasExpired = utils.hasTokenExpired(token);
		if (hasExpired)
			return returnedValue;
		// get passwordRestEntity from token
		PasswordRestTokenEntity passwordRestTokenEntity = passwordResetTokenRepository.findByToken(token);
		if (passwordRestTokenEntity == null)
			return returnedValue;

		String encodedPassword = bCryptPasswordEncoder.encode(password);
		UserEntity userEntity = passwordRestTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		// persist in the database
		UserEntity savedUser = userRepository.save(userEntity);
		// verify if encodedPassword equals user encrypted password
		if (savedUser != null && savedUser.getEncryptedPassword().equalsIgnoreCase(encodedPassword))
			returnedValue = true;
		// remove passwordVerification token from db
		passwordResetTokenRepository.delete(passwordRestTokenEntity);
		return returnedValue;

	}

}
