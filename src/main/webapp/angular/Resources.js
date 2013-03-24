angular.module('Resources', ['ngResource']).
    factory('ApplicationController', function($resource) {
        var allApplications = $resource('/AppConfig/application/');
        var applicationResource = $resource('/AppConfig/application/:applicationName', { applicationName: '@applicationName'})
        var saveResource = $resource('/AppConfig/application/:applicationName', { applicationName: '@applicationName', name: '@name'})

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