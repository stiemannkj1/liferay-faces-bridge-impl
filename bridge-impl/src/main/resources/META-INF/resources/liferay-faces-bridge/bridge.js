jsf.ajax.addOnEvent(function(event) {

	if (event.status === 'complete' && event.responseXML !== null) {

		var updateResourcesElement = event.responseXML.getElementById('javax.faces.Resource');

		if (updateResourcesElement && updateResourcesElement.firstChild &&
			updateResourcesElement.firstChild.nodeValue) {

			var resources = updateResourcesElement.firstChild.nodeValue;

			// According to http://stackoverflow.com/questions/10585029/parse-a-html-string-with-js the best way to
			// parse a string as html (in a cross-browser compatible way) is to create a temporary new <html> element
			// and insert the string within that <html> element.
			var tempElement = document.createElement('html');
			tempElement.innerHTML = "<html><head>" + resources + "</head><body></body></html>";
			var tempHeadElement = tempElement.getElementsByTagName('head')[0];
			var updateResources = Array.prototype.slice.call(tempHeadElement.getElementsByTagName('link'), 0);
			updateResources = updateResources.concat(
				Array.prototype.slice.call(tempHeadElement.getElementsByTagName('script'), 0));
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
						tempHeadElement.removeChild(updateResources[i]);
					}
				}
			}

			if (tempHeadElement.firstChild) {
				updateResourcesElement.firstChild.nodeValue = tempHeadElement.innerHTML;
			}
			else {
				updateResourcesElement.parentNode.removeChild(updateResourcesElement);
			}
		}
	}
});
