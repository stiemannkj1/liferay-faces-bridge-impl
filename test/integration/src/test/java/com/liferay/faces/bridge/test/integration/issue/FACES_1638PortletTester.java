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
public class FACES_1638PortletTester {

	// Logger
	private static final Logger logger = Logger.getLogger(FACES_1638PortletTester.class.getName());

	static {
		logger.setLevel(TestUtil.getLogLevel());
	}

	@Test
	public void runFACES_1638PortletTest() {

		Browser browser = Browser.getInstance();
		browser.get(BridgeTestUtil.getIssuePageURL("faces-1638"));
		List<WebElement> listItems = browser.findElements(By.xpath("//div[contains(@class,'liferay-faces-bridge-body')]//ul/li"));
		int expectedNumberOfListItems = 6;
		Assert.assertEquals("There are not " + expectedNumberOfListItems + " links on the page.", expectedNumberOfListItems, listItems.size());

		for (WebElement listItem : listItems) {

			WebElement paramNameSpan = listItem.findElement(By.xpath(".//span[@class='param-name']"));
			String paramName = paramNameSpan.getText();

			WebElement paramValueSpan = listItem.findElement(By.xpath(".//span[@class='param-value']"));
			String paramValue = paramValueSpan.getText();

			String param = paramName + "=" + paramValue;

			WebElement link = listItem.findElement(By.xpath(".//a[contains(text(),'-Item')][@href]"));
			String url = link.getAttribute("href");

			logger.log(Level.INFO, "URL:\n\n{0}", new String[] { url } );
			Assert.assertTrue("The link does not contain the parameter.", url.contains(param));
			int expectedOccurencesOfParamName = 1;
			Assert.assertEquals("The link contains more than than " + expectedOccurencesOfParamName + " \"" + paramName + "\" parameter.", expectedOccurencesOfParamName, occurencesOf(paramName, url));
		}
	}

	private int occurencesOf(String occuringString, String inString) {
		return (inString.length() - inString.replace(occuringString, "").length()) / occuringString.length();
	}
}
