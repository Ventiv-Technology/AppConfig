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

import org.aon.esolutions.appconfig.client.http.HttpAppConfigClient;
import org.aon.esolutions.appconfig.client.local.LocalAppConfigClient;

import spock.lang.Specification;

class AppConfigClientFactorySpec extends Specification {
	
	def setup() {
		// Reset all System properties
		System.clearProperty("appconfig.username");
		System.clearProperty("appconfig.password");
		System.clearProperty("appconfig.serverurl");
		System.clearProperty("appconfig.propertiesfilename");
		System.clearProperty("appconfig.privatekeyfilename");
	}

	def "no setup"() {
		when:
		def factory = new AppConfigClientFactory();
		def client = factory.getAppConfigClient();
		
		then:
		thrown(IllegalArgumentException)
	}
	
	def "only properties file, missing privatekey"() {
		when:
		def factory = new AppConfigClientFactory();
		factory.setPropertiesFileName("src/test/resources")
		def client = factory.getAppConfigClient();
		
		then:
		thrown(IllegalArgumentException)
	}
	
	def "only properties file (from system), missing privatekey"() {
		when:
		System.setProperty("appconfig.propertiesfilename", "src/test/resources")
		def factory = new AppConfigClientFactory();
		def client = factory.getAppConfigClient();
		
		then:
		thrown(IllegalArgumentException)
	}
	
	def "valid local configuration"() {
		when:
		def factory = new AppConfigClientFactory();
		factory.setPropertiesFileName("src/test/resources")
		factory.setPrivateKeyFileName("src/test/resources")
		def client = factory.getAppConfigClient();
		
		then:
		client
		client instanceof LocalAppConfigClient
		client.propertiesFile == new File("src/test/resources")
		client.privateKeyFile == new File("src/test/resources")
	}
	
	def "valid local configuration - from system properties"() {
		when:
		System.setProperty("appconfig.propertiesfilename", "src/test/resources")
		System.setProperty("appconfig.privatekeyfilename", "src/test/resources")
		def factory = new AppConfigClientFactory();
		def client = factory.getAppConfigClient();
		
		then:
		client
		client instanceof LocalAppConfigClient
		client.propertiesFile == new File("src/test/resources")
		client.privateKeyFile == new File("src/test/resources")
	}
	
	def "url with private key - system properties"() {
		when:
		System.setProperty("appconfig.serverurl", "http://localhost:8080/AppConfig")
		System.setProperty("appconfig.privatekeyfilename", "src/test/resources")
		def factory = new AppConfigClientFactory();
		def client = factory.getAppConfigClient();
		
		then:
		client
		client instanceof HttpAppConfigClient
		client.privateKeyFile == new File("src/test/resources")
		client.remoteUrl == "http://localhost:8080/AppConfig"
		client.userName == null
		client.password == null
	}
	
	def "url user pass - system properties"() {
		when:
		System.setProperty("appconfig.serverurl", "http://localhost:8080/AppConfig")
		System.setProperty("appconfig.username", "user")
		System.setProperty("appconfig.password", "pass")
		def factory = new AppConfigClientFactory();
		def client = factory.getAppConfigClient();
		
		then:
		client
		client instanceof HttpAppConfigClient
		client.privateKeyFile == null
		client.remoteUrl == "http://localhost:8080/AppConfig"
		client.userName == "user"
		client.password == "pass"
	}	
	
	def "url only"() {
		when:
		def factory = new AppConfigClientFactory();
		factory.setUserName("user")
		factory.setPassword("pass")
		factory.setServerUrl("http://localhost:8080/AppConfig")
		def client = factory.getAppConfigClient();
		
		then:
		client
		client instanceof HttpAppConfigClient
		client.privateKeyFile == null
		client.remoteUrl == "http://localhost:8080/AppConfig"
		client.userName == "user"
		client.password == "pass"
	}
}
