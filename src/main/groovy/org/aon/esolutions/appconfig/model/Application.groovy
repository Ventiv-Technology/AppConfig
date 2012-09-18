package org.aon.esolutions.appconfig.model

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo

@NodeEntity
class Application {
	
	@GraphId
	Long id;
	
	@RelatedTo(type = "USED_IN")
	Set<Environment> environments;
	
	String name;
	String privateKey;
	String publicKey;
	String[] ownerRoles;
	String[] ownerLogins;
	
	public void addEnvironment(Environment env) {
		if (environments == null)
			environments = new TreeSet<Environment>()
			
		environments.add(env);
	}
}
