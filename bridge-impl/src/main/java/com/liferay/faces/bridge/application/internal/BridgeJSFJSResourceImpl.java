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
package com.liferay.faces.bridge.application.internal;

import com.liferay.faces.util.application.FilteredResourceBase;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.application.Resource;

/**
 * @author  Kyle Stiemann
 */
public class BridgeJSFJSResourceImpl extends FilteredResourceBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeJSFJSResourceImpl.class);

	// Private Data Members
	private Resource wrappedResource;

	public BridgeJSFJSResourceImpl(Resource wrappedResource) {
		this.wrappedResource = wrappedResource;
	}

	@Override
	protected String filter(String resourceText) {

		InputStream bridgeJSInputStream = BridgeJSFJSResourceImpl.class.getResourceAsStream("/META-INF/resources/liferay-faces-bridge/bridge.js");

		String bridgeJS = "";

		try {
			bridgeJS = ResourceUtil.toString(bridgeJSInputStream, getEncoding(), getBufferSize());
		}
		catch (IOException e) {

			// This should never occur.
			logger.error(e);
		}
		finally {

			try {
				bridgeJSInputStream.close();
			}
			catch (IOException e) {

				// We cannot do anything but log an error here.
				logger.error(e);
			}
		}

		return resourceText + bridgeJS;
	}

	@Override
	public Resource getWrapped() {
		return wrappedResource;
	}
}
