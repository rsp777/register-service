package com.pawar.todo.register.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.model.Inventory;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;
import com.pawar.todo.dto.UserRolesDto;
import com.pawar.todo.register.entity.Permission;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.entity.UserRolePermissions;
import com.pawar.todo.register.exception.PermissionNotFoundException;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.repository.PermissionRepository;
import com.pawar.todo.register.repository.RoleRepository;
import com.pawar.todo.register.repository.UserRepository;
import com.pawar.todo.register.repository.UserRolePermissionsRepository;

import jakarta.persistence.EntityManager;

@Service
public class UserRoleService {

	private static final Logger logger = LoggerFactory.getLogger(UserRoleService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	private EntityManager entityManager;

	@Autowired
	private KafkaTemplate<String, String> userRoleKafkaTemplate;

	private final ObjectMapper objectMapper;

	public UserRoleService(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Transactional
	public void assignRolesToUser(Long user_id, Integer role_id) throws UserNotFoundException, RoleNotFoundException, JsonProcessingException {
		User user = userRepository.findById(user_id)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + user_id));

		Role assignedRole = roleRepository.findById(role_id)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + role_id));

		Set<Role> assignedRoles = new HashSet<>(user.getRoles());
		assignedRoles.add(assignedRole);
		user.setRoles(assignedRoles);
		User savedUser = userRepository.save(user);
		logger.info("Updated User : {} ", savedUser);

		String TO_DO_ASSIGN_USER_ROLE = "TO.DO.ASSIGN.USER.ROLE";
		String user_role_json = objectMapper.writeValueAsString(savedUser);

		logger.info("UPDATED USER PERMISSION EVENT : {}", user_role_json);
		userRoleKafkaTemplate.send(TO_DO_ASSIGN_USER_ROLE, user_role_json);

		logger.info("User Role message published to Topic : {}", TO_DO_ASSIGN_USER_ROLE);

		logger.info("Roles assigned successfully to user ID: {}", user_id);
	}

	@Transactional
	public void unassignRolesToUser(Long userId, Integer roleId) throws UserNotFoundException, RoleNotFoundException, JsonProcessingException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));

		user.getRoles().removeIf(r -> r.getRole_id().equals(roleId));
		User savedUser = userRepository.save(user);
		
		logger.info("Updated Role : {} ", savedUser);

		String TO_DO_UNASSIGN_USER_ROLE = "TO.DO.UNASSIGN.USER.ROLE";
		String user_role_json = objectMapper.writeValueAsString(savedUser);

		logger.info("UPDATED USER PERMISSION EVENT : {}", user_role_json);
		userRoleKafkaTemplate.send(TO_DO_UNASSIGN_USER_ROLE, user_role_json);

		logger.info("User Role message published to Topic : {}", TO_DO_UNASSIGN_USER_ROLE);
		
		logger.info("Role ID: {} removed from user ID: {}", roleId, userId);
	}

	@Transactional(readOnly = true)
	public Set<RoleDto> getUserRoles(Long userId) throws UserNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		return user.getRoles().stream().map(role -> new RoleDto(role.getRole_id(), role.getName()))
				.collect(Collectors.toSet());

	}

}