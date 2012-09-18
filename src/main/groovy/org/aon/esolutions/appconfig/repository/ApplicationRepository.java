package org.aon.esolutions.appconfig.repository;

import org.aon.esolutions.appconfig.model.Application;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends GraphRepository<Application> {
	public Application findByName(String name);
}
