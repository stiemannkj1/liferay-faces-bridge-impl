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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.faces.component.PortletNamingContainerUIViewRoot;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.component.internal.ComponentUtil;
import com.liferay.faces.bridge.context.BridgePortalContext;
import com.liferay.faces.bridge.context.HeadResponseWriter;
import com.liferay.faces.bridge.context.HeadResponseWriterFactory;
import static com.liferay.faces.bridge.renderkit.html_basic.internal.RenderKitBridgeImpl.SCRIPT_RENDERER_TYPE;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import javax.faces.component.UIOutput;
import javax.faces.render.RenderKit;
import javax.portlet.PortalContext;


/**
 * This class is a JSF renderer that is designed for use with the h:head component tag. Portlets are forbidden from
 * rendering the <head>...</head> section, which is what is done by the JSF implementation's version of this renderer.
 * This renderer avoids rendering the <head>...</head> section and instead delegates that responsibility to the portal.
 *
 * @author  Neil Griffin
 */
public class HeadRendererBridgeImpl extends Renderer {

	// Package-Private Constants
	/* package-private */ static final String RENDERER_TYPE = "javax.faces.Head";

	// Private Constants
	private static final String FIRST_FACET = "first";
	private static final String MIDDLE_FACET = "middle";
	private static final String LAST_FACET = "last";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadRendererBridgeImpl.class);

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		// no-op because Portlets are forbidden from rendering the <head>...</head> section.
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		// During an Ajax request, the head resources must be rendered in an extension element by the PartialResponseWriterBridgeImpl.
		if (!facesContext.getPartialViewContext().isAjaxRequest()) {

			List<UIComponent> uiComponentResources = getAllResources(facesContext, uiComponent);
			List<UIComponent> resourcesForAddingToHead = new ArrayList<UIComponent>();
			ExternalContext externalContext = facesContext.getExternalContext();
			PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
			PortalContext portalContext = portletRequest.getPortalContext();

			// For each resource in the ViewRoot: Determine if it should added to the <head> section of the portal page,
			// or if it should be relocated to the body (which is actually not a <body> element, but a <div> element
			// rendered by the bridge's BodyRenderer).
			for (UIComponent uiComponentResource : uiComponentResources) {

				// If the portlet container has the ability to add the resource to the <head> section of the
				// portal page, then add it to the list of resources that are to be added to the <head> section.
				if (canAddResourceToHead(portalContext, uiComponentResource)) {
					resourcesForAddingToHead.add(uiComponentResource);
				}

				// Otherwise the resource will be relocated to the <body> section.
				else if (logger.isDebugEnabled()) {

					Map<String, Object> componentResourceAttributes = uiComponentResource.getAttributes();

					logger.debug(
						"Relocating resource to body (since it was added via Ajax and is not yet present in head): name=[{0}] library=[{1}] rendererType=[{2}] value=[{3}] className=[{4}]",
						componentResourceAttributes.get("name"), componentResourceAttributes.get("library"),
						uiComponentResource.getRendererType(), ComponentUtil.getComponentValue(uiComponentResource),
						uiComponentResource.getClass().getName());
				}
			}

			if (uiComponent.getChildCount() > 0 || !resourcesForAddingToHead.isEmpty()) {

				// Save a temporary reference to the ResponseWriter provided by the FacesContext.
				ResponseWriter responseWriterBackup = facesContext.getResponseWriter();

				// Replace the ResponseWriter in the FacesContext with a HeadResponseWriter that knows how to write to
				// the <head>...</head> section of the rendered portal page.
				HeadResponseWriter headResponseWriter = (HeadResponseWriter) portletRequest.getAttribute(
						HeadResponseWriter.class.getName());

				if (headResponseWriter == null) {

					HeadResponseWriterFactory headResponseWriterFactory = (HeadResponseWriterFactory) BridgeFactoryFinder
						.getFactory(HeadResponseWriterFactory.class);
					PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
					headResponseWriter = headResponseWriterFactory.getHeadResponseWriter(responseWriterBackup,
						portletResponse);
				}

				portletRequest.setAttribute(HeadResponseWriter.class.getName(), headResponseWriter);
				facesContext.setResponseWriter(headResponseWriter);

				for (UIComponent uiComponentResource : resourcesForAddingToHead) {
					uiComponentResource.encodeAll(facesContext);
				}

				super.encodeChildren(facesContext, uiComponent);

				// Restore the temporary ResponseWriter reference.
				facesContext.setResponseWriter(responseWriterBackup);
			}
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		// no-op because Portlets are forbidden from rendering the <head>...</head> section.
	}

	protected List<UIComponent> getAllResources(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		// Build up a list of components that are intended for the <head> section of the portal page.
		PortletNamingContainerUIViewRoot uiViewRoot = (PortletNamingContainerUIViewRoot) facesContext.getViewRoot();
		List<UIComponent> uiComponentResources = new ArrayList<UIComponent>();

		// Add the list of components that are to appear first.
		List<UIComponent> firstResources = getFirstResources(facesContext, uiComponent);

		if (firstResources != null) {
			uiComponentResources.addAll(firstResources);
		}

		// Sort the components that are in the view root into stylesheets and scripts.
		List<UIComponent> uiViewRootComponentResources = uiViewRoot.getComponentResources(facesContext, "head");
		List<UIComponent> uiViewRootStyleSheetResources = null;
		List<UIComponent> uiViewRootScriptResources = null;

		for (UIComponent curComponent : uiViewRootComponentResources) {

			if (isStyleSheetResource(curComponent) ||
				isInlineStyleSheet(curComponent)) {

				if (uiViewRootStyleSheetResources == null) {
					uiViewRootStyleSheetResources = new ArrayList<UIComponent>();
				}

				uiViewRootStyleSheetResources.add(curComponent);
			}
			else {

				if (uiViewRootScriptResources == null) {
					uiViewRootScriptResources = new ArrayList<UIComponent>();
				}

				uiViewRootScriptResources.add(curComponent);
			}
		}

		// Add the list of stylesheet components that are in the view root.
		if (uiViewRootStyleSheetResources != null) {
			uiComponentResources.addAll(uiViewRootStyleSheetResources);
		}

		// Add the list of components that are to appear in the middle.
		List<UIComponent> middleResources = getMiddleResources(facesContext, uiComponent);

		if (middleResources != null) {
			uiComponentResources.addAll(middleResources);
		}

		// Add the list of script components that are in the view root.
		if (uiViewRootScriptResources != null) {
			uiComponentResources.addAll(uiViewRootScriptResources);
		}

		UIOutput bridgeJS = new UIOutput();
		Map<String, Object> attributes = bridgeJS.getAttributes();
		attributes.put("library", "liferay-faces-bridge");
		attributes.put("name", "bridge.js");
		attributes.put("rendererType", RenderKitBridgeImpl.SCRIPT_RENDERER_TYPE);

		uiComponentResources.add(bridgeJS);

		// Add the list of components that are to appear last.
		List<UIComponent> lastResources = getLastResources(facesContext, uiComponent);

		if (lastResources != null) {
			uiComponentResources.addAll(lastResources);
		}

		return uiComponentResources;
	}

	protected List<UIComponent> getFirstResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = null;

		UIComponent firstFacet = uiComponent.getFacet(FIRST_FACET);

		if (firstFacet != null) {
			resources = new ArrayList<UIComponent>();
			resources.add(firstFacet);
		}

		return resources;
	}

	protected List<UIComponent> getLastResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = null;

		UIComponent firstFacet = uiComponent.getFacet(LAST_FACET);

		if (firstFacet != null) {
			resources = new ArrayList<UIComponent>();
			resources.add(firstFacet);
		}

		return resources;
	}

	protected List<UIComponent> getMiddleResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> resources = null;

		UIComponent firstFacet = uiComponent.getFacet(MIDDLE_FACET);

		if (firstFacet != null) {
			resources = new ArrayList<UIComponent>();
			resources.add(firstFacet);
		}

		return resources;
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	public static List<UIComponent> getHeadResources(FacesContext facesContext, UIComponent htmlHead) throws IOException {

		RenderKit renderKit = facesContext.getRenderKit();
		HeadRendererBridgeImpl headRendererBridgeImpl = (HeadRendererBridgeImpl) renderKit.getRenderer(UIOutput.COMPONENT_FAMILY, RENDERER_TYPE);
		return headRendererBridgeImpl.getAllResources(facesContext, htmlHead);
	}

	public static boolean canAddResourceToHead(PortalContext portalContext, UIComponent componentResource) {

		boolean canAddResourceToHead = false;

		if (isStyleSheetResource(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(
				BridgePortalContext.ADD_STYLE_SHEET_RESOURCE_TO_HEAD_SUPPORT) != null;
		}
		else if (isScriptResource(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(
				BridgePortalContext.ADD_SCRIPT_RESOURCE_TO_HEAD_SUPPORT) != null;
		}
		else if (isInlineStyleSheet(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(
				BridgePortalContext.ADD_STYLE_SHEET_TEXT_TO_HEAD_SUPPORT) != null;			
		}
		else if (isInlineScript(componentResource)) {
			canAddResourceToHead = portalContext.getProperty(
				BridgePortalContext.ADD_SCRIPT_TEXT_TO_HEAD_SUPPORT) != null;			
		}

		return canAddResourceToHead;
	}

	private static boolean isScriptResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("js");
	}

	private static boolean isStyleSheetResource(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");

		return (resourceName != null) && resourceName.endsWith("css");
	}

	private static boolean isInlineScript(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String rendererType = componentResource.getRendererType();

		return (resourceName == null) && RenderKitBridgeImpl.SCRIPT_RENDERER_TYPE.equals(rendererType);
	}

	private static boolean isInlineStyleSheet(UIComponent componentResource) {

		Map<String, Object> componentResourceAttributes = componentResource.getAttributes();
		String resourceName = (String) componentResourceAttributes.get("name");
		String rendererType = componentResource.getRendererType();

		return (resourceName == null) && RenderKitBridgeImpl.STYLESHEET_RENDERER_TYPE.equals(rendererType);
	}
}
