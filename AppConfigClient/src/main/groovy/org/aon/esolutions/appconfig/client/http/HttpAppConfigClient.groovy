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
package org.aon.esolutions.appconfig.client.http

import org.aon.esolutions.appconfig.client.AbstractAppConfigClient
import org.aon.esolutions.appconfig.client.util.HttpUtil

/**
 * Http Client for the AppConfig application.  This client will always get the properties from the remote
 * server, but has the option (via constructors) to get the private key locally
 *  
 * @author John Crygier
 */
class HttpAppConfigClient extends AbstractAppConfigClient {
	
	private File privateKeyFile;
	private String remoteUrl;
	private String userName;
	private String password;
	
	/**
	 * Construct the HttpAppConfigClient to get everything remotely, and not using authentication.
	 * 
	 * @param url The Endpoint of AppConfig (i.e. http://localhost:8080/AppConfig)
	 */
	public HttpAppConfigClient(String url) {
		this.remoteUrl = url;
	}
	
	/**
	 * Construct the HttpAppConfigClient to get everything remotely, and using BASIC authentication with the
	 * supplied credentials
	 *
	 * @param url The Endpoint of AppConfig (i.e. http://localhost:8080/AppConfig)
	 * @param userName User Name for the credentials
	 * @param password Password for the credentials
	 */
	public HttpAppConfigClient(String url, String userName, String password) {
		this.remoteUrl = url;
		this.password = password;
		this.userName = userName;		
	}
	
	/**
	 * Construct the HttpAppConfigClient to get properties remotely, using no credentials.  It will read
	 * the private key locally
	 * 
	 * @param url The Endpoint of AppConfig (i.e. http://localhost:8080/AppConfig)
	 * @param privateKeyFile Location of the private key, stored on the file system
	 */
	public HttpAppConfigClient(String url, File privateKeyFile) {
		this.remoteUrl = url;	
		this.privateKeyFile = privateKeyFile;	
	}
	
	/**
	 * Construct the HttpAppConfigClient to get properties remotely, using BASIC authentication with the
	 * supplied credentials.  It will read the private key locally
	 *
	 * @param url The Endpoint of AppConfig (i.e. http://localhost:8080/AppConfig)
	 * @param privateKeyFile Location of the private key, stored on the file system
	 */
	public HttpAppConfigClient(String url, String userName, String password, File privateKeyFile) {
		this.remoteUrl = url;
		this.password = password;
		this.userName = userName;
		this.privateKeyFile = privateKeyFile;
	}
	
	@Override
	protected String loadPrivateKey(String applicationName, String environmentName) {
		if (privateKeyFile != null && privateKeyFile.exists())
			return privateKeyFile.text
		else
			return HttpUtil.loadPropertiesFromRemote("$remoteUrl/application/$applicationName/environment/$environmentName/keys", userName, password)["private"]
	}

	@Override
	protected String loadRawProperties(String applicationName, String environmentName) {
		HttpUtil.getTextFromRemote("$remoteUrl/application/$applicationName/environment/$environmentName", userName, password)
	}

}
