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
			 * @returns {boolean} true if the elements type attribute contains the string type that was passed to this function.
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
			}
		}
	};

	jsf.ajax.addOnEvent(function(event) {

		if (event.status === 'complete') {

			// TODO only do this after you are sure that we are rerendering an entire portlet (maybe check id for ':' if it has none then we are rerendering the whole thing, plus we can find out the id of our portlet that way)
			// TODO also get scripts in the case where they are rendered in a <div class="liferay-faces-bridge-relocated-resources">
			// TODO if stuff is in the body, then check if we are attaching it to the right portlet
			var headResources = liferayFacesBridge.internal.headResources,
				headElement = document.getElementsByTagName("head")[0],
				scriptSrcSet = headResources.scriptSrcSet,
				styleSheetHrefSet = headResources.styleSheetHrefSet,
				stringSetAdd = liferayFacesBridge.internal.stringSetAdd,
				stringSetContains = liferayFacesBridge.internal.stringSetContains,
				isScriptResource = liferayFacesBridge.internal.isScriptResource,
				isInlineScript = liferayFacesBridge.internal.isInlineScript,
				isStyleSheetResource = liferayFacesBridge.internal.isStyleSheetResource,
				isInlineStyleSheet = liferayFacesBridge.internal.isInlineStyleSheet;

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


				// TODO If stuff needs to get relocated to the body, then CSS should be checked every time because it can be removed from the body section.
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

			if (event.responseXML !== null) {

				var partialResponse = event.responseXML.getElementsByTagName('partial-response')[0],
					responseType = partialResponse.firstChild;

				if (responseType.nodeName === 'changes') {

					var changes = responseType.childNodes,
						portletNamespace = null,
						update = null;

					for (var i = 0; i < changes.length; i++) {

						var child = changes[i];

						if (child.nodeName === 'update' && (child.hasAttribute('id') &&
								child.getAttribute('id').indexOf(':') === -1)) {

							portletNamespace = child.getAttribute('id');
							var updateCDATA = child.firstChild;

							if (updateCDATA.nodeName === '#cdata-section') {
								update = child;
							}
							else {
								console.log('// TODO throw error because there is no CDATA section');
							}
						}
						else if (portletNamespace !== null && child.nodeName === 'extension') {

							var id = child.getAttribute('id');

							if (id === 'liferayFacesBridgeHeadResources' || id === 'liferayFacesBridgeRelocatedResources') {
								
								// TODO initialize set here

								if (update !== null) {

									// According to http://stackoverflow.com/questions/10585029/parse-a-html-string-with-js
									// the best way to parse a string as html (in a cross-browser compatible way) is to
									// create a temporary new <html> element and insert the string within that <html>
									// element.
									var headResourceText = child.firstChild.nodeValue,
										tempElement = document.createElement('html');

									tempElement.innerHTML = "<html><head>" + headResourceText +
											"</head><body></body></html>";

									var tempHeadElement = tempElement.getElementsByTagName('head')[0],
										scriptsText = '',
										scripts = tempHeadElement.getElementsByTagName('script'),
										links = tempHeadElement.getElementsByTagName('link'),
										styles = tempHeadElement.getElementsByTagName('style');

									for (var j = 0; j < scripts.length; j++) {

										if (isScriptResource(scripts[j])) {
												
											if (!stringSetContains(scriptSrcSet, scripts[j].getAttribute('src'))) {

												stringSetAdd(scriptSrcSet, scripts[j].getAttribute('src'));
												scriptsText = scriptsText + scripts[j].outerHTML.replace(/[&]amp;/g, '&');
											}
										}
										else if (isInlineScript(scripts[j], 'type', 'javascript')) {
											scriptsText = scriptsText + scripts[j].outerHTML;
										}
									}

									// If there are scripts, insert them inside the first element of the partial
									// response update so that jsf.js will load them correctly.
									if (scriptsText !== '') {

										var updateText = update.firstChild.nodeValue,
											scriptsInsertionPoint = updateText.indexOf('>') + 1;

										// If scriptsInsertionPoint === 0 then updateText.indexOf('>') === -1 and
										// no '>' character was found.
										if (scriptsInsertionPoint === 0) {
											console.log('TODO throw new error since we should be updating the whole DOM, so there must be at least one <div> for the portlet');
										}

										update.firstChild.nodeValue = updateText.substring(0, scriptsInsertionPoint) +
												scriptsText +
												updateText.substring(scriptsInsertionPoint, updateText.length);
									}

									for (var j = 0; j < links.length; j++) {

										if (isStyleSheetResource(links[j]) &&
												!stringSetContains(styleSheetHrefSet, links[j].getAttribute('href'))) {

											stringSetAdd(styleSheetHrefSet, links[j].getAttribute('href'));
											headElement.appendChild(links[j].cloneNode(true));
										}
									}

									for (var j = 0; j < styles.length; j++) {

										if (isInlineStyleSheet(styles[j])) {
											headElement.appendChild(styles[j].cloneNode(true));
										}
									}	
								}
								else {
									console.log('// TODO throw error since we are attempting to render the head resources by none of the page is being updated.');
								}
							}
						}
					}
				}
			}
		}
	});
}