package org.ventiv.appconfig.repository;

import org.springframework.data.repository.CrudRepository;
import org.ventiv.appconfig.model.Environment;

/**
 * @author John Crygier
 */
public interface EnvironmentRepository extends CrudRepository<Environment, Long> {
}
