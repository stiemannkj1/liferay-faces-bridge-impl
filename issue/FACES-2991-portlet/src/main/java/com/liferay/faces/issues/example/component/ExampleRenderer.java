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

import java.io.IOException;
import javax.faces.application.ResourceDependencies;

import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;


/**
 * @author  Kyle Stiemann
 */
@ResourceDependencies({
	@ResourceDependency(library = "example", name = "example.js"),
	@ResourceDependency(library = "example", name = "example.css")
})
@FacesRenderer(componentFamily = ExampleComponent.COMPONENT_FAMILY, rendererType = ExampleComponent.RENDERER_TYPE)
public class ExampleRenderer extends Renderer {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		super.encodeBegin(context, component);

		ResponseWriter responseWriter = context.getResponseWriter();
		responseWriter.write("<br />");
		responseWriter.startElement("strong", component);
		responseWriter.writeText("example:component rendered", null);
		responseWriter.endElement("strong");
	}
}
