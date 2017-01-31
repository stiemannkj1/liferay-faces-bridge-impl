/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import com.liferay.faces.util.application.ResourceUtil;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.RendererWrapper;

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
