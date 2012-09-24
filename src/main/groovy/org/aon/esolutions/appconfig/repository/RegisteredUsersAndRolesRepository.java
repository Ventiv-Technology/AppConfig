package org.aon.esolutions.appconfig.repository;

import org.aon.esolutions.appconfig.model.RegisteredUsersAndRoles;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUsersAndRolesRepository extends GraphRepository<RegisteredUsersAndRoles> {

}
