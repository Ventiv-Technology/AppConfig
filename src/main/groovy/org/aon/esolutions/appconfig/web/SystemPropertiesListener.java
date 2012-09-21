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
package org.aon.esolutions.appconfig.web;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application Lifecycle Listener implementation class SystemPropertiesListener
 *
 */
public class SystemPropertiesListener implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(SystemPropertiesListener.class);
    /**
     * Default constructor. 
     */
    public SystemPropertiesListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent evt) {
    	String systemProperty = evt.getServletContext().getInitParameter("system.properties.property");
    	Properties props = System.getProperties();
    	String fileLocation = props.getProperty(systemProperty);
    	
    	try {
			props.load(new FileReader(new File(fileLocation)));
		} catch (Exception e) {
			logger.error("Error loading properties", e);
		}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent evt) {
        // TODO Auto-generated method stub
    }
	
}
