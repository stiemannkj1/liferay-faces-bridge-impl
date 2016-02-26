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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

/**
 * @author  Kyle Stiemann
 */
public class PartialViewContextBridgeImpl extends PartialViewContextWrapper {

	// Private Members
	private PartialViewContext wrappedPartialViewContext;

	public PartialViewContextBridgeImpl(PartialViewContext wrappedPartialViewContext) {
		this.wrappedPartialViewContext = wrappedPartialViewContext;
	}

	@Override
	public PartialResponseWriter getPartialResponseWriter() {
		return new PartialResponseWriterBridgeImpl(super.getPartialResponseWriter());
	}

	@Override
	public PartialViewContext getWrapped() {
		return wrappedPartialViewContext;
	}
}
