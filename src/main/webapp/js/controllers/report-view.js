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
app.controller('ViewReportController',
		function ($scope, $rootScope, $routeParams, $modal, $window, $log, $http, uuid2) {
		console.log("ViewReportController");


	$scope.author = null;
	
	var reportUuid = $routeParams.reportUuid;
	
	$scope.user = null;
	$scope.report = null;
	$scope.comments = null;
	$scope.comment = null;
	$scope.statuses = null;
	$scope.err = null;

	$scope.showComments = true;
	$scope.showReportStatusHistory= false;

	$scope.validationErrorMessage = null;
	$scope.disclaimer = false;

	$scope.validation = null;

	$scope.reportCurrentStatusMessageShow = false;
	$scope.reportCurrentStatusMessage = null;

	$scope.statusOptions = app.util.constants.statusOptions;

	$scope.tabs = {
		'base' : {active : true},
		'advance' : {active : false}	
	} 
			
	function init(){

		$scope.user = app.auth.getCurrentUser().user;

		if(reportUuid != null)
		{
			$scope.report = app.data.getReport(reportUuid);

			if($scope.report == null) {
				return $window.location.href = "#/report-list";
			}

			if($scope.report != null)
			{
				$scope.comments = app.data.getComments($scope.report.uuid);	
				$scope.statuses = app.data.getStatuses($scope.report.uuid);

				if($scope.comments != null)
					$scope.comments.forEach(function(c){
						c.isMine = (c.sender.email == $scope.user.email);
					});

				if($scope.statuses != null && $scope.statuses.length > 0) {
					$scope.reportCurrentStatus = JSON.parse(JSON.stringify($scope.statuses[0]));
				} else {
					// register status read
					$scope.reportCurrentStatus = new Status(uuid2.newuuid(), $scope.report.uuid);
					
					if($scope.report.folder == 'received')
					{
						var message = {
					      "user": $scope.user,
					      "sender": $scope.report.recipient,    
					      "recipient": $scope.report.sender,
					      "payload": JSON.stringify($scope.reportCurrentStatus)
					    };

					   	app.net.sendReport(message, $http, function(err) {
							if(!err)
								$scope.statuses.unshift(JSON.parse(JSON.stringify($scope.reportCurrentStatus)));
				  		});
			  		};		
				}
			}
		}
	}

	$scope.download = function(attachment)	{
		if(attachment && attachment.content)
		{
			var mimeType = attachment.content.substr(5,attachment.content.indexOf(';')-5);
			var base64Data = attachment.content.substr(attachment.content.indexOf(',')+1);
			var blob = app.util.base64toBlob(base64Data, mimeType);

			if (window.saveAs) {
				window.saveAs(blob, attachment.filename); }
			else if (navigator.msSaveBlob){
				navigator.msSaveBlob(blob, attachment.filename);
			}
			else {
				$window.open(attachment.content, '_blank');
			}
		}
	};

	$scope.getAuthor = function() {
		app.net.getAuthor($scope.report.id, $http, function(err, data){
			if(!err && data.author)
				$scope.author = data.author
		});
	};

	$scope.addComment = function() {
		
		$scope.comment = new Comment(uuid2.newuuid(), $scope.report.uuid);
		$scope.comment.sender = $scope.user;

		if($scope.report.recipient.email == $scope.user.email)
			$scope.comment.recipient = $scope.report.sender;
		else
			$scope.comment.recipient = $scope.report.recipient;
	};

	$scope.toggleCommentsVisibility = function() {
		$scope.showComments = !$scope.showComments;
	};

	$scope.toggleReportStatusHistoryVisibility = function() {
		$scope.showReportStatusHistory = !$scope.showReportStatusHistory;
	};

	$scope.cancelComment = function() {
		$scope.comment = null;
	};

	$scope.sendComment = function() {
		if($scope.comment != null)
		{		
			$scope.sending = { 
      		isSending : true,
      		mode: 'comment'
      	}
			
   		var message = {
	        "user": $scope.user,
	        "sender": $scope.comment.sender, 
	        "recipient": $scope.comment.recipient,
		      "payload": JSON.stringify($scope.comment)
		    };

	    app.net.sendReport(message, $http, function(err, data){

			if(err)
			{
				$scope.sending.errorMessage = "Errore";
				$scope.sending.errorDetails = err;
			}
			else
			{
			$scope.sending.successMessage = "Discussione inviata con successo!";
					$scope.sending.success = true;

					if($scope.comments != null)
					{
						$scope.comment.isMine = true;
						$scope.comments.unshift(JSON.parse(JSON.stringify($scope.comment)));
					}

					$scope.comment = null;
				};
    		});
		}
	};

	$scope.sendStatus = function() {
		
		$scope.reportCurrentStatusMessageShow = false;
		
		if($scope.statuses != null && $scope.statuses.length > 0)
			if($scope.statuses[0].value == $scope.reportCurrentStatus.value)
			{
				alert("stato non modificato");
				return;
			}

		$scope.reportCurrentStatus.createDate = new Date().getTime();
		delete $scope.reportCurrentStatus.isChanging;

		var message = {
			"user": $scope.user,
			"sender": $scope.report.recipient,
			"recipient": $scope.report.sender,
			"payload": JSON.stringify($scope.reportCurrentStatus)
		};

   		app.net.sendReport(message, $http, function(err) {

			if(err)
			{
				$scope.reportCurrentStatusMessage = "errore di salvataggio dello stato";
			}
			else
			{
				$scope.reportCurrentStatusMessage = "nuovo stato salvato";
				
				setTimeout(function(){
					 $scope.$apply(function () {
            	$scope.reportCurrentStatusMessageShow = false;
       		 });
				}, 3000);

				if($scope.statuses == null)
					$scope.statuses = []; 
				
				$scope.statuses.unshift(JSON.parse(JSON.stringify($scope.reportCurrentStatus)));
			};

			$scope.reportCurrentStatusMessageShow = true;
		});
	};

	$scope.closeDialog = function() {
		$scope.sending = { isSending : false }
	};
			
	$scope.onReportCurrentStatusChange = function() {

		if($scope.reportCurrentStatus != null)
		{
			var value = $scope.reportCurrentStatus.value;

			$scope.reportCurrentStatus = new Status(uuid2.newuuid(), $scope.report.uuid);
			$scope.reportCurrentStatus.value = value;

			$scope.reportCurrentStatus.isChanging = true;
			$scope.reportCurrentStatus.note = null;
		}
	};

	init();
});