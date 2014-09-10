package org.ventiv.appconfig.exception

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.RequestAttributes

/**
 *
 *
 * @author John Crygier
 */
@Component
public class ResponseStatusFindingErrorAttributes extends DefaultErrorAttributes {

    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";

    /**
     * Intercepts the normal method so we can 'find' the exception that we want (one with the ResponseStatus Annotation).
     *
     * @param requestAttributes
     * @param includeStackTrace
     * @return
     */
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        // Get the error Spring thinks it is
        Throwable ex = super.getError(requestAttributes);

        // Search until we find one with @ResponseStatus
        while (ex != null && ex.getClass().getAnnotation(ResponseStatus) == null && ex.getCause() != null) {
            ex = ex.getCause();
        }

        // If we found one, set the error code in the RequestAttributes
        if (ex.getClass().getAnnotation(ResponseStatus))
            requestAttributes.setAttribute("javax.servlet.error.status_code", ex.getClass().getAnnotation(ResponseStatus).value().value(), RequestAttributes.SCOPE_REQUEST);

        // Override the exception that the super class expects
        requestAttributes.setAttribute(ERROR_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);

        return super.getErrorAttributes(requestAttributes, includeStackTrace);
    }

}
