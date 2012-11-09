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
package org.aon.esolutions.appconfig.model

import groovy.transform.ToString;

import java.util.Map.Entry

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.fieldaccess.DynamicProperties
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer

@NodeEntity
@ToString(excludes = "children,parent")
class Environment {
	
	@GraphId
	Long id;
	
	String name;
	String publicKey;
	
	List<String> permittedUsers;
	List<String> permittedRoles;
	List<String> permittedMachines;
	boolean visibleToAll = true;
	
	@RelatedTo(type = "INHERITS_FROM")
	Environment parent;
	
	@RelatedTo(type = "INHERITS_FROM", direction = Direction.INCOMING)
	Set<Environment> children;
	
	@RelatedTo(type = "PROTECTED_BY")
	PrivateKeyHolder privateKeyHolder;
	
	DynamicProperties variables;
	List<String> encryptedVariables;
	
	public void put(String key, String value) {
		getVariables().setProperty(key, value);
	}

	public String get(String key) {
		return getVariables().getProperty(key);
	}
	
	public String remove(String key) {
		return getVariables().removeProperty(key);
	}
	
	public DynamicProperties getVariables() {
		if (variables == null)
			variables = new DynamicPropertiesContainer();
			
		return variables;
	}
	
	public void clearVariables() {
		variables = new DynamicPropertiesContainer();
	}
	
	public Set<Entry<String, String>> getVariableEntries() {
		getVariables().asMap().entrySet();
	}
	
	public void addEncryptedVariable(String key) {
		getEncryptedVariables().add(key);
	}
	
	public List<String> getEncryptedVariables() {
		if (encryptedVariables == null)
			encryptedVariables = new ArrayList<String>();
			
		return encryptedVariables;
	}
	
	public boolean getVisibleToAll() {
		return visibleToAll
	}
	
	List<String> getPermittedUsers() {
		if (permittedUsers == null)
			permittedUsers = new ArrayList<String>();
		
		return permittedUsers;
	}
	
	List<String> getPermittedRoles() {
		if (permittedRoles == null)
			permittedRoles = new ArrayList<String>();
			
		return permittedRoles
	}
}
