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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;


/**
 * @author  Kyle Stiemann
 */
public class HeadRendererBridgeCompatImpl extends Renderer {

	@Override
	public boolean getRendersChildren() {

		// When rendering all the markup in a partial response, the head renderer must be run early to populate the
		// <update id="javax.faces.Resource"> section, so avoid running the head renderer during normal rendering.
		return !FacesContext.getCurrentInstance().getPartialViewContext().isRenderAll();
	}
}
