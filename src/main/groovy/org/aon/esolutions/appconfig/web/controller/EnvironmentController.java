package org.aon.esolutions.appconfig.web.controller;

import org.aon.esolutions.appconfig.model.Application;
import org.aon.esolutions.appconfig.model.Environment;
import org.aon.esolutions.appconfig.repository.ApplicationRepository;
import org.aon.esolutions.appconfig.repository.EnvironmentRepository;
import org.aon.esolutions.appconfig.util.InheritanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.crygier.spring.util.web.MimeTypeViewResolver.ResponseMapping;

@Controller
@RequestMapping("/application/{applicationName}/environment")
public class EnvironmentController {
	
	@Autowired private ApplicationRepository applicationRepository;	
	@Autowired private EnvironmentRepository environmentRepository;	
	@Autowired private Neo4jTemplate template;
	@Autowired private InheritanceUtil inheritanceUtil;

	@RequestMapping(value = "/{environmentName}", method = RequestMethod.GET)
	@ResponseMapping("environmentDetails")
	public Environment getEnvironment(@PathVariable String applicationName, @PathVariable String environmentName) {
		// TODO: Push this into the Repo by writing a query
		Application app = applicationRepository.findByName(applicationName);
		template.fetch(app.getEnvironments());
		
		for (Environment anEnvironment : app.getEnvironments()) {
			if (anEnvironment.getName().equals(environmentName)) {
				RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
				
				if (attributes != null)
					attributes.setAttribute("allVariables", inheritanceUtil.getVariablesForEnvironment(anEnvironment), RequestAttributes.SCOPE_REQUEST);
					
				return anEnvironment;
			}
		}
		
		return null;
	}
	
	@RequestMapping(value = "/{environmentName}", method = RequestMethod.PUT)
	public Environment addEnvironment(@PathVariable String applicationName, @PathVariable String environmentName, @RequestParam("parentId") String parentId) {
		Application app = applicationRepository.findByName(applicationName);
		Environment parent = environmentRepository.findOne(Long.parseLong(parentId));
		
		Environment newEnv = new Environment();
		newEnv.setName(environmentName);
		newEnv.setParent(parent);
		app.addEnvironment(newEnv);
		
		return newEnv;
	}
	
	@RequestMapping(value = "/{environmentName}/variable/{existingKey}", method = RequestMethod.POST)
	public void updateVariable(@PathVariable String applicationName, @PathVariable String environmentName, 
			                  @PathVariable String existingKey, @RequestParam("key") String updatedKey, @RequestParam("value") String updatedValue) {
		Environment env = getEnvironment(applicationName, environmentName);
		if (env != null) {
			env.remove(existingKey);
			env.put(updatedKey.trim(), updatedValue);
			
			environmentRepository.save(env);
		}
	}
}
