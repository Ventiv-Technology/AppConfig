package org.ventiv.appconfig.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.ventiv.appconfig.model.Application;

import java.util.List;

/**
 * @author John Crygier
 */
@RepositoryRestResource(collectionResourceRel = "application", path = "application")
public interface ApplicationRepository extends CrudRepository<Application, Long> {

    List<Application> findByName(@Param("name") String name);

}
