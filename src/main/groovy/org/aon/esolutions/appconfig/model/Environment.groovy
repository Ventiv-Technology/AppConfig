package org.aon.esolutions.appconfig.model

import java.util.Map.Entry;

import org.springframework.data.neo4j.annotation.GraphId
import org.springframework.data.neo4j.annotation.NodeEntity
import org.springframework.data.neo4j.annotation.RelatedTo
import org.springframework.data.neo4j.fieldaccess.DynamicProperties
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer

@NodeEntity
class Environment {
	
	@GraphId
	Long id;
	
	String name;
	
	@RelatedTo(type = "INHERITS_FROM")
	Environment parent;
	
	DynamicProperties variables;
	
	public void put(String key, String value) {
		getVariables().setProperty(key, value);
	}

	public String get(String key) {
		return getVariables().getProperty(key);
	}
	
	public String remove(String key) {
		return getVariables().removeProperty(key);
	}
	
	public DynamicProperties getVariables() {
		if (variables == null)
			variables = new DynamicPropertiesContainer();
			
		return variables;
	}
	
	public Set<Entry<String, String>> getVariableEntries() {
		getVariables().asMap().entrySet();
	}
}
