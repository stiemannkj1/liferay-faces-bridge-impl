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
package com.liferay.faces.bridge.test.integration.issue;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import org.junit.Test;

import com.liferay.faces.test.selenium.IntegrationTesterBase;
import com.liferay.faces.test.selenium.assertion.SeleniumAssert;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * @author  Kyle Stiemann
 */
public class FACES_2991PortletTester extends IntegrationTesterBase {

	// Logger
	private static final Logger logger = Logger.getLogger(FACES_2991PortletTester.class.getName());

	@Test
	public void runFACES_2991PortletDuplicateDynamicResource_jsf_ajax_request_Test() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2991-dynamic-duplicate-test-jsf-ajax-request"));
		runFACES_2991PortletTest(browser);
	}

	@Test
	public void runFACES_2991PortletDuplicateDynamicResourceTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2991-dynamic-duplicate-test"));
		runFACES_2991PortletTest(browser);
	}

	@Test
	public void runFACES_2991PortletDynamicComponentTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2991"));
		runFACES_2991PortletTest(browser, "dynamicComponent");
	}

	@Test
	public void runFACES_2991PortletDynamicIncludeWithCSSTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2991"));
		runFACES_2991PortletTest(browser, "dynamicIncludeWithCSS");
	}

	@Test
	public void runFACES_2991PortletDynamicIncludeWithJSTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2991"));
		runFACES_2991PortletTest(browser, "dynamicIncludeWithJS");
	}

	@Test
	public void runFACES_2991PortletPortletRenderAllTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-2991"));
		runFACES_2991PortletTest(browser, "portletRenderAll");
	}

	private void runFACES_2991PortletTest(Browser browser) {

		String runTestLinkXpath = "//a[@class=\"ppr-redisplay-link\" and contains(.,\"Run Test\")]";
		browser.waitForElementVisible(runTestLinkXpath);
		browser.click(runTestLinkXpath);
		String testResultStatusXpath = "//span[@id='FACES-2991-result-status']";
		browser.waitForElementVisible(testResultStatusXpath);
		List<WebElement> testResultDetails = browser.findElements(By.xpath("//span[@id='FACES-2991-result-detail']"));

		if (testResultDetails.size() > 0) {
			logger.log(Level.INFO, "Test Result Details: {0}", testResultDetails.get(0).getText());
		}

		SeleniumAssert.assertElementTextVisible(browser, testResultStatusXpath, "SUCCESS");
	}

	private void runFACES_2991PortletTest(Browser browser, String viewName) {

		String viewLinkXpath = "//a[contains(text(),'/WEB-INF/views/" + viewName + "')]";
		browser.waitForElementVisible(viewLinkXpath);
		browser.click(viewLinkXpath);
		runFACES_2991PortletTest(browser);
	}
}
