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

import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;


/**
 * @author  Kyle Stiemann
 */
public final class PrimeFacesMobileUtil {

	private PrimeFacesMobileUtil() {
		throw new AssertionError();
	}

	/**
	 * Returns true if a resource should be suppressed when PrimeFaces' PRIMEFACES_MOBILE RenderKit is used. The
	 * criteria for which resources should be suppressed was obtained from here:
	 * https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L96-L104.
	 */
	public static boolean isComponentResourceSuppressedWhenMobile(UIComponent componentResource) {

		Map<String, Object> attributes = componentResource.getAttributes();
		String libraryName = (String) attributes.get("library");
		String resourceName = (String) attributes.get("name");

		return !isMobileComponentResource(resourceName, libraryName) && "primefaces".equals(libraryName) &&
			(resourceName.startsWith("jquery") || resourceName.startsWith("primefaces") ||
				resourceName.startsWith("components") || resourceName.startsWith("core"));
	}

	public static boolean isMobile(FacesContext facesContext) {

		String renderKitId;
		UIViewRoot uiViewRoot = facesContext.getViewRoot();

		if (uiViewRoot != null) {
			renderKitId = uiViewRoot.getRenderKitId();
		}
		else {

			Application application = facesContext.getApplication();
			ViewHandler viewHandler = application.getViewHandler();
			renderKitId = viewHandler.calculateRenderKitId(facesContext);
		}

		return "PRIMEFACES_MOBILE".equals(renderKitId);
	}

	/**
	 * Returns true if a resource must be rendered in a certain place when PrimeFaces' PRIMEFACES_MOBILE RenderKit is
	 * used. For more information, see {@link #isMobileComponentResource(javax.faces.component.UIComponent)}.
	 */
	/* package-private */ static boolean isMobileComponentResource(UIComponent componentResource) {

		Map<String, Object> attributes = componentResource.getAttributes();
		String libraryName = (String) attributes.get("library");
		String resourceName = (String) attributes.get("name");

		return isMobileComponentResource(resourceName, libraryName);
	}

	/**
	 * Returns true if a resource must be rendered in a certain place when PrimeFaces' PRIMEFACES_MOBILE RenderKit is
	 * used. The list of resources was obtained from here:
	 * https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L68-L87.
	 */
	/* package-private */ static boolean isMobileComponentResource(String resourceName, String libraryName) {

		return "primefaces".equals(libraryName) &&
			("jquery/jquery.js".equals(resourceName) || "mobile/jquery-mobile.js".equals(resourceName) ||
				"core.js".equals(resourceName) || "components-mobile.js".equals(resourceName));
	}
}
