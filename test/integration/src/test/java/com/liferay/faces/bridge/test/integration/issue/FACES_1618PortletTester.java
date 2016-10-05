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
package com.liferay.faces.bridge.test.integration.issue;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.Browser;
import com.liferay.faces.test.selenium.TestUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author  Kyle Stiemann
 */
public class FACES_1618PortletTester {

	// Logger
	private static final Logger logger = Logger.getLogger(FACES_1618PortletTester.class.getName());

	static {
		logger.setLevel(TestUtil.getLogLevel());
	}

	@Test
	public void runFACES_1618PortletTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-1618"));
		String componentResourcesSizeXpath = "//span[@id='component_resources_size']";
		browser.waitForElementVisible(componentResourcesSizeXpath);
		String resourcesXpath = "//ul[@id='FACES_1618_resources']/li";
		waitForAllResources(browser, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources are rendered.
		List<WebElement> resources = browser.findElements(By.xpath(resourcesXpath));
		List<String> loadedResourceIds = new ArrayList<String>();
		String childSpanXpath = ".//span";

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.log(Level.INFO, resourceText);
			assertRendered(resourceId, resourceText);
			loadedResourceIds.add(resourceId);
		}

		String immediateNavButtonXpath = "//input[contains(@value,'immediate=\"true\"')]";
		browser.click(immediateNavButtonXpath);
		String navButtonXpath = "//input[contains(@value,'immediate=\"false\"')]";
		browser.waitForElementVisible(navButtonXpath);
		waitForAllResources(browser, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources loaded in the previous view are suppressed and not rendered/loaded a second time in the current view. Also test that the resources new to this view are rendered and not suppressed.
		resources = browser.findElements(By.xpath(resourcesXpath));

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.log(Level.INFO, resourceText);

			if (loadedResourceIds.contains(resourceId)) {
				assertSuppressed(resourceId, resourceText);
			}
			else {

				assertRendered(resourceId, resourceText);
				loadedResourceIds.add(resourceId);
			}
		}

		browser.click(navButtonXpath);
		browser.waitForElementVisible(immediateNavButtonXpath);
		waitForAllResources(browser, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources loaded in the previous views are still suppressed.
		resources = browser.findElements(By.xpath(resourcesXpath));

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.log(Level.INFO, resourceText);

			if (loadedResourceIds.contains(resourceId)) {
				assertSuppressed(resourceId, resourceText);
			}
			else {

				assertRendered(resourceId, resourceText);
				loadedResourceIds.add(resourceId);
			}
		}

		browser.click(immediateNavButtonXpath);
		browser.waitForElementVisible(navButtonXpath);
		waitForAllResources(browser, componentResourcesSizeXpath, resourcesXpath);

		// Test that the resources loaded in the previous views are still suppressed.
		resources = browser.findElements(By.xpath(resourcesXpath));

		for (WebElement resource : resources) {

			WebElement resourceIdElement = resource.findElement(By.xpath(childSpanXpath));
			String resourceId = resourceIdElement.getText();
			String resourceText = resource.getText();
			logger.log(Level.INFO, resourceText);

			if (loadedResourceIds.contains(resourceId)) {
				assertSuppressed(resourceId, resourceText);
			}
			else {

				assertRendered(resourceId, resourceText);
				loadedResourceIds.add(resourceId);
			}
		}
	}

	private void assertSuppressed(String resourceId, String resourceText) {
		Assert.assertTrue(resourceId + " was rendered, but it should have been suppressed.", resourceText.contains(resourceId) && resourceText.contains("was suppressed"));
	}

	private void assertRendered(String resourceId, String resourceText) {
		Assert.assertTrue(resourceId + " was suppressed, but it should have been rendered.", resourceText.contains(resourceId) && resourceText.contains("was rendered"));
	}

	private void waitForAllResources(Browser browser, String componentResourcesSizeXpath, String resourcesXpath) {

		WebElement componentResourcesSizeElement = browser.findElementByXpath(componentResourcesSizeXpath);
		String componentResourcesSize = componentResourcesSizeElement.getText();
		browser.waitForElementVisible(resourcesXpath + "[" + componentResourcesSize + "]");	
	}
}
