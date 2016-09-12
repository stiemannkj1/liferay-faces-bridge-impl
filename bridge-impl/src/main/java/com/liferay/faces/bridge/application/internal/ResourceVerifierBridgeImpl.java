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
package com.liferay.faces.bridge.application.internal;

import java.io.Serializable;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.renderkit.html_basic.internal.HeadManagedBean;
import com.liferay.faces.bridge.renderkit.primefaces.internal.PrimeFacesMobileUtil;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.application.ResourceVerifier;
import com.liferay.faces.util.application.ResourceVerifierWrapper;
import com.liferay.faces.util.el.internal.ProductMap;
import com.liferay.faces.util.product.Product;
import com.liferay.faces.util.product.ProductFactory;


/**
 * @author  Kyle Stiemann
 */
public class ResourceVerifierBridgeImpl extends ResourceVerifierWrapper implements Serializable {

	// serialVersionUID
	private static final long serialVersionUID = 7367617042346492537L;

	// Private Constants
	private static final boolean PRIMEFACES_DETECTED = ProductFactory.getProduct(Product.Name.PRIMEFACES).isDetected();

	// Private Members
	private ResourceVerifier wrappedResourceVerifier;

	public ResourceVerifierBridgeImpl(ResourceVerifier wrappedResourceVerifier) {
		this.wrappedResourceVerifier = wrappedResourceVerifier;
	}

	@Override
	public ResourceVerifier getWrapped() {
		return wrappedResourceVerifier;
	}

	@Override
	public boolean isDependencySatisfied(FacesContext facesContext, UIComponent componentResource) {

		boolean dependencySatisfied = false;
		HeadManagedBean headManagedBean = HeadManagedBean.getInstance(facesContext);

		if (headManagedBean != null) {

			Set<String> headResourceIds = headManagedBean.getHeadResourceIds();
			String resourceId = ResourceUtil.getResourceId(componentResource);
			dependencySatisfied = headResourceIds.contains(resourceId);
		}

		if (!dependencySatisfied && PRIMEFACES_DETECTED) {

			// Certain resources should not be rendered when PrimeFaces' PRIMEFACES_MOBILE RenderKit is used. For
			// more information, see
			// https://github.com/primefaces/primefaces/blob/6_0/src/main/java/org/primefaces/mobile/renderkit/HeadRenderer.java#L96-L104.
			dependencySatisfied = PrimeFacesMobileUtil.isMobile(facesContext) &&
				PrimeFacesMobileUtil.isComponentResourceSuppressedWhenMobile(componentResource);
		}

		if (!dependencySatisfied) {
			dependencySatisfied = super.isDependencySatisfied(facesContext, componentResource);
		}

		return dependencySatisfied;
	}
}
