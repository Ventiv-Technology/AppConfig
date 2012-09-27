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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional;

import spock.lang.Specification

@ContextConfiguration(locations = ["/spring/applicationContext.xml", "/spring/applicationContext-data.xml", "/spring/test/dispatcher-servlet.xml"])
class ApplicationControllerSpec extends Specification {
	
	static {
		System.setProperty("data.provider", "neo4j");
		System.setProperty("data.neo4j.configuration", "local-test");
	}

	@Autowired private ApplicationController appController;
	
	@Transactional
	def "create application with default environment"() {
		when:
		def setupApplication = appController.addApplication("TestingApplication");
		
		then:
		setupApplication
		setupApplication.getName() == "TestingApplication"
		setupApplication.getOwnerLogins() == null
		setupApplication.getOwnerRoles() == null
		setupApplication.getEnvironments() // Tests existence and population of at least 1
		setupApplication.getEnvironments().iterator().next().getName() == "Default"
	}
	
	@Transactional
	def "create and read application"() {
		when:
		def setupApplication = appController.addApplication("TestingApplication");
		def readApplication = appController.getApplicationDetail("TestingApplication")
		
		then:
		readApplication
		readApplication.getId() == setupApplication.getId()
		readApplication != setupApplication
		readApplication.getName() == "TestingApplication"
		readApplication.getOwnerLogins() == null
		readApplication.getOwnerRoles() == null
		readApplication.getEnvironments() // Tests existence and population of at least 1
		readApplication.getEnvironments().iterator().next().getName() == "Default"
	}
	
	@Transactional
	def "read all applications"() {
		when:
		appController.addApplication("TestingApplication");
		appController.addApplication("TestingApplication2");
		appController.addApplication("TestingApplication3");
		def allApplications = appController.getAllApplications()
		
		
		then:
		allApplications
		allApplications.size() == 3
		allApplications.find { it.getName() == "TestingApplication" }
		allApplications.find { it.getName() == "TestingApplication2" }
		allApplications.find { it.getName() == "TestingApplication3" }
	}
}
