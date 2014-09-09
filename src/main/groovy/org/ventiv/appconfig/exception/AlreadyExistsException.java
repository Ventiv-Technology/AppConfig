package org.ventiv.appconfig.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author John Crygier
 */
@ResponseStatus(value= HttpStatus.CONFLICT, reason="Object already Exists")
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }

}
