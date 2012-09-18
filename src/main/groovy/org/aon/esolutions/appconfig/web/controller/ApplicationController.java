package org.aon.esolutions.appconfig.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.aon.esolutions.appconfig.model.Application;
import org.aon.esolutions.appconfig.model.Environment;
import org.aon.esolutions.appconfig.repository.ApplicationRepository;
import org.neo4j.helpers.collection.ClosableIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.crygier.spring.util.web.MimeTypeViewResolver.ResponseMapping;

@Controller
@RequestMapping("/application")
public class ApplicationController {
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	@Autowired
	private Neo4jTemplate template;
	
	@RequestMapping(value= "/", method = RequestMethod.GET)
	public List<Application> getAllApplications() {
		List<Application> answer = new ArrayList<Application>();
		ClosableIterable<Application> searchResults = applicationRepository.findAll();
		
		for (Application anApp : searchResults) {
			answer.add(anApp);
		}
		
		searchResults.close();		
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
		template.fetch(answer.getEnvironments());
		
		return answer;
	}
	
	@RequestMapping(value= "/{applicationName}", method = RequestMethod.PUT)
	public Application addApplication(@PathVariable String applicationName) {
		Application app = new Application();
		app.setName(applicationName);
		
		// Create the default environment
		Environment defaultEnv = new Environment();
		defaultEnv.setName("Default");
		app.addEnvironment(defaultEnv);		
		
		return applicationRepository.save(app);
	}

}
