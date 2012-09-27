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
package org.aon.esolutions.appconfig.client;

import java.util.Properties;

public interface AppConfigClient {
	/**
	 * Reads the properties that are backed by this service.  Could be a remote read, or file system
	 * read, depending on the implementation.
	 * 
	 * @return
	 */
	public Properties loadProperties(String applicationName, String environmentName);
}
