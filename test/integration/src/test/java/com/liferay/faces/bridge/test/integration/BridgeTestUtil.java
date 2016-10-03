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
package com.liferay.faces.bridge.test.integration;

import com.liferay.faces.test.selenium.TestUtil;

/**
 * @author  Kyle Stiemann
 */
public final class BridgeTestUtil {

	// Private Constants
	private static final String DEFAULT_DEMO_CONTEXT;
	private static final String DEFAULT_ISSUE_CONTEXT;

	static {

		String defaultDemoContext = "/group/bridge-demos";
		String defaultIssueContext = "/group/bridge-issues";

		if (TestUtil.getContainer().contains("pluto")) {
			defaultDemoContext = TestUtil.DEFAULT_PLUTO_CONTEXT;
			defaultIssueContext = TestUtil.DEFAULT_PLUTO_CONTEXT;
		}

		DEFAULT_DEMO_CONTEXT = defaultDemoContext;
		DEFAULT_ISSUE_CONTEXT = defaultIssueContext;
	}

	private BridgeTestUtil() {
		throw new AssertionError();
	}

	public static String getDemoContext(String portletPageName) {
		return TestUtil.getSystemPropertyOrDefault("integration.context",
				DEFAULT_DEMO_CONTEXT) + "/" + portletPageName;
	}

	public static String getDemoPageURL(String portletPageName) {
		return TestUtil.DEFAULT_BASE_URL + getDemoContext(portletPageName);
	}

	public static String getIssueContext(String portletPageName) {
		return TestUtil.getSystemPropertyOrDefault("integration.context",
				DEFAULT_ISSUE_CONTEXT) + "/" + portletPageName;
	}

	public static String getIssuePageURL(String portletPageName) {
		return TestUtil.DEFAULT_BASE_URL + getDemoContext(portletPageName);
	}
}
