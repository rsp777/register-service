package com.pawar.todo.register.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pawar.todo.dto.RoleDto;
import com.pawar.todo.dto.UserDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@JsonInclude(value = Include.CUSTOM)
	@Column(unique = true, nullable = true)
	private String username;

	@JsonInclude(value = Include.CUSTOM)
	@Column(unique = true, nullable = true)
	private String email;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "password_hash", nullable = true)
	private String passwordHash;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "first_name")
	private String firstName;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "middle_name")
	private String middleName;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "last_name")
	private String lastName;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "created_at")
	private Date createdAt;

	@JsonInclude(value = Include.CUSTOM)
	@Column(name = "updated_at")
	private Date updatedAt;

	@Column(name = "logged_in", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean loggedIn = false;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//        name = "roles",
//        joinColumns = @JoinColumn(name = "user_id"),
//        inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private Set<Role> roles = new HashSet<>();

	@JsonInclude(value = Include.CUSTOM)
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", // This should be your association table
			joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User() {

	}

	public User(Long user_id, String username, String email, String passwordHash, String firstName, String middleName,
			String lastName, Date createdAt, Date updatedAt, Boolean loggedIn, Set<Role> roles) {
		super();
		this.userId = user_id;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.loggedIn = loggedIn;
	}

	public User(String username, String email, String passwordHash, String firstName, String middleName,
			String lastName) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
	}

	public Long getUser_id() {
		return userId;
	}

	public void setUser_id(Long user_id) {
		this.userId = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "User [user_id=" + userId + ", username=" + username + ", email=" + email + ", passwordHash="
				+ passwordHash + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", loggedIn=" + loggedIn + ", userRoles="
				+ roles + "]";
	}

	public List<UserDto> convertUserEntityToDto(List<User> users) {
			
		List<UserDto> userDtos = new ArrayList<>();
		UserDto userDto = new UserDto();
		
		for (User user : users) {
			userDto.setUser_id(user.getUser_id());
			userDto.setUsername(user.getUsername());
			userDto.setEmail(user.getEmail());
			userDto.setpasswordHash(user.getPasswordHash());
			userDto.setFirstName(user.getFirstName());
			userDto.setMiddleName(user.getMiddleName());
			userDto.setLastName(user.getLastName());
			userDto.setCreatedAt(user.getCreatedAt());
			userDto.setUpdatedAt(user.getUpdatedAt());
			userDto.setLoggedIn(user.getLoggedIn());
			userDto.setRoles(convertRolesEntityToDto(user.getRoles()));
			userDtos.add(userDto); 
		}
		return userDtos;
	}
	
	public Set<RoleDto> convertRolesEntityToDto(Set<Role> roles) {

		Set<RoleDto> roleDtos = new HashSet<>();
		RoleDto roleDto = new RoleDto();
		for (Role role : roles) {
			roleDto.setRole_id(role.getRole_id());
			roleDto.setName(role.getName());
			roleDto.setPermissions(role.convertPermissionEntityToDto(role.getPermissions()));
			roleDto.setCreatedDttm(role.getCreatedDttm());
			roleDto.setLastUpdatedDttm(role.getLastUpdatedDttm());
			roleDto.setCreatedSource(role.getCreatedSource());
			roleDto.setLastUpdatedSource(role.getLastUpdatedSource());
			roleDtos.add(roleDto);
		}
		return roleDtos;

	}

}
