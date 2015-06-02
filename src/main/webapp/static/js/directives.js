var app = angular.module("jorchive");

app.directive('fileStatus', function () {
    function mapStatusToClass(status) {
        switch (status) {
            case 'NONE':
                return 'label-default';
            case 'CONTAINED':
                return 'label-info';
            case 'PROCESSING':
                return 'label-warning';
            case 'PROCESSED':
                return 'label-success';
            default:
                return 'label-error';
        }
    }

    return {
        restrict: 'A',
        link: function (scope, element, attributes) {
            scope.$watch(attributes.fileStatus, function(newVal, oldVal) {
                element.removeClass(mapStatusToClass(oldVal));
                element.addClass(mapStatusToClass(newVal));
            });
        }
    }
});
