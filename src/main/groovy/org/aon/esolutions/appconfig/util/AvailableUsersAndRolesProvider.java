package org.aon.esolutions.appconfig.util;

import java.util.Set;

public interface AvailableUsersAndRolesProvider {
	
	/**
	 * Gets the available users for this environment, for this authentication mechanisim.
	 * 
	 * @return
	 */
	public Set<String> getAvailableUsers();
	
	/**
	 * Gets the available roles for this environment, for this authentication mechanisim.
	 * 
	 * @return
	 */
	public Set<String> getAvailableRoles();
}
