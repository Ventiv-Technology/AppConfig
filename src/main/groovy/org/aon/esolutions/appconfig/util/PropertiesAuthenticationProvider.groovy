package org.aon.esolutions.appconfig.util

import org.apache.commons.lang.StringUtils
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authentication.encoding.Md4PasswordEncoder
import org.springframework.security.authentication.encoding.Md5PasswordEncoder
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException


public class PropertiesAuthenticationProvider extends DaoAuthenticationProvider {
	
	public PropertiesAuthenticationProvider() {
		setUserDetailsService(new PropertiesUserDetailsService());
		
		String hashAlgorithm = System.getProperty("security.properties.hash");
		if ("MD5".equalsIgnoreCase(hashAlgorithm))
			setPasswordEncoder(new Md5PasswordEncoder());
		else if ("MD4".equalsIgnoreCase(hashAlgorithm))
			setPasswordEncoder(new Md4PasswordEncoder());
		else if ("SHA".equalsIgnoreCase(hashAlgorithm))
			setPasswordEncoder(new ShaPasswordEncoder());
		else if ("PLAINTEXT".equalsIgnoreCase(hashAlgorithm))
			setPasswordEncoder(new PlaintextPasswordEncoder());
	}
	
	private class PropertiesUserDetailsService implements UserDetailsService {

		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			String userPassword = System.getProperty("security.properties.user.${username}.password");
			String userAuthorities = System.getProperty("security.properties.user.${username}.authorities");
			
			if (StringUtils.isEmpty(userPassword))
				throw new UsernameNotFoundException("$username not found in properties files");

			List<GrantedAuthority> grantedAuthorities = [];
			userAuthorities.split(',').each {
				grantedAuthorities << new SimpleGrantedAuthority(it.trim());
			}
								
			new User(username, userPassword, grantedAuthorities);
		}
		
	}
}