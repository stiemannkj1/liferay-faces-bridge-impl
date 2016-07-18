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
package com.liferay.faces.bridge.context.internal;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * @author  Neil Griffin
 */
public abstract class FacesContextFactoryCompatImpl extends FacesContextFactory {

	public abstract FacesContextFactory getWrapped();

	public FacesContext getFacesContext(PortletContext portletContext, PortletRequest portletRequest,
		PortletResponse portletResponse, Lifecycle lifecycle) throws FacesException {

		String requestContextPath = portletRequest.getContextPath();
		ServletContext servletContext = new ServletContextAdapterImpl(portletContext, requestContextPath);
		ServletRequest servletRequest = new ServletRequestAdapterImpl(portletRequest);
		ServletResponse servletResponse = new HttpServletResponseAdapterImpl(portletResponse);
		FacesContext wrappedFacesContext = getWrapped().getFacesContext(servletContext, servletRequest, servletResponse,
				lifecycle);

		ExternalContext externalContext = new ExternalContextImpl(portletContext, portletRequest, portletResponse);

		return new FacesContextImpl(wrappedFacesContext, externalContext);
	}
}
