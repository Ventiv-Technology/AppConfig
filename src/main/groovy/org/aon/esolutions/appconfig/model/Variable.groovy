package org.aon.esolutions.appconfig.model

class Variable {
	String key;
	String value;
	Environment inheritedFrom;
	Environment overrides;
	String overrideValue;
	Boolean encrypted = Boolean.FALSE;
	Boolean overrideEncrypted = Boolean.FALSE;
	
	public boolean isOverridden() {
		return overrides != null;
	}
	
	public boolean isInherited() {
		return inheritedFrom != null;
	}
	
	public boolean isOwnedProperty() {
		return !isOverridden() && !isInherited();
	}
	
	public String getOverrideValueDisplay() {
		if (overrideEncrypted)
			return "[ENCRYPTED]"
		else
			return overrideValue;
	}
	
	public String getValueDisplay() {
		if (encrypted)
			return "[ENCRYPTED]"
		else
			return value;
	}
}
