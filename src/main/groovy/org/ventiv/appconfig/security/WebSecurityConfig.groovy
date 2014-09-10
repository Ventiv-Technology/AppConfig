package org.ventiv.appconfig.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.ventiv.appconfig.AllProperties

import javax.annotation.Resource

/**
 * @author John Crygier
 */
@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll();
        http.authorizeRequests().antMatchers("/**").permitAll();
    }

    @Configuration
    @ConditionalOnProperty(prefix = "ldap.server.", value = "url")
    protected static class LdapServerAuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Resource AllProperties props;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.ldapAuthentication()
                    .userSearchBase(props.ldap.user.search.base)
                    .userSearchFilter(props.ldap.user.search.filter)
                    .groupSearchBase(props.ldap.group.search.base)
                    .groupSearchFilter(props.ldap.group.search.filter)
                    .contextSource()
                        .url(props.ldap.server.url)
                        .managerDn(props.ldap.server.manager.dn)
                        .managerPassword(props.ldap.server.manager.password)
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "ldap.server.", value = "ldif")
    protected static class LdapFileAuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Resource AllProperties props;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.ldapAuthentication()
                    .userSearchBase(props.ldap.user.search.base)
                    .userSearchFilter(props.ldap.user.search.filter)
                    .groupSearchBase(props.ldap.group.search.base)
                    .groupSearchFilter(props.ldap.group.search.filter)
                    .contextSource().ldif(props.ldap.server.ldif);
        }
    }
}
