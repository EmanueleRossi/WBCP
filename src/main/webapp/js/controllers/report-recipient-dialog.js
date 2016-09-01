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
app.controller('ReportRecipientDialogController', function ($scope, $modalInstance, $http, $location) {

    $scope.selectedOrganization = null;

    $scope.organizations = null;

    $scope.recipient = null;

    $scope.selected =  {
        recipient : null
    };

    $scope.onSelected = function () {
        $scope.selected.recipient = null;
    };

    $scope.orgInputFormatter = function(item)
      {
        if(item != null)
            return item.name;
      };

    function init() {

        var host = $location.host().replace("www.", "");

        var req = {
            method: 'GET',
            url: app.net.host + '/organization/findAll',
            headers: {
                'AuthorizationToken': app.auth.getAuthToken()
            }
        };

        return $http(req).then(function(response){

            if(response.data != null) {

                $scope.organizations = response.data.filter(function(org){
                    if(host != 'localhost') {
                        return org.uiStyle == host;
                    }
                    return true;
                });

                if($scope.organizations.length == 1) {
                    $scope.selectedOrganization = $scope.organizations[0];
                };
            }
        });
    };

    init();


    $scope.ok = function () {
        $modalInstance.close( {recipient: $scope.selected.recipient, organization: $scope.selectedOrganization});
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
  
});