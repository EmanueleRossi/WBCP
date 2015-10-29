

app.controller('ReportSendDialogController', function ($scope, $modalInstance, $http, items) {

  $scope.organization = null;

  $scope.organizations = null;

  $scope.recipients = [];


  //$scope.recipients =  [{ firstName: "Emanuele" , lastName: "Rossi", email: "emanuele.rossi@gpi.it" }];
  
  $scope.selected = {
  	item: null
  };


  $scope.orgInputFormatter = function(item)
  {
  	if(item != null)
  		return item.name;
  };

  $scope.serachUserByOrganization = function(item, model, label){

	console.log("serachUserByOrganization()", item);

	$scope.recipients = [];

	$scope.organizations.forEach(function(org){

		if(org.name == item.name)
		{
			$scope.recipients = org.users;	
			return;
		}
	});

	
  };

 
  $scope.searchOrganization = function(val) {

 // 	return [ {id: 3, name: "Gpi.SPA"}];

    if(app.DEBUG)
    {
        $scope.organizations =   [{ 
            id: "GPI S.p.a",
            name: "GPI S.p.at",
            users : [{
              name: "test",
              email : "test@gpi.it"
            }]
          }]
       return $scope.organizations;
    }

    return $http.get( app.net.host + '/organization/fullTextSearch/' + val)
    	.then(function(response){

    		console.log("searchOrganization:" , response.data);

    		$scope.organizations = response.data;

    		if(response.data.length == 0)
    			return null;

    		return response.data.map(function(item){
  				return { 
  					id: item.id,
  					name: item.name 
  				};
			});
    });
  };

  $scope.ok = function () {
    $modalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});