package com.pawar.todo.register.controller;

import java.util.List;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pawar.todo.dto.PermissionDto;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.register.entity.Permission;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.exception.PermissionNotFoundException;
import com.pawar.todo.register.service.PermissionService;
import com.pawar.todo.register.service.RoleService;

@RestController
@RequestMapping("/register-service")
public class PermissionController {

	private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

	@Autowired
	private PermissionService permissionService;

	@PostMapping("/permission/add")
	public ResponseEntity<Permission> createPermission(@Valid @RequestBody PermissionDto permissionDto) {
		try {
			Permission permission = permissionService.savePermission(permissionDto);
			logger.info("Permission created successfully with ID: {}", permission.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(permission);
		} catch (Exception e) {
			logger.error("Failed to create permission", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Permission creation failed", e);
		}
	}

	@GetMapping("/permissions")
	public ResponseEntity<List<Permission>> getAllPermissions() {
		try {
			List<Permission> permissions = permissionService.getAllPermissions();
			logger.info("Retrieved all permissions successfully");
			return ResponseEntity.ok(permissions);
		} catch (Exception e) {
			logger.error("Failed to retrieve roles", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve permissions", e);
		}
	}

	@PutMapping("/permission/{permissionId}")
	public ResponseEntity<?> updateRole(@PathVariable Integer permissionId,
			@Valid @RequestBody PermissionDto permissionDto) {
		try {
			Permission permission = permissionService.updatePermission(permissionId, permissionDto);
			logger.info("Permission updated successfully with ID: {}", permission.getId());
			return ResponseEntity.ok("Permission updated successfully");
		} catch (PermissionNotFoundException e) {
			logger.error("Permission not found with ID: {}", permissionId, e);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Failed to update permission with ID: {}", permissionId, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Permission update failed", e);
		}
	}
	
  @GetMapping("/permission/{permissionId}")
  public ResponseEntity<Permission> getRoleById(@PathVariable Integer permissionId) {
      try {
    	  Permission permission = permissionService.getPermissionById(permissionId);
          logger.info("Permission retrieved successfully with ID: {}", permission.getId());
          return ResponseEntity.ok(permission);
      } catch (PermissionNotFoundException e) {
          logger.error("Permission not found with ID: {}", permissionId, e);
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
      } catch (Exception e) {
          logger.error("Failed to retrieve permission with ID: {}", permissionId, e);
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Permission retrieval failed", e);
      }
  }

    @DeleteMapping("/permission/{permissionId}")
    public ResponseEntity<?> deletePermission(@PathVariable Integer permissionId) {
        try {
        	permissionService.deletePermission(permissionId);
            logger.info("Permission deleted successfully with ID: {}", permissionId);
            return ResponseEntity.ok().build();
        } catch (PermissionNotFoundException e) {
            logger.error("Permission not found with ID: {}", permissionId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to delete permission with ID: {}", permissionId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Permission deletion failed", e);
        }
    }
}