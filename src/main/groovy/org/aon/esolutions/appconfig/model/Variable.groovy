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
