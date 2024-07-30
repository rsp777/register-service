package com.pawar.todo.register.events;

public class PermissionDeleteEvent {
	private Integer permissionId;
	
	public PermissionDeleteEvent() {
	}
	
	
	public PermissionDeleteEvent(Integer permissionId) {
		this.permissionId = permissionId;
	}

	public Integer getPermissionId() {
		return permissionId;
	}

	@Override
	public String toString() {
		return "PermissionDeleteEvent [permissionId=" + permissionId + "]";
	}

}
