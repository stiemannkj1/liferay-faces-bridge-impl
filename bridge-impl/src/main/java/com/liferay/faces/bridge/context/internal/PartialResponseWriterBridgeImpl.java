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
package com.liferay.faces.bridge.context.internal;

import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadRendererBridgeImpl;
import com.liferay.faces.bridge.renderkit.html_basic.internal.RenderKitBridgeImpl;
import com.liferay.faces.util.context.PartialResponseWriterWrapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;

/**
 * @author  Kyle Stiemann
 */
public class PartialResponseWriterBridgeImpl extends PartialResponseWriterWrapper {

	// Private Data Members
	private boolean resourcesWritten;

	public PartialResponseWriterBridgeImpl(PartialResponseWriter wrappedPartialResponseWriter) {
		super(wrappedPartialResponseWriter);
	}

	@Override
	public void startUpdate(String targetId) throws IOException {

		// TODO add spec language about using javax.faces.Resource when PartialViewContext.isRenderAll() (because in
		// portlets, we cannot render the <head> section during render all)
		FacesContext facesContext = FacesContext.getCurrentInstance();
		PartialViewContext partialViewContext = facesContext.getPartialViewContext();
		
		if (!resourcesWritten && partialViewContext.isRenderAll()) {

			UIViewRoot uiViewRoot = facesContext.getViewRoot();
			String viewRootClientId = uiViewRoot.getClientId(facesContext);

			if (viewRootClientId.equals(targetId)) {

				List<UIComponent> children = uiViewRoot.getChildren();

				for (UIComponent child : children) {

					if (RenderKitBridgeImpl.JAVAX_FACES_HEAD.equals(child.getRendererType())) {
						RenderKit renderKit = facesContext.getRenderKit();
						Renderer headRenderer = renderKit.getRenderer(UIOutput.COMPONENT_FAMILY, RenderKitBridgeImpl.JAVAX_FACES_HEAD);
						headRenderer.encodeBegin(facesContext, child);
						headRenderer.encodeChildren(facesContext, child);
						headRenderer.encodeEnd(facesContext, child);
						break;
					}
				}

				Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
				List<UIComponent> headResourcesToRenderInBody = (List<UIComponent>) facesContextAttributes.get(
						HeadRendererBridgeImpl.HEAD_RESOURCES_TO_RENDER_IN_BODY);

				for (UIComponent componentResource : headResourcesToRenderInBody) {

					if (!resourcesWritten) {

						super.startUpdate("javax.faces.Resource");
						resourcesWritten = true;
					}

					componentResource.encodeAll(facesContext);
				}

				if (resourcesWritten) {
					super.endUpdate();
				}
			}
		}

		super.startUpdate(targetId);
	}
}
