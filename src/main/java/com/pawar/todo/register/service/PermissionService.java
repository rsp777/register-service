package com.pawar.todo.register.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.todo.dto.PermissionDto;
import com.pawar.todo.register.entity.Permission;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.events.PermissionDeleteEvent;
import com.pawar.todo.register.exception.PermissionDeletionException;
import com.pawar.todo.register.exception.PermissionNotFoundException;
import com.pawar.todo.register.exception.RoleDeletionException;
import com.pawar.todo.register.repository.PermissionRepository;

@Service
public class PermissionService {

	private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	@Autowired
	private KafkaTemplate<String, String> permissionkafkaTemplate;
	
	@Autowired
	private KafkaTemplate<String, String> permissionDeletekafkaTemplate;


	@Autowired
	private PermissionRepository permissionRepository;

	private final ObjectMapper objectMapper;

	public PermissionService() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Transactional
	public Permission savePermission(@Valid PermissionDto permissionDto) throws JsonProcessingException {
		logger.info("PermissionDto : {}", permissionDto.toString());
		Permission permission = new Permission(permissionDto);
		permission.setName(permissionDto.getName());
		permission.setCreatedDttm(LocalDateTime.now());
		permission.setLastUpdatedDttm(LocalDateTime.now());
		permission.setCreatedSource("PMS");
		permission.setLastUpdatedSource("PMS");
		Permission savedPermission = permissionRepository.save(permission);
		logger.info("New Permission : {}",savedPermission);
		logger.info("Permission saved successfully with ID: {}", savedPermission.getId());

		String TO_DO_NEW_PERMISSION = "TO.DO.NEW.PERMISSION";
		String permission_json = objectMapper.writeValueAsString(savedPermission);
		logger.info("NEW PERMISSION EVENT : {}", permission_json);

		permissionkafkaTemplate.send(TO_DO_NEW_PERMISSION, permission_json);
		logger.info("New Permission message published to Topic : {}", TO_DO_NEW_PERMISSION);
		return savedPermission;
	}

	@Transactional(readOnly = true)
	public List<Permission> getAllPermissions() {
		return permissionRepository.findAll();
	}

	@Transactional
	public Permission updatePermission(Integer permissionId, @Valid PermissionDto permissionDto)
			throws PermissionNotFoundException, JsonProcessingException {
		Permission permission = permissionRepository.findById(permissionId)
				.orElseThrow(() -> new PermissionNotFoundException("Permission not found with ID: " + permissionId));
		permission.setName(permissionDto.getName());
		permission.setLastUpdatedDttm(LocalDateTime.now());
		Permission updatedPermission = permissionRepository.save(permission);
		logger.info("Permission updated successfully with ID: {}", updatedPermission.getId());
		logger.info("Updated Permission : {}",updatedPermission);
		String TO_DO_UPDATE_PERMISSION = "TO.DO.UPDATE.PERMISSION";
		String update_permission_json = objectMapper.writeValueAsString(updatedPermission);
		logger.info("UPDATED PERMISSION EVENT : {}", update_permission_json);

		permissionkafkaTemplate.send(TO_DO_UPDATE_PERMISSION, update_permission_json);
		logger.info("Updated Permission message published to Topic : {}", TO_DO_UPDATE_PERMISSION);

		return updatedPermission;
	}

	@Transactional(readOnly = true)
	public Permission getPermissionById(Integer permissionId) throws PermissionNotFoundException {
		return permissionRepository.findById(permissionId)
				.orElseThrow(() -> new PermissionNotFoundException("Permission not found with ID: " + permissionId));
	}

	@Transactional
	public void deletePermission(Integer permissionId) throws PermissionNotFoundException, PermissionDeletionException {
		try {
			permissionRepository.deleteById(permissionId);
			logger.info("Permission deleted successfully with ID: {}", permissionId);
			String TO_DO_DELETE_PERMISSION = "TO.DO.DELETE.PERMISSION";
			PermissionDeleteEvent deleteEvent = new PermissionDeleteEvent(permissionId);
			String delete_permission_json = objectMapper.writeValueAsString(deleteEvent);
			logger.info("DELETE PERMISSION EVENT : {}", delete_permission_json);

			permissionDeletekafkaTemplate.send(TO_DO_DELETE_PERMISSION, delete_permission_json);
			logger.info("Delete Permission message published to Topic : {}", TO_DO_DELETE_PERMISSION);
		} catch (Exception e) {
			logger.error("Error occurred while deleting permission with ID: {}", permissionId, e);
			throw new PermissionDeletionException("Could not delete permission with ID: " + permissionId, e);
		}

	}

}
