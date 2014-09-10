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
package org.ventiv.appconfig.repository

import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Service
import org.ventiv.appconfig.exception.AlreadyExistsException
import org.ventiv.appconfig.model.Application

import javax.annotation.Resource

/**
 *
 *
 * @author John Crygier
 */
@Service
@RepositoryEventHandler
class BeforeSaveApplicationValidator {

    @Resource ApplicationRepository applicationRepository;

    @HandleBeforeCreate(Application)
    @HandleBeforeSave(Application)
    public void handleApplicationSave(Application application) {
        if (applicationRepository.findByName(application.getName()))
            throw new AlreadyExistsException("Application '${application.getName()}' already exists.  Please use a different name");
    }

}
