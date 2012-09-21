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
package org.aon.esolutions.appconfig.security

import org.aon.esolutions.appconfig.security.model.Neo4jAcl
import org.aon.esolutions.appconfig.security.model.Neo4jObjectIdentity
import org.aon.esolutions.appconfig.security.repository.Neo4jAccessControlEntryRepository
import org.aon.esolutions.appconfig.security.repository.Neo4jAclRepository
import org.aon.esolutions.appconfig.security.repository.Neo4jObjectIdentityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.ObjectIdentityImpl
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ChildrenExistException
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import spock.lang.Specification

@ContextConfiguration(locations = ["/spring/applicationContext.xml", "/spring/test-applicationContext-security.xml", "/spring/test-applicationContext-data.xml"])
class Neo4jAclServiceSpec {// comment out - doesn't work w/ local db just remote extends Specification {

	@Autowired Neo4jAclService aclService;
	@Autowired private Neo4jTemplate template;
	@Autowired private Neo4jObjectIdentityRepository objectIdentityRepo;
	@Autowired private Neo4jAccessControlEntryRepository aceRepo;
	@Autowired private Neo4jAclRepository aclRepo;
	
	@Transactional
	def "get children object identity"() {
		when:
		// Set up some Object Id structures
		Neo4jObjectIdentity parentId = aclService.createOrLookupInternalIdentity(new ObjectIdentityImpl("test", 5))
		Neo4jObjectIdentity childOne = aclService.createOrLookupInternalIdentity(new ObjectIdentityImpl("test", 6))
		Neo4jObjectIdentity childTwo = aclService.createOrLookupInternalIdentity(new ObjectIdentityImpl("test", 7))

		parentId.setChildren([childOne, childTwo] as Set<Neo4jObjectIdentity>);		
		parentId = objectIdentityRepo.save(parentId)
		
		List<ObjectIdentity> result = aclService.findChildren(new ObjectIdentityImpl("test", 5))
		
		then:
		result.size() == 2
		result.find { it.getIdentifier() == childOne.getIdentifier() }
		result.find { it.getIdentifier() == childTwo.getIdentifier() }
	}
	
	@Transactional
	def "no acl records yet defined"() {
		when:
		Acl acl = aclService.readAclById(new ObjectIdentityImpl("not found", 283));
		
		then:
		acl
	}
	
	@Transactional
	def "complete workflow"() {
		ObjectIdentity id = new ObjectIdentityImpl("test", 5);
		
		when:
		// Create it
		Neo4jAcl acl = aclService.createAcl(id)
		
		// Add some Control Records
		acl.insertAce(0, BasePermission.READ, new GrantedAuthoritySid("jcrygier"), true)
		acl.insertAce(1, BasePermission.WRITE, new GrantedAuthoritySid("jcrygier"), true)
		
		then:
		Neo4jAcl readAcl = aclService.readAclById(id)
		readAcl.entries.size() == 2
		readAcl.ownerLoginId == null
		readAcl.entries[0].getPermission() == BasePermission.READ
		readAcl.entries[1].getPermission() == BasePermission.WRITE
		
		when:
		readAcl.setOwnerLoginId("jcrygier")
		aclService.updateAcl(readAcl)
		
		then:
		Neo4jAcl readAcl2 = aclService.readAclById(id)
		readAcl2.ownerLoginId == "jcrygier"
		readAcl2.entries[0].getPermission() == BasePermission.READ
		readAcl2.entries[1].getPermission() == BasePermission.WRITE
		
		when:
		readAcl2.deleteAce(0)
		
		then:
		Neo4jAcl readAcl3 = aclService.readAclById(id)
		readAcl3.entries.size() == 1
		readAcl3.entries.find { it.getPermission() == BasePermission.WRITE }
		
		when:
		readAcl3.insertAce(1, BasePermission.DELETE, new GrantedAuthoritySid("jcrygier"), true)
		
		then:
		Neo4jAcl readAcl4 = aclService.readAclById(id)
		readAcl4.entries.size() == 2
		readAcl4.entries[0].getPermission() == BasePermission.WRITE
		readAcl4.entries[1].getPermission() == BasePermission.DELETE
		
		when:
		readAcl4.updateAce(1, BasePermission.CREATE)
		
		then:
		Neo4jAcl readAcl5 = aclService.readAclById(id)
		readAcl5.entries.size() == 2
		readAcl5.entries[0].getPermission() == BasePermission.WRITE
		readAcl5.entries[1].getPermission() == BasePermission.CREATE
		
		when:
		aclService.deleteAcl(id, false);
		
		then:
		thrown(ChildrenExistException)
		
		when:
		aclService.deleteAcl(id, true);
		
		then:
		Neo4jAcl readFinal = aclService.readAclById(id)
		readFinal.getIdentifier() == null;
		
	}
}
