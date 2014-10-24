/*
 * Copyright (c) 2014 Ventiv Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
'use strict';

define(['angular', 'translations-en', 'ui-bootstrap-tpls', 'restangular', 'angular-translate', 'angular-ui-router'], function (angular, translations) {

    // Declare app level module which depends on filters, and services

    return angular.module('myApp', ['ui.bootstrap', 'restangular', 'pascalprecht.translate', 'ui.router'])
        .config(function(RestangularProvider, $translateProvider, $stateProvider) {
            // Configure RESTAngular
            RestangularProvider.setBaseUrl("/api");
            RestangularProvider.setResponseExtractor(function(response, operation, what) {
                if (response['_embedded'])
                    return response['_embedded'][what];
                else
                    return response;
            });

            // Configure Translations
            $translateProvider.translations('en', translations).preferredLanguage('en');

            // Configure UI-Router
            $stateProvider
                .state('application', {
                    url: '/{applicationId}',
                    templateUrl: '/app/partials/application.html'
                })
                .state('environment', {
                    url: '/{applicationId}/{environmentId}',
                    templateUrl: '/app/partials/environment.html',
                    controller: 'EnvironmentController'
                });
        })

        .controller('MainCtrl', function($scope, $modal, Restangular) {
            var applicationInterface = Restangular.all('application');
            $scope.applications = applicationInterface.getList().$object;

            $scope.addApplication = function() {
                var addAppModal = $modal.open({
                    templateUrl: '/app/partials/addApplication.html',
                    controller: 'AddApplicationController',
                    resolve: {
                        applicationInterface: function() { return applicationInterface; }
                    }
                });

                addAppModal.result.then(function () {
                    $scope.applications = applicationInterface.getList().$object;
                });
            };
        })

        .controller('AddApplicationController', function($scope, $modalInstance, applicationInterface) {
            $scope.application = { id: undefined, name: undefined };
            $scope.alerts = [];

            $scope.save = function() {
                applicationInterface.customPUT($scope.application).then(
                    function success() {
                        $modalInstance.close();
                    },
                    function error(response) {
                        console.log("Error Adding New Application: ", response);
                        $scope.alerts.push({ msg: response.data.message, type: 'danger' });
                    }
                );
            };

            $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            }
        })

        .controller('EnvironmentController', function($scope, Restangular, $stateParams) {
            var applicationInterface = Restangular.all('application');
            $scope.environment = applicationInterface.one($stateParams.applicationId).one($stateParams.environmentId).get();
        });
});