package com.pawar.todo.register.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;
import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.entity.VerificationToken;
import com.pawar.todo.register.exception.UserAlreadyExistException;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.service.MailService;
import com.pawar.todo.register.service.UserService;
import com.pawar.todo.register.service.VerificationTokenService;

@RefreshScope
@RestController
@RequestMapping("/register-service")
public class UserRegistrationController {

	private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private VerificationTokenService verificationTokenService;
	
	@Autowired
	private MailService mailService;

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping("/register")
	public ResponseEntity<User> registerUserAccount(@RequestBody UserDto userDto) {
		logger.debug("Registering user account with username: {}", userDto.getUsername());
		try {
			Set<RoleDto> roleDtos = defaultUserRole();
			logger.info("RoleDtos : {}",roleDtos);
			
			
			try {
				User registered = userService.registerNewUserAccount(userDto, roleDtos);
				VerificationToken token = verificationTokenService.createVerificationToken(registered);
				mailService.sendMail(registered.getEmail(), "Registration Confirmation", "To confirm your e-mail address, please click the link below:\n" + "http://"+InetAddress.getLocalHost().getHostAddress()+":8082/register-service/confirm?token=" + token.getToken());
				logger.info("User registered successfully with username: {}", userDto.getUsername());
				return new ResponseEntity<>(registered, HttpStatus.CREATED);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			}
			
			
			
		} catch (UserAlreadyExistException u) {
			logger.error("Registration failed for username: {}", userDto.getUsername(), u);
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return null;
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(path = "/mail/send")
	public void sendMail() {
		mailService.sendMail("test123@outlook.com", "testmail", "hello!");
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	    public String confirmMail(@RequestParam("token") String token) {
	        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
	        if (verificationToken == null || verificationToken.getVerified() || verificationToken.getExpiryDate().before(new Date())) {
	            return "Link is invalid or broken!";
	        }

	        verificationTokenService.confirmToken(verificationToken);
	        logger.info("User Verified");	
	        return "User verified!";
	    }
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		logger.info("Getting All Users");
		try {

			List<User> users = userService.getAllUsers();
			logger.info("Users retrived");
			logger.debug("Users retrived: {}", users.toString());

			return new ResponseEntity<>(users, HttpStatus.CREATED);
		} catch (UserNotFoundException uaeEx) {
			logger.error("Retrieving failed for fetching users: {}", uaeEx);
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/users/id/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
		try {
			User user = userService.getUserById(userId);
			logger.info("User retrieved successfully with ID: {}", user.getUser_id());
			return ResponseEntity.ok(user);
		} catch (UserNotFoundException e) {
			logger.error("User not found with ID: {}", userId, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to retrieve User with ID: {}", userId, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User retrieval failed", e);
		}
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/users/name/{userName}")
	public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
		try {
			User user = userService.getUserByuserName(userName);
			logger.info("User retrieved successfully with userName: {}", user.getUsername());
			return ResponseEntity.ok(user);
		} catch (UserNotFoundException e) {
			logger.error("User not found with userName: {}", userName, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to retrieve User with userName: {}", userName, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User retrieval failed", e);
		}
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PatchMapping("/users/id/{userId}")
	public ResponseEntity<User> updateRole(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
		try {
			User user = userService.updateUser(userId, userDto);
			logger.info("User updated successfully with ID: {}", user.getUser_id());
			return ResponseEntity.ok(user);
		} catch (UserNotFoundException e) {
			logger.error("User not found with ID: {}", userId, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to update user with ID: {}", userId, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User update failed", e);
		}
	}
	
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@DeleteMapping("/users/id/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		try {
			userService.deleteUser(userId);
			logger.info("User deleted successfully with ID: {}", userId);
			return ResponseEntity.ok().build();
		} catch (UserNotFoundException e) {
			logger.error("User not found with ID: {}", userId, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to delete user with ID: {}", userId, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role deletion failed", e);
		}
	}

	// This method should contain the logic to determine the roles for a user
	private Set<RoleDto> defaultUserRole() {
		// Example: Assign a default 'USER' role to all new registrations
		return userService.defaultUserRole();
	}
}
