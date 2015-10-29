
app.controller('RegisterController', function ($scope, $window, $http) {

  $scope.user = {};
  $scope.errors = {};
  $scope.accountCreated = false;

  $scope.createAccount = function () {
    
    console.log("createAccount", $scope.user);
   
    $scope.errors = {};
    
    var account = {
        "firstName":$scope.user.firstname,
        "lastName": $scope.user.lastname,
        "taxCode":$scope.user.fiscalcode,
        "email":$scope.user.email,
        "requestedClearPassword":$scope.user.password
    }

    app.net.createAccount(account, $http, function(err, acc){

        console.log("callback: ", err);
        if(err)
        {
           $scope.errors.message = err.message;
           $scope.errors.details = err.details;
           
           return;
        }

        $scope.accountCreated = true;
    });

  };

});


