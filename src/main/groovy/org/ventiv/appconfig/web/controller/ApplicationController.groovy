/*
 * Copyright (c) 2014 Ventiv Technology
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
package org.ventiv.appconfig.web.controller

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
