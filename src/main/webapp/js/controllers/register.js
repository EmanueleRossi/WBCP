/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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


