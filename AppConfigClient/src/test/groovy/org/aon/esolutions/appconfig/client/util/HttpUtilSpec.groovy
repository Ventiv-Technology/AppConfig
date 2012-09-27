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

import spock.lang.Specification;

class HttpUtilSpec extends Specification {
	
	def "read properties (auth) - requires golden data"() {
		when:
		def propertiesStr = HttpUtil.getTextFromRemote("http://localhost:8080/AppConfig/application/Test/environment/Default", "user", "user")
		
		then:
		propertiesStr
		propertiesStr.length() > 20
	}
	
	def "load properties (auth) - requires golden data"() {
		when:
		def properties = HttpUtil.loadPropertiesFromRemote("http://localhost:8080/AppConfig/application/Test/environment/Default", "user", "user")
		
		then:
		properties.size() == 2
		properties.get("database.url") == "http://localhost/test"
		properties.get("database.password").length() > 20
	}

}
