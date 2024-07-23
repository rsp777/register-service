package com.pawar.todo.register.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.todo.dto.PermissionDto;
import com.pawar.todo.register.entity.Permission;
import com.pawar.todo.register.repository.PermissionRepository;

@Service
public class PermissionService {

	private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	@Autowired
	private KafkaTemplate<String, String> permissionkafkaTemplate;

	@Autowired
	private PermissionRepository permissionRepository;

	public Permission savePermission(@Valid PermissionDto permissionDto) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		logger.info("PermissionDto : {}", permissionDto.toString());
		Permission permission = new Permission(permissionDto);
		permission.setName(permissionDto.getName());
		permission.setCreatedDttm(LocalDateTime.now());
		permission.setLastUpdatedDttm(LocalDateTime.now());
		permission.setCreatedSource("PMS");
		permission.setLastUpdatedSource("PMS");
		Permission savedPermission = permissionRepository.save(permission);
		logger.info("Permission saved successfully with ID: {}", savedPermission.getId());

		String TO_DO_NEW_PERMISSION = "TO.DO.NEW.PERMISSION";
		String permission_json = objectMapper.writeValueAsString(savedPermission);
		logger.info("NEW ROLE EVENT : {}", permission_json);

		permissionkafkaTemplate.send(TO_DO_NEW_PERMISSION, permission_json);
		logger.info("New Permission message published to Topic : {}", TO_DO_NEW_PERMISSION);
		return savedPermission;
	}

	public List<Permission> getAllPermissions() {
		return permissionRepository.findAll();
	}

	public Permission updatePermission(Integer permissionId, @Valid PermissionDto permissionDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
