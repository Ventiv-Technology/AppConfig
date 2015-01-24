/*
 * Copyright (c) 2013 - 2015 Ventiv Technology
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
package org.ventiv.appconfig

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * All of the properties that will be handled in the system.
 *
 * @author John Crygier
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class AllProperties {

    Ldap ldap;

    public static class Ldap {
        Server server;
        Entity user;
        Entity group;

        public static class Server {
            String url;
            String ldif;                // An LDIF resource - classpath:test-server.ldif
            Manager manager;

            public static class Manager {
                String dn;
                String password;
            }
        }

        public static class Entity {
            LdapSearch search;

            public static class LdapSearch {
                String base;
                String filter;
            }
        }
    }

}