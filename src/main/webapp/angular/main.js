window.isDetached = location.search.indexOf("detached=true") > -1 || location.hash.indexOf("detached=true") > -1 || location.protocol == 'file:';

angular.module('AppConfig', ['Resources', 'AppConfigDirectives', 'ui.bootstrap.modal', 'ui.bootstrap.tpls', 'ngRoute'])
    .controller("AppConfigCtrl", function($scope, $modal, ApplicationController, AppConfigModel) {

        $scope.data = AppConfigModel;

        if (AppConfigModel.applicationList == null) {
            ApplicationController.getAllApplications(function(data) {
                if (data.applicationList.length == 0) {
                    var modalInstance = $modal.open({ templateUrl: 'angular/dialogs/addApplication.html', controller: 'AddApplicationCtrl', keyboard: false, backdrop: true });
                } else {
                    AppConfigModel.applicationList = data.applicationList;
                    $scope.selectApplication(0);
                }
            });
        }

        $scope.selectApplication = function(appIndex) {
            var application = AppConfigModel.applicationList[appIndex];

            ApplicationController.getApplicationDetail(application.name, function(data) {
                angular.forEach(AppConfigModel.applicationList, function(otherApp) {
                    otherApp.active = "";
                });
                application.active = "active";

                AppConfigModel.activeApp = data.application;
                AppConfigModel.activeAppIndex = appIndex;
            });
        }

        $scope.saveApplication = function() {
            var oldName = AppConfigModel.applicationList[AppConfigModel.activeAppIndex].name;
            ApplicationController.saveApplication(oldName, AppConfigModel.activeApp.name, function(data) {
                AppConfigModel.applicationList[AppConfigModel.activeAppIndex] = data.application;
                AppConfigModel.applicationList[AppConfigModel.activeAppIndex].active = "active";
            });
        }

    })

    .controller("AddApplicationCtrl", function($scope, ApplicationController, AppConfigModel, $modalInstance) {
        $scope.addApplication = function(applicationName) {
            if (applicationName) {
                ApplicationController.saveApplication(applicationName, null, function(data) {
                    AppConfigModel.applicationList = [ data.application ]
                    AppConfigModel.activeApp = data.application;
                    $modalInstance.close();
                });
            }
        }
    })

    .factory("AppConfigModel", function() {
        return {
            applicationList: null,
            activeApp: null,
            activeAppIndex: -1
        }
    })

    .config(function($routeProvider) {
        $routeProvider.
            when('/', {controller:'AppConfigCtrl', templateUrl:'angular/application/ApplicationDetails.html'}).
            when('/env/:environmentId', {controller:'AppConfigCtrl', templateUrl:'angular/environment/EnvironmentDetails.html'}).
            otherwise({redirectTo:'/'});
    });