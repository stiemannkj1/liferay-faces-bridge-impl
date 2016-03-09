/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.demos;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.faces.GenericFacesPortlet;


@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.ajaxable=false",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"javax.portlet.display-name=Blade-JSF-Portlet",
		"javax.portlet.init-param.javax.portlet.faces.defaultViewId.view=/WEB-INF/views/portletViewMode.xhtml",
		"javax.portlet.init-param.javax.portlet.faces.defaultViewId.edit=/WEB-INF/views/portletEditMode.xhtml",
		"javax.portlet.security-role-ref=administrator,guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html",
		"javax.portlet.supports.portlet-mode=view",
		"javax.portlet.supports.portlet-mode=edit"
	},
	service = Portlet.class
)
public class JSFPortlet extends GenericFacesPortlet {

	@Activate
	public void activate(BundleContext bundleContext) {
		System.err.println("!@#$ Activating " + this);
	}

	@Override
	public void destroy() {
		System.err.println("!@#$ Destroying " + this);
		super.destroy();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {

		System.err.println("!@#$ init(): portletConfig.getPortletName() = " + portletConfig.getPortletName());
		super.init(portletConfig);
	}
}

