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
package com.liferay.faces.bridge.bean.internal;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import javax.portlet.PortletContext;
import javax.servlet.ServletContext;

import com.liferay.faces.util.product.info.ProductInfo;
import com.liferay.faces.util.product.info.ProductInfoFactory;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


/**
 * @author  Neil Griffin
 */
public class PreDestroyInvokerFactoryImpl extends PreDestroyInvokerFactory {

	@Override
	public PreDestroyInvoker getPreDestroyInvoker(ServletContext servletContext) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		final ProductInfo MOJARRA =
				ProductInfoFactory.getProductInfoInstance(externalContext, ProductInfo.Name.MOJARRA);

		if (MOJARRA.isDetected()) {
			return new PreDestroyInvokerMojarraImpl(servletContext);
		}
		else {
			return new PreDestroyInvokerImpl();
		}
	}

	@Override
	public PreDestroyInvoker getPreDestroyInvoker(PortletContext portletContext) {

		ProductInfoFactory productInfoFactory =
				(ProductInfoFactory) BridgeFactoryFinder.getFactory(portletContext, ProductInfoFactory.class);
		final ProductInfo MOJARRA = productInfoFactory.getProductInfo(ProductInfo.Name.MOJARRA);

		if (MOJARRA.isDetected()) {
			return new PreDestroyInvokerMojarraImpl(portletContext);
		}
		else {
			return new PreDestroyInvokerImpl();
		}
	}

	@Override
	public PreDestroyInvokerFactory getWrapped() {

		// Since this is the factory instance provided by the bridge, it will never wrap another factory.
		return null;
	}
}
