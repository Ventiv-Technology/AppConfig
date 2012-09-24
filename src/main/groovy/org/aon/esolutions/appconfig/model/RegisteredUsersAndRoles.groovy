package org.aon.esolutions.appconfig.model

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
class RegisteredUsersAndRoles {

	@GraphId
	Long id;
	
	Set<String> registeredUsers;
	Set<String> registeredRoles;
	
	public Set<String> getRegisteredUsers() {
		if (registeredUsers == null)
			registeredUsers = new HashSet<String>();
			
		return registeredUsers;			
	}
	
	public Set<String> getRegisteredRoles() {
		if (registeredRoles == null)
			registeredRoles = new HashSet<String>();
			
		return registeredRoles;
	}
}
