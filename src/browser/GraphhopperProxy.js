function getJSON(url, success, error) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.onreadystatechange = function (aEvt) {
        if (xhr.readyState == 4) {
            if(xhr.status >= 200 && xhr.status <= 299) {
                try {
                    var response = {
                        status: xhr.status,
                        statusText: xhr.statusText,
                        data: JSON.parse(xhr.responseText)
                    };
                    success(response);
                } catch (err) {
                    error(err);
                }
            } else {
                error({
                    status: xhr.status,
                    statusText: xhr.statusText,
                    data: xhr.responseText
                });
            }
        }
    };
    xhr.send(null);
}


module.exports = {
    settings: {
        apiUrl: 'https://graphhopper.com/api/1',
        apiKey: ''
    },
    "test": function (success, error, args) {
        success(args);
    },
    "initialize": function (success, error, args) {
		var settings = args[0];
        module.exports.settings.apiUrl = settings.apiUrl || module.exports.settings.apiUrl;
        module.exports.settings.apiKey = settings.apiKey || module.exports.settings.apiKey;
        success();
    },
    "destroy": function (success, error) {
        success();
    },

    "set-map-file-path": function (success, error, args) {
        success();
    },
    "set-api-url": function (success, error, args) {
		var apiUrl = args[0];
        module.exports.settings.apiUrl = apiUrl || module.exports.settings.apiUrl;
        success();
    },
    "set-api-key": function (success, error, args) {
		var apiKey = args[0];
        module.exports.settings.apiKey = apiKey || module.exports.settings.apiKey;
        success();
    },

    "get-routes": function (success, error, args) {
		var request = args[0];
        var url = module.exports.settings.apiUrl + '/route';
        var parameters = [];
        for(var i=0; i<request.points.length; i++) {
            var point = request.points[i];
            parameters.push('points=' + point.lat + ',' + point.lng);
        }
        parameters.push('vehicle=' + request.vehicle || 'car');
        parameters.push('debug=true');
        parameters.push('key=' + module.exports.settings.apiKey);
        parameters.push('type=json');
        parameters.push('points_encoded=false');
        url = url + '?' + parameters.join('&');
        getJSON(url, function(response) {
			success(response.data);
		}, error);
    }
};

require("cordova/exec/proxy").add("GraphhopperPlugin", module.exports);