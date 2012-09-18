package org.aon.esolutions.appconfig.model

class Variable {
	String key;
	String value;
	Environment inheritedFrom;
	Environment overrides;
	String overrideValue;
}
