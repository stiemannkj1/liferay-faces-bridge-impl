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
package com.liferay.faces.bridge.renderkit.html_basic.internal;

import java.io.IOException;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

/**
 * @author  Kyle Stiemann
 */
public class ResponseWriterJavascriptStringImpl extends ResponseWriterWrapper {

	// Private Members
	private ResponseWriter wrappedResponseWriter;

	public ResponseWriterJavascriptStringImpl(ResponseWriter wrappedResponseWriter) {
		this.wrappedResponseWriter = wrappedResponseWriter;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {

		String stringValue = String.valueOf(cbuf, off, len);
		stringValue = stringValue.replace("\n", "\\n");
		stringValue = stringValue.replace("'", "\'");
		super.write(stringValue);
	}

	@Override
	public ResponseWriter getWrapped() {
		return wrappedResponseWriter;
	}
}
