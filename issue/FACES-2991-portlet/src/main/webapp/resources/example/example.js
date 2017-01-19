(function() {

	var duplicateResourcesLoaded = false;
	var loadedResources = Array.prototype.slice.call(document.getElementsByTagName('link'), 0);
	loadedResources = loadedResources.concat(Array.prototype.slice.call(document.getElementsByTagName('script'), 0));
	var loadedResourceIds = [];

	for (var j = 0; j < loadedResources.length; j++) {

		var loadedResourceId = loadedResources[j].getAttribute('data-liferay-faces-bridge-resource-id');

		if (loadedResourceIds.includes(loadedResourceId)) {

			duplicateResourcesLoaded = true;
			break;
		}

		if (loadedResourceId) {
			loadedResourceIds.push(loadedResourceId);
		}
	}

	var div = document.getElementById('FACES-2991-results');

	if (div) {

		if (!duplicateResourcesLoaded) {
			div.innerHTML = '<p>Test: <span id="FACES-2991-test-name">FACES-2991</span></p>' +
							'<p>Status: <span id="FACES-2991-result-status">SUCCESS</span></p>' +
							'<p>Detail: <span id="FACES-2991-result-detail">Dynamic JavaScript loaded successfully.</span></p>';
		}
		else {
			div.innerHTML = '<p>Test: <span id="FACES-2991-test-name">FACES-2991</span></p>' +
							'<p>Status: <span id="FACES-2991-result-status">FAILED</span></p>' +
							'<p>Detail: <span id="FACES-2991-result-detail">Duplicate resources loaded.</span></p>';
		}
	}
})();