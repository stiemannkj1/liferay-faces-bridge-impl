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
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;

import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadManagedBean;
import com.liferay.faces.bridge.renderkit.html_basic.internal.RenderKitBridgeImpl;
import com.liferay.faces.bridge.util.internal.RendererUtil;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.context.PartialResponseWriterWrapper;


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

						// TODO is this appropriate to do here?
						RenderKit renderKit = facesContext.getRenderKit();
						Renderer headRenderer = renderKit.getRenderer(UIOutput.COMPONENT_FAMILY,
								RenderKitBridgeImpl.JAVAX_FACES_HEAD);
						headRenderer.encodeBegin(facesContext, child);
						headRenderer.encodeChildren(facesContext, child);
						headRenderer.encodeEnd(facesContext, child);

						break;
					}
				}

				Map<Object, Object> facesContextAttributes = facesContext.getAttributes();
				List<UIComponent> relocatedHeadResources = (List<UIComponent>) facesContextAttributes.remove(
						RendererUtil.HEAD_RESOURCES_TO_RELOCATE_KEY);

				if (relocatedHeadResources != null) {

					HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);
					Set<String> headResourceIds;

					if (headManagedBean == null) {
						headResourceIds = new HashSet<String>();
					}
					else {
						headResourceIds = headManagedBean.getHeadResourceIds();
					}

					for (UIComponent componentResource : relocatedHeadResources) {

						if (!resourcesWritten) {

							super.startUpdate("javax.faces.Resource");
							resourcesWritten = true;
						}

						componentResource.encodeAll(facesContext);

						if (RendererUtil.isScriptResource(componentResource) ||
								RendererUtil.isStyleSheetResource(componentResource)) {
							headResourceIds.add(ResourceUtil.getResourceId(componentResource));
						}
					}

					if (resourcesWritten) {
						super.endUpdate();
					}
				}
			}
		}

		super.startUpdate(targetId);
	}
}
