/**
 * Copyright (c) 2012 Aon eSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
