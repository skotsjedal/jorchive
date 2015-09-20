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
        fileService.process(fileId, status.categoryName)
            .success(function (resp) {
                $rootScope.$broadcast('processing', fileId, status);
            }).error(function (resp) {
                status.status = 'FAILED';
            });
    };

    init();
}]);

app.controller('NavController', ['$scope', '$rootScope', '$timeout', '$interval', 'Category', 'Progress', function ($scope, $rootScope, $timeout, $interval, Category, Progress) {
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

    var progressList = [];
    $scope.progressList = progressList;

    var findProgress = function (id) {
        var target = undefined;
        progressList.forEach(function (item) {
            if (item.id == id) {
                target = item;
            }
        });
        return target;
    };

    $rootScope.$on('processing', function (event, id, status) {
        status.status = 'PROCESSING';
        progressList.push(Progress.get({id: id}));
        const refreshRate = 1000;
        const timeout = 5000;
        var intervalPromise = $interval(function () {
            Progress.get({id: id}, function (response) {

                var previous = findProgress(id);
                var index = progressList.indexOf(previous);
                progressList.splice(index, 1);
                progressList.push(response);

                if (response.done) {
                    status.status = 'PROCESSED';
                    $interval.cancel(intervalPromise);
                    $timeout(function () {
                        var index = progressList.indexOf(previous);
                        progressList.splice(index, 1);
                    }, timeout);
                }
            }, function () {
                status.status = 'FAILED';
            });
        }, refreshRate);
    });

    init();
}]);
