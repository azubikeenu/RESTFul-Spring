package com.azubike.ellpisis.app.ws.ui.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azubike.ellpisis.app.ws.exceptions.UserServiceException;
import com.azubike.ellpisis.app.ws.service.AddressService;
import com.azubike.ellpisis.app.ws.service.UserService;
import com.azubike.ellpisis.app.ws.shared.dto.AddressDto;
import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.ui.model.request.PasswordResetModel;
import com.azubike.ellpisis.app.ws.ui.model.request.PasswordResetRequestModel;
import com.azubike.ellpisis.app.ws.ui.model.request.UserDetailsRequestModel;
import com.azubike.ellpisis.app.ws.ui.model.response.AddressRest;
import com.azubike.ellpisis.app.ws.ui.model.response.ErrorMessages;
import com.azubike.ellpisis.app.ws.ui.model.response.OperationStatusModel;
import com.azubike.ellpisis.app.ws.ui.model.response.RequestOperationName;
import com.azubike.ellpisis.app.ws.ui.model.response.RequestOperationStatus;
import com.azubike.ellpisis.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

//http://localhost:8080/users
@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "*")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header") })
	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserRest> getUser(@PathVariable String id) {
		UserDto user = userService.getUserById(id);
		return ResponseEntity.status(HttpStatus.OK).body(new ModelMapper().map(user, UserRest.class));
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserRest> createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		if (userDetails.getFirstName().isEmpty()) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessages());
		}
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		UserRest returnedValue = modelMapper.map(createdUser, UserRest.class);
		return ResponseEntity.status(HttpStatus.OK).body(returnedValue);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header") })
	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<UserRest> updateUser(@PathVariable String id,
			@RequestBody UserDetailsRequestModel userDetails) {

		if (userDetails.getFirstName().isEmpty()) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessages());
		}
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto updatedUser = userService.updateUser(userDto, id);
		UserRest returnedValue = modelMapper.map(updatedUser, UserRest.class);
		return ResponseEntity.status(HttpStatus.OK).body(returnedValue);

	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header") })
	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnedValue = new OperationStatusModel();
		userService.deleteUser(id);
		returnedValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		returnedValue.setOperationName(RequestOperationName.DELETE.name());
		return returnedValue;
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header") })
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnedValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		if (users != null && !users.isEmpty()) {
			Type listType = new TypeToken<List<UserRest>>() {
			}.getType();
			returnedValue = new ModelMapper().map(users, listType);
		}
		return returnedValue;
	}

	// http://localhost:8080/mobile-app-ws/users/:id/addresses
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header") })
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public ResponseEntity<List<AddressRest>> getUserAddresses(@PathVariable String id) {
		List<AddressRest> addressesListRestModel = new ArrayList<>();
		List<AddressDto> addressesDto = addressService.getAddresses(id);
		if (addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressRest>>() {
			}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDto, listType);
		}
		for (AddressRest address : addressesListRestModel) {
			Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(address.getAddressId(), id))
					.withSelfRel();
			Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
			address.add(addressLink, userLink);
		}

		return ResponseEntity.status(HttpStatus.OK).body(addressesListRestModel);
	}

	// http://localhost:8080/mobile-app-ws/users/:id/addresses/:addressId
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Authorization", value = "${userController.authorizationHeader.description}", paramType = "header") })
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public ResponseEntity<AddressRest> getUserAddress(@PathVariable String addressId, @PathVariable String userId) {
		AddressDto addressesDto = addressService.getAddress(addressId);
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(addressId, userId)).withSelfRel();
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
		AddressRest addressRest = new ModelMapper().map(addressesDto, AddressRest.class);
		addressRest.add(addressLink);
		addressRest.add(addressesLink);
		addressRest.add(userLink);
		return ResponseEntity.status(HttpStatus.OK).body(addressRest);
	}

	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
		OperationStatusModel verifyEmail = new OperationStatusModel();
		verifyEmail.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		boolean isVerified = userService.verifyEmailToken(token);
		if (isVerified)
			verifyEmail.setOperationResult(RequestOperationStatus.SUCCESS.name());
		else
			verifyEmail.setOperationResult(RequestOperationStatus.ERROR.name());
		return verifyEmail;

	}

	@PostMapping(path = "/password-reset-request", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel passwordResetRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		operationStatusModel.setOperationName(RequestOperationName.SEND_PASSWORD_RESET_TOKEN.name());
		if (!operationResult) {
			operationStatusModel.setOperationResult(RequestOperationStatus.ERROR.name());
		} else {
			operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		return operationStatusModel;

	}

	// http://localhost:8080/mobile-app-ws/users/password-reset

	@PostMapping(path = "/password-reset", produces = { MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel passwordReset(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		boolean operationResult = userService.resetPassword(passwordResetModel.getPassword(),
				passwordResetModel.getToken());
		operationStatusModel.setOperationName(RequestOperationName.SEND_PASSWORD_RESET_TOKEN.name());
		if (!operationResult) {
			operationStatusModel.setOperationResult(RequestOperationStatus.ERROR.name());
		} else {
			operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		return operationStatusModel;

	}

}
