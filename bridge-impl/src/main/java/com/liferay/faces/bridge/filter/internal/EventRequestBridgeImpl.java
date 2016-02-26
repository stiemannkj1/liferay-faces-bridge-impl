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
package com.liferay.faces.bridge.filter.internal;

import javax.portlet.EventRequest;
import javax.portlet.PortalContext;
import javax.portlet.filter.EventRequestWrapper;

import com.liferay.faces.bridge.context.internal.PortalContextBridgeImpl;


/**
 * @author  Neil Griffin
 */
public class EventRequestBridgeImpl extends EventRequestWrapper {

	// Private Data Members
	private PortalContext portalContext;

	public EventRequestBridgeImpl(EventRequest eventRequest) {
		super(eventRequest);
		this.portalContext = new PortalContextBridgeImpl(eventRequest.getPortalContext());
	}

	@Override
	public Object getAttribute(String name) {
		return RequestAttributeUtil.getAttribute(getRequest(), name);
	}

	@Override
	public PortalContext getPortalContext() {
		return portalContext;
	}
}
