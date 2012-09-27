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
class EnvironmentControllerSpec extends Specification {
	
	static {
		System.setProperty("data.provider", "neo4j");
		System.setProperty("data.neo4j.configuration", "local-test");
	}

	@Autowired private EnvironmentController envController;
	@Autowired private ApplicationController appController;
	
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
	
	private Environment setupEnvironment() {
		def newApplication = appController.addApplication("TestingApplication");
		newApplication.getEnvironments().iterator().next()
	}
}
