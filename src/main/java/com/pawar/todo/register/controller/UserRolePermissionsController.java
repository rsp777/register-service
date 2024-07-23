package com.pawar.todo.register.controller;

import java.util.Set;

import javax.management.relation.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.register.exception.PermissionAssignmentException;
import com.pawar.todo.register.exception.PermissionNotFoundException;
import com.pawar.todo.register.exception.RoleAssignmentException;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.service.UserRolePermissionService;

@RestController
@RequestMapping("/register-service")
public class UserRolePermissionsController {

	private static final Logger logger = LoggerFactory.getLogger(UserRolePermissionsController.class);

	@Autowired
	private UserRolePermissionService userRolePermissionService;

	// Assign roles to a user
	@PostMapping("/assign/user/{user_id}/role/{role_id}")
	public ResponseEntity<?> assignRolesToUser(@PathVariable Long user_id, @PathVariable Integer role_id) {
		try {
			try {
				userRolePermissionService.assignRolesToUser(user_id, role_id);
			} catch (UserNotFoundException | RoleNotFoundException e) {
				e.printStackTrace();
			}
			logger.info("Roles assigned successfully to user ID: {}", role_id);
			return ResponseEntity.ok("Roles assigned successfully.");
		} catch (IllegalArgumentException e) {
			logger.error("Error assigning roles: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RoleAssignmentException e) {
			logger.error("Role assignment failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Role assignment failed.");
		}
	}

	// List roles for a user
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
		try {
//        	logger.info("User Roles",userRolePermissionService.getUserRoles(userId));
			Set<RoleDto> roles = userRolePermissionService.getUserRoles(userId);
			logger.info("Roles retrieved for user ID: {}", userId);
			return ResponseEntity.ok(roles);
		} catch (UserNotFoundException e) {
			logger.error("User not found: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
	}

	// Remove a role from a user
	@DeleteMapping("/unassign/roles/{userId}/{roleId}")
	public ResponseEntity<?> removeUserRole(@PathVariable Long userId, @PathVariable Integer roleId) {
		try {
			try {
				userRolePermissionService.removeRoleFromUser(userId, roleId);
			} catch (RoleNotFoundException | UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Role ID: {} removed from user ID: {}", roleId, userId);
			return ResponseEntity.ok("Role removed successfully.");
		} catch (RoleAssignmentException e) {
			logger.error("Error removing role: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing role.");
		}
	}
	
	//Assign Permission to a Role
	@PostMapping("/assign/permissions/{role_id}/{permission_id}")
	public ResponseEntity<?> assignPermissionToRole(@PathVariable Integer role_id, @PathVariable Integer permission_id) {
		try {
			try {
				userRolePermissionService.assignPermissionToRole(role_id, permission_id);
			} 
			catch (RoleNotFoundException e) {
				logger.info("Role Not Found : {}",e.getMessage());
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role does not exist!");
			}
			catch (PermissionNotFoundException e) {
				logger.info("Permission Not Found : {}",e.getMessage());
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Permission does not exist!");
			}
			logger.info("Permission assigned successfully to ROLE ID: {}", permission_id);
			return ResponseEntity.ok("Permission assigned successfully.");
		} catch (IllegalArgumentException e) {
			logger.error("Error assigning roles: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (PermissionAssignmentException e) {
			logger.error("Permission assignment failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Permission assignment failed.");
		}
	}
}
