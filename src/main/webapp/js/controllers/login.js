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
app.controller('LoginController', function ($scope, $window, $http) {

	$scope.username = null;
	$scope.password = null;
  $scope.privateKey = null;

  $scope.privateKeyFilename = null;
  $scope.remember = true;
  $scope.errors = {};
  $scope.canRemember = false; 

  init();

  $scope.dropzoneConfig = {
      
      readAsText : function(content) {

        if(content == null)
          return; 

        $scope.$apply(function(){
          
          $scope.errors = {};

          if(content.size < 3164 || content.size > 3168)
          {
             $scope.errors = {
                message : "Chiave non valida",
                details : "il file selezionato non contiene una chiave privata valida"
             };
            
            return;
          }

          $scope.privateKey = content.content;
          $scope.privateKeyFilename = content.name;
        });
      }
    };



  function init() {

      $scope.canRemember = app.auth.hasLocalStorage();
  }


  $scope.doLogin = function () {
    
 	$scope.errorMessage = "";
    app.auth.cleanCurrentUser();

    var login = {
        "loginEmail": $scope.username,
        "loginPassword": $scope.password,
        "privateKeyBase64": $scope.privateKey
    } 

     app.net.login(login, $http, function(err, userSession){

    if(err)
      {
         $scope.errors.message = err.message;
         $scope.errors.details = err.details;
         
         return;
      }

     /* app.net.getUserByEmail($scope.username, $http, function(err, user){

        console.log("getUserByEmail callback: ", user);

         if(err)
          {
             $scope.errors.message = "Errori di accesso";
             $scope.errors.details = "Utente non trovato a sistema";
             
             return;
          }
*/
          var currentUser =  {
            userSession : userSession,
            user: { firstName : $scope.username , email: $scope.username  }//user
          }

          app.auth.setCurrentUser(currentUser, $scope.remember);
          $window.location.href = "#/report-list";
      
  //    });
    });
  };

  $scope.cleanPrivateKey = function(){
    $scope.privateKey = null;
    $scope.privateKeyFilename = null;
  }

});
