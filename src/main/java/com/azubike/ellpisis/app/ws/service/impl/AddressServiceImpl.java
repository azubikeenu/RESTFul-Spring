package com.azubike.ellpisis.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azubike.ellpisis.app.ws.exceptions.UserServiceException;
import com.azubike.ellpisis.app.ws.io.entity.AddressEntity;
import com.azubike.ellpisis.app.ws.io.entity.UserEntity;
import com.azubike.ellpisis.app.ws.repo.AddressRepository;
import com.azubike.ellpisis.app.ws.repo.UserRepository;
import com.azubike.ellpisis.app.ws.service.AddressService;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;
import com.azubike.ellpisis.app.ws.ui.model.response.ErrorMessages;

@Service
public class AddressServiceImpl implements AddressService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AddressRepository addressRepository;

	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnedValue = new ArrayList<>();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());

		List<AddressEntity> addressEntities = addressRepository.findAllByUserDetails(userEntity);
		ModelMapper modelMapper = new ModelMapper();
		for (AddressEntity addressEntity : addressEntities) {
			returnedValue.add(modelMapper.map(addressEntity, AddressDto.class));
		}
		return returnedValue;
	}

}
