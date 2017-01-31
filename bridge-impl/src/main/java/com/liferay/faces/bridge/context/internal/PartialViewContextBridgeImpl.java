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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;


/**
 * @author  Kyle Stiemann
 */
public class PartialViewContextBridgeImpl extends PartialViewContextWrapper {

	// Private Data Members
	private PartialResponseWriter partialResponseWriter;

	public PartialViewContextBridgeImpl(PartialViewContext wrappedPartialViewContext) {
		super(wrappedPartialViewContext);
	}

	@Override
	public PartialResponseWriter getPartialResponseWriter() {

		if (partialResponseWriter == null) {
			partialResponseWriter = new PartialResponseWriterBridgeImpl(super.getPartialResponseWriter());
		}

		return partialResponseWriter;
	}
}
