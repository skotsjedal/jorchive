console.log("init app");

var app = angular.module("jorchive", ['ngRoute', 'ngResource',
    'spring-security-csrf-token-interceptor', 'angular-loading-bar']);

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
