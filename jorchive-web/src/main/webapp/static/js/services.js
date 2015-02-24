var app = angular.module("jorchive");

app.factory("filterService", ['$http', function ($http) {
    var service = {};

    service.getFilters = function () {
        return $http.get('filters')
    };

    service.setFilter = function (type) {
        return $http.post('filter', type)
    };

    return service;
}]);

app.factory("fileService", ['$http', function ($http) {
    var service = {};

    service.getFiles = function () {
        return $http.get('files')
    };

    return service;
}]);

app.factory("navService", ['$http', function ($http) {
    var service = {};

    service.getCateggories = function () {
        return $http.get('categories')
    };

    service.setCategory = function (categoryId) {
        return $http.post('category', categoryId);
    };

    return service;
}]);
