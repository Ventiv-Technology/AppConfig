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
package org.aon.esolutions.appconfig.client.util

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import org.apache.commons.codec.binary.Base64
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class HttpUtil {
	private static final Log logger = LogFactory.getLog(HttpUtil.class);
	
	public static String getTextFromRemote(String uri, String userName, String password) {
		String answer;
		
		def auth = new String(Base64.encodeBase64("$userName:$password".getBytes()))
		
		def appConfig = new HTTPBuilder(uri);
		
		appConfig.request (Method.GET, ContentType.TEXT)  {
			if (userName)
				headers.'Authorization' = "Basic $auth"
				
			response.success = { resp, text ->
				answer = text.text;
			}
			
			response.failure = { resp ->
				logger.warn("Got Error Code ${resp.status} calling URI: $uri with user $userName")
				answer = ""
			}
		}
		
		return answer;
	}
	
	public static Properties loadPropertiesFromRemote(String uri, String userName, String password) {
		def answer = new Properties()
		answer.load(new StringReader(getTextFromRemote(uri, userName, password)));
		
		return answer;
	}
}
