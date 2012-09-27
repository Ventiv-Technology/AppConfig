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

import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.PrivateKeyHolder
import org.aon.esolutions.appconfig.model.Variable

import spock.lang.Specification

class InheritanceUtilSpec extends Specification {

	InheritanceUtil util;
	
	def setup() {
		util = [ fetchPersistable: { true }] as InheritanceUtil
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
	
	def "test with real encryption - parent unauthorized"() {
		when:
		Environment parent = new Environment();
		parent.put("variable.1", "value.1");
		parent.put("variable.2", "bhiXzGjtgP8l1T/MZZLUN5SkrJoeyeq+fcAcDzVZLeMaqqtEz78PvX8556dmtUp5qf3Sz8knvgc4YL6JoPhMpv1C/zY8oJBpIK2rzB6k4C4Po4oYBxzsT5VIw5m+4LW4s9p4EVlCQh3t9yFjWjsXQpOy80o5TNWaPokG4V/4Mlg=");	// value.2
		parent.put("variable.4", "hnj1UsxxnFGyx1YDVh3QreB0QR8zrJaBtL1VgiEYHgnR5Yn0DkfsDzP3WxvLfyu/nKK/Vtw/ZMMhf2IIC1I7aySg4I5CqdezRCGvJv8dc+wtAk/VGsbYfrO4y7k0pNpArgCEESzLPdEwe/U0UtiMRl65b8EOnEydAJ8yKcWL0qA=");	// value.4
		parent.addEncryptedVariable("variable.2");
		parent.addEncryptedVariable("variable.4");
		parent.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3vI/P6aZ0Hr30udA9kLntGnLSUu69bJ2LBjeXXmnC7KbW7s/qy7sBLFc4tvpmG1sWL232zJgaUxA0oW7+Rjodl1hAs4hs4q2l4FFOV5laOMDi5NsFzuE3iKtxq/j/8Ns8vZY1Bhn+QNJkXf1JGc56YxOJqIUWNyB4WYQRIUCBjwIDAQAB")
		
		Environment child = new Environment();
		child.put("variable.2", "feJXenlTbQ21Mk8/k1T/x7iHFjjye7RnCOzF2XdNaIU88BWuNkxM5WBsqo/mWEoj1T+ptHStJ/Tp3lNVr7RZlNTReofBbkMw7biRlEpOKo4MSs3o0NHT0QKvSzJBXjrUaXtTx2dKCmkDErBxO+oN6M+ekoDeHkyKGG/XiaXXNfk=");	// value.overridden
		child.put("variable.3", "VpVTR9i4Lh8C6D85IVj7I6XFIXPzhe8EVTJ7OnVQEykVEzUA2RrpaZ0oRANLsaLETxGYPUa9f06Mjn4jcleOPWRBNPG1LoPUU6sk1D28fSI3YOyb54C4ABLR/MBqNUEEuAEiLGPwmkW0/+uQTP5ZcM+wgjUJ6G7oN8bFvXCa7RA=");	// value.3
		child.addEncryptedVariable("variable.2");
		child.addEncryptedVariable("variable.3");
		child.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCV9fqEtMdTdBO0nvUyapN6dFSmjH4jqzmGDdyvKTFwT4ApilQo2gP6kIf4HaoBrz/qZr3ZX3e4yz4zk273vL5Z6ZmM+GlRw75y5vRYGf6/ZGdUllU0KLvH5tBKMs7a6IF58qMs1TePxaCDkmr/twFPoAsMtR+EO2kts8eicPebDQIDAQAB")
		child.setPrivateKeyHolder(new PrivateKeyHolder(privateKey : "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJX1+oS0x1N0E7Se9TJqk3p0VKaMfiOrOYYN3K8pMXBPgCmKVCjaA/qQh/gdqgGvP+pmvdlfd7jLPjOTbve8vlnpmYz4aVHDvnLm9FgZ/r9kZ1SWVTQou8fm0EoyztrogXnyoyzVN4/FoIOSav+3AU+gCwy1H4Q7aS2zx6Jw95sNAgMBAAECgYAa29Wk0yQoRFALN7U+9Gu5sQBAXEVzagWBhxYRoVUjgnm39vif4Hx58k0IGXgLmTHfb8ttFXZB+NUJM8zaPknxVkhRIRzXHK+sJasjmYGbx1BRcZsOu0LmXtAQpJo5OxMsVs5355p3GHUkFM3T0GNxrn7ZMuWDATrjFrHuARC1GQJBANP033HpLAl7r6vD5MlA/rEBMVhKdiWAgjjFVSY9peuSmL3kaltnFG2Di5k2k0HlH4JBgq9J9nMLlSpaeKabLKsCQQC1Hzdq4/FZ+fzo8HoxcKZg3qP2tsSt67gZSt72m6K0hEmhYexC49VajPh0V99YYaC98dmLuqSfdA19D137EWcnAkEAxCuvYs5cY6qGPINiDKKLF2fzM7Q3BR6bZ8+7r1j/Z7iWhaoSheYxkepeGb/ZKvPU5cmcGoBqCLB/rmNtp0WXYQJAAsCqsvEr5dYqxc+By+aOYMKBc2prqsj+T0QYB19uJZgtFkrKsGmOAIZL8Fi1iD5Tlw9p4vm/Lr0wx0nIf9xP2QJAdycaI+gBbAQpO3zH1lbchkPjPGt7aqkkZ82uTmDCDtm5DeiKZ6GjW4ypz6AAw/a9hb63R2v77xKY8lsC5Ja7+Q=="))
		child.setParent(parent);
		
		Collection<Variable> variables = util.getVariablesForEnvironment(child);
		
		then:
		variables
		variables.find() { it.key == "variable.1" }.value == "value.1"
		variables.find() { it.key == "variable.2" }.value == "feJXenlTbQ21Mk8/k1T/x7iHFjjye7RnCOzF2XdNaIU88BWuNkxM5WBsqo/mWEoj1T+ptHStJ/Tp3lNVr7RZlNTReofBbkMw7biRlEpOKo4MSs3o0NHT0QKvSzJBXjrUaXtTx2dKCmkDErBxO+oN6M+ekoDeHkyKGG/XiaXXNfk="
		variables.find() { it.key == "variable.3" }.value == "VpVTR9i4Lh8C6D85IVj7I6XFIXPzhe8EVTJ7OnVQEykVEzUA2RrpaZ0oRANLsaLETxGYPUa9f06Mjn4jcleOPWRBNPG1LoPUU6sk1D28fSI3YOyb54C4ABLR/MBqNUEEuAEiLGPwmkW0/+uQTP5ZcM+wgjUJ6G7oN8bFvXCa7RA="
		RSAEncryptUtil.decrypt(variables.find() { it.key == "variable.2" }.value, RSAEncryptUtil.getPrivateKeyFromString(child.getPrivateKeyHolder().getPrivateKey())) == "value.overridden"
		RSAEncryptUtil.decrypt(variables.find() { it.key == "variable.3" }.value, RSAEncryptUtil.getPrivateKeyFromString(child.getPrivateKeyHolder().getPrivateKey())) == "value.3"
		variables.find() { it.key == "variable.4" }.value == null
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
	
	private Environment generateEnvironment(def properties, def encryptedProperties) {
		Environment answer = new Environment();
		KeyPair keys = RSAEncryptUtil.generateKey()
		answer.setPublicKey(RSAEncryptUtil.getKeyAsString(keys.getPublic()))
		answer.setPrivateKeyHolder(new PrivateKeyHolder(privateKey : RSAEncryptUtil.getKeyAsString(keys.getPrivate())))		
		properties.each { answer.put(it.key, it.value); }
		encryptedProperties.each {
			def encrypted = RSAEncryptUtil.encrypt(answer.get(it), keys.getPublic())
			answer.put(it, encrypted);
			answer.addEncryptedVariable(it);
		}
		
		return answer;
	}
	
	private String getDecryptedValue(String variableName, Collection<Variable> variables, Environment env) {
		try {
			RSAEncryptUtil.decrypt(variables.find() { it.key == variableName }.value, RSAEncryptUtil.getPrivateKeyFromString(env?.getPrivateKeyHolder()?.getPrivateKey()))
		} catch (any) {
			variables.find() { it.key == variableName }.value
		}
	}
}
