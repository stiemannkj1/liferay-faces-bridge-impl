/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlHead;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;

import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadRendererBridgeImpl;
import com.liferay.faces.util.context.PartialResponseWriterWrapper;
import javax.faces.context.ExternalContext;
import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;

/**
 * @author  Kyle Stiemann
 */
public class PartialResponseWriterBridgeImpl extends PartialResponseWriterWrapper {

	public PartialResponseWriterBridgeImpl(PartialResponseWriter wrappedPartialResponseWriter) {
		super(wrappedPartialResponseWriter);
	}

	@Override
	public void endDocument() throws IOException {

		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final UIViewRoot uiViewRoot = facesContext.getViewRoot();
		final List<UIComponent> children = uiViewRoot.getChildren();

		for (UIComponent child : children) {

			if (child instanceof HtmlHead) {

				List<UIComponent> headResources = HeadRendererBridgeImpl.getHeadResources(facesContext, child);

				if (!headResources.isEmpty()) {

					HashMap<String, String> extensionAttributes = new HashMap<String, String>();
					extensionAttributes.put("id", "liferayFacesBridgeHeadResources");
					super.startExtension(extensionAttributes);
					super.startCDATA();

					for (UIComponent headResource : headResources) {
						headResource.encodeAll(facesContext);
					}

					super.endCDATA();
					super.endExtension();
				}

				break;
			}
		}

		super.endDocument();
	}	
}
