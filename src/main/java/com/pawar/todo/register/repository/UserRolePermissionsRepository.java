package com.pawar.todo.register.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pawar.todo.register.entity.UserRolePermissions;
import com.pawar.todo.register.entity.UserRolePermissionsId;

@Repository
public interface UserRolePermissionsRepository extends CrudRepository<UserRolePermissions, UserRolePermissionsId> {

	@Query("SELECT ur FROM UserRolePermissions ur WHERE ur.roleId = :roleId and ur.userId = :userId")
	UserRolePermissions findUserRolesById(@Param("roleId") Integer roleId, @Param("userId") Long userId);

}
