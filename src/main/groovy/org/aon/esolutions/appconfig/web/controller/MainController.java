package org.aon.esolutions.appconfig.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.crygier.spring.util.web.MimeTypeViewResolver.ResponseMapping;

@Controller
@RequestMapping("/")
public class MainController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseMapping("welcome")
	public String getApplicationsAndEnvironments() {
		return "hello";
	}
}
