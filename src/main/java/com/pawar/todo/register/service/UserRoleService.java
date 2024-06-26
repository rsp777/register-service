package com.pawar.todo.register.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;
import com.pawar.todo.dto.UserRolesDto;
import com.pawar.todo.register.entity.Role;
import com.pawar.todo.register.entity.User;
import com.pawar.todo.register.exception.UserNotFoundException;
import com.pawar.todo.register.repository.RoleRepository;
import com.pawar.todo.register.repository.UserRepository;
import com.pawar.todo.register.repository.UserRoleRepository;

@Service
public class UserRoleService {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public void assignRolesToUser(Long user_id,Integer role_id) throws UserNotFoundException, RoleNotFoundException {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + user_id));  
        
        Role assignedRole = roleRepository.findById(role_id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + role_id));
    
        Set<Role> assignedRoles = new HashSet<>(user.getRoles());
        assignedRoles.add(assignedRole);
        
//        Set<Role> assignedRoles = new HashSet<>(user.getRoles()).stream()
//                .map(roleDto -> {
//					try {
//						return roleRepository.findById(roleDto.getRole_id())
//						        .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + roleDto.getRole_id()));
//					} catch (RoleNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return null;
//				})
//                .collect(Collectors.toSet());

        user.setRoles(assignedRoles);
        userRepository.save(user);

        logger.info("Roles assigned successfully to user ID: {}", user_id);
    }

    @Transactional(readOnly = true)
    public Set<RoleDto> getUserRoles(Long userId) throws UserNotFoundException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
             
        return user.getRoles().stream()
        	    .map(role -> new RoleDto(role.getRole_id(), role.getName()))
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
}