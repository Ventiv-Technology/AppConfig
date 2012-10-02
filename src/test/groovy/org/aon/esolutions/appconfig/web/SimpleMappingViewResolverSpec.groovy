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
package org.aon.esolutions.appconfig.web

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.aon.esolutions.appconfig.web.controller.ApplicationController
import org.aon.esolutions.appconfig.web.controller.EnvironmentController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import spock.lang.Specification

@ContextConfiguration(locations = ["/spring/applicationContext.xml", "/spring/applicationContext-data.xml", "/spring/test/dispatcher-servlet.xml"])
class SimpleMappingViewResolverSpec extends Specification {
	
	@Autowired private EnvironmentController envController;
	@Autowired private ApplicationController appController;
	@Autowired private SimpleMappingViewResolver viewResolver;
	
	private OutputStream outputStream;
	private StringWriter w;
	
	static {
		System.setProperty("data.provider", "neo4j");
		System.setProperty("data.neo4j.configuration", "local-test");
	}
	
	def setup() {
		w = new StringWriter()
		outputStream = [ write: { b, off, len -> w.write((char[]) b, off, len) } ] as ServletOutputStream
	}
	
	@Transactional
	def "render an environment"() {
		given:
		def application = appController.addApplication("TestingApplication");
		def defaultEnv = application.getEnvironments().iterator().next()
		defaultEnv.put("testing.variable.1", "testing.value.1")
		defaultEnv.put("testing.variable.2", "testing.value.2")
		defaultEnv.put("testing.variable.3", "testing.value.3")
		
		when:
		viewResolver.resolveViewName("whoCares", Locale.getDefault()).render(["environment" : defaultEnv, "org.springframework.bindingresult" : "test"], getMockedRequest(), getMockedResponse())
		
		then:
		w.toString().length() == 108
		w.toString().contains("testing.variable.1=testing.value.1")
		w.toString().contains("testing.variable.2=testing.value.2")
		w.toString().contains("testing.variable.3=testing.value.3")
	}
	
	@Transactional
	def "render keys"() {
		given:
		def application = appController.addApplication("TestingApplication");
		def defaultEnv = application.getEnvironments().iterator().next()
		def keyMap = envController.getKeys("TestingApplication", "Default");
		
		when:
		viewResolver.resolveViewName("whoCares", Locale.getDefault()).render(keyMap, getMockedRequest(), getMockedResponse())
		
		then:
		w.toString().length() > 1050 && w.toString().length() < 1100
		w.toString().contains("public=")
		w.toString().contains("private=")
	}
	
	def getMockedResponse() {
		return [getOutputStream : { this.outputStream }] as HttpServletResponse
	}
	
	def getMockedRequest() {
		return [getParameter : { "false" }] as HttpServletRequest
	}
}
