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

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

/**
 * @author  Kyle Stiemann
 */
public class PartialViewContextFactoryBridgeImpl extends PartialViewContextFactory {

	// Private Members
	private PartialViewContextFactory wrappedPartialViewContextFactory;

	public PartialViewContextFactoryBridgeImpl(PartialViewContextFactory wrappedPartialViewContextFactory) {
		this.wrappedPartialViewContextFactory = wrappedPartialViewContextFactory;
	}

	@Override
	public PartialViewContext getPartialViewContext(FacesContext context) {
		return new PartialViewContextBridgeImpl(wrappedPartialViewContextFactory.getPartialViewContext(context));
	}

	@Override
	public PartialViewContextFactory getWrapped() {
		return wrappedPartialViewContextFactory;
	}
}
