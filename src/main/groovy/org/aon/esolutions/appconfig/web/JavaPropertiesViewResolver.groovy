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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.aon.esolutions.appconfig.model.Environment
import org.aon.esolutions.appconfig.model.Variable
import org.aon.esolutions.appconfig.util.InheritanceUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.View
import org.springframework.web.servlet.ViewResolver

class JavaPropertiesViewResolver implements ViewResolver {
	
	private JavaPropertiesView view = new JavaPropertiesView();
	@Autowired private InheritanceUtil inheritanceUtil;

	@Override
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		return view;
	}
	
	private class JavaPropertiesView implements View {

		@Override
		public String getContentType() {
			return "text/plain";
		}

		@Override
		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			model.each { modelName, modelValue ->
				if (modelValue instanceof Environment)
					renderEnvironment(modelValue, response)
				else if (modelValue != null && modelName.startsWith("org.springframework") == false)	// Don't print spring injected variables
					response.getOutputStream() << "${modelName}=${modelValue}\r\n"
			}
		}
		
		private void renderEnvironment(Environment env, HttpServletResponse response) throws Exception {
			Collection<Variable> variables = inheritanceUtil.getVariablesForEnvironment(env);
			variables.each {
				response.getOutputStream() << "${it.key}=${it.value}\r\n"
			}
		}
		
	}

}
