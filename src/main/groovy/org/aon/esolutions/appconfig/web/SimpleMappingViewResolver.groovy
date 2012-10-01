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
package org.aon.esolutions.appconfig.web

import java.util.Map;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.Variable
import org.aon.esolutions.appconfig.util.InheritanceUtil
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.View
import org.springframework.web.servlet.ViewResolver

class SimpleMappingViewResolver implements ViewResolver {
	
	String contentType;
	String documentBegin = ""
	String documentEnd = ""
	String mappingCharacter = "="
	String propertySeparator = "\r\n"
	
	private View view = new SimpleMappingView();
	@Autowired private InheritanceUtil inheritanceUtil;

	@Override
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		return view;
	}
	
	private class SimpleMappingView implements View {
	
		@Override
		public String getContentType() {
			return contentType;
		}
	
		@Override
		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			if (StringUtils.isNotEmpty(documentBegin))
				response.getOutputStream() << documentBegin + "\r\n";
			
			model.each { modelName, modelValue ->
				if (modelValue instanceof Environment)
					renderEnvironment(modelValue, request, response)
				else if (modelValue != null && modelName.startsWith("org.springframework") == false)	// Don't print spring injected variables
					response.getOutputStream() << "${modelName}${mappingCharacter}${modelValue}${propertySeparator}"
			}
			
			if (StringUtils.isNotEmpty(documentEnd))
				response.getOutputStream() << documentEnd + "\r\n";
		}
	
		private void renderEnvironment(Environment env, HttpServletRequest request, HttpServletResponse response) throws Exception {
			boolean decrypt = "true".equalsIgnoreCase(request.getParameter("decrypt"));			
			Collection<Variable> variables = inheritanceUtil.getVariablesForEnvironment(env, decrypt);
			
			variables.each {
				if (it.value)
					response.getOutputStream() << "${it.key}${mappingCharacter}${it.value}${propertySeparator}"
				else
					response.getOutputStream() << "${it.key}${mappingCharacter}${propertySeparator}"
			}
		}
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDocumentBegin(String documentBegin) {
		this.documentBegin = documentBegin;
	}

	public void setDocumentEnd(String documentEnd) {
		this.documentEnd = documentEnd;
	}

	public void setMappingCharacter(String mappingCharacter) {
		this.mappingCharacter = mappingCharacter;
	}

	public void setPropertySeparator(String propertySeparator) {
		this.propertySeparator = propertySeparator;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void setInheritanceUtil(InheritanceUtil inheritanceUtil) {
		this.inheritanceUtil = inheritanceUtil;
	}
}
