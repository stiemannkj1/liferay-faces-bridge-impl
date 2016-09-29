/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.test.integration.demo;

import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;
import com.liferay.faces.test.selenium.applicant.ApplicantTesterBase;


/**
 * @author  Kyle Stiemann
 */
public class IceFacesApplicantPortletTester extends ApplicantTesterBase {

	@Override
	protected String getContext() {
		return TestUtil.getSystemPropertyOrDefault("integration.context", "/group/bridge-demos/icefaces-applicant");
	}

	@Override
	protected String getExtraLibraryName() {
		return "ICEfaces";
	}

	@Override
	protected String getFieldErrorXpath(String fieldXpath) {

		String errorMessageXpath = "/../span/span[@class='portlet-msg-error']";

		return "(" + fieldXpath + errorMessageXpath + "|" + fieldXpath + "/.." + errorMessageXpath + "|" + fieldXpath +
			"/../.." + errorMessageXpath + ")";
	}

	@Override
	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post' and @enctype]/input[@type='submit' and @value='Add Attachment']";
	}

	@Override
	protected void selectDate(Browser browser) {

		browser.click(getDateOfBirthFieldXpath());

		String dateElement = "//table[contains(@class, 'ui-datepicker-calendar')]//a[contains(text(), '14')]";
		browser.waitForElementVisible(dateElement);
		browser.click(dateElement);
	}
}
