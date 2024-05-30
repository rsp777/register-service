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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;
import com.pawar.todo.dto.UserRolesDto;
import com.pawar.todo.register.exception.RoleAssignmentException;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.service.UserRoleService;


@RestController
@RequestMapping("/api/user_roles")
public class UserRoleController {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleController.class);

    @Autowired
    private UserRoleService userRoleService;

    // Assign roles to a user
    @PostMapping("/assign/{user_id}/{role_id}")
    public ResponseEntity<?> assignRolesToUser(@PathVariable Long user_id,@PathVariable Integer role_id) {
        try {
            try {
				userRoleService.assignRolesToUser(user_id,role_id);
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
//        	logger.info("User Roles",userRoleService.getUserRoles(userId));
            Set<RoleDto> roles = userRoleService.getUserRoles(userId);
            logger.info("Roles retrieved for user ID: {}", userId);
            return ResponseEntity.ok(roles);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    // Remove a role from a user
    @DeleteMapping("/unassign/{userId}/{roleId}")
    public ResponseEntity<?> removeUserRole(@PathVariable Long userId, @PathVariable Integer roleId) {
        try {
            try {
				userRoleService.removeRoleFromUser(userId, roleId);
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
}

