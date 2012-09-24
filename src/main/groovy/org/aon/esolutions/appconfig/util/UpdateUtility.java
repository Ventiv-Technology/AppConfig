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
package org.aon.esolutions.appconfig.util;

import org.aon.esolutions.appconfig.model.Environment;
import org.aon.esolutions.appconfig.model.PrivateKeyHolder;
import org.aon.esolutions.appconfig.repository.ApplicationRepository;
import org.aon.esolutions.appconfig.repository.EnvironmentRepository;
import org.aon.esolutions.appconfig.repository.PrivateKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UpdateUtility {

	@Autowired private ApplicationRepository applicationRepository;	
	@Autowired private EnvironmentRepository environmentRepository;
	@Autowired private PrivateKeyRepository privateKeyRepository;	
	
	/**
	 * Simple passthrough to the repository.  Needs to be in a solid class, since pre-authroize
	 * doesn't work well with interfaces that are proxied.
	 * 
	 * @param env
	 * @return
	 */
	@PreAuthorize("hasPermission(#env, 'WRITE')")
	public Environment saveEnvironment(Environment env) {
		return environmentRepository.save(env);
	}
	
	@PostAuthorize("hasPermission(returnObject, 'WRITE')")
	public Environment getEnvironmentForWrite(String applicationName, String environmentName) {
		return environmentRepository.getEnvironment(applicationName, environmentName);
	}
	
	/**
	 * Simple passthrough to the repository.  Needs to be in a solid class, since pre-authroize
	 * doesn't work well with interfaces that are proxied.
	 * 
	 * @param env
	 * @return
	 */
	@PreAuthorize("hasPermission(#holder, 'WRITE')")
	public PrivateKeyHolder savePrivateKeyHolder(PrivateKeyHolder holder) {
		return privateKeyRepository.save(holder);
	}
}
