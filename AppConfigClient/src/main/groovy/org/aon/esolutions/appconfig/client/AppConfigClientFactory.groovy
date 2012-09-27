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

import org.aon.esolutions.appconfig.client.http.HttpAppConfigClient
import org.aon.esolutions.appconfig.client.local.LocalAppConfigClient

/**
 * Factory class to create an instance of an AppConfigClient.  The intended use is via spring (to inject
 * the configuration) or to create based off of System Properties.  It's also possible to configure
 * this object itself by using the setters.
 * <br><br>
 * The following are valid System Properties (Set these with -D on the command line - or pass via spring)
 * <ul>
 * 	<li>appconfig.username : User Name for authentication against remote server</li>
 *  <li>appconfig.password : Password for authentication against remote server</li>
 *  <li>appconfig.serverurl : Remote server URL</li>
 *  <li>appconfig.propertiesfilename : Local location of properties file</li>
 *  <li>appconfig.privatekeyfilename : Local location of Private Key (For decryption)</li>
 * </ul>
 * Valid Configurations:
 * <ul>
 *  <li>Properties File AND Private Key File : Reads everything locally, no remote connection needed</li>
 *  <li>Server URL AND Private Key File : Uses remote connection for getting properties, and reads the private key locally.  This is helpful if you don't want to use a username / password in filesystems.</li>
 *  <li>Server URL : Uses remote connection for getting properies and private key for decryption.</li>
 * </ul> 
 * NOTE: If going against a Server URL, Username and Password are optional, but could be required depending how your environments are set up on the server.  For example, if no one owns an
 * environment, no user / password is required.  If there is an "owner" and the environment is "visible to all" then not supplying the owner's credentials will result in a scenario where you
 * will receive encrypted variables.  If there is an "owner" and the environment is not visible, then errors could result.
 * <br><br>
 * NOTE on File Names: You can supply the exact path, or a parent path that will follow this convention:
 * <ul>
 * 	<li>Properties File : ${given path}/${application name}/${environment name}.properties</li>
 *  <li>Private Key File : ${given path}/${application name}/${environment name}.private.key</li>
 * </ul>
 * 
 * @author John Crygier
 */
class AppConfigClientFactory {

	String userName;
	String password;
	String serverUrl;
	String propertiesFileName;
	String privateKeyFileName;
	
	public AppConfigClient getAppConfigClient() {
		// Priority One - Lets try with Properties / PK Files
		if (getPropertiesFile()?.exists() && getPrivateKeyFile()?.exists()) {
			return new LocalAppConfigClient(getPropertiesFile(), getPrivateKeyFile());
		}
		// Take Two...Let's try getting the Properties remotely, and the PK locally
		else if (getServerUrl() != null && getPrivateKeyFile()?.exists()) {
			return new HttpAppConfigClient(getServerUrl(), getPrivateKeyFile(), getUserName(), getPassword());
		}
		// Take Three...Everything Remote!
		else if (getServerUrl() != null) {
			return new HttpAppConfigClient(getServerUrl(), getUserName(), getPassword());
		}
		else {
			throw new IllegalArgumentException("Sorry, AppConfigClientFactory is incorrectly configured.  Please read the JavaDoc on minimum requirements")
		}
	}
	
	public String getUserName() {
		return getVariable(userName, "appconfig.username");
	}
	
	public String getPassword() {
		return getVariable(password, "appconfig.password");
	}
	
	public String getServerUrl() {
		return getVariable(serverUrl, "appconfig.serverurl");
	}
	
	public File getPropertiesFile() {
		if (getVariable(propertiesFileName, "appconfig.propertiesfilename"))
			return new File(getVariable(propertiesFileName, "appconfig.propertiesfilename"));
		else
			return null;
	}
	
	public File getPrivateKeyFile() {
		if (getVariable(privateKeyFileName, "appconfig.privatekeyfilename"))
			return new File(getVariable(privateKeyFileName, "appconfig.privatekeyfilename"));
		else
			return null;
	}
	
	private getVariable(String variableValue, String systemProperty) {
		if (variableValue == null) {
			variableValue = System.getProperty(systemProperty);
		}
		
		return variableValue;
	}
}
