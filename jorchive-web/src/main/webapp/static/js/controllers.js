var app = angular.module("jorchive");

app.controller("JorchiveController", ['$scope', '$rootScope', 'filterService', 'fileService', function ($scope, $rootScope, filterService, fileService) {
    var init = function () {
        $scope.title = 'Initializing';
        filterService.getFilters().success(function (resp) {
            $scope.filters = resp;
        });
    };

    $rootScope.$on('refreshFiles', function (event, categoryName) {
        getFiles();
        $scope.title = categoryName;
    });

    var getFiles = function () {
        fileService.getFiles().success(function (resp) {
            $scope.files = resp;
        });
    };

    $scope.filter = function (filterType) {
        filterService.setFilter(filterType).success(function () {
            getFiles();
        });
    };

    init();
}]);

app.controller('NavController', ['$scope', '$rootScope', 'navService', function ($scope, $rootScope, navService) {
    var init = function () {
        $scope.title = app.name;
        navService.getCateggories().success(function (categories) {
            $scope.categories = categories;
            $scope.setCategory($scope.categories[0].name);
        });
    };

    $scope.setCategory = function (categoryName) {
        navService.setCategory(categoryName).success(function () {
            $rootScope.$broadcast('refreshFiles', categoryName);
        });
    };

    init();
}]);
