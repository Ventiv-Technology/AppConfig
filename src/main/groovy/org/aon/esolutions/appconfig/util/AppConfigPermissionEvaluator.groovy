package org.aon.esolutions.appconfig.util

import org.aon.esolutions.appconfig.model.Application
import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.PrivateKeyHolder;
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.acls.domain.DefaultPermissionFactory
import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid
import org.springframework.security.acls.model.SidRetrievalStrategy
import org.springframework.security.core.Authentication

class AppConfigPermissionEvaluator implements PermissionEvaluator {
	
	private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		List<Sid> sids = sidRetrievalStrategy.getSids(authentication);
		List<Permission> requiredPermission = resolvePermission(permission);
		
		if (targetDomainObject == null)
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
	
	private boolean environmentHasPermission(Environment environment, List<Sid> sids, List<Permission> permission) {
		return true;
	}
	
	private boolean privateKeyHasPermission(PrivateKeyHolder privateKey, List<Sid> sids, List<Permission> permission) {
		return false;
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
}
