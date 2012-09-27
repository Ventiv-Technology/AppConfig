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
package org.aon.esolutions.appconfig.client.local

import org.aon.esolutions.appconfig.client.AbstractAppConfigClient

/**
 * File System Client for the AppConfig application.  This client will always get the properties and private
 * key from the local server
 *
 * @author John Crygier
 */
class LocalAppConfigClient extends AbstractAppConfigClient {

	private File propertiesFile;
	private File privateKeyFile;
	
	/**
	 * Construct the LocalAppConfigClient to get properties from a file.  It will read
	 * the private key locally
	 *
	 * @param propertiesFile Location of the properties key, stored on the file system.  If a directory, it assumes the file is under <propertiesFile>/<applicationName>/<environmentName>.properties
	 * @param privateKeyFile Location of the private key, stored on the file system.  If a directory, it assumes the file is under <propertiesFile>/<applicationName>/<environmentName>.private.key
	 */
	public LocalAppConfigClient(File propertiesFile, File privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
		this.propertiesFile = propertiesFile;
	}	

	@Override
	protected String loadPrivateKey(String applicationName, String environmentName) {
		if (privateKeyFile.isDirectory())
			return new File(privateKeyFile, "${applicationName}/${environmentName}.private.key").text
		else
			return privateKeyFile.text
	}

	@Override
	protected String loadRawProperties(String applicationName, String environmentName) {
		if (propertiesFile.isDirectory())
			return new File(propertiesFile, "${applicationName}/${environmentName}.properties").text
		else
			return propertiesFile.text
	}
	

}
