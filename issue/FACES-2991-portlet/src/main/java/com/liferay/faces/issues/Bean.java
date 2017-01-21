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
package com.liferay.faces.issues;

import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.liferay.faces.issues.example.component.ExampleComponent;


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

	public void includeCSS() {
		setInclude("includeCSS.xhtml");
	}

	public void includeJS() {
		setInclude("includeJS.xhtml");
	}

	public void setInclude(String include) {
		this.include = include;
	}
}
