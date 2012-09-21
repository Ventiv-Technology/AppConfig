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

import org.springframework.data.neo4j.annotation.Fetch
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.security.acls.domain.DefaultPermissionFactory
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.model.AccessControlEntry
import org.springframework.security.acls.model.Acl
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid

@NodeEntity
public class Neo4jAccessControlEntry implements AccessControlEntry {
	
	private static final PermissionFactory permissionFactory = new DefaultPermissionFactory();	// TODO: Allow this to be configurable via spring

	@GraphId Long identifier;
	
	String entryFor;
	Integer permissionMask;
	boolean granting;
	int order;
	
	@Fetch
	@RelatedTo(type = "OWNED_BY")
	Neo4jAcl acl;
	
	@Override
	public Permission getPermission() {
		return permissionFactory.buildFromMask(permissionMask);
	}

	@Override
	public Sid getSid() {
		return new GrantedAuthoritySid(getEntryFor());
	}

	@Override
	public Serializable getId() {
		return identifier;
	}
	
	@Override
	public String toString() {
		return "Neo4jAccessControlEntry($order): ID: $identifier, EntryFor: $entryFor, Permission: ${getPermission()}, Granting: $granting";
	}
	
}