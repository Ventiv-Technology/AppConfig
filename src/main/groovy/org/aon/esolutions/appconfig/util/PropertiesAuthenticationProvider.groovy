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