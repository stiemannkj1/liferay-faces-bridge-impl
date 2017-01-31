/**
 * Comments.
 */
jsf.ajax.addOnEvent(function(event) {

	if (event.status === 'complete' && event.responseXML !== null) {

		var updateResourcesElement = event.responseXML.getElementById('javax.faces.Resource');

		if (updateResourcesElement && updateResourcesElement.firstChild &&
			updateResourcesElement.firstChild.nodeValue) {

			var resources = updateResourcesElement.firstChild.nodeValue;

			// According to http://stackoverflow.com/questions/10585029/parse-a-html-string-with-js the best way to
			// parse a string as html (in a cross-browser compatible way) is to create a temporary new <div> element
			// and insert the string within that <div> element.

			// TODO consider performance improvement of loading resources ourselves and removing this node so mojarra
			// won't handle it at all (currently the JS is parsing this twice).
			var parserElement = document.createElement('div');
			parserElement.innerHTML = resources;
			var updateResources = Array.prototype.slice.call(parserElement.getElementsByTagName('link'), 0);
			updateResources = updateResources.concat(
				Array.prototype.slice.call(parserElement.getElementsByTagName('script'), 0));
			var loadedResourceIds = null;

			for (var i = 0; i < updateResources.length; i++) {

				var updateResourceId = updateResources[i].getAttribute('data-liferay-faces-bridge-resource-id');

				if (updateResourceId) {

					if (!loadedResourceIds) {

						var loadedResources = Array.prototype.slice.call(document.getElementsByTagName('link'), 0);
						loadedResources = loadedResources.concat(
							Array.prototype.slice.call(document.getElementsByTagName('script'), 0));
						loadedResourceIds = [];

						for (var j = 0; j < loadedResources.length; j++) {

							var loadedResourceId =
								loadedResources[j].getAttribute('data-liferay-faces-bridge-resource-id');

							if (loadedResourceId) {
								loadedResourceIds.push(loadedResourceId);
							}
						}
					}

					if (loadedResourceIds.indexOf(updateResourceId) > -1) {
						parserElement.removeChild(updateResources[i]);
					}
				}
			}

			if (parserElement.firstChild) {

				// jsf.js expects self-closing link tags, so replace HTML4 compliant unclosed link tags with XHTML/HTML5
				// compliant self-closing link tags
				updateResourcesElement.firstChild.nodeValue = parserElement.innerHTML
						.replace(/([^/t])(>\s*<[^/])/igm, '$1/$2')
						.replace(/([^/t])>$/igm, '$1/>');

				if (jsf.getProjectStage() === 'Development') {
					console.log(updateResourcesElement.outerHTML);
				}
			}
			else {
				updateResourcesElement.parentNode.removeChild(updateResourcesElement);
			}
		}
	}
});

// TODO spec language about this in 378
jsf.getProjectStage = function() {

	var scripts = document.getElementsByTagName("script");
	var jsfProjectStage;

	for (var i = 0; i < scripts.length; i++) {
		jsfProjectStage = scripts[i].getAttribute('data-jsf-project-stage');

		if (jsfProjectStage) {
			break;
		}
	}

	if (!jsfProjectStage) {
		jsfProjectStage = 'Production';
	}

	return jsfProjectStage;
};
