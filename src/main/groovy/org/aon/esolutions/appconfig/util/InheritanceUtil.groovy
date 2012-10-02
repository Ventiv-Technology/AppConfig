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

import java.security.PrivateKey
import java.security.PublicKey

import org.aon.esolutions.appconfig.client.util.RSAEncryptUtil
import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.Variable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class InheritanceUtil {
		
	@Autowired(required = false)
	private PermissionEvaluator permissionEvaluator;
	
	@Autowired
	private Neo4jTemplate template;
	
	public Collection<Variable> getVariablesForEnvironment(Environment env, boolean decrypt = false) {
		Map<String, Variable> answer = [:];
		
		Environment currentEnv = env;
		PublicKey encryptingKey = env.getPublicKey() != null ? RSAEncryptUtil.getPublicKeyFromString(env.getPublicKey()) : null
		boolean currentlyReadable = true;
		
		while (currentEnv != null) {
			currentEnv.getVariableEntries().each {
				if (currentlyReadable == false) {
					if (currentEnv?.getEncryptedVariables()?.contains(it.key))
						it.value = RSAEncryptUtil.encrypt("[UNAUTHORIZED]", getPublicKeyForEnvironment(currentEnv));
					else
						it.value = "[UNAUTHORIZED]";
				}
				
				if (answer.containsKey(it.key) == false) {		// Doesn't exist yet, so this environment created this key
					def inheritedFrom = env == currentEnv ? null : currentEnv;
					answer.put(it.key, new Variable([key: it.key, value: it.value, inheritedFrom: inheritedFrom]))
					
					if (currentEnv?.getEncryptedVariables()?.contains(it.key)) {
						answer.get(it.key).setEncrypted(Boolean.TRUE);
						PrivateKey decryptingKey = getPrivateKeyForEnvironment(currentEnv);
						
						if (env != currentEnv) { 				// We're overriding an encrypted value, re-encrypt it							
							if (decrypt == false) {
								String reencryptedValue = reencryptValue(it.value, encryptingKey, decryptingKey)
								answer.get(it.key).setValue(reencryptedValue)
							} else {
								answer.get(it.key).setValue(decryptValue(it.value, decryptingKey))
							}
						} else if (decrypt) {
							answer.get(it.key).setValue(decryptValue(it.value, decryptingKey))
						}
					}
				} else {										// Already exists, this environment overrode the key
					answer.get(it.key).overrides = currentEnv
					answer.get(it.key).overrideValue = it.value
					
					if (currentEnv?.getEncryptedVariables()?.contains(it.key)) 	
						answer.get(it.key).setOverrideEncrypted(Boolean.TRUE);
				}
			}
			
			currentEnv = currentEnv.getParent();
			currentlyReadable = fetchPersistable(currentEnv);
		}
		
		answer.values().sort { a, b ->
			a.key <=> b.key
		}
	}
	
	protected String reencryptValue(String encryptedValue, PublicKey encryptingKey, PrivateKey decryptingKey) {
		if (decryptingKey) {
			String unEncrypted = decryptValue(encryptedValue, decryptingKey)
			return RSAEncryptUtil.encrypt(unEncrypted, encryptingKey);
		}
		
		return null;
	}
	
	protected String decryptValue(String encryptedValue, PrivateKey decryptingKey) {
		if (decryptingKey) {
			return RSAEncryptUtil.decrypt(encryptedValue, decryptingKey);
		} else {
			return null
		}
	}
	
	protected PrivateKey getPrivateKeyForEnvironment(Environment env) {
		fetchPersistable(env.getPrivateKeyHolder());
		if (env.getPrivateKeyHolder()?.getPrivateKey()) {
			return RSAEncryptUtil.getPrivateKeyFromString(env.getPrivateKeyHolder().getPrivateKey());
		}
		
		return null;
	}
	
	protected PublicKey getPublicKeyForEnvironment(Environment env) {
		return RSAEncryptUtil.getPublicKeyFromString(env.getPublicKey());
	}
	
	protected boolean fetchPersistable(def persistable) {
		template.fetch(persistable);
		
		if (permissionEvaluator)
			return permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), persistable, BasePermission.READ)
		else
			return true;
	}
}
