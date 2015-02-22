var app = angular.module("jorchive");

app.controller("JorchiveController", function ($scope, constantService, fileService) {
    var init = function () {
        constantService.getFilters().then(function (filters) {
            $scope.filters = filters;
        });
        getFiles();

    };

    var getFiles = function () {
        fileService.getFiles().then(function (response) {
            $scope.files = response;
        });
    };

    $scope.title = app.name;
    $scope.filter = function (filterType) {
        fileService.setFilter(filterType).then(function () {
            getFiles();
        });
    };

    init();
});
