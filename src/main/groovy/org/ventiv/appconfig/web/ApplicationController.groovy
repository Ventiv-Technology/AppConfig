/*
 * Copyright (c) 2013 - 2015 Ventiv Technology
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
package org.ventiv.appconfig.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.ventiv.appconfig.exception.AlreadyExistsException
import org.ventiv.appconfig.model.Application
import org.ventiv.appconfig.model.Environment
import org.ventiv.appconfig.repository.ApplicationRepository

import javax.annotation.Resource

/**
 *
 *
 * @author John Crygier
 */
@RestController
@RequestMapping("/api/application")
class ApplicationController {

    @Resource ApplicationRepository applicationRepository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @RequestMapping(value = "/{applicationId}", method = RequestMethod.GET)
    public Application getApplication(@PathVariable String applicationId) {
        return applicationRepository.findOne(applicationId);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Application insert(@RequestBody Application application) {
        List<Application> existingByName = applicationRepository.findByName(application.getName());
        if (!existingByName.isEmpty() && existingByName[0].getId() != application.getId())
            throw new AlreadyExistsException("Application '${application.getName()}' already exists.  Please use a different name");        // TODO: i18n
        else if (existingByName.isEmpty()) {                            // TODO: Merge the two applications to make this call truly idempotent?
            // Ensure there is a default environment
            if (application.getEnvironments() == null)
                application.setEnvironments(new HashSet<>());

            if (application.getEnvironments().isEmpty()) {
                Environment defaultEnvironment = new Environment();
                defaultEnvironment.setName("Default");                  // TODO: Get this from a properties file?
                defaultEnvironment.setApplication(application);

                application.getEnvironments().add(defaultEnvironment);
            }
        }

        return applicationRepository.save(application);
    }

    @RequestMapping(value = "/{applicationId}", method = RequestMethod.POST)
    public Application update(@RequestBody Application application) {
        // TODO: Handle ID switch
        return applicationRepository.save(application);
    }

}
