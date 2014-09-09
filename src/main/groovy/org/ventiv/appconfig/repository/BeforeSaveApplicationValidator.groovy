package org.ventiv.appconfig.repository

import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.ventiv.appconfig.exception.AlreadyExistsException
import org.ventiv.appconfig.model.Application

/**
 *
 *
 * @author John Crygier
 */
@Service
@RepositoryEventHandler
class BeforeSaveApplicationValidator {

    @HandleBeforeCreate(Application)
    @HandleBeforeSave(Application)
    public void handleApplicationSave(Application p) {
        throw new AlreadyExistsException("Wassup!");
    }

}
