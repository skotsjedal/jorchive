var app = angular.module("jorchive");

app.factory("constantService", function ($http) {
    var service = {};

    service.getFilters = function () {
        return $http.get('filters')
            .then(function (reponse) {
                return reponse.data;
            });
    };

    return service;
});

app.factory("fileService", function ($http) {
    var service = {};

    service.getFiles = function () {
        return $http.get('files')
            .then(function (reponse) {
                return reponse.data;
            });
    };

    service.setFilter = function (type) {
        return $http.post('filter', type)
            .then(function (reponse) {
                return reponse.data;
            }, function (response) {
                console.log(response);
            });
    };

    return service;
});
