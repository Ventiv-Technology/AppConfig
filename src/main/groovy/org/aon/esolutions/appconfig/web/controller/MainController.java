package org.aon.esolutions.appconfig.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.aon.esolutions.appconfig.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class MainController {
	
	@Autowired
	private ApplicationController applicationController;

	@RequestMapping(method = RequestMethod.GET)
	public void getApplicationsAndEnvironments(HttpServletResponse response) throws Exception {
		List<Application> allApps = applicationController.getAllApplications();
		if (allApps != null && allApps.isEmpty() == false)
			response.sendRedirect("application/" + allApps.iterator().next().getName());
	}
}
