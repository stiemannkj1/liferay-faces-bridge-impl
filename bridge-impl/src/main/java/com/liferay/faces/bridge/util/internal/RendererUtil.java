/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
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

	public static void writePassThroughAttributes(ResponseWriter responseWriter, UIComponent uiComponent) throws IOException {

		// TODO Duplicates code in HeadResponseWriterCompatImpl
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
