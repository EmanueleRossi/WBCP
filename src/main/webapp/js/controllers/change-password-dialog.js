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
app.controller('ChangePasswordDialogController', function ($scope, $modalInstance, $http) {

    $scope.password = null;
    $scope.passwordConfirm = null;

    $scope.successMessage = null;
    $scope.errorMessage = null;

    $scope.doChange = function () {

        $scope.successMessage = null;
        $scope.errorMessage = null;

        if($scope.password && $scope.password != '' &&
            $scope.password == $scope.passwordConfirm) {


            var account = {
                email: app.auth.getCurrentUser().user.email,
                requestedClearPassword: $scope.password
            };

            var req = {
                method: 'POST',
                url: app.net.host + '/user/changepwd',
                data: account,
                headers: {
                    'AuthorizationToken': app.auth.getAuthToken()
                }
            };

            $http(req).then(
                function successCallback(response){

                    $scope.successMessage = "Password modificata con successo!";

                }, function errorCallback(response){
                    console.log(response);
                    $scope.errorMessage = response.data.localizedMessage;
                });
        }
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
  
});