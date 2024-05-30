package com.pawar.todo.register.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.entity.UserRole;
import com.pawar.todo.register.entity.UserRoleId;
import com.pawar.todo.register.exception.RoleDeletionException;
import com.pawar.todo.register.exception.UserAlreadyExistException;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.repository.RoleRepository;
import com.pawar.todo.register.repository.UserRepository;
import com.pawar.todo.register.repository.UserRoleRepository;

@Service
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private KafkaTemplate<String, String> userKafkaTemplate;

	@Autowired
	private KafkaTemplate<String, String> userRoleKafkaTemplate;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public User registerNewUserAccount(UserDto userDto, Set<RoleDto> roleDtos)
			throws UserAlreadyExistException, JsonProcessingException {
		logger.debug("Registering user account with username: {}", userDto.getUsername());
		logger.debug("User Email: {}", userDto.getEmail());
		logger.debug("User Password: {}", userDto.getpasswordHash());
		if (emailExist(userDto.getEmail())) {
			logger.warn("User registration failed. Email already exists: {}", userDto.getEmail());
			throw new UserAlreadyExistException("There is an account with that email address: " + userDto.getEmail());
		}

		User user = new User();
		user.setUsername(userDto.getUsername());
		user.setEmail(userDto.getEmail());
		user.setPasswordHash(passwordEncoder.encode(userDto.getpasswordHash()));
		user.setCreatedAt(Date.valueOf(LocalDate.now()));
		user.setUpdatedAt(Date.valueOf(LocalDate.now()));
		Set<Role> roles = roleDtos.stream().map(this::findRoleByName).collect(Collectors.toSet());

		user.setRoles(roles);

		userDto.setRoles(roleDtos);
		User registeredUser = userRepository.save(user);
		User new_user = userRepository.findByUsername(registeredUser.getUsername()).get();
		logger.info("created date time : {}", new_user.getCreatedAt());
		logger.info("update date time : {}", new_user.getUpdatedAt());
		logger.info("User registered successfully with username: {}", userDto.getUsername());

//		Set<Role> roles = roleDtos.stream().map(roleDto -> roleRepository.findByName(roleDto.getName())
//				.orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet());

//		Set<Role> roles = roleDtos.stream().map(roleDto -> roleRepository.findByName(roleDto.getName())
//				.<Role>orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet());

		logger.info("User Role: {}", userDto.getRoles());
		logger.info("User {} assigned with default role successfully", userDto.getUsername());

		UserRoleId userRoleId = new UserRoleId(getRoleIdFromRoleDtos(roleDtos), user.getUser_id());
		logger.info("user rold id : " + userRoleId);
//		Optional<UserRole> userRoles = userRoleRepository.findById(userRoleId);
		UserRole userRole = userRoleRepository.findUserRolesById(userRoleId.getRoleId(), userRoleId.getUserId());

		String TO_DO_NEW_USER = "TO.DO.NEW.USER";
		String TO_DO_NEW_USER_ROLE = "TO.DO.NEW.USER.ROLE";

		/*
		 * Publish new user info to TO.DO.NEW.USER Topic
		 */

		logger.info("User Roles : {} ", userRole.toString());
//		ProducerRecord<String, String> new_user_event = new ProducerRecord<>(TO_DO_NEW_USER, user.toString());
//		logger.info("NEW USER EVENT : {}", new_user_event);
		Gson gson = new Gson();
//	    String jsonString = gson.toJson(user.toString());	
		ObjectMapper objectMapper = new ObjectMapper();
		String user_json = objectMapper.writeValueAsString(user);
		String user_roles_json = objectMapper.writeValueAsString(userRole);

		logger.info("json user event : {}", user_json);
		logger.info("json user roles event : {}", user_roles_json);
		userKafkaTemplate.send(TO_DO_NEW_USER, user_json);
		logger.info("New User message published to Topic : {}", TO_DO_NEW_USER);

		// Replace "TO.DO.NEW.USER.ROLE" with your actual topic name
		ProducerRecord<String, String> new_user_role_event = new ProducerRecord<>(TO_DO_NEW_USER_ROLE, user_roles_json);
		logger.info("NEW USER ROLE EVENT : {}", new_user_role_event);
		userRoleKafkaTemplate.send(new_user_role_event);
		logger.info("New User Role message published to Topic : {}", TO_DO_NEW_USER_ROLE);

		return registeredUser;
	}

	private Integer getRoleIdFromRoleDtos(Set<RoleDto> roleDtos) {

		logger.info("RoleDtos : {}", roleDtos.toString());

		for (RoleDto roleDto : roleDtos) {
			Integer roleId = roleDto.getRole_id();
			logger.info("roleId : {}", roleId);

			if (roleId != null) {
				logger.info("roleId is not null : {}", roleId);
				return roleId;
			}
		}
		return null;
	}

	@Transactional
	public List<User> getAllUsers() throws UserNotFoundException {
		return userRepository.findAll();
	}

	private Role findRoleByName(RoleDto roleDto) {
		return roleRepository.findByName(roleDto.getName()).orElseThrow(() -> new RuntimeException("Role not found"));
	}

	private boolean emailExist(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	@Transactional
	public User getUserById(Long userId) throws UserNotFoundException {
		// TODO Auto-generated method stub
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
	}

	@Transactional
	public User updateUser(Long userId, @Valid UserDto userDto) throws UserNotFoundException, RoleNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RoleNotFoundException("User not found with ID: " + userId));
		user.setUsername(userDto.getUsername());
		user.setPasswordHash(userDto.getpasswordHash());
		user.setEmail(userDto.getEmail());

		Set<Role> roles = userDto.getRoles().stream().map(roleDto -> new Role(roleDto.getRole_id(), roleDto.getName()))
				.collect(Collectors.toSet());

		user.setRoles(roles);

		User updatedUser = userRepository.save(user);
		logger.info("User updated successfully with ID: {}", updatedUser.getUser_id());
		return updatedUser;
	}

	@Transactional
	public void deleteUser(Long userId) throws UserNotFoundException, RoleDeletionException {
		try {
			userRepository.deleteById(userId);
			logger.info("User deleted successfully with ID: {}", userId);
		} catch (Exception e) {
			logger.error("Error occurred while deleting user with ID: {}", userId, e);
			throw new RoleDeletionException("Could not delete user with ID: " + userId, e);
		}
	}

	@Transactional
	public Set<RoleDto> defaultUserRole() {

		Optional<Role> roleOptional = roleRepository.findByName("USER");
		logger.info("roleOptional : {}", roleOptional);
		if (roleOptional.isPresent()) {
			Role role = roleOptional.get();

			logger.info("DEFAULT ROLE : {}", role);

			RoleDto defaultRole = new RoleDto();
			// Assuming RoleDto has similar fields as Role
			defaultRole.setRole_id(role.getRole_id());
			defaultRole.setName(role.getName());
			// Set other fields as necessary
			return Collections.singleton(defaultRole);
		} else {
			// Handle the case where the role is not found
			throw new RuntimeException("Role not found");
		}

	}

	public User getUserByuserName(String userName) throws UserNotFoundException {

		return userRepository.findByUsername(userName)
				.orElseThrow(() -> new UserNotFoundException("User not found with userName: " + userName));

	}
}
