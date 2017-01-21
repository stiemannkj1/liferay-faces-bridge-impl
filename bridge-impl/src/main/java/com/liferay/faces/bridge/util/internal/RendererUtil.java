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
package com.liferay.faces.bridge.util.internal;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;


/**
 * @author  Kyle Stiemann
 */
public class RendererUtil {

	// Public Constants
	public static final String HEAD_RESOURCES_TO_RELOCATE_KEY = RendererUtil.class.getName() +
		"_HEAD_RESOURCES_TO_RELOCATE";

	public static void writePassThroughAttributes(ResponseWriter responseWriter, UIComponent uiComponent)
		throws IOException {

		if (uiComponent != null) {

			Map<String, Object> passThroughAttributes = uiComponent.getPassThroughAttributes(false);

			if (passThroughAttributes != null) {

				for (Map.Entry<String, Object> passThroughAttribute : passThroughAttributes.entrySet()) {

					String attributeName = passThroughAttribute.getKey();
					String value = (String) passThroughAttribute.getValue();

					if (value == null) {
						value = "";
					}

					responseWriter.writeAttribute(attributeName, value, null);
				}
			}
		}
	}
}
