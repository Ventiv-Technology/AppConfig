/**
 * Copyright (c) 2012 Aon eSolutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.aon.esolutions.appconfig.repository;

import java.util.Set;

import org.aon.esolutions.appconfig.model.Environment;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends GraphRepository<Environment> {
	
	@Query("START env=node(*) " +
		   "MATCH (app)-[:USED_IN]-(env) " +
		   "WHERE has(env.__type__) and " +
		   "      env.__type__ = 'org.aon.esolutions.appconfig.model.Environment' and " +
		   "      env.name = {1} and " +
		   "      app.name = {0} " +
		   "return env")
	@PostAuthorize("hasPermission(returnObject, 'READ')")
	public Environment getEnvironment(String applicationName, String environmentName);
	
	@Query("START env=node(*) " +
		   "MATCH (app)-[:USED_IN]-(env) " +
		   "WHERE has(env.__type__) and " +
		   "      env.__type__ = 'org.aon.esolutions.appconfig.model.Environment' and " +
		   "      app.name = {0} " +
		   "return env")
	@PostFilter("hasPermission(filterObject, 'READ')")
	public Set<Environment> getAllEnvironmentsForApplication(String applicationName);
}
