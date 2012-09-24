package org.aon.esolutions.appconfig.util

import org.aon.esolutions.appconfig.model.RegisteredUsersAndRoles
import org.aon.esolutions.appconfig.repository.RegisteredUsersAndRolesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

class LdapAvailableUsersAndRolesProvider implements AvailableUsersAndRolesProvider, ApplicationListener<AuthenticationSuccessEvent> {
	
	@Autowired private RegisteredUsersAndRolesRepository repo;
	private Long userAndRolesId = null;

	@Override
	public Set<String> getAvailableUsers() {
		return getRegisteredUsersAndRoles().getRegisteredUsers();
	}

	@Override
	public Set<String> getAvailableRoles() {
		return getRegisteredUsersAndRoles().getRegisteredRoles();
	}

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent successfulAuthentication) {
		RegisteredUsersAndRoles usersAndRoles = getRegisteredUsersAndRoles()
		usersAndRoles.getRegisteredUsers().add(successfulAuthentication.getAuthentication().getPrincipal().getUsername())
		successfulAuthentication.getAuthentication().getPrincipal().getAuthorities().each {
			usersAndRoles.getRegisteredRoles().add(it.getAuthority());
		}
		
		repo.save(usersAndRoles);
	}
	
	private RegisteredUsersAndRoles getRegisteredUsersAndRoles() {
		if (userAndRolesId != null)
			return repo.findOne(userAndRolesId);
			
		RegisteredUsersAndRoles answer = repo.findAll().singleOrNull()
		if (answer == null) {
			answer = new RegisteredUsersAndRoles()
			repo.save(answer);
		}
		
		userAndRolesId = answer.getId();
		
		return answer;
	}

}
