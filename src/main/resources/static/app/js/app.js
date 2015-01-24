/*
 * Copyright (c) 2013 - 2015 Ventiv Technology
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

define(['jquery', 'angular', 'translations-en', 'ui-bootstrap-tpls', 'restangular', 'angular-translate', 'angular-ui-router', 'bootstrap', 'angular-xeditable'], function ($, angular, translations) {

    // Declare app level module which depends on filters, and services

    return angular.module('myApp', ['ui.bootstrap', 'restangular', 'pascalprecht.translate', 'ui.router', 'xeditable'])
        .config(function(RestangularProvider, $translateProvider, $stateProvider, $urlRouterProvider) {
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
            $urlRouterProvider.otherwise('/');
            $stateProvider
                .state('landing', {
                    url: '/',
                    templateUrl: '/app/partials/landing.html'
                })
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

        .run(function(editableOptions) {
            editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
        })

        .controller('MainCtrl', function($scope, $modal, Restangular) {
            var applicationInterface = Restangular.all('application');
            $scope.applications = applicationInterface.getList().$object;

            $scope.addApplication = function() {
                var addAppModal = $modal.open({
                    templateUrl: '/app/partials/addApplication.html',
                    controller: 'AddObjectController',
                    resolve: {
                        savingInterface: function() { return applicationInterface; }
                    }
                });

                addAppModal.result.then(function () {
                    $scope.applications = applicationInterface.getList().$object;
                });
            };

            $scope.addEnvironment = function(application) {
                $scope.currentApplication = application;

                var addEnvModal = $modal.open({
                    templateUrl: '/app/partials/addEnvironment.html',
                    controller: 'AddObjectController',
                    scope: $scope,
                    resolve: {
                        savingInterface: function() { return applicationInterface.one(application.id); }
                    }
                });

                addEnvModal.result.then(function (saveResponseData) {
                    application.environments.push(saveResponseData);
                });

                if (window.event)
                    window.event.stopPropagation();
            };
        })

        .controller('EnvironmentController', function($scope, Restangular, $stateParams, $modal, $translate) {
            var applicationInterface = Restangular.all('application');
            var environmentInterface = applicationInterface.one($stateParams.applicationId).one($stateParams.environmentId);
            $scope.environment = environmentInterface.get().$object;

            $scope.addPropertyGroup = function() {
                var addPropertyGroupModal = $modal.open({
                    templateUrl: '/app/partials/addPropertyGroup.html',
                    controller: 'AddObjectController',
                    resolve: {
                        savingInterface: function() { return environmentInterface; }
                    }
                });

                addPropertyGroupModal.result.then(function () {
                    $scope.environment = environmentInterface.get().$object;
                });
            };

            $scope.addProperty = function(propertyGroup) {
                propertyGroup.allProperties.push({key: undefined, value: undefined});

                if (window.event)
                    window.event.stopPropagation();
            };

            $scope.savePropertyKey = function(propertyGroup, property, newValue) {
                if (newValue) {
                    property.key = newValue;
                    var promise = environmentInterface.customPUT(propertyGroup);
                    promise.then(function(data) {
                        property.id = _.find(data.allProperties, function(e) { return e.key == newValue; }).id
                    });

                    return promise;
                } else
                    return "Property Key Needs to be a value";  // TODO: i18n
            };

            $scope.savePropertyValue = function(propertyGroup, property, newValue) {
                property.value = newValue;
                return environmentInterface.customPUT(propertyGroup);
            };

            $scope.getIconInfo = function(property) {
                if ($scope.iconInfo === undefined)
                    $scope.iconInfo = {};

                if ($scope.iconInfo[property.key] === undefined) {
                    var propertyIconInfo = {
                        inheritance: {},
                        delete: {},
                        encrypt: {}
                    };
                    $scope.iconInfo[property.key] = propertyIconInfo;

                    // Derive Inheritance
                    if (property.inheritanceType === 'Inherited') {
                        propertyIconInfo.inheritance.icon = 'glyphicon-arrow-up';
                        $translate('INHERITS_FROM', {property: property}).then(function(data) { propertyIconInfo.inheritance.toolTip = data });
                    } else if (property.inheritanceType === 'Overridden' || property.inheritanceType === 'OverriddenUnchanged') {
                        propertyIconInfo.inheritance.icon = 'glyphicon-share-alt';
                        $translate('OVERRIDES_ORIGINAL_VALUE', {property: property}).then(function(data) { propertyIconInfo.inheritance.toolTip = data });
                    }

                    // Derive Delete
                    if (property.inheritanceType === 'Overridden' || property.inheritanceType === 'OverriddenUnchanged') {
                        propertyIconInfo.delete.icon = 'glyphicon-trash';
                        $translate('REVERT_TO_ORIGINAL_VALUE', {property: property}).then(function(data) { propertyIconInfo.delete.toolTip = data });
                    } else if (property.inheritanceType === 'New') {
                        propertyIconInfo.delete.icon = 'glyphicon-trash';
                        $translate('DELETE_PERMANENTLY', {property: property}).then(function(data) { propertyIconInfo.delete.toolTip = data });
                    }

                    // Derive Encryption
                    if (property.inheritanceType != 'Inherited' && property.encrypted == false) {
                        propertyIconInfo.encrypt.icon = 'glyphicon-lock';
                        $translate('ENCRYPT_PROPERTY', {property: property}).then(function(data) { propertyIconInfo.encrypt.toolTip = data });
                    } else if (property.inheritanceType != 'Inherited' && property.encrypted == true) {
                        propertyIconInfo.encrypt.icon = 'glyphicon-ok-sign';
                        $translate('DECRYPT_PROPERTY', {property: property}).then(function(data) { propertyIconInfo.encrypt.toolTip = data });
                    }
                }

                return $scope.iconInfo[property.key];
            }
        })

        .controller('AddObjectController', function($scope, $modalInstance, savingInterface) {
            $scope.addingObject = { id: undefined, name: undefined };
            $scope.alerts = [];

            $scope.save = function() {
                savingInterface.customPUT($scope.addingObject).then(
                    function success(data) {
                        $modalInstance.close(data);
                    },
                    function error(response) {
                        console.log("Error Adding New Object Group: ", response);
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
        });
});