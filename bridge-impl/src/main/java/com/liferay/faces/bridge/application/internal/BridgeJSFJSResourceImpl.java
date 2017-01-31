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
package com.liferay.faces.bridge.application.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.Resource;

import com.liferay.faces.util.application.FilteredResourceBase;
import com.liferay.faces.util.application.ResourceUtil;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


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
	public Resource getWrapped() {
		return wrappedResource;
	}

	@Override
	protected String filter(String resourceText) {

		InputStream bridgeJSInputStream = BridgeJSFJSResourceImpl.class.getResourceAsStream(
				"/META-INF/resources/liferay-faces-bridge/bridge.js");

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
}
