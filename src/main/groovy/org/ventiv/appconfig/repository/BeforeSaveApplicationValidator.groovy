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
