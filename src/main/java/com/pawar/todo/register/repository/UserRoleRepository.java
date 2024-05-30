package com.pawar.todo.register.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pawar.todo.register.entity.UserRole;
import com.pawar.todo.register.entity.UserRoleId;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole,UserRoleId> {
	
	@Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :roleId and ur.userId = :userId")
    UserRole findUserRolesById(@Param("roleId") Integer roleId,@Param("userId") Long userId);
	
}
