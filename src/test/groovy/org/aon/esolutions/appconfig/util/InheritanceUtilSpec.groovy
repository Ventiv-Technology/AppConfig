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

import java.security.KeyPair

import org.aon.esolutions.appconfig.client.util.RSAEncryptUtil
import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.PrivateKeyHolder
import org.aon.esolutions.appconfig.model.Variable

import spock.lang.Specification

class InheritanceUtilSpec extends Specification {

	InheritanceUtil util;
	def authenticatedClasses;
	
	def setup() {
		authenticatedClasses = []
		util = [ fetchPersistable: { authenticatedClasses.contains(it) }] as InheritanceUtil
	}
	
	def "test one env"() {
		when:
		Environment env = generateEnvironment(["variable.1":"value.1"])
		
		Collection<Variable> variables = util.getVariablesForEnvironment(env);
		
		then:
		variables
		variables.size() == 1
		variables.iterator().next().key == "variable.1"
	}
	
	def "test one inheritance no overlap"() {
		when:
		Environment parent = generateEnvironment(["variable.2":"value.2"])
		Environment child = generateEnvironment(["variable.1":"value.1"])
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		variables.size() == 2
		variables.find() { it.key == "variable.2" }.inheritedFrom == parent
		variables.find() { it.key == "variable.1" }.inheritedFrom == null
	}
	
	def "test one inheritance with overlap"() {
		when:
		Environment parent = generateEnvironment(["variable.2":"value.2"])
		Environment child = generateEnvironment(["variable.1":"value.1", "variable.2": "value.overridden"])
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		variables.size() == 2
		variables.find() { it.key == "variable.2" }.inheritedFrom == null
		variables.find() { it.key == "variable.2" }.overrides == parent
		variables.find() { it.key == "variable.2" }.overrideValue == "value.2"
		variables.find() { it.key == "variable.2" }.value == "value.overridden"
	}
	
	def "test override encrypted with unencrypted"() {
		when:
		Environment parent = generateEnvironment(["variable.1":"value.1", "variable.2":"value.2.encrypted", "variable.4": "value.4.encrypted"], ["variable.2", "variable.4"])
		Environment child = generateEnvironment(["variable.2":"value.overridden", "variable.3": "value.3.encrypted"], ["variable.3"])
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		variables.find() { it.key == "variable.1" }.encrypted == false
		variables.find() { it.key == "variable.2" }.encrypted == false
		variables.find() { it.key == "variable.3" }.encrypted
		variables.find() { it.key == "variable.4" }.encrypted
	}
	
	def "test with real encryption - parent unauthorized"() {
		when:
		Environment parent = generateEnvironment(["variable.1":"value.1", "variable.2":"value.2", "variable.4": "value.4"], ["variable.2", "variable.4"], false)
		Environment child = generateEnvironment(["variable.2":"value.overridden", "variable.3": "value.3"], ["variable.2", "variable.3"])
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		getDecryptedValue("variable.1", variables, child) == "[UNAUTHORIZED]"
		getDecryptedValue("variable.2", variables, child) == "value.overridden"
		variables.find() { it.key == "variable.2" }.encrypted == true
		getDecryptedValue("variable.3", variables, child) == "value.3"
		variables.find() { it.key == "variable.3" }.encrypted == true
		getDecryptedValue("variable.4", variables, child) == "[UNAUTHORIZED]"
	}
	
	def "test with real encryption"() {
		when:
		Environment parent = generateEnvironment(["variable.1":"value.1", "variable.2":"value.2", "variable.4": "value.4"], ["variable.2", "variable.4"])
		Environment child = generateEnvironment(["variable.2":"value.overridden", "variable.3": "value.3"], ["variable.2", "variable.3"])
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		getDecryptedValue("variable.1", variables, child) == "value.1"
		getDecryptedValue("variable.2", variables, child) == "value.overridden"
		getDecryptedValue("variable.3", variables, child) == "value.3"
		getDecryptedValue("variable.4", variables, child) == "value.4"
	}
	
	private Environment generateEnvironment(def properties, def encryptedProperties = [], def authenticatedForCurrentUser = true) {
		Environment answer = new Environment()
		KeyPair keys = RSAEncryptUtil.generateKey()
		answer.setPublicKey(RSAEncryptUtil.getKeyAsString(keys.getPublic()))
		answer.setPrivateKeyHolder(new PrivateKeyHolder(privateKey : RSAEncryptUtil.getKeyAsString(keys.getPrivate())))
		properties.each { answer.put(it.key, it.value); }
		encryptedProperties.each {
			def encrypted = RSAEncryptUtil.encrypt(answer.get(it), keys.getPublic())
			answer.put(it, encrypted);
			answer.addEncryptedVariable(it);
		}
		
		if (authenticatedForCurrentUser) {
			authenticatedClasses.add(answer)
			authenticatedClasses.add(answer.getPrivateKeyHolder())
		}
		
		return answer;
	}
	
	private String getDecryptedValue(String variableName, Collection<Variable> variables, Environment env) {
		def foundVariable = variables.find() { it.key == variableName }
		
		if (foundVariable?.encrypted)
			return RSAEncryptUtil.decrypt(foundVariable.value, RSAEncryptUtil.getPrivateKeyFromString(env?.getPrivateKeyHolder()?.getPrivateKey()))
		else if (foundVariable)
			return foundVariable.value
		else
			return null;
	}
}
