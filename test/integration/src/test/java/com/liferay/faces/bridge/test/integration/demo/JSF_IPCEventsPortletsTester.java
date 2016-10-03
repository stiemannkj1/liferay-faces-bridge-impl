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
package com.liferay.faces.bridge.test.integration.demo;

import com.liferay.faces.test.selenium.Browser;
import org.junit.Test;

/**
 * @author  Kyle Stiemann
 */
public class JSF_IPCEventsPortletsTester extends JSF_IPCPortletsTesterBase {

	@Test
	public void runJSF_IPCEventsPortletsTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getDemoPageURL("jsf-events"));
		runJSF_IPCPortletsTest(browser);
	}
}
