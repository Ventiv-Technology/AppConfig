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
package org.aon.esolutions.appconfig.security.model

import org.apache.commons.lang.ObjectUtils;
import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.Indexed
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.support.index.IndexType;
import org.springframework.security.acls.model.ObjectIdentity

@NodeEntity
class Neo4jObjectIdentity implements ObjectIdentity {

	@GraphId	
	Long id;
	
	String type;
	
	@Indexed( indexName="domainObjectId", indexType=IndexType.SIMPLE)
	Long domainObjectId;
	
	@RelatedTo(type = "PARENT_OF")
	Set<Neo4jObjectIdentity> children;
	
	@Override
	public Serializable getIdentifier() {
		return domainObjectId;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.equals(domainObjectId, obj?.domainObjectId)
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
