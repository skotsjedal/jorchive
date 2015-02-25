var app = angular.module("jorchive");

app.controller("JorchiveController", ['$scope', '$rootScope', 'filterService', 'fileService', function ($scope, $rootScope, filterService, fileService) {
    var init = function () {
        $scope.title = 'Initializing';
        filterService.getFilters().success(function (resp) {
            $scope.filters = resp;
        });
    };

    $rootScope.$on('refreshFiles', function (event, category) {
        getFiles();
        $scope.title = category.name;
    });

    var getFiles = function () {
        $scope.files = [];
        fileService.getFiles().success(function (resp) {
            $scope.files = resp;
        });
    };

    $scope.filter = function (filterType) {
        filterService.setFilter(filterType).success(function () {
            getFiles();
        });
    };

    $scope.process = function (event, file, categoryName) {
        fileService.process(file, categoryName).success(function (resp) {
            $(event.target).removeClass('label-info');
            $(event.target).addClass('label-success');
        });
    };

    init();
}]);

app.controller('NavController', ['$scope', '$rootScope', 'navService', function ($scope, $rootScope, navService) {
    var init = function () {
        $scope.title = app.name;
        navService.getCategories().success(function (categories) {
            $rootScope.categories = categories;
            $scope.setCategory(categories[0]);
        });
    };

    $scope.setCategory = function (category) {
        navService.setCategory(category.name).success(function () {
            $rootScope.$broadcast('refreshFiles', category);
        });
    };

    init();
}]);
