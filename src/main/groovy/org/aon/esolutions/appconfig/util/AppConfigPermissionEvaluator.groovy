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

import org.aon.esolutions.appconfig.model.Application
import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.PrivateKeyHolder
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.DefaultPermissionFactory
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid
import org.springframework.security.acls.model.SidRetrievalStrategy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder;

class AppConfigPermissionEvaluator implements PermissionEvaluator {
	
	private static final Log logger = LogFactory.getLog(AppConfigPermissionEvaluator.class);
	
	private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();
	
	public boolean hasPermission(Object targetDomainObject, Object permission) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return hasPermission(auth, targetDomainObject, permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
		List<Permission> requiredPermission = resolvePermission(permission);
		
		if (targetDomainObject == null)
			return true;
		else if (isAdministrator(sids))
			return true;
		else if (targetDomainObject instanceof Application)
			return applicationHasPermission(targetDomainObject, sids, requiredPermission)
		else if (targetDomainObject instanceof Environment)
			return environmentHasPermission(targetDomainObject, sids, requiredPermission)
		else if (targetDomainObject instanceof PrivateKeyHolder)
			return privateKeyHasPermission(targetDomainObject, sids, requiredPermission)

	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		return false;
	}
	
	private boolean applicationHasPermission(Application application, List<Sid> sids, List<Permission> permission) {
		return true;		// No authorization on applications - yet
	}
	
	private boolean environmentHasPermission(Environment environment, List<Sid> sids, List<Permission> permission, boolean checkForVisible = true) {
		if (checkForVisible && environment.isVisibleToAll() && permission.contains(BasePermission.READ))
			return true;
			
		// If no security has been set up - default to allowing all
		
		if (environment.getPermittedUsers()?.isEmpty() && environment.getPermittedRoles()?.isEmpty())
			return true;
			
		def authorizedSid = sids.find {
			if (it instanceof PrincipalSid)
				return environment.getPermittedUsers()?.contains(it.getPrincipal());
			else if (it instanceof GrantedAuthoritySid) {
				if (it.getGrantedAuthority().startsWith("ROLE"))
					return environment.getPermittedRoles()?.contains(it.getGrantedAuthority());
				else if (it.getGrantedAuthority().startsWith("MACHINE"))
					return environment.getPermittedMachines()?.contains(it.getGrantedAuthority().replace("MACHINE_", ""));
			}
		}
		
		if (logger.debugEnabled && authorizedSid != null)
			logger.debug("Found an authorized SID for Environment(${environment.id}) ${environment.name}: $authorizedSid");
		else
			logger.debug("No authorized SID for Environment(${environment.id}) ${environment.name}.  Authorized sids: ${sids}");
			
		return authorizedSid != null;
	}
	
	private boolean privateKeyHasPermission(PrivateKeyHolder privateKey, List<Sid> sids, List<Permission> permission) {
		Environment env = privateKey.getEnvironment();
		
		return environmentHasPermission(env, sids, permission, false);
	}
	
	private boolean isAdministrator(List<Sid> sids) {
		List<String> principalAdmins = System.getProperty("security.authentication.administrators.users")?.split(",")
		List<String> roleAdmins = System.getProperty("security.authentication.administrators.roles")?.split(",")
		
		def adminSid = sids.find {
			if (it instanceof PrincipalSid)
				return principalAdmins?.contains(it.getPrincipal());
			else if (it instanceof GrantedAuthoritySid)
				return  roleAdmins?.contains(it.getGrantedAuthority());
		}
		
		return adminSid != null;
	}

	/*
	 * Borrowed from Spring AclPermissionEvaluator
	 */
	List<Permission> resolvePermission(Object permission) {
		if (permission instanceof Integer) {
			return Arrays.asList(permissionFactory.buildFromMask(((Integer)permission).intValue()));
		}

		if (permission instanceof Permission) {
			return Arrays.asList((Permission)permission);
		}

		if (permission instanceof Permission[]) {
			return Arrays.asList((Permission[])permission);
		}

		if (permission instanceof String) {
			String permString = (String)permission;
			Permission p;

			try {
				p = permissionFactory.buildFromName(permString);
			} catch(IllegalArgumentException notfound) {
				p = permissionFactory.buildFromName(permString.toUpperCase());
			}

			if (p != null) {
				return Arrays.asList(p);
			}

		}
		throw new IllegalArgumentException("Unsupported permission: " + permission);
	}
	
	public void setSidRetrievalStrategy(SidRetrievalStrategy value) {
		this.sidRetrievalStrategy = value;
	}
}
