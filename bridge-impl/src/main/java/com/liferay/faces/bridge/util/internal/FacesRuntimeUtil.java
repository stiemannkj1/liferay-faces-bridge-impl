/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.util.internal;

import javax.faces.render.ResponseStateManager;

import com.liferay.faces.util.lang.OnDemand;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.info.ProductInfo;
import com.liferay.faces.util.product.info.ProductInfoFactory;


/**
 * @author  Neil Griffin
 */
public class FacesRuntimeUtil {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FacesRuntimeUtil.class);

	// Private Constants
	private static final OnDemandNamespacedViewStateSupported ON_DEMAND_NAMESPACED_VIEW_STATE_SUPPORTED =
		new OnDemandNamespacedViewStateSupported();

	private FacesRuntimeUtil() {
		throw new AssertionError();
	}

	public static boolean isNamespacedViewStateSupported(ProductInfoFactory productInfoFactory) {
		return ON_DEMAND_NAMESPACED_VIEW_STATE_SUPPORTED.get(productInfoFactory);
	}

	private static final class OnDemandNamespacedViewStateSupported extends OnDemand<Boolean, ProductInfoFactory> {

		@Override
		public Boolean computeInitialValue(ProductInfoFactory productInfoFactory) {

			boolean namespacedViewStateSupported = false;
			ProductInfo MOJARRA = productInfoFactory.getProductInfo(ProductInfo.Name.MOJARRA);

			if (MOJARRA.isDetected()) {

				int mojarraMajorVersion = MOJARRA.getMajorVersion();

				if (mojarraMajorVersion == 2) {

					int mojarraMinorVersion = MOJARRA.getMinorVersion();

					if (mojarraMinorVersion == 1) {
						namespacedViewStateSupported = (MOJARRA.getPatchVersion() >= 27);
					}
					else if (mojarraMinorVersion == 2) {
						namespacedViewStateSupported = (MOJARRA.getPatchVersion() >= 4);
					}
					else if (mojarraMinorVersion > 2) {
						namespacedViewStateSupported = true;
					}
				}
				else if (mojarraMajorVersion > 2) {
					namespacedViewStateSupported = true;
				}
			}

			if (logger.isDebugEnabled()) {

				final ProductInfo JSF = productInfoFactory.getProductInfo(ProductInfo.Name.JSF);
				logger.debug("JSF runtime [{0}] version [{1}].[{2}].[{3}] supports namespacing [{4}]: [{5}]",
					JSF.getTitle(), JSF.getMajorVersion(), JSF.getMinorVersion(), JSF.getPatchVersion(),
					ResponseStateManager.VIEW_STATE_PARAM, namespacedViewStateSupported);
			}

			return namespacedViewStateSupported;
		}
	}
}
