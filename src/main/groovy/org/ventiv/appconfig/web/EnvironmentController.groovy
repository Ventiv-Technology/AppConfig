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

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.ventiv.appconfig.exception.NotFoundException
import org.ventiv.appconfig.model.Environment
import org.ventiv.appconfig.model.InheritanceType
import org.ventiv.appconfig.model.Property
import org.ventiv.appconfig.model.PropertyGroup
import org.ventiv.appconfig.repository.ApplicationRepository

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
                        // Set the InheritanceType to Overridden or OverriddenUnchanged
                        if (foundProperty.getValue() == currentProp.getValue())
                            foundProperty.setInheritanceType(InheritanceType.OverriddenUnchanged);
                        else
                            foundProperty.setInheritanceType(InheritanceType.Overridden);
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

                        groupToAddTo.addProperty((Property) currentProp.clone()).setInheritanceType(InheritanceType.Inherited);
                    }
                }
            }
        }

        // Sort (now that we may have new properties)
        env.setPropertyGroups(env.getPropertyGroups().sort() as LinkedHashSet);
        env.getPropertyGroups().each {
            it.setAllProperties(it.getAllProperties().sort() as ArrayList);
        }

        return env;
    }

    private List<Environment> getEnvironmentInheritanceList(Environment env) {
        if (env)
            return getEnvironmentInheritanceList(env.getParent()) + [env]
        else
            return []
    }

}
