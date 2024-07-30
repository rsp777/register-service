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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.register.exception.PermissionAssignmentException;
import com.pawar.todo.register.exception.PermissionNotFoundException;
import com.pawar.todo.register.exception.RoleAssignmentException;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.service.UserRoleService;

@RestController
@RequestMapping("/register-service")
public class UserRoleController {

	private static final Logger logger = LoggerFactory.getLogger(UserRoleController.class);

	@Autowired
	private UserRoleService userRoleService;

	// Assign roles to a user
	@PostMapping("/assign/roles/user/{userId}/role/{roleId}")
	public ResponseEntity<?> assignRolesToUser(@PathVariable Long userId, @PathVariable Integer roleId) {
		try {
			try {
				userRoleService.assignRolesToUser(userId, roleId);
			} catch (UserNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found : {}" + e.getMessage());
			} catch (RoleNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found : {}" + e.getMessage());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body("Exception in Json Processing : {}" + e.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("Roles assigned successfully to user ID: {}", userId);
			return ResponseEntity.ok("Roles assigned successfully.");
		} catch (IllegalArgumentException e) {
			logger.error("Error assigning roles: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (RoleAssignmentException e) {
			logger.error("Role assignment failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Role assignment failed.");
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Role assignment failed.");
		}
	}

	@DeleteMapping("/unassign/roles/user/{userId}/role/{roleId}")
	public ResponseEntity<?> unassignRolesToUser(@PathVariable Long userId, @PathVariable Integer roleId) {
		try {
			try {
				userRoleService.unassignRolesToUser(userId, roleId);
			} catch (UserNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found : {}" + e.getMessage());
			} catch (RoleNotFoundException e) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found : {}" + e.getMessage());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
						.body("Exception in Json Processing : {}" + e.getMessage());
			}
			logger.info("Role ID: {} removed from user ID: {}", roleId, userId);
			return ResponseEntity.ok("Role unassigned successfully.");
		} catch (RoleAssignmentException e) {
			logger.error("Error unassigning role: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unassigning role.");
		}
	}

	// List roles for a user
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
		try {
//	        	logger.info("User Roles",userRolePermissionService.getUserRoles(userId));
			Set<RoleDto> roles = userRoleService.getUserRoles(userId);
			logger.info("Roles retrieved for user ID: {}", userId);
			return ResponseEntity.ok(roles);
		} catch (UserNotFoundException e) {
			logger.error("User not found: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
	}
}
