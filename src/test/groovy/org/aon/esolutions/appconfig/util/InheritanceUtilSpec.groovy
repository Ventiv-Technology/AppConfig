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

import spock.lang.Specification

class InheritanceUtilSpec extends Specification {

	InheritanceUtil util;
	
	def setup() {
		util = [ fetchPersistable: { null }] as InheritanceUtil
	}
	
	def "test one env"() {
		when:
		Environment env = new Environment();
		env.put("variable.1", "value.1");
		
		Collection<Variable> variables = util.getVariablesForEnvironment(env);
		
		then:
		variables
		variables.size() == 1
		variables.iterator().next().key == "variable.1"
	}
	
	def "test one inheritance no overlap"() {
		when:
		Environment env1 = new Environment();
		env1.put("variable.1", "value.1");
		
		Environment env2 = new Environment();
		env2.put("variable.2", "value.2");
		env1.setParent(env2);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(env1);
		
		then:
		variables
		variables.size() == 2
		variables.find() { it.key == "variable.2" }.inheritedFrom == env2
		variables.find() { it.key == "variable.1" }.inheritedFrom == null
	}
	
	def "test one inheritance with overlap"() {
		when:
		Environment env1 = new Environment();
		env1.put("variable.1", "value.1");
		env1.put("variable.2", "value.overridden");
		
		Environment env2 = new Environment();
		env2.put("variable.2", "value.2");
		env1.setParent(env2);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(env1);
		
		then:
		variables
		variables.size() == 2
		variables.find() { it.key == "variable.2" }.inheritedFrom == null
		variables.find() { it.key == "variable.2" }.overrides == env2
		variables.find() { it.key == "variable.2" }.overrideValue == "value.2"
		variables.find() { it.key == "variable.2" }.value == "value.overridden"
	}
	
	def "test override encrypted with unencrypted"() {
		when:
		Environment parent = new Environment();
		parent.put("variable.1", "value.1");
		parent.put("variable.2", "value.2.encrypted");
		parent.put("variable.4", "value.4.encrypted");
		parent.addEncryptedVariable("variable.2");
		parent.addEncryptedVariable("variable.4");
		
		Environment child = new Environment();
		child.put("variable.2", "value.overridden");
		child.put("variable.3", "value.3.encrypted");
		child.addEncryptedVariable("variable.3");
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		variables.find() { it.key == "variable.1" }.encrypted == false
		variables.find() { it.key == "variable.2" }.encrypted == false
		variables.find() { it.key == "variable.3" }.encrypted
		variables.find() { it.key == "variable.4" }.encrypted
	}
}
