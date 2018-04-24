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
package com.liferay.faces.bridge.scope.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.liferay.faces.bridge.BridgeFactoryFinder;
import com.liferay.faces.bridge.internal.PortletConfigEmptyImpl;
import com.liferay.faces.bridge.internal.PortletConfigParam;
import com.liferay.faces.bridge.servlet.BridgeSessionListener;
import com.liferay.faces.util.cache.Cache;
import com.liferay.faces.util.cache.CacheFactory;
import com.liferay.faces.util.lang.OnDemand;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeRequestScopeManagerImpl implements BridgeRequestScopeManager {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeRequestScopeManagerImpl.class);

	// Private Final Data Members
	private final OnDemandBridgeRequestScopeCache onDemandBridgeRequestScopeCache =
		new OnDemandBridgeRequestScopeCache();

	@Override
	public Cache<String, BridgeRequestScope> getBridgeRequestScopeCache(PortletContext portletContext) {
		return onDemandBridgeRequestScopeCache.get(portletContext);
	}

	@Override
	public void removeBridgeRequestScopesByPortlet(PortletConfig portletConfig) {

		PortletContext portletContext = portletConfig.getPortletContext();
		Cache<String, BridgeRequestScope> bridgeRequestScopeCache = getBridgeRequestScopeCache(portletContext);
		String portletNameToRemove = portletConfig.getPortletName();
		removeBridgeRequestScopes(bridgeRequestScopeCache, true, portletNameToRemove);
	}

	/**
	 * This method is designed to be invoked from a {@link javax.servlet.http.HttpSessionListener} like {@link
	 * BridgeSessionListener} when a session timeout/expiration occurs. The logic in this method is a little awkward
	 * because we have to try and remove BridgeRequestScope instances from {@link Cache} instances in the {@link
	 * ServletContext} rather than the {@link PortletContext} because we only have access to the Servlet-API when
	 * sessions expire.
	 */
	@Override
	public void removeBridgeRequestScopesBySession(HttpSession httpSession) {

		if (onDemandBridgeRequestScopeCache.isInitialized()) {

			String sessionId = httpSession.getId();

			// Since the bridge request scope has already been initialized, there's no need to consult the
			// PortletContext.
			Cache<String, BridgeRequestScope> bridgeRequestScopeCache = onDemandBridgeRequestScopeCache.get(null);
			removeBridgeRequestScopes(bridgeRequestScopeCache, false, sessionId);
		}
	}

	private void removeBridgeRequestScopes(Cache bridgeRequestScopeCache, boolean removeByPortletId,
		String portletOrSessionId) {

		int indexOfSessionIdSection = -1;

		// Iterate over the map entries, and build up a list of BridgeRequestScope keys that are to be
		// removed. Doing it this way prevents ConcurrentModificationExceptions from being thrown.
		List<String> keysToRemove = new ArrayList<String>();
		Set<String> keySet = (Set<String>) bridgeRequestScopeCache.getKeys();
		String portletOrSessionIdWithSeparatorSuffix = portletOrSessionId + ":::";

		for (String bridgeRequestScopeId : keySet) {

			if (removeByPortletId) {

				if (bridgeRequestScopeId.startsWith(portletOrSessionIdWithSeparatorSuffix)) {
					keysToRemove.add(bridgeRequestScopeId);
				}
			}
			else {

				if (indexOfSessionIdSection < 0) {
					indexOfSessionIdSection = bridgeRequestScopeId.indexOf(":::") + ":::".length();
				}

				String idWithoutPortletNamePrefix = bridgeRequestScopeId.substring(indexOfSessionIdSection);

				if (idWithoutPortletNamePrefix.startsWith(portletOrSessionIdWithSeparatorSuffix)) {
					keysToRemove.add(bridgeRequestScopeId);
				}
			}
		}

		for (String keyToRemove : keysToRemove) {

			Object bridgeRequestScope = bridgeRequestScopeCache.removeValue(keyToRemove);

			if (!removeByPortletId) {
				logger.debug(
					"Removed bridgeRequestScopeId=[{0}] bridgeRequestScope=[{1}] from cache due to session timeout",
					keyToRemove, bridgeRequestScope);
			}
		}
	}

	private static final class OnDemandBridgeRequestScopeCache
		extends OnDemand<Cache<String, BridgeRequestScope>, PortletContext> {

		@Override
		protected Cache<String, BridgeRequestScope> computeInitialValue(PortletContext portletContext) {

			Cache<String, BridgeRequestScope> bridgeRequestScopeCache;
			CacheFactory cacheFactory = (CacheFactory) BridgeFactoryFinder.getFactory(portletContext,
					CacheFactory.class);

			PortletConfig emptyPortletConfig = new PortletConfigEmptyImpl(portletContext);
			int initialCacheCapacity = PortletConfigParam.BridgeRequestScopeInitialCacheCapacity.getIntegerValue(
					emptyPortletConfig);
			int maxCacheCapacity = PortletConfigParam.BridgeRequestScopeMaxCacheCapacity.getIntegerValue(
					emptyPortletConfig);

			if (maxCacheCapacity > -1) {
				bridgeRequestScopeCache = cacheFactory.getConcurrentLRUCache(initialCacheCapacity, maxCacheCapacity);
			}
			else {
				bridgeRequestScopeCache = cacheFactory.getConcurrentCache(initialCacheCapacity);
			}

			return bridgeRequestScopeCache;
		}
	}
}
