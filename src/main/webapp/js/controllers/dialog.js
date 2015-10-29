

app.controller('DialogController', function ($scope, $modalInstance, $http, params) {

  $scope.cancelButtonLabel = params.button

  $scope.hasCancelButton = function() {

  }
  
  $scope.ok = function () {
    $modalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});