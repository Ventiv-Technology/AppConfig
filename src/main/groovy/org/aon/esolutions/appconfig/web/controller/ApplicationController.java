package org.aon.esolutions.appconfig.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/application")
public class ApplicationController {
	
	@RequestMapping(value= "/{applicationName}", method = RequestMethod.PUT)
	public void addApplication(@PathVariable String applicationName) {
		System.out.println(applicationName);
	}

}
