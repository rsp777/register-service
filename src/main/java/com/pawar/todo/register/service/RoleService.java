package com.pawar.todo.register.service;

import java.util.List;

import javax.management.relation.RoleNotFoundException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.exception.RoleDeletionException;
import com.pawar.todo.register.repository.RoleRepository;

@Service
public class RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

	@Autowired
	private KafkaTemplate<String, String> rolekafkaTemplate;

	@Autowired
	private RoleRepository roleRepository;

	@Transactional
	public Role saveRole(RoleDto roleDto) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		logger.info("RoleDto : {}",roleDto.toString());
		Role role = new Role();
		role.setName(roleDto.getName());
		Role savedRole = roleRepository.save(role);
		logger.info("Role saved successfully with ID: {}", savedRole.getRole_id());
		String TO_DO_NEW_ROLE = "TO.DO.NEW.ROLE";

		String role_json = objectMapper.writeValueAsString(savedRole);
//        List<Role> role2 =  roleRepository.findAll();

//        for (Role role3 : role2) {

//        	String roles_json = objectMapper.writeValueAsString(role3);
		
//		ProducerRecord<String, String> new_role_event = new ProducerRecord<>(TO_DO_NEW_ROLE, role_json);
		logger.info("NEW ROLE EVENT : {}", role_json);
		rolekafkaTemplate.send(TO_DO_NEW_ROLE,role_json);
		
//		rolekafkaTemplate.send(TO_DO_NEW_ROLE, role_json);
		logger.info("New User Role message published to Topic : {}", TO_DO_NEW_ROLE);
//		}

		return savedRole;
	}

	@Transactional
	public Role updateRole(Integer roleId, RoleDto roleDto) throws RoleNotFoundException {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));
		role.setName(roleDto.getName());
		Role updatedRole = roleRepository.save(role);
		logger.info("Role updated successfully with ID: {}", updatedRole.getRole_id());
		return updatedRole;
	}

	@Transactional
	public void deleteRole(Integer roleId) throws RoleDeletionException, RoleNotFoundException {
		try {
			roleRepository.deleteById(roleId);
			logger.info("Role deleted successfully with ID: {}", roleId);
		} catch (Exception e) {
			logger.error("Error occurred while deleting role with ID: {}", roleId, e);
			throw new RoleDeletionException("Could not delete role with ID: " + roleId, e);
		}
	}

	@Transactional(readOnly = true)
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Role getRoleById(Integer roleId) throws RoleNotFoundException {
		return roleRepository.findById(roleId)
				.orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleId));
	}
}