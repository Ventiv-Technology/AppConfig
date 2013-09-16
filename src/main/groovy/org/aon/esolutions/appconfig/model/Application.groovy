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

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
@ToString
class Application {
	
	@GraphId
	Long id;
	
	@RelatedTo(type = "USED_IN")
	Set<Environment> environments;
	
	String name;
	String[] ownerRoles;
	String[] ownerLogins;
	
	public void addEnvironment(Environment env) {
		if (environments == null)
			environments = new TreeSet<Environment>()
			
		environments.add(env);
	}
	
	public Set<Environment> getEnvironments() {
		return environments
	}
}
