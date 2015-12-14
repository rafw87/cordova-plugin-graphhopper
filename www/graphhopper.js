var exec = require('cordova/exec');

function graphhopper_plugin(){

};

graphhopper_plugin.prototype.test = function(arg1, arg2, callback){
    exec(callback || graphhopper_plugin.doNothing, graphhopper_plugin.doNothing, "GraphhopperPlugin", "test", [arg1, arg2]);
};

graphhopper_plugin.prototype.initialize = function(options, success, error){
    exec(success || graphhopper_plugin.doNothing, error || graphhopper_plugin.doNothing, "GraphhopperPlugin", "initialize", [options]);
};
graphhopper_plugin.prototype.onDestroy = function(absoluteMapFilePath, success, fail){
    exec(success || graphhopper_plugin.doNothing, error || graphhopper_plugin.doNothing, "GraphhopperPlugin", "destroy");
};


graphhopper_plugin.prototype.setMapFilePath = function(mapFilePath, success, error){
    exec(success || graphhopper_plugin.doNothing, error || graphhopper_plugin.doNothing, "GraphhopperPlugin", "set-map-file-path", [mapFilePath]);
};
graphhopper_plugin.prototype.setApiUrl = function(apiUrl, success, error){
    exec(success || graphhopper_plugin.doNothing, error || graphhopper_plugin.doNothing, "GraphhopperPlugin", "set-api-url", [apiUrl]);
};
graphhopper_plugin.prototype.setApiKey = function(apiKey, success, error){
    exec(success || graphhopper_plugin.doNothing, error || graphhopper_plugin.doNothing, "GraphhopperPlugin", "set-api-key", [apiKey]);
};


graphhopper_plugin.prototype.getRoutes = function(request, success, error){
    exec(success || graphhopper_plugin.doNothing, error || graphhopper_plugin.doNothing, "GraphhopperPlugin", "get-routes", [request]);
};



graphhopper_plugin.doNothing = function(){return;};

module.exports = new graphhopper_plugin();