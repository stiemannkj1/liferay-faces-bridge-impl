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
package com.liferay.faces.bridge.test.integration.demo.applicant;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.applicant.ApplicantTesterBase;

/**
 * @author  Kyle Stiemann
 */
public abstract class BridgeApplicantPortletTester extends ApplicantTesterBase {

	@Override
	protected String getContext() {
		return BridgeTestUtil.getDemoContext(getPortletPageName());
	}

	protected abstract String getPortletPageName();
}
