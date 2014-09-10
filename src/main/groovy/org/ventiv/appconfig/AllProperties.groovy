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