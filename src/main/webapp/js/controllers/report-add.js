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
app.controller('AddReportController',
		function ($scope, $rootScope, $routeParams, $modal, $window, $log, $http, uuid2) {

	$scope.user = app.auth.getCurrentUser().user;

	$scope.author = null;

	$scope.report = null;
	$scope.err = null;

	$scope.validationErrorMessage = null;
	$scope.disclaimer = false;

	$scope.validation = null;

	$scope.sending = { isSending: false };

	$scope.tabs = {
		'base' : {active : true},
		'advance' : {active : false}	
	} 

	$scope.recipientOptions = app.util.constants.otherRecipientOptions;
	$scope.reasonOptions = app.util.constants.reasonOptions;
	$scope.areaOptions = app.util.constants.areaOptions;
	$scope.sectorOptions = app.util.constants.sectorOptions;
	$scope.placeTypeOptions = app.util.constants.placeTypeOptions;




	function init(){

		chooseRecipientDialog(function(err, selection){
			if(err || selection == null) {
				return $window.location.href = "#/report-list";
			}

			var mtemplate = selection.organization.newMessageTemplate || 'default';
			$scope.report = new Report(uuid2.newuuid(), mtemplate);
			$scope.report.sender = $scope.user;
			$scope.report.recipient = selection.recipient;
			$scope.report.recipientOrganization = selection.organization;
			$scope.report.attachments = [];

			$scope.addAttachment();
		});
	}

	$scope.addAttachment = function(){

		if($scope.report && $scope.report.attachments) {
			$scope.report.attachments.push({

			});
		}
	};

	$scope.removeAttachment = function(attachment) {

		if(attachment) {

			var idx = $scope.report.attachments.indexOf(attachment);

			if(idx != -1) {
				$scope.report.attachments.splice(idx,1);
			}
		}
	};

  	$scope.identityCardConfig = {
      
      readAsDataURL : function(content) {

        if(content == null)
          return; 

        $scope.$apply(function(){
          
          $scope.errors = {};

        /*  if(content.size != 3168) TODO: check sul max file size
          {
             $scope.errors = {
                message : "Chiave non valida",
                details : "il file selezionato non contiene una chiave privata valida"
             };
            
            return;
          }
		*/
          $scope.report.identityCard =  {
          	content : content.content,
          	filename :content.name,
          	size: content.size,
          	type: content.type
          };

        });
      }
    };

	$scope.cleanIdentityCard = function(){
	    $scope.report.identityCard = null;
	}	

	$scope.closeDialog = function() {
		$scope.sending = { isSending : false }
	};

	$scope.doSend = function (form) {

  		if(!$scope.report.alreadyReported)
  			$scope.relatedReports = [];

  		if(form.$valid)
  		{
			$scope.sending = { 
	  			isSending : true,
	  			mode: 'report'
	  		}

	  		sendReport(function(err, data){

	  			if(err)
	  			{
	  				$scope.sending.errorMessage = "Errore";
	  				$scope.sending.errorDetails = err;
	  			}
				else
				{
		      		$scope.sending.successMessage = "Segnalazione inviata con successo!";
					$scope.sending.success = true;
				};
	  		});
  		}
  		else
  		{
  			$scope.tabs = {
				'base' : {active : true},
				'advance' : {active : false}	
			}	 
  		}
	  };

	function chooseRecipientDialog(callback) {

		var modalInstance = $modal.open({
	      animation: true,
	      templateUrl: 'partials/report-recipient-dialog.html',
	      controller: 'ReportRecipientDialogController'
	    });

	    modalInstance.result.then(
	    	function (selection) {
	      		return callback(null, selection);
	    	},
	    	function () {
	      		return callback("NO RECIPIENT SELECTED");
	    });
  };


  	function sendReport(callback) {

  		// clean report unused fields
  		if(!$scope.report.alreadyReported)
  			$scope.report.relatedReports = null;

  		if($scope.report.area != "Altro")
  			delete $scope.report.otherArea;
  		if($scope.report.sector != "Altro")
  			delete $scope.report.otherSector;
  		if($scope.report.reason != "Altro")
  			delete $scope.report.otherReason; 	

  		if($scope.report.deponents.length == 1)
  			if( $scope.report.deponents[0].firstname == "" &&
  				$scope.report.deponents[0].lastname == "" &&
  				$scope.report.deponents[0].role == "" &&
  				$scope.report.deponents[0].contacts == "")
	  			{
	  				$scope.report.deponents = null;
	  			}

  		var message = {
	        "user": $scope.user,
	        "sender": $scope.report.sender,    
	        "recipient": $scope.report.recipient,
			"payload": JSON.stringify($scope.report)
		};

	    app.net.sendReport(message, $http, callback);

  	};

	$scope.deleteRelated = function(idx) {
		if($scope.report.relatedReports.length == 1)
			$scope.report.relatedReports = [{}];

		$scope.report.relatedReports.splice(idx, 1);
	};

	$scope.addRelated = function() {
		$scope.report.relatedReports.push({});
	};

	$scope.deleteAuthor = function(idx) {

		if($scope.report.authors.length == 1)
			$scope.report.authors = [{}];
		else
			$scope.report.authors.splice(idx, 1);
	};

	$scope.addAuthor = function() {
		$scope.report.authors.push({});
	};

	$scope.deleteDeponent = function(idx) {
		
		if($scope.report.deponents.length == 1)
			$scope.report.deponents = [{}];
		else
			$scope.report.deponents.splice(idx, 1);
	};

	$scope.addDeponent = function() {
		$scope.report.deponents.push({});
	};

	
	// ----------- date-picker methods -------------------------------------------------
	$scope.today = function() {
		$scope.dt = new Date();
	};
	$scope.today();

	$scope.clear = function () {
		$scope.dt = null;
	};

	$scope.dateOptions = {
		formatYear: 'yy',
		startingDay: 1
	};

	$scope.datePickerStatus = {};

	$scope.openDatePicker = function(key, $event) {
		$scope.datePickerStatus[key] = {opened : true};
	};
	// ------------------------------------------------------------------------------

			
	init();
});