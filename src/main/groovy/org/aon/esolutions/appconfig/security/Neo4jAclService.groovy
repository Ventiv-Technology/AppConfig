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
package org.aon.esolutions.appconfig.security;

import org.aon.esolutions.appconfig.security.model.Neo4jAccessControlEntry
import org.aon.esolutions.appconfig.security.model.Neo4jAcl
import org.aon.esolutions.appconfig.security.model.Neo4jObjectIdentity
import org.aon.esolutions.appconfig.security.repository.Neo4jAccessControlEntryRepository
import org.aon.esolutions.appconfig.security.repository.Neo4jAclRepository
import org.aon.esolutions.appconfig.security.repository.Neo4jObjectIdentityRepository
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.AlreadyExistsException
import org.springframework.security.acls.model.ChildrenExistException
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.MutableAclService
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.PermissionGrantingStrategy
import org.springframework.security.acls.model.Sid


public class Neo4jAclService implements MutableAclService, ApplicationContextAware {

	private static final Log logger = LogFactory.getLog(Neo4jAclService.class);	
	private static Neo4jAclService instance;
	
	private ApplicationContext ac;
	private PermissionGrantingStrategy permissionGrantingStrategy = new DefaultPermissionGrantingStrategy(new CommonsLoggingAuditLogger());
	private Map<String, List<Permission>> defaultGrantedPermissions = new HashMap<String, List<Permission>>();
	
	@Autowired private Neo4jTemplate template;
	
	public Neo4jAclService() {
		instance = this;
	}
	
	public static Neo4jAclService getInstance() {
		return instance;
	}
	
	protected Neo4jObjectIdentity createOrLookupInternalIdentity(ObjectIdentity parentIdentity) {
		Neo4jObjectIdentity internalParentIdentity;
		
		if (parentIdentity instanceof Neo4jObjectIdentity)
			internalParentIdentity = parentIdentity
		else
			internalParentIdentity = objectIdentityRepo.findByDomainObjectId(parentIdentity.getIdentifier())
			
		if (internalParentIdentity == null) {
			internalParentIdentity = new Neo4jObjectIdentity([type: parentIdentity.getType(), domainObjectId: parentIdentity.getIdentifier()]);
			return objectIdentityRepo.save(internalParentIdentity);
		}
		
		return internalParentIdentity
	}
	
	@Override
	public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
		Neo4jObjectIdentity internalParentIdentity = createOrLookupInternalIdentity(parentIdentity);
		template.fetch(internalParentIdentity.getChildren())
		
		def answer = []
		internalParentIdentity.getChildren().each {
			answer << it
		}
		
		return answer as List<ObjectIdentity>;
	}

	@Override
	public Acl readAclById(ObjectIdentity object) throws NotFoundException {
		return readAclsById([object], null as List<Sid>)?.get(object);
	}

	@Override
	public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
		return readAclsById([object], sids)?.get(object);
	}

	@Override
	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
		return readAclsById(objects, null as List<Sid>);
	}

	@Override
	public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
		def answer = [:]
		
		objects.each {
			Acl foundAcl = aclRepo.findAclByDomainIdAndType(it.getIdentifier(), it.getType());
			if (foundAcl == null)
				foundAcl = new Neo4jAcl([objectIdentity: new Neo4jObjectIdentity([type: it.getType(), domainObjectId: it.getIdentifier()])]);
				
			answer.put(it, foundAcl);
		}
		
		return answer;
	}

	@Override
	public MutableAcl createAcl(ObjectIdentity objectIdentity) throws AlreadyExistsException {
		Neo4jAcl acl = readAclById(objectIdentity);
		if (acl.getIdentifier() != null)
			throw new AlreadyExistsException("Acl (ID: " + acl.id + ") already exists for Domain Object " + objectIdentity);
		
		acl = new Neo4jAcl()
		acl.setObjectIdentity(createOrLookupInternalIdentity(objectIdentity));
		
		aclRepo.save(acl);
	}

	@Override
	public void deleteAcl(ObjectIdentity objectIdentity, boolean deleteChildren) throws ChildrenExistException {
		Neo4jAcl acl = readAclById(objectIdentity);
		if (acl.getIdentifier() != null) {
			List<ObjectIdentity> childIdentities = findChildren(objectIdentity);
			if ((childIdentities != null || childIdentities.isEmpty() == false) && deleteChildren == false)
				throw new ChildrenExistException("${childIdentities.size()} children exist, but I'm not allowed to delete them.");
			
			// Delete the children
			childIdentities.each {
				deleteAcl(it, true);
			}
			
			aceRepo.delete(acl.getEntries() as Iterable)
			aclRepo.delete(acl.getIdentifier());
		}
	}

	@Override
	public MutableAcl updateAcl(MutableAcl acl) throws NotFoundException {
		aceRepo.save(acl.getEntries());
		return aclRepo.save(acl);
	}
	
	public void updateAccessControlEntry(Neo4jAcl acl, int aceIndex, Permission permission) {
		Neo4jAccessControlEntry ace = acl.getEntries().get(aceIndex)
		ace.setPermissionMask(permission.getMask());
		aceRepo.save(ace);
	}
	
	public void insertAccessControlEntry(Neo4jAcl acl, int atIndexLocation, Permission permission, Sid sid, boolean granting) {
		Neo4jAccessControlEntry newEntity = new Neo4jAccessControlEntry([entryFor: getLoginFromSid(sid), permissionMask: permission.getMask(), granting: granting, acl: acl, order: atIndexLocation])
		acl.addEntry(newEntity, atIndexLocation);
		
		updateAcl(acl);
	}
	
	public void deleteAccessControlEntry(Neo4jAcl acl, int aceIndex) {
		Neo4jAccessControlEntry removed = acl.removeEntry(aceIndex);
		aceRepo.delete(removed.getIdentifier());
		updateAcl(acl);
	}
	
	public boolean isGranted(Neo4jAcl acl, List<Permission> permission, List<Sid> sids, boolean administrativeMode) {
		try {
			return permissionGrantingStrategy.isGranted(acl, permission, sids, administrativeMode);
		} catch (NotFoundException nfe) {
			String aclType = acl.getObjectIdentity().getType();
			List<Permission> grantedPermissionList = defaultGrantedPermissions.get(aclType);
			
			if (grantedPermissionList != null && grantedPermissionList.isEmpty() == false) {
				for (Permission p : permission) {
					def foundPermission = grantedPermissionList.find { it.getMask() == p.getMask() }
					if (foundPermission != null)
						return true;
				}
			}
			
			return false;		// Default grant none
		}
	}
	
	private static String getLoginFromSid(Sid sid) {
		if (sid instanceof GrantedAuthoritySid)
			return sid.getGrantedAuthority();
		else if (sid instanceof PrincipalSid)
			return sid.getPrincipal();
			
		return null;
	}
	
	private Neo4jAccessControlEntryRepository getAceRepo() {
		return ac.getBean(Neo4jAccessControlEntryRepository.class);
	}
	
	private Neo4jAclRepository getAclRepo() {
		return ac.getBean(Neo4jAclRepository.class);
	}
	
	private Neo4jObjectIdentityRepository getObjectIdentityRepo() {
		return ac.getBean(Neo4jObjectIdentityRepository.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	public void setDefaultGrantedPermissions(Map<String, List<Permission>> map) {
		this.defaultGrantedPermissions = map;
	}
	
}