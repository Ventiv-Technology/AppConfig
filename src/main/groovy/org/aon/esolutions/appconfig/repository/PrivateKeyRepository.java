package org.aon.esolutions.appconfig.repository;

import org.aon.esolutions.appconfig.model.PrivateKeyHolder;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.security.access.prepost.PostAuthorize;

public interface PrivateKeyRepository extends GraphRepository<PrivateKeyHolder> {

	@Override
	@PostAuthorize("hasPermission(returnObject, 'READ')")
	public PrivateKeyHolder findOne(Long id);
}
