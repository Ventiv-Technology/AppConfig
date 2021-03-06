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
package org.aon.esolutions.appconfig.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.aon.esolutions.appconfig.model.Application;
import org.aon.esolutions.appconfig.model.Environment;
import org.aon.esolutions.appconfig.repository.ApplicationRepository;
import org.aon.esolutions.appconfig.repository.EnvironmentRepository;
import org.aon.esolutions.appconfig.repository.PrivateKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.crygier.spring.util.web.MimeTypeViewResolver.ResponseMapping;

@Controller
@RequestMapping("/application")
public class ApplicationController {
	
	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired 
	private EnvironmentRepository environmentRepository;
	@Autowired 
	private PrivateKeyRepository privateKeyRepository;

	@Autowired
	private EnvironmentController environmentController;
	
	@PostFilter("hasPermission(returnObject, 'READ')")
	@RequestMapping(value= "/", method = RequestMethod.GET)
	public List<Application> getAllApplications() {
		List<Application> answer = new ArrayList<Application>();
		EndResult<Application> searchResults = applicationRepository.findAll();
		
		for (Application anApp : searchResults) {
			answer.add(anApp);
		}
		
		return answer;
	}
	
	@RequestMapping(value= "/{applicationName}", method = RequestMethod.GET)
	@ResponseMapping("applicationDetails")
	public Application getApplicationDetail(@PathVariable String applicationName) {
		
		// Grab all apps - for the navigation - Web Only
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		
		if (attributes != null)
			attributes.setAttribute("applicationList", getAllApplications(), RequestAttributes.SCOPE_REQUEST);
		
		Application answer = applicationRepository.findByName(applicationName);
		if (answer != null)
			answer.setEnvironments(environmentController.getAllEnvironments(applicationName));	// Could use template.fetch - but this handles security
		
		return answer;
	}
	
	@RequestMapping(value= "/{applicationName}", method = RequestMethod.PUT)
	public Application addApplication(@PathVariable String applicationName) throws Exception {
		if (getApplicationDetail(applicationName) != null) {
			throw new AlreadyExistsException("Application " + applicationName + " already exists.");
		}
		
		Application app = new Application();
		app.setName(applicationName);
		app = applicationRepository.save(app);
		
		app.addEnvironment(environmentController.addEnvironment(applicationName, "Default", null));
		
		return app;
	}
	
	@RequestMapping(value= "/{applicationName}", method = RequestMethod.POST)
	public Application updateApplication(@PathVariable String applicationName, @RequestParam("name") String name) throws Exception {
		Application appDetail = getApplicationDetail(applicationName);
		if (appDetail == null)
			throw new IllegalArgumentException("Application " + applicationName + " does not exist");
		
		appDetail.setName(name);
		applicationRepository.save(appDetail);
		
		return appDetail;
	}

	@Transactional
	@RequestMapping(value= "/{applicationName}", method = RequestMethod.DELETE)
	public void deleteApplication(@PathVariable String applicationName) throws Exception {
		Application appDetail = getApplicationDetail(applicationName);
		if (appDetail == null)
			throw new IllegalArgumentException("Application " + applicationName + " does not exist");
		
		if(appDetail.getEnvironments() != null) {
			for(final Environment environment : appDetail.getEnvironments()) {
				privateKeyRepository.delete(environment.getPrivateKeyHolder());
				environmentRepository.delete(environment);
			}
		}
	
		
		applicationRepository.delete(appDetail);
	}
}
