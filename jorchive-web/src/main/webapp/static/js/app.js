console.log("init app");

var app = angular.module("jorchive", ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/main', {
            templateUrl: 'html/main.html',
            controller: 'JorchiveController'
        }).
        otherwise({
            redirectTo: '/main'
        })
}]);

app.controller("JorchiveController", function ($scope) {
    $scope.title = "Wee";
});
