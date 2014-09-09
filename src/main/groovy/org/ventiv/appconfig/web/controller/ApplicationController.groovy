package org.ventiv.appconfig.web.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.ventiv.appconfig.exception.AlreadyExistsException
import org.ventiv.appconfig.model.Application
import org.ventiv.appconfig.repository.ApplicationRepository

import javax.annotation.Resource

/**
 *
 *
 * @author John Crygier
 */
@RestController
@RequestMapping("/api")
class ApplicationController {

    @Resource ApplicationRepository applicationRepository;

    @RequestMapping(value= "/", method = RequestMethod.GET)
    public Iterable<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @RequestMapping(value= "/{applicationName}", method = RequestMethod.GET)
    public Application getApplicationDetail(@PathVariable String applicationName) {
        List<Application> applicationsByName = applicationRepository.findByName(applicationName);

        if (applicationsByName)
            return applicationsByName.first();
        else
            return null;
    }

    @RequestMapping(value= "/{applicationName}", method = RequestMethod.PUT)
    public Application addApplication(@PathVariable String applicationName) throws Exception {
        if (getApplicationDetail(applicationName) != null) {
            throw new AlreadyExistsException("Application " + applicationName + " already exists.");
        }

        Application app = new Application();
        app.setName(applicationName);
        app = applicationRepository.save(app);

        //app.addEnvironment(environmentController.addEnvironment(applicationName, "Default", null));

        return app;
    }

}
