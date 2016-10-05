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
package com.liferay.faces.bridge.issue;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * @author  Kyle Stiemann
 */
@ManagedBean
@RequestScoped
public class BackingBean {

	public int getComponentResourcesSize() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot uiViewRoot = facesContext.getViewRoot();
		List<UIComponent> componentResources = uiViewRoot.getComponentResources(facesContext, "head");
		return componentResources.size();		
	}
}
