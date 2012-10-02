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
package org.aon.esolutions.appconfig.client.web;

import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.aon.esolutions.appconfig.client.AppConfigClientFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * A J2EE Servlet Context Listener to load properties into the System properties when the Servlet
 * container starts up.  Looks at the following context-parm values in web.xml to load properties:
 * 
 * <ul>
 * 	<li>properties.location.property : The value of the system property to look at to get the Properties File Location</li>
 *  <li>privatekey.location.property : The value of the system property to look at to get the Private Key File Location</li>
 *  <li>application.name : Name of the application.  This is used when if the properties file location gets set to a directory</li>
 *  <li>environment.name : Name of the environment.  This is used when if the properties file location gets set to a directory</li>
 *  <li>classpath.file.location : Location of a file in the classpath to load from.  If not present, it will not load these properties</li>
 * </ul>
 *
 */
public class SystemPropertiesListener implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(SystemPropertiesListener.class);
    /**
     * Default constructor. 
     */
    public SystemPropertiesListener() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent evt) {
    	loadPropsFromSystem(evt.getServletContext());
    	loadPropsFromClasspath(evt.getServletContext());
    }
    
    private void loadPropsFromClasspath(ServletContext sc) {
    	String propertiesLocationProp = sc.getInitParameter("classpath.file.location");
    	
    	Properties systemProps = System.getProperties();
    	Properties loadedProps = new Properties();
    	ClassPathResource appConfigResource = new ClassPathResource(propertiesLocationProp);
    	if (appConfigResource.exists()) {
    		
    		try {
    			loadedProps.load(appConfigResource.getInputStream());
    			
    			for (Map.Entry<Object, Object> e : loadedProps.entrySet()) {
    	    		if (systemProps.containsKey(e.getKey()) == false)
    	    			systemProps.put(e.getKey(), e.getValue());
    	    	}
    		} catch (Exception e) {
    			logger.error("Error loading properties", e);
    		}
    	}
    }
    
    private void loadPropsFromSystem(ServletContext sc) {
    	String propertiesLocationProp = sc.getInitParameter("properties.location.property");
    	String privateKeyLocationProp = sc.getInitParameter("privatekey.location.property");
    	String applicationName = sc.getInitParameter("application.name");
    	String environmentName = sc.getInitParameter("environment.name");
    	
    	Properties props = System.getProperties();
    	String propertiesFileLocation = props.getProperty(propertiesLocationProp);
    	String privateKeyLocation = props.getProperty(privateKeyLocationProp);
    	
    	if (StringUtils.isEmpty(propertiesFileLocation)) {
    		logger.warn("Not loading properties from system property (" + propertiesLocationProp + ") as it was not provided.");
    		return;
    	}
    		
    	AppConfigClientFactory acClient = new AppConfigClientFactory();
    	acClient.setPropertiesFileName(propertiesFileLocation);
    	acClient.setPrivateKeyFileName(privateKeyLocation);
    	
    	Properties loadedProps = acClient.getAppConfigClient().loadProperties(applicationName, environmentName);
    	
    	for (Map.Entry<Object, Object> e : loadedProps.entrySet()) {
    		if (props.containsKey(e.getKey()) == false)
    			props.put(e.getKey(), e.getValue());
    	}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent evt) {
    }
	
}
