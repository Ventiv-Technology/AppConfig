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
package org.aon.esolutions.appconfig.security

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.acls.domain.GrantedAuthoritySid
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl
import org.springframework.security.acls.model.Sid
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.xbill.DNS.DClass
import org.xbill.DNS.ExtendedResolver
import org.xbill.DNS.Message
import org.xbill.DNS.Name
import org.xbill.DNS.Record
import org.xbill.DNS.Resolver
import org.xbill.DNS.ReverseMap
import org.xbill.DNS.Section
import org.xbill.DNS.Type

class MachineSidRetrievalStrategy extends SidRetrievalStrategyImpl {
	private static final Log logger = LogFactory.getLog(MachineSidRetrievalStrategy.class);
	
	public static long CACHE_FLUSH_TIME = 60 * 60 * 1000; // 60 Seconds
	private Map<String, String> cachedDnsLookups = [:]
	private long lastCacheTime = 0;
	
	@Override
	public List<Sid> getSids(Authentication authentication) {
		List<Sid> answer = super.getSids(authentication);
		
		if (authentication.getDetails() instanceof WebAuthenticationDetails) {
			String remoteAddress = authentication.getDetails().getRemoteAddress();
			answer.add(new GrantedAuthoritySid("MACHINE_${remoteAddress}"));
			String lookedUpAddress = lookupDnsFromCache(remoteAddress);
			if (remoteAddress.equals(lookedUpAddress) == false) {
				if (lookedUpAddress.endsWith("."))
					lookedUpAddress = lookedUpAddress.substring(0, lookedUpAddress.length() - 1)
					
				answer.add(new GrantedAuthoritySid("MACHINE_${lookedUpAddress}"));
			}
		}
		
		return answer;
	}
	
	public String lookupDnsFromCache(String hostIp) {
		if (lastCacheTime + CACHE_FLUSH_TIME < System.currentTimeMillis()) {
			cachedDnsLookups.clear();
			lastCacheTime = System.currentTimeMillis();
		}
		
		if (cachedDnsLookups.containsKey(hostIp) == false)
			cachedDnsLookups.put(hostIp, dnsLookup(hostIp));
			
		cachedDnsLookups[hostIp];		
	}
	
	public String dnsLookup(String hostIp) {
		Record opt = null;
		Resolver res = new ExtendedResolver();

		Name name = ReverseMap.fromAddress(hostIp);
		Record rec = Record.newRecord(name, Type.PTR, DClass.IN);
		
		Message query = Message.newQuery(rec);
		Message response = res.send(query);

		Record[] answers = response.getSectionArray(Section.ANSWER);
		if (answers.length == 0)
		   return hostIp;
		else
		   return answers[0].rdataToString();
	}
}
