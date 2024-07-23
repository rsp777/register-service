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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class RolePermissionService {

	private static final Logger logger = LoggerFactory.getLogger(RolePermissionService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;
	
	private EntityManager entityManager;
	
	public RolePermissionService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	public void assignPermissionsToRoles(Integer role_id,Integer permission_id) throws RoleNotFoundException, PermissionNotFoundException {
		
		Role role = roleRepository.findById(role_id)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + role_id));

		Permission assignedPermission = permissionRepository.findById(permission_id)
				.orElseThrow(() -> new PermissionNotFoundException("Permission not found with id: " + permission_id));

		Set<Permission> assignedPermissions = new HashSet<>(role.getPermissions());
		assignedPermissions.add(assignedPermission);
		role.setPermissions(assignedPermissions);
		roleRepository.save(role);

		logger.info("Permissions assigned successfully to Role ID: {}", role_id);
	}

	@Transactional(readOnly = true)
	public Set<RoleDto> getUserRoles(Long userId) throws UserNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		return user.getRoles().stream().map(role -> new RoleDto(role.getRole_id(), role.getName()))
				.collect(Collectors.toSet());

	}

	@Transactional
	public void removeRoleFromUser(Long userId, Integer roleId) throws UserNotFoundException, RoleNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));

		user.getRoles().removeIf(r -> r.getRole_id().equals(roleId));
		userRepository.save(user);

		logger.info("Role ID: {} removed from user ID: {}", roleId, userId);
	}

	@Transactional
	public void assignPermissionToRole(Integer role_id, Integer permission_id)
			throws RoleNotFoundException, PermissionNotFoundException {
		
		Session currentSession = entityManager.unwrap(Session.class);
		Query<UserRolePermissions> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0", UserRolePermissions.class);
		query.executeUpdate();
		
		
		Role role = roleRepository.findById(role_id)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + role_id));

		Permission assignedPermission = permissionRepository.findById(permission_id)
				.orElseThrow(() -> new PermissionNotFoundException("Permission not found with id: " + permission_id));

		Set<Permission> assignedPermissions = new HashSet<>(role.getPermissions());
		assignedPermissions.add(assignedPermission);

		role.setPermissions(assignedPermissions);
		roleRepository.save(role);
		Query<UserRolePermissions> query2 = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", UserRolePermissions.class);
		query2.executeUpdate();
		logger.info("Permissions assigned successfully to Role ID: {}", permission_id);

	}
}