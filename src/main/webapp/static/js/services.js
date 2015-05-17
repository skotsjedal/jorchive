var app = angular.module("jorchive");

app.factory("fileService", ['$http', function ($http) {
    var service = {};

    service.getFiles = function () {
        return $http.get('files')
    };

    service.process = function (file, categoryName) {
        return $http.post('process/' + file, categoryName);
    };

    return service;
}]);

app.factory('Filter', ['$resource', function ($resource) {
    return $resource('/filter/:id', {id: '@id'}, {change: {method: 'POST'}});
}]);

app.factory('Category', ['$resource', function ($resource) {
    return $resource('/category/:id', {id: '@id'}, {change: {method: 'POST'}});
}]);