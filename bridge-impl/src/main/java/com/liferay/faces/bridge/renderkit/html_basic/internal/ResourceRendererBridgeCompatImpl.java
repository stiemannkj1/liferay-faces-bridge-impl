/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.RendererWrapper;

import com.liferay.faces.util.application.ResourceUtil;


/**
 * @author  Kyle Stiemann
 */
public abstract class ResourceRendererBridgeCompatImpl extends RendererWrapper {

	// Private Constants
	private static final String JSF_JS_RESOURCE_ID = ResourceUtil.getResourceId(ResourceHandler.JSF_SCRIPT_LIBRARY_NAME,
			ResourceHandler.JSF_SCRIPT_RESOURCE_NAME);

	protected void addBridgeAttributes(FacesContext facesContext, UIComponent uiComponentResource) {

		String resourceId = ResourceUtil.getResourceId(uiComponentResource);
		boolean resourceIdNotEmpty = (resourceId != null) && !"".equals(resourceId);

		if (resourceIdNotEmpty) {
			uiComponentResource.getPassThroughAttributes().put("data-liferay-faces-bridge-resource-id", resourceId);
		}

		if (JSF_JS_RESOURCE_ID.equals(ResourceUtil.getResourceId(uiComponentResource))) {

			Application application = facesContext.getApplication();
			ProjectStage projectStage = application.getProjectStage();
			String projectStageString = projectStage.toString();
			uiComponentResource.getPassThroughAttributes().put("data-jsf-project-stage", projectStageString);
		}
	}
}
