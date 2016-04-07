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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import javax.portlet.PortletResponse;

import com.liferay.faces.bridge.component.internal.ResourceComponent;
import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadRendererBridgeImpl;
import com.liferay.faces.bridge.util.internal.URLUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class replaces the PrimeFaces HeadRenderer because it renders a &lt;head&gt;...&lt;/head&gt; element to the
 * response writer which is forbidden in a portlet environment.
 *
 * @author  Neil Griffin
 */
public class HeadRendererPrimeFacesImpl extends HeadRendererBridgeImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(HeadRendererPrimeFacesImpl.class);

	// Private Constants
	private static final String PRIMEFACES_THEME_PREFIX = "primefaces-";
	private static final String PRIMEFACES_THEME_RESOURCE_NAME = "theme.css";
	private static final Renderer PRIMEFACES_HEAD_RENDERER;

	static {

		Renderer primeFacesHeadRenderer = null;

		try {
			Class<?> headRendererClass = Class.forName("org.primefaces.renderkit.HeadRenderer");
			primeFacesHeadRenderer = (Renderer) headRendererClass.newInstance();
		}
		catch (Exception e) {
			logger.error(e);
		}

		PRIMEFACES_HEAD_RENDERER = primeFacesHeadRenderer;
	}

	@Override
	public List<UIComponent> getAllResources(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		// Invoke the PrimeFaces HeadRenderer so that it has the opportunity to add css and/or script resources to the
		// view root. However, the PrimeFaces HeadRenderer must be captured (and thus prevented from actually rendering
		// any resources) so that they can instead be rendered by the superclass.
		FacesContext primeFacesContext = new FacesContextPrimeFacesHeadImpl(facesContext);
		ResponseWriter origResponseWriter = primeFacesContext.getResponseWriter();
		PrimeFacesHeadResponseWriter primeFacesHeadResponseWriter = new PrimeFacesHeadResponseWriter();
		primeFacesContext.setResponseWriter(primeFacesHeadResponseWriter);

		UIViewRoot originalUIViewRoot = facesContext.getViewRoot();
		ResourceCapturingUIViewRoot resourceCapturingUIViewRoot = new ResourceCapturingUIViewRoot();
		primeFacesContext.setViewRoot(resourceCapturingUIViewRoot);
		PRIMEFACES_HEAD_RENDERER.encodeBegin(primeFacesContext, uiComponent);
		primeFacesContext.setViewRoot(originalUIViewRoot);
		primeFacesContext.setResponseWriter(origResponseWriter);

		// Get the list of captured resources.
		List<UIComponent> capturedResources = resourceCapturingUIViewRoot.getCapturedComponentResources("head");

		// The PrimeFaces 5.1+ HeadRenderer properly adds resources like "validation/validation.js" to the view root,
		// which makes it possible to easily capture the resources that it wants to add to the head. However, the
		// PrimeFaces 5.0/4.0 HeadRenderer does not add resources to the view root. Instead, it encodes a <script>
		// element to the response writer with a "src" attribute containing a URL (an external script). When this
		// occurs, it is necessary to reverse-engineer the URL of each external script in order to determine the
		// name/library of the corresponding JSF2 resource.
		List<String> externalResourceURLs = primeFacesHeadResponseWriter.getExternalResourceURLs();

		// For each external script URL:
		if (externalResourceURLs.size() > 0) {

			ExternalContext externalContext = facesContext.getExternalContext();
			PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
			String namespace = portletResponse.getNamespace();
			String resourceNameParam = namespace + "javax.faces.resource";
			String libraryNameParam = namespace + "ln";

			for (String externalResourceURL : externalResourceURLs) {

				// Determine the value of the "javax.faces.resource" and "ln" parameters from the URL.
				String resourceName = null;
				String libraryName = null;
				String decodedExternalResourceURL = URLDecoder.decode(externalResourceURL, "UTF-8");
				Map<String, String[]> parsedParameterMapValuesArray = URLUtil.parseParameterMapValuesArray(
						decodedExternalResourceURL);

				if (parsedParameterMapValuesArray != null) {

					String[] resourceNameParamValues = parsedParameterMapValuesArray.get(resourceNameParam);

					if ((resourceNameParamValues != null) && (resourceNameParamValues.length > 0)) {
						resourceName = resourceNameParamValues[0];
					}

					String[] libraryNameParamValues = parsedParameterMapValuesArray.get(libraryNameParam);

					if ((libraryNameParamValues != null) && (libraryNameParamValues.length > 0)) {
						libraryName = libraryNameParamValues[0];
					}
				}

				// If the "javax.faces.resource" and "ln" parameters were found, then create the corresponding JSF2
				// resource and add it to the view root.
				if ((resourceName != null) && (libraryName != null)) {

					if (resourceName.equals(PRIMEFACES_THEME_RESOURCE_NAME) &&
							libraryName.startsWith(PRIMEFACES_THEME_PREFIX)) {

						ResourceComponent primefacesThemeResource = new ResourceComponent(facesContext, resourceName,
								libraryName, namespace);
						Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
						facesContextAttributes.put("primefacesTheme", primefacesThemeResource);
					}
					else {

						Application application = facesContext.getApplication();
						ResourceHandler resourceHandler = application.getResourceHandler();
						UIComponent resource = application.createComponent(UIOutput.COMPONENT_TYPE);
						String rendererType = resourceHandler.getRendererTypeForResourceName(resourceName);
						resource.setRendererType(rendererType);
						resource.setTransient(true);
						resource.getAttributes().put("name", resourceName);
						resource.getAttributes().put("library", libraryName);
						resource.getAttributes().put("target", "head");
						capturedResources.add(resource);
					}
				}
			}
		}

		// FACES-2061: If the PrimeFaces HeadRenderer attempted to render an inline script (as is the case when
		// PrimeFaces client side validation is activated) then add a component that can render the script to the view
		// root.
		String inlineScriptText = primeFacesHeadResponseWriter.toString();

		if ((inlineScriptText != null) && (inlineScriptText.length() > 0)) {

			PrimeFacesInlineScript primeFacesInlineScript = new PrimeFacesInlineScript(inlineScriptText);
			capturedResources.add(primeFacesInlineScript);
		}

		List<UIComponent> headResources = super.getAllResources(facesContext, uiComponent);
		capturedResources.addAll(headResources);
		return capturedResources;
	}

	@Override
	protected List<UIComponent> getFirstResources(FacesContext facesContext, UIComponent uiComponent) {

		List<UIComponent> firstResources = super.getFirstResources(facesContext, uiComponent);

		if (firstResources == null) {
			firstResources = new ArrayList<UIComponent>();
		}

		Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
		ResourceComponent primefacesThemeResource = (ResourceComponent) facesContextAttributes.remove(
				"primefacesTheme");

		if (primefacesThemeResource != null) {
			firstResources.add(primefacesThemeResource);
		}

		return firstResources;
	}
}
