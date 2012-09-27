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

import org.aon.esolutions.appconfig.client.local.LocalAppConfigClient;

import spock.lang.Specification;
import spock.lang.Unroll;

class AbstractAppConfigClientSpec extends Specification {

	def "get properties from remote but private key from file - requires running server w/ gold data"() {
		given:
		def client = new HttpAppConfigClient("http://localhost:8080/AppConfig", new File("src/test/resources/Test/VisibleToAll.private.key"));
		
		when:
		def loadedProperties = client.loadProperties("Test", "VisibleToAll")
		
		then:
		loadedProperties
		loadedProperties.size() == 2
		loadedProperties["database.password"] == "[UNAUTHORIZED]"
	}
	
	@Unroll("Get Properties from #propertiesLocation and PrivateKey from #privateKeyLocation")
	def "get all from local"() {
		given:
		def client = new LocalAppConfigClient(new File(propertiesLocation), new File(privateKeyLocation));
		def loadedProperties = client.loadProperties("Test", "VisibleToAll")
		
		expect:		
		loadedProperties.size() == 2
		loadedProperties["database.password"] == "secret"
		loadedProperties["database.url"] == "http://localhost/visibletoall"
		
		where:
		propertiesLocation										| privateKeyLocation
		"src/test/resources/Test/VisibleToAll.properties"		| "src/test/resources/Test/VisibleToAll.private.key"
		"src/test/resources/Test/VisibleToAll.properties"		| "src/test/resources"
		"src/test/resources"									| "src/test/resources/Test/VisibleToAll.private.key"
		"src/test/resources"									| "src/test/resources"
	}
	
	def "all remote for override encrypted - requires running server w/ gold data"() {
		given:
		def client = new HttpAppConfigClient("http://localhost:8080/AppConfig");
		
		when:
		def loadedProperties = client.loadProperties("Test", "OverrideEncrypted")
		
		then:
		loadedProperties
		loadedProperties.size() == 2
		loadedProperties["database.password"] == "differentsecret"
	}
	
	@Unroll("Environment #environmentName has property #propertyName with value #propertyValue - authenticated with #userName / #password")
	def "all remote - requires running server w/ gold data"() {
		given:
		def client = new HttpAppConfigClient("http://localhost:8080/AppConfig", userName, password);
		def loadedProperties = client.loadProperties("Test", environmentName)
		
		expect:
		loadedProperties[propertyName] == propertyValue
		
		where:
		environmentName			| userName	| password	| 	propertyName 		| 	propertyValue
		"Default"				| "user"	| "user"	|	"database.password"	|	"secret"
		"Default"				| "user"	| "user"	|	"database.url"		|	"http://localhost/test"
		"Unauthenticated"		| "user"	| "user"	|	"database.password"	|	"secret"
		"Unauthenticated"		| "user"	| "user"	|	"database.url"		|	"http://localhost/unauthenticated"
		"VisibleToAll"			| "user"	| "user"	|	"database.url"		|	"http://localhost/visibletoall"
		"OverrideEncrypted"		| "user"	| "user"	|	"database.password"	|	"differentsecret"
		"OverrideEncrypted"		| "user"	| "user"	|	"database.url"		|	"http://localhost/test"
		
		"Default"				| null		| null		|	"database.password"	|	null
		"Default"				| null		| null		|	"database.url"		|	null
		"Unauthenticated"		| null		| null		|	"database.password"	|	"[UNAUTHORIZED]"
		"Unauthenticated"		| null		| null		|	"database.url"		|	"http://localhost/unauthenticated"
		"VisibleToAll"			| null		| null		|	"database.url"		|	"http://localhost/visibletoall"
		"OverrideEncrypted"		| null		| null		|	"database.password"	|	"differentsecret"
		"OverrideEncrypted"		| null		| null		|	"database.url"		|	"[UNAUTHORIZED]"
		
		"Default"				| "admin"	| "admin"	|	"database.password"	|	"secret"
		"Default"				| "admin"	| "admin"	|	"database.url"		|	"http://localhost/test"
		"Unauthenticated"		| "admin"	| "admin"	|	"database.password"	|	"secret"
		"Unauthenticated"		| "admin"	| "admin"	|	"database.url"		|	"http://localhost/unauthenticated"
		"VisibleToAll"			| "admin"	| "admin"	|	"database.password"	|	"secret"
		"VisibleToAll"			| "admin"	| "admin"	|	"database.url"		|	"http://localhost/visibletoall"
		"OverrideEncrypted"		| "admin"	| "admin"	|	"database.password"	|	"differentsecret"
		"OverrideEncrypted"		| "admin"	| "admin"	|	"database.url"		|	"http://localhost/test"
		
		"Default"				| "user2"	| "user2"	|	"database.password"	|	null
		"Default"				| "user2"	| "user2"	|	"database.url"		|	null
		"Unauthenticated"		| "user2"	| "user2"	|	"database.password"	|	"[UNAUTHORIZED]"
		"Unauthenticated"		| "user2"	| "user2"	|	"database.url"		|	"http://localhost/unauthenticated"
		"VisibleToAll"			| "user2"	| "user2"	|	"database.url"		|	"http://localhost/visibletoall"
		"OverrideEncrypted"		| "user2"	| "user2"	|	"database.password"	|	"differentsecret"
		"OverrideEncrypted"		| "user2"	| "user2"	|	"database.url"		|	"[UNAUTHORIZED]"
	}
	
	@Unroll("Environment #environmentName has property #propertyName who's value is NOT #propertyValue - authenticated with #userName / #password")
	def "all remote unencryptable - requires running server w/ gold data"() {
		given:
		def client = new HttpAppConfigClient("http://localhost:8080/AppConfig", userName, password);
		def loadedProperties = client.loadProperties("Test", environmentName)
		
		expect:
		loadedProperties[propertyName] != propertyValue
		
		where:
		environmentName			| userName	| password	| 	propertyName 		| 	propertyValue
		"VisibleToAll"			| "user"	| "user"	|	"database.password"	|	"secret"
		"VisibleToAll"			| "user2"	| "user2"	|	"database.password"	|	"secret"
		"VisibleToAll"			| null		| null		|	"database.password"	|	"secret"
	}
}
