if (!window.liferayFacesBridge) {

	var liferayFacesBridge = {
		internal: {
			headResources: {
				scriptSrcSet: null,
				styleSheetHrefSet: null
			},

			/**
			 * <strong>Note:</strong> This method assumes that the element passed to it is a &lt;script&gt; element.
			 */
			isScriptResource: function(scriptElement) {
				return scriptElement.hasAttribute('src');
			},

			/**
			 * <strong>Note:</strong> This method assumes that the element passed to it is a &lt;script&gt; element.
			 */
			isInlineScript: function(scriptElement) {
				return !scriptElement.hasAttribute('src') && (!scriptElement.hasAttribute('type') ||
						liferayFacesBridge.internal.elementTypeIs(scriptElement, 'javascript'));
			},

			/**
			 * <strong>Note:</strong> This method assumes that the element passed to it is a &lt;link&gt; element.
			 */
			isStyleSheetResource: function(styleSheetElement) {
				return styleSheetElement.hasAttribute('href') && styleSheetElement.hasAttribute('rel') &&
						styleSheetElement.getAttribute('rel') === 'stylesheet';
			},

			/**
			 * <strong>Note:</strong> This method assumes that the element passed to it is a &lt;style&gt; element.
			 */
			isInlineStyleSheet: function(styleSheetElement) {
				return !styleSheetElement.hasAttribute('type') ||
						liferayFacesBridge.internal.elementTypeIs(styleSheetElement, 'css');
			},

			/**
			 * @returns {Boolean} true if the elements type attribute contains the string type that was passed to this function.
			 */
			elementTypeIs: function(element, type) {
				return element.hasAttribute('type') && element.getAttribute('type').indexOf(type) !== -1;
			},
			createStringSet: function() {
				return {};
			},
			stringSetAdd: function(set, string) {
				set[string] = true;
			},
			stringSetContains: function(set, string) {
				return Object.prototype.hasOwnProperty.call(set, string);
			},

			/**
			 * 
			 * @param {type} script
			 * @param {type} headElement
			 * @param {type} originalElement
			 */
			runScript: function(script, headElement, originalElement) {

				var newScriptElement = document.createElement('script');
				newScriptElement.type = "text/javascript";
				newScriptElement.text = script;
				headElement.appendChild(newScriptElement);
				headElement.removeChild(newScriptElement);

				// Append the original script element to the head for tracking purposes. It will not be run.
				headElement.appendChild(originalElement.cloneNode(true));
			},

			/**
			 * Loads a script programmatically and syncronously.
			 * 
			 * @param {type} scriptElement
			 * @param {type} headElement
			 */
			loadScriptResource: function(scriptElement, headElement) {

				var xmlHttpRequest = new XMLHttpRequest(),
					src = scriptElement.getAttribute('src'),
					src = src.replace(/[&]amp;/g, '&');

				// The partial response could contain scripts that depend on this script, so Load the script
				// syncronously so that it will be loaded an run before the partial response is updated.
				xmlHttpRequest.open('GET', src, false);
				xmlHttpRequest.send(null);

				if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200) {
					liferayFacesBridge.internal.runScript(xmlHttpRequest.responseText, headElement, scriptElement);
				}
			},

			/**
			 * 
			 * @param {type} url
			 * @returns {String}
			 */
			getResourceId: function(url) {

				var libraryName = url.substring(url.indexOf('ln='), url.length),
					resourceName = url.substring(url.indexOf('javax.faces.resource='), url.length);
				libraryName = libraryName.substring('ln='.length, libraryName.indexOf('&'));
				resourceName = resourceName.substring('javax.faces.resource='.length, resourceName.indexOf('&'));
				return libraryName + ':' + resourceName;
			},

			/**
			 * Loads head resources programmatically.
			 * 
			 * @param {String} headResourcesAsString
			 * @param {Boolean} renderAllMarkup
			 */
			loadHeadResources: function(headResourcesAsString, renderAllMarkup) {

				// According to http://stackoverflow.com/questions/10585029/parse-a-html-string-with-js
				// the best way to parse a string as html (in a cross-browser compatible way) is to
				// create a temporary new <html> element and insert the string within that <html>
				// element.
				var tempElement = document.createElement('html'),
					headResources = liferayFacesBridge.internal.headResources,
					headElement = document.getElementsByTagName("head")[0],
					scriptSrcSet = headResources.scriptSrcSet,
					styleSheetHrefSet = headResources.styleSheetHrefSet,
					stringSetAdd = liferayFacesBridge.internal.stringSetAdd,
					stringSetContains = liferayFacesBridge.internal.stringSetContains,
					isScriptResource = liferayFacesBridge.internal.isScriptResource,
					isInlineScript = liferayFacesBridge.internal.isInlineScript,
					isStyleSheetResource = liferayFacesBridge.internal.isStyleSheetResource,
					isInlineStyleSheet = liferayFacesBridge.internal.isInlineStyleSheet,
					loadScriptResource = liferayFacesBridge.internal.loadScriptResource,
					getResourceId = liferayFacesBridge.internal.getResourceId,
					runScript = liferayFacesBridge.internal.runScript;

				// Lazily initialize the sets of resources that already exist on the page.
				if (scriptSrcSet === null || styleSheetHrefSet === null) {

					var createStringSet = liferayFacesBridge.internal.createStringSet;

					if (scriptSrcSet === null) {

						if (scriptSrcSet === null) {
							scriptSrcSet = createStringSet();
						}

						var scripts = headElement.getElementsByTagName('script');

						for (var i = 0; i < scripts.length; i++) {

							if (isScriptResource(scripts[i])) {
								stringSetAdd(scriptSrcSet, scripts[i].getAttribute('src'));
							}
						}
					}

					if (styleSheetHrefSet === null) {

						styleSheetHrefSet = createStringSet();
						var links = headElement.getElementsByTagName('link');

						for (var i = 0; i < links.length; i++) {

							if (isStyleSheetResource(links[i])) {
								stringSetAdd(styleSheetHrefSet, links[i].getAttribute('href'));
							}
						}
					}
				}

				// TODO check body resources
				// TODO, make sure this doesn't cause scripts to load
				// TODO, add data-senna-track to js and css resources when loaded via ajax
				// According to http://stackoverflow.com/questions/10585029/parse-a-html-string-with-js
				// the best way to parse a string as html (in a cross-browser compatible way) is to
				// create a temporary new <html> element and insert the string within that <html>
				// element.
				tempElement.innerHTML = "<html><head>" + headResourcesAsString +
						"</head><body></body></html>";

				var tempHeadElement = tempElement.getElementsByTagName('head')[0],
					scripts = tempHeadElement.getElementsByTagName('script'),
					links = tempHeadElement.getElementsByTagName('link'),
					styles = tempHeadElement.getElementsByTagName('style');

				for (var j = 0; j < links.length; j++) {

					var href = links[j].getAttribute('href'),
						resourceId = getResourceId(href);

					if (isStyleSheetResource(links[j]) &&
							!stringSetContains(styleSheetHrefSet, resourceId)) {

						stringSetAdd(styleSheetHrefSet, resourceId);
						headElement.appendChild(links[j].cloneNode(true));
					}
				}

				if (renderAllMarkup) {

					for (var j = 0; j < styles.length; j++) {

						if (isInlineStyleSheet(styles[j])) {
							headElement.appendChild(styles[j].cloneNode(true));
						}
					}
				}

				for (var j = 0; j < scripts.length; j++) {

					if (isScriptResource(scripts[j])) {

						var src = scripts[j].getAttribute('src'),
							resourceId = getResourceId(src);

						if (!stringSetContains(scriptSrcSet, resourceId)) {

							stringSetAdd(scriptSrcSet, resourceId);
							loadScriptResource(scripts[j], headElement);
						}
					}
					else if (renderAllMarkup && isInlineScript(scripts[j], 'type', 'javascript')) {
						runScript(scripts[j].innerHTML, headElement, scripts[j]);
					}
				}
			}
		}
	};

	jsf.ajax.addOnEvent(function(event) {

		if (event.status === 'complete') {

			if (event.responseXML !== null) {

				var partialResponse = event.responseXML.getElementsByTagName('partial-response')[0],
					responseType = partialResponse.firstChild;

				if (responseType.nodeName === 'changes') {

					var changes = responseType.childNodes,
						updateId = null,
						renderAllMarkup = false;

					for (var i = 0; i < changes.length; i++) {

						var child = changes[i];

						if (child.nodeName === 'update' && child.hasAttribute('id')) {

							var updateId = child.getAttribute('id');

							if (updateId.indexOf('javax.faces.ViewState') === -1) {

								renderAllMarkup = (updateId === "javax.faces.ViewRoot" ||
									updateId === "javax.faces.ViewBody" ||
									(updateId.indexOf(':') === -1));
							}
						}
						else if (child.nodeName === 'extension') {

							var id = child.getAttribute('id');

							if (id === 'liferayFacesBridgeHeadResources' && updateId !== null) {

								var headResourcesAsString = child.firstChild.nodeValue;
								liferayFacesBridge.internal.loadHeadResources(headResourcesAsString, renderAllMarkup);
							}
						}
					}
				}
			}
		}
	});
}