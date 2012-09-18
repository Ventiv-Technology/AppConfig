package org.aon.esolutions.appconfig.repository;

import org.aon.esolutions.appconfig.model.Environment;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends GraphRepository<Environment> {

}
