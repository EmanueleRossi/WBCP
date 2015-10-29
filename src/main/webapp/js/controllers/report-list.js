
app.controller('ReportListController', function ($scope, $rootScope, $window, $http) {
 	
	$scope.user = null;
	$scope.reports = null;
	$scope.err = null;

	var userSession = null; 
	var folders = ['sent','received', 'draft'];

	$scope.tabs = {
		'sent' : {active : true, enabled: false},
		'received' : {active : false , enabled: false},
		'draft' : {active : false , enabled: false}	
	} 

	init();
 	
	function init(){

		var currentUser = $scope.user = app.auth.getCurrentUser();
      	
		if(currentUser == null)
      		return $window.location.href = "#/login";

  	$scope.user = currentUser.user;
  	userSession = currentUser.userSession;		

  	loadData();
	};


	function loadData() {

		$scope.err = null;
	
		app.data.getReports(userSession, $http, function(err, reports){

			if(err)
			{
				$scope.err = err;
				return;
			}

			var sentUuids = [];

			// detect folder (sent or received)
			if(reports)
				for(var i = 0, len = reports.length; i< len; ++i)
				{
					var report = reports[i];

					if(report.recipient && report.recipient.email == $scope.user.email)
					{
						if(sentUuids.indexOf(report.uuid) != -1)
						{
							report.folder = folders[0];
						}
						else
						{
							report.folder = folders[1];
							sentUuids.push(report.uuid);
						}
					}
					else if(report.sender && report.sender.email == $scope.user.email)
					{
						report.folder = folders[0];
					}

					report.comments = app.data.getComments(report.uuid);
			 		report.status = app.data.getStatuses(report.uuid)[0];
				}

			var hasSent = (reports.find(function(r){return r.folder=='sent'}) != null);
			var hasReseived = (reports.find(function(r){return r.folder=='received'}) != null);
			
			$scope.tabs['sent']['enabled'] = hasSent;
			$scope.tabs['received']['enabled'] = hasReseived;

			if(!hasSent)
			{
				$scope.tabs['sent']['active'] = false;
				$scope.tabs['received']['active'] = hasReseived;
			}


			$scope.reports = reports;
		});
	};

	$scope.viewDetail = function(index) {
		console.log('viewDetail: ', index);
		
		if($scope.reports != null)
			$window.location.href = "#/report/" + index;
	};

	$scope.add = function(){
		$window.location.href = "#/report/";
	};

	



});