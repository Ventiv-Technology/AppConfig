angular.module('AppConfig', ['Resources', 'ui.bootstrap.modal'])
    .controller("AppConfigCtrl", function($scope, $dialog, ApplicationController, AppConfigModel) {

        $scope.data = AppConfigModel;

        if (AppConfigModel.applicationList == null) {
            ApplicationController.getAllApplications(function(data) {
                if (data.applicationList.length == 0) {
                    var newApplicationDialog = $dialog.dialog({ keyboard: false, backdrop: false });
                    newApplicationDialog.open('dialogs/addApplication.html', 'AddApplicationCtrl');
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

    .controller("AddApplicationCtrl", function($scope, ApplicationController, AppConfigModel, dialog) {
        $scope.addApplication = function(applicationName) {
            if (applicationName) {
                ApplicationController.saveApplication(applicationName, null, function(data) {
                    AppConfigModel.applicationList = [ data.application ]
                    AppConfigModel.activeApp = data.application;
                    dialog.close();
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
            when('/', {controller:'AppConfigCtrl', templateUrl:'application/ApplicationDetails.html'}).
            otherwise({redirectTo:'/'});
    });