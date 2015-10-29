
app.controller('HomeController', function ($scope, $rootScope, $window, $http) {
 	


 	$scope.enter = function(){
		
		$window.location.href = "#/login";
	};

});