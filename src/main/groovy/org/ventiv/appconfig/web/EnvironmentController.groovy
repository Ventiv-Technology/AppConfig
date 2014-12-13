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
package org.ventiv.appconfig.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.ventiv.appconfig.exception.NotFoundException
import org.ventiv.appconfig.model.Application
import org.ventiv.appconfig.model.Environment
import org.ventiv.appconfig.model.InheritanceType
import org.ventiv.appconfig.model.Property
import org.ventiv.appconfig.model.PropertyGroup
import org.ventiv.appconfig.repository.ApplicationRepository
import org.ventiv.appconfig.repository.EnvironmentRepository

import javax.annotation.Resource

/**
 *
 *
 * @author John Crygier
 */
@RestController
@RequestMapping("/api/application/{applicationId}")
class EnvironmentController {

    @Resource ApplicationRepository applicationRepository;
    @Resource EnvironmentRepository environmentRepository;

    @RequestMapping(value = "/{environmentName}", method = RequestMethod.GET)
    public Environment getEnvironment(@PathVariable String applicationId, @PathVariable String environmentName) {
        Environment env = applicationRepository.findOne(applicationId)?.getEnvironments()?.find { it.getName() == environmentName };
        if (env == null)
            throw new NotFoundException();

        // Inherit properties
        List<Environment> inheritanceList = getEnvironmentInheritanceList(env);
        List<Property> currentPropertyList = inheritanceList[0].getAllProperties()
        if (inheritanceList.size() > 1) {       // No need to inherit if we're only 1 environment
            inheritanceList[1..-1].each { Environment currentEnv ->
                currentPropertyList.each { Property currentProp ->
                    // Check if it's a new property
                    Property foundProperty = currentEnv.getAllProperties().find { it.getKey() == currentProp.getKey() }
                    if (foundProperty) {
                        if (foundProperty.getInheritanceType() == InheritanceType.New) {
                            // Set the InheritanceType to Overridden or OverriddenUnchanged
                            if (foundProperty.getValue() == currentProp.getValue())
                                foundProperty.setInheritanceType(InheritanceType.OverriddenUnchanged);
                            else
                                foundProperty.setInheritanceType(InheritanceType.Overridden);
                        }
                    } else {
                        // We have a new property, see if the property group already exists, or else add it
                        PropertyGroup groupToAddTo = currentEnv.getPropertyGroups().find {
                            it.getName() == currentProp.getPropertyGroup().getName()
                        }
                        if (groupToAddTo == null) {
                            groupToAddTo = new PropertyGroup()
                            groupToAddTo.setName(currentProp.getPropertyGroup().getName());

                            currentEnv.addPropertyGroup(groupToAddTo);
                        }

                        if (currentProp.getInheritanceType() == InheritanceType.New)
                            groupToAddTo.addProperty((Property) currentProp.clone()).setInheritanceType(InheritanceType.Inherited);
                        else
                            groupToAddTo.addProperty((Property) currentProp.clone());
                    }
                }

                currentPropertyList = currentEnv.getAllProperties();
            }
        }

        // Sort (now that we may have new properties)
        env.setPropertyGroups(env.getPropertyGroups().sort() as LinkedHashSet);
        env.getPropertyGroups().each {
            it.setAllProperties(it.getAllProperties().sort() as ArrayList);
        }

        return env;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Environment> saveEnvironment(@PathVariable String applicationId, @RequestBody Environment environment) {
        HttpStatus responseCode = environment.getId() ? HttpStatus.OK :HttpStatus.CREATED;
        Application app = applicationRepository.findOne(applicationId);
        environment.setApplication(app);

        // Hydrate the parent
        environment.setParent(environmentRepository.findOne(environment.getParent().getId()));

        Environment savedEnvironment = environmentRepository.save(environment);
        return new ResponseEntity<Environment>(savedEnvironment, responseCode);
    }

    @RequestMapping(value = "/{environmentName}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<PropertyGroup> savePropertyGroup(@PathVariable String applicationId, @PathVariable String environmentName, @RequestBody PropertyGroup propertyGroup) {
        Environment env = applicationRepository.findOne(applicationId)?.getEnvironments()?.find { it.getName() == environmentName };
        if (env == null)
            throw new NotFoundException();

        propertyGroup.setEnvironment(env);
        boolean removedExisting = env.getPropertyGroups().removeAll { return it.getId() == propertyGroup.getId() }        // Remove the 'old' one, if it exists
        env.getPropertyGroups().add(propertyGroup);

        Environment savedEnv = environmentRepository.save(env);
        PropertyGroup savedPropertyGroup = savedEnv.getPropertyGroups().find { it.getId() == propertyGroup.getId() };
        if (savedPropertyGroup == null)
            savedPropertyGroup = savedEnv.getPropertyGroups().find { it.getId() == savedEnv.getPropertyGroups().collect { it.getId() }.max() }

        return new ResponseEntity<PropertyGroup>(savedPropertyGroup, removedExisting ? HttpStatus.OK :HttpStatus.CREATED);
    }


    private List<Environment> getEnvironmentInheritanceList(Environment env) {
        if (env)
            return getEnvironmentInheritanceList(env.getParent()) + [env]
        else
            return []
    }

}
