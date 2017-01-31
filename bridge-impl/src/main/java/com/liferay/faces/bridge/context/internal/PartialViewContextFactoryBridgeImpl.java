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
package com.liferay.faces.bridge.context.internal;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;


/**
 * @author  Kyle Stiemann
 */
public class PartialViewContextFactoryBridgeImpl extends PartialViewContextFactory {

	public PartialViewContextFactoryBridgeImpl(PartialViewContextFactory wrappedPartialViewContextFactory) {
		super(wrappedPartialViewContextFactory);
	}

	@Override
	public PartialViewContext getPartialViewContext(FacesContext facesContext) {

		PartialViewContextFactory wrappedPartialViewContextFactory = getWrapped();
		PartialViewContext partialViewContext = wrappedPartialViewContextFactory.getPartialViewContext(facesContext);

		return new PartialViewContextBridgeImpl(partialViewContext);
	}
}
