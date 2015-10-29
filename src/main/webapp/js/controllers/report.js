
app.controller('ReportController', 
		function ($scope, $rootScope, $routeParams, $modal, $window, $log, $http, uuid2) {
 	
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


	var reportUuid = $routeParams.reportUuid;
	var mode = ['add','view'];

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

	$scope.mode = mode[0];

	$scope.sending = { isSending: false };

	$scope.reportCurrentStatusMessageShow = false;
	$scope.reportCurrentStatusMessage = null;

	$scope.tabs = {
		'base' : {active : true},
		'advance' : {active : false}	
	} 

	init();
 	
/*
	$scope.reasonOptions = [
		{ name : "ragione 1" , type : "discriminazione"},
		{ name : "ragione 2" , type : "discriminazione"},
		{ name : "ragione 1" , type : "altro"},
		{ name : "ragione 1" , type : "altro"}
	];

	$scope.placeOptions = [
		{ name : "in azienda" , type : "in orario di lavoro"},
		{ name : "fuori azienda" , type : "in orario di lavoro"}
	];
*/


	$scope.recipientOptions = app.util.constants.otherRecipientOptions;
	$scope.statusOptions = app.util.constants.statusOptions;
	$scope.reasonOptions = app.util.constants.reasonOptions;
	$scope.areaOptions = app.util.constants.areaOptions;
	$scope.sectorOptions = app.util.constants.sectorOptions;

	function init(){

		$scope.mode = (reportUuid != null) ? mode[1] : mode[0];

		if(app.auth.getCurrentUser() == null)
    {
      	return $window.location.href = "#/login";
		}
		else
		{
      		$scope.user = app.auth.getCurrentUser().user;
		}

		if(reportUuid != null)
		{

			$scope.report = app.data.getReport(reportUuid);

			if($scope.report != null)
			{
				$scope.comments = app.data.getComments($scope.report.uuid);	
				$scope.statuses = app.data.getStatuses($scope.report.uuid);

				//data:application/pdf;base64, 'data:application/octet-stream');
	/*
				if($scope.report.identityCard.content)
				{
					$scope.report.identityCard.content = $scope.report.identityCard.content.replace("data:application/pdf", "data:application/octet-stream");
				
					var file = new Blob([ $scope.report.identityCard.content ], {
                        type : 'application/csv'
                    });
                    //trick to download store a file having its URL
                    $scope.report.identityCard.fileURL = URL.createObjectURL(file);
				}
	*/				

				console.log($scope.report);

				if($scope.statuses != null && $scope.statuses.length > 0)
					$scope.reportCurrentStatus = JSON.parse(JSON.stringify($scope.statuses[0]));
				else
				{
					// register stutus read
					$scope.reportCurrentStatus = new Status(uuid2.newuuid(), $scope.report.uuid);
					
					if($scope.report.folder == 'received')
					{
						var message = {
					      "user": $scope.user,
					      "sender": $scope.report.recipient,    
					      "recipient": $scope.report.sender,
					      "payload": JSON.stringify($scope.reportCurrentStatus)
					    };

					   // app.data.sendReportStatus(message, $http, function(err) {
					   	app.net.sendReport(message, $http, function(err) {
							if(!err)
								$scope.statuses.unshift(JSON.parse(JSON.stringify($scope.reportCurrentStatus)));
				  		});
			  		};		
				}
			}
		}
		else
		{
			$scope.report = new Report(uuid2.newuuid());
			$scope.report.sender = $scope.user;
		
			chooseRecipientDialog(function(err, selection){

					if(err || selection == null)
						return $window.location.href = "#/report-list";
					
					$scope.report.recipient = selection.recipient;
					$scope.report.recipientOrganization = selection.organization;
			});

		}
	}

$scope.download = function(attachment)
{
	if(attachment && attachment.content)
	{
		$window.open(attachment.content, '_blank');
	}

}

  $scope.identityCardConfig = {
      
      readAsDataURL : function(content) {
        console.log("readAsDataURL", content);
        
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

	$scope.doSend = function () {
    
  	//	console.log("doSend", $scope.report);
	  	
  		if(!$scope.report.alreadyReported)
  			$scope.relatedReports = [];

  		console.log("doSend", $scope.form.$valid);
  		console.log("error", $scope.form.$error);



  		if($scope.form.$valid)
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
	      controller: 'ReportRecipientDialogController',
	       resolve: {
	        items: function () {
	          return $scope.items;
	        }
	      }
	      
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

  		console.log("sender:",$scope.report.sender);
  		console.log("recipient:",$scope.report.recipient);
  		
  		// clena report unused fields
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
							$scope.comments.unshift(JSON.parse(JSON.stringify($scope.comment)));

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
      "sender": $scope.report.sender,    
      "recipient": $scope.report.recipient,
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

  

	$scope.onReportCurrentStatusChange = function() {

		/*
		if($scope.statuses != null && $scope.statuses.length > 0)
			if($scope.statuses[0].value == $scope.reportCurrentStatus.value)
				return;
		*/

		if($scope.reportCurrentStatus != null)
		{
			$scope.reportCurrentStatus.isChanging = true;
			$scope.reportCurrentStatus.note = null;
		}
	};


	function isValid()
	{
		//$scope.form.$setValidity("current-position", false, $scope.form);


		return 
		$scope.validationErrorMessage = "";
	   
		$scope.validation = {
			disclaimer : {}
		};

		$scope.validation.disclaimer.error = 'has-error';


		return false;
	};

	/*
	function showLoader()
	{
		$('.modal').modal('show');
	}

	function hideLoader()
	{
		$('.modal').modal('hide');
	}
	*/
});