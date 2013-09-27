angular.module('Resources', ['ngResource']).
    factory('ApplicationController', function($resource) {
    	var baseUrl = "";
    	if (window.isDetached)
    		baseUrl = "http://localhost:8080"
    	
        var allApplications = $resource(baseUrl + '/AppConfig/app/application/');
        var applicationResource = $resource(baseUrl + '/AppConfig/app/application/:applicationName', { applicationName: '@applicationName'})
        var saveResource = $resource(baseUrl + '/AppConfig/app/application/:applicationName', { applicationName: '@applicationName', name: '@name'})

        return {
            getAllApplications: function(successCallback, errorCallback) {
                return allApplications.get({}, successCallback, errorCallback);
            },

            getApplicationDetail: function(applicationName, successCallback, errorCallback) {
                return applicationResource.get({applicationName: applicationName}, successCallback, errorCallback);
            },

            saveApplication: function(applicationName, newApplicationName, successCallback, errorCallback) {
                if (newApplicationName != null)
                    return saveResource.save({applicationName: applicationName, name: newApplicationName}, null, successCallback, errorCallback);
                else
                    return applicationResource.save({applicationName: applicationName}, null, successCallback, errorCallback);
            }
        }
    });