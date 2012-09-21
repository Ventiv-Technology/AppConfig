package org.aon.esolutions.appconfig.model

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
class PrivateKeyHolder {
	
	@GraphId
	Long id;
	
	String privateKey;
}
