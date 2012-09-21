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
package org.aon.esolutions.appconfig.security.model

import java.io.Serializable;

import org.aon.esolutions.appconfig.security.Neo4jAclService
import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid
import org.springframework.security.acls.model.UnloadedSidException

@NodeEntity
class Neo4jAcl implements MutableAcl {

	@GraphId Long identifier;
	
	String ownerLoginId;
	boolean entriesInheriting;
	
	@Fetch
	@RelatedTo(type = "CONTAINS_ENTRY")
	Set<Neo4jAccessControlEntry> entries;
	
	@Fetch
	@RelatedTo(type = "IDENTIFIED_BY")
	Neo4jObjectIdentity objectIdentity;
	
	@Fetch
	@RelatedTo(type = "PARENT")
	Neo4jAcl parentAcl;
	
	@Override
	public ObjectIdentity getObjectIdentity() {
		return objectIdentity;
	}
	
	@Override
	public Serializable getId() {
		return getIdentifier();
	}
	
	@Override
	public List<AccessControlEntry> getEntries() {
		if (entries == null)
			entries = new TreeSet<AccessControlEntry>();
			
		entries.sort { a, b -> a.order <=> b.order } as List<AccessControlEntry>
	}
	
	public void addEntry(Neo4jAccessControlEntry entry, int index) {
		if (entries == null)
			entries = new TreeSet<AccessControlEntry>();
			
		entry.setOrder(index);
		entries.add(entry);
		
		entries.each {
			if (it.getOrder() > index)
				it.setOrder(it.getOrder() + 1);
		}
	}
	
	public Neo4jAccessControlEntry removeEntry(int index) {
		if (entries == null)
			entries = new TreeSet<AccessControlEntry>();
		
		def answer = entries.find { it.getOrder() == index }
		entries.each {
			if (it.getOrder() > index)
				it.setOrder(it.getOrder() - 1);
		}
		
		entries.remove(answer);

		return answer;
	}

	@Override
	public Sid getOwner() {
		return new GrantedAuthoritySid(ownerLoginId);
	}

	@Override
	public boolean isGranted(List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException, UnloadedSidException {
		return Neo4jAclService.getInstance().isGranted(this, permission, sids, administrativeMode);
	}

	@Override
	public boolean isSidLoaded(List<Sid> sids) {
		return true;		// We're fetching the entire record - Maybe improve for performance here
	}

	@Override
	public void deleteAce(int aceIndex) throws NotFoundException {
		Neo4jAclService.getInstance().deleteAccessControlEntry(this, aceIndex);
	}

	@Override
	public void insertAce(int atIndexLocation, Permission permission, Sid sid, boolean granting) throws NotFoundException {
		Neo4jAclService.getInstance().insertAccessControlEntry(this, atIndexLocation, permission, sid, granting);
	}

	@Override
	public void setOwner(Sid newOwner) {
		if (newOwner instanceof GrantedAuthoritySid)
			ownerLoginId = newOwner.getGrantedAuthority()
	}

	@Override
	public void setParent(Acl newParent) {
		if (newParent instanceof Neo4jAcl)
			parentAcl = newParent;
	}

	@Override
	public void updateAce(int aceIndex, Permission permission) throws NotFoundException {
		Neo4jAclService.getInstance().updateAccessControlEntry(this, aceIndex, permission);
	}

}
