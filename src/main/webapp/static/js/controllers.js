var app = angular.module("jorchive");

app.controller("JorchiveController", ['$scope', '$rootScope', 'fileService', 'Filter', function ($scope, $rootScope, fileService, Filter) {
    var init = function () {
        $scope.title = 'Initializing';
        var filters = Filter.query(function () {
            $scope.filters = filters;
        });
    };

    $rootScope.$on('refreshFiles', function (event, category) {
        getFiles();
        $scope.title = category;
    });

    var getFiles = function () {
        $scope.files = [];
        fileService.getFiles().success(function (resp) {
            $scope.files = resp;
        });
    };

    $scope.filter = function (filterType) {
        Filter.change({id: filterType});
        getFiles();
    };

    $scope.process = function (event, fileId, status) {
        // TODO add currently processing list in the ui
        status.status = 'PROCESSING';
        fileService.process(fileId, status.categoryName)
            .success(function (resp) {
                status.status = 'PROCESSED';
            }).error(function (resp) {
                status.status = 'FAILED';
            });
    };

    init();
}]);

app.controller('NavController', ['$scope', '$rootScope', '$timeout', 'Category', function ($scope, $rootScope, $timeout, Category) {
    var init = function () {
        $scope.title = app.name;
        var categories = Category.query(function () {
            $rootScope.categories = categories;
            // JorchiveController must be done loading
            $timeout(function () {
                $scope.setCategory(categories[0].name);
            }, 10);
        });
    };

    $scope.setCategory = function (category) {
        Category.change({id: category});
        $rootScope.$broadcast('refreshFiles', category);
    };

    init();
}]);
