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
package org.aon.esolutions.appconfig.client

import java.security.PrivateKey
import java.util.Map.Entry;

import org.aon.esolutions.appconfig.client.util.RSAEncryptUtil
import org.apache.commons.lang.StringUtils;

abstract class AbstractAppConfigClient implements AppConfigClient {
	
	/**
	 * Loads the private key for this application / environment as a string from 
	 * where ever it is located.
	 * 
	 * @param applicationName
	 * @param environmentName
	 * @return
	 */
	protected abstract String loadPrivateKey(String applicationName, String environmentName);
	
	/**
	 * Loads the raw, encrypted properties from where ever it is located.
	 * 
	 * @param applicationName
	 * @param environmentName
	 * @return
	 */
	protected abstract String loadRawProperties(String applicationName, String environmentName);
	
	@Override
	public Properties loadProperties(String applicationName, String environmentName) {
		Properties answer = new Properties();
		answer.load(new StringReader(loadRawProperties(applicationName, environmentName)));
		
		decryptProperties(answer, applicationName, environmentName);
		
		return answer;
	}
	
	private void decryptProperties(Properties props, String applicationName, String environmentName) {
		String privateKeyStr = loadPrivateKey(applicationName, environmentName)
		if (StringUtils.isNotEmpty(privateKeyStr)) {
			PrivateKey key = RSAEncryptUtil.getPrivateKeyFromString(loadPrivateKey(applicationName, environmentName));
			
			props.each { Entry<String, String> e ->
				try {
					String decrypted = RSAEncryptUtil.decrypt(e.value, key)
					e.setValue(decrypted);
				} catch (any) {
					// Probably just not encrypted...
				}
			}
		}
	}
}
