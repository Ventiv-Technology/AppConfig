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

import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.Variable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.stereotype.Service

@Service
class InheritanceUtil {
	
	@Autowired
	private Neo4jTemplate template;
	
	public Collection<Variable> getVariablesForEnvironment(Environment env) {
		Map<String, Variable> answer = [:];
		
		Environment currentEnv = env;
		while (currentEnv != null) {
			currentEnv.getVariableEntries().each {
				if (answer.containsKey(it.key) == false) {
					def inheritedFrom = env == currentEnv ? null : currentEnv;
					answer.put(it.key, new Variable([key: it.key, value: it.value, inheritedFrom: inheritedFrom]))
					
					if (currentEnv?.getEncryptedVariables()?.contains(it.key))
						answer.get(it.key).setEncrypted(Boolean.TRUE);
				} else {
					answer.get(it.key).overrides = currentEnv
					answer.get(it.key).overrideValue = it.value
					
					if (currentEnv?.getEncryptedVariables()?.contains(it.key))
						answer.get(it.key).setOverrideEncrypted(Boolean.TRUE);
				}
			}
			
			currentEnv = currentEnv.getParent();
			fetchPersistable(currentEnv);
		}
		
		answer.values().sort { a, b ->
			a.key <=> b.key
		}
	}
	
	protected void fetchPersistable(def persistable) {
		template.fetch(persistable);
	}
}
