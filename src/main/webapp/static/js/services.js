var app = angular.module("jorchive");

app.factory("filterService", ['$http', function ($http) {
    var service = {};

    service.getFilters = function () {
        return $http.get('filter')
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

    service.process = function(file, categoryName) {
        return $http.post('process/' + file, categoryName);
    };

    return service;
}]);

app.factory("navService", ['$http', function ($http) {
    var service = {};

    service.getCategories = function () {
        return $http.get('category')
    };

    service.setCategory = function (categoryId) {
        return $http.post('category', categoryId);
    };

    return service;
}]);
