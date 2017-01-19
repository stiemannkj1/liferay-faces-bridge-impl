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
package com.liferay.faces.issues.example.component;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;


/**
 * @author  Kyle Stiemann
 */
@FacesComponent(ExampleComponent.COMPONENT_TYPE)
public class ExampleComponent extends UIComponentBase {

	public static final String COMPONENT_FAMILY =
		"com.liferay.faces.dynamic.resource.loading.ajax.reproducer.component";
	public static final String COMPONENT_TYPE =
		"com.liferay.faces.dynamic.resource.loading.ajax.reproducer.component.ExampleComponent";
	public static final String RENDERER_TYPE =
		"com.liferay.faces.dynamic.resource.loading.ajax.reproducer.component.ExampleRenderer";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
}
