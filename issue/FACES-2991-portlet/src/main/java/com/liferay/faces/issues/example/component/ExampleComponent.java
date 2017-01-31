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