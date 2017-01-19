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
package com.liferay.faces.issues;

import com.liferay.faces.issues.example.component.ExampleComponent;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


/**
 * @author  Kyle Stiemann
 */
@ManagedBean
@ViewScoped
public class Bean {

	private String include = "includeEmpty.xhtml";

	public void addExampleComponentToView(ActionEvent actionEvent) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application application = facesContext.getApplication();
		UIComponent exampleComponent = application.createComponent(facesContext, ExampleComponent.COMPONENT_TYPE,
				ExampleComponent.RENDERER_TYPE);
		UIComponent parent = actionEvent.getComponent().getParent();
		parent.getChildren().add(exampleComponent);
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public void includeJS() {
		setInclude("includeJS.xhtml");
	}

	public void includeCSS() {
		setInclude("includeCSS.xhtml");
	}
}
