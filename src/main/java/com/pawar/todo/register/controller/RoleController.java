package com.pawar.todo.register.controller;

import java.util.List;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.exception.RoleDeletionException;
import com.pawar.todo.register.service.RoleService;


@RestController
@RequestMapping("/register-service")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    
    @Autowired
    private RoleService roleService;



    @PostMapping("/roles/add")
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleDto roleDto) {
        try {
            Role role = roleService.saveRole(roleDto);
            logger.info("Role created successfully with ID: {}", role.getRole_id());
            return ResponseEntity.status(HttpStatus.CREATED).body(role);
        } catch (Exception e) {
            logger.error("Failed to create role", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role creation failed", e);
        }
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<Role> updateRole(@PathVariable Integer roleId, @Valid @RequestBody RoleDto roleDto) {
        try {
            Role role = roleService.updateRole(roleId, roleDto);
            logger.info("Role updated successfully with ID: {}", role.getRole_id());
            return ResponseEntity.ok(role);
        } catch (RoleNotFoundException e) {
            logger.error("Role not found with ID: {}", roleId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to update role with ID: {}", roleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role update failed", e);
        }
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer roleId) {
        try {
            roleService.deleteRole(roleId);
            logger.info("Role deleted successfully with ID: {}", roleId);
            return ResponseEntity.ok().build();
        } catch (RoleNotFoundException e) {
            logger.error("Role not found with ID: {}", roleId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to delete role with ID: {}", roleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role deletion failed", e);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            logger.info("Retrieved all roles successfully");
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Failed to retrieve roles", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve roles", e);
        }
    }

    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer roleId) {
        try {
            Role role = roleService.getRoleById(roleId);
            logger.info("Role retrieved successfully with ID: {}", role.getRole_id());
            return ResponseEntity.ok(role);
        } catch (RoleNotFoundException e) {
            logger.error("Role not found with ID: {}", roleId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to retrieve role with ID: {}", roleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role retrieval failed", e);
        }
    }
}

@ControllerAdvice
class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException e) {
        logger.error("Exception: ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(RoleDeletionException.class)
    public ResponseEntity<String> handleRoleDeletionException(RoleDeletionException e) {
        logger.error("Exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    // Add other exception handlers as needed
}

