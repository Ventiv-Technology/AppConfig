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
package org.aon.esolutions.appconfig.web.controller

import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.repository.ApplicationRepository
import org.aon.esolutions.appconfig.repository.PrivateKeyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.acls.model.NotFoundException
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import spock.lang.Specification

@ContextConfiguration(locations = ["/spring/applicationContext.xml", "/spring/applicationContext-data.xml", "/spring/test/dispatcher-servlet.xml"])
class EnvironmentControllerSpec extends Specification {
	
	static {
		System.setProperty("data.provider", "neo4j");
		System.setProperty("data.neo4j.configuration", "local-test");
	}

	@Autowired private EnvironmentController envController;
	@Autowired private ApplicationController appController;
	@Autowired private PrivateKeyRepository pkRepository;
	@Autowired private ApplicationRepository appRepository;
	
	@Transactional
	def "get environment keys"() {
		given:
		def setupEnvironment = setupEnvironment();
		
		when:
		def keys = envController.getKeys("TestingApplication", "Default");
		
		then:
		keys
		keys['private'].length() > 10
		keys['public'].length() > 10
	}
	
	@Transactional
	def "delete environment"() {
		given:
		def setupEnvironment = setupEnvironment();
		
		when:
		envController.deleteEnvironment("TestingApplication", "Default")
		envController.getEnvironment("TestingApplication", "Default")
		
		then:		
		def e = thrown(IllegalStateException)	// Node4j throws this - same txn
		e.getMessage() == "Node[${setupEnvironment.getId()}] has been deleted in this tx"
		
		when:
		pkRepository.findOne(setupEnvironment.getPrivateKeyHolder().getId()) == null
		
		then:
		def e2 = thrown(IllegalStateException)
		e2.getMessage() == "Node[${setupEnvironment.getPrivateKeyHolder().getId()}] has been deleted in this tx"
	}
	
	@Transactional
	def "delete environment where there is a child"() {
		given:
		def setupEnvironment = setupEnvironment();
		envController.addEnvironment("TestingApplication", "ExtendsDefault", setupEnvironment.getId().toString())
		
		when:
		envController.deleteEnvironment("TestingApplication", "Default")
		
		then:
		def e = thrown(IllegalStateException)	// Node4j throws this - same txn
		e.getMessage() == "Environment ExtendsDefault is extending Default.  Cannot delete Default."
		
	}
	
	private Environment setupEnvironment() {
		def newApplication = appController.addApplication("TestingApplication");
		newApplication.getEnvironments().iterator().next()
	}
}
