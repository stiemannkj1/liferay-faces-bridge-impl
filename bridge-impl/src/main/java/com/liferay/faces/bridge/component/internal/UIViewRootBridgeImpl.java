/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.component.internal;

import com.liferay.faces.util.application.ResourceUtil;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.portlet.faces.BridgeUtil;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIOutput;


/**
 * This class overrides the behavior of the {@link PortletNamingContainerUIViewRoot} standard class.
 *
 * @author  Neil Griffin
 */
public class UIViewRootBridgeImpl extends PortletNamingContainerUIViewRoot {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(UIViewRootBridgeImpl.class);

	// serialVersionUID
	private static final long serialVersionUID = 1523062041951774729L;

	// Private Constants
	public static final String JSF_JS_RESOURCE_ID = ResourceUtil.getResourceId(ResourceHandler.JSF_SCRIPT_LIBRARY_NAME,
		ResourceHandler.JSF_SCRIPT_RESOURCE_NAME);

	/**
	 * <p>This method fixes a problem with {@link UIComponent#findComponent(String)} where sometimes it is unable to
	 * find components due to incorrect clientId values. For more info, see the following issues:
	 *
	 * <ul>
	 *   <li>http://issues.liferay.com/browse/FACES-198</li>
	 *   <li>http://jira.icesoft.org/browse/ICE-6659</li>
	 *   <li>http://jira.icesoft.org/browse/ICE-6667</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public void setId(String id) {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (BridgeUtil.isPortletRequest(facesContext)) {
			super.setId(getContainerClientId(facesContext));
		}
	}

	@Override
	public void addComponentResource(FacesContext facesContext, UIComponent componentResource, String target) {

		super.addComponentResource(facesContext, componentResource, target);
		addBridgeJSIfNecessary(facesContext, componentResource, target);
	}

	@Override
	public void addComponentResource(FacesContext facesContext, UIComponent componentResource) {
		super.addComponentResource(facesContext, componentResource);
		addBridgeJSIfNecessary(facesContext, componentResource, null);
	}

	private void addBridgeJSIfNecessary(FacesContext facesContext, UIComponent componentResource, String target) {

		// Ignore RenderKitUtils.renderJsfJsIfNecessary(context); use case for now.
		// https://github.com/javaserverfaces/mojarra/blob/master/jsf-ri/src/main/java/com/sun/faces/renderkit/RenderKitUtils.java#L1186
		if (JSF_JS_RESOURCE_ID.equals(ResourceUtil.getResourceId(componentResource))) {

			UIOutput bridgeJS = new UIOutput();
			bridgeJS.setRendererType("javax.faces.resource.Script");
			bridgeJS.getAttributes().put("name", "bridge.js");
			bridgeJS.getAttributes().put("library", "liferay-faces-bridge");
			super.addComponentResource(facesContext, bridgeJS, target);
		}
	}
}
