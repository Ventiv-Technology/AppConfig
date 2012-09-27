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

class HttpAppConfigClient extends AbstractAppConfigClient {
	
	private File privateKeyFile;
	private String remoteUrl;
	private String userName;
	private String password;
	
	public HttpAppConfigClient(String url) {
		this.remoteUrl = url;
	}
	
	public HttpAppConfigClient(String url, String userName, String password) {
		this.remoteUrl = url;
		this.password = password;
		this.userName = userName;		
	}
	
	public HttpAppConfigClient(String url, File privateKeyFile) {
		this.remoteUrl = url;	
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
