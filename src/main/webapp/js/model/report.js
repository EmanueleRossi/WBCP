
var Report = function (uuid) {
 	this.mversion = 0.1;
 	this.contentType = "REPORT";
 	this.uuid = uuid;
 	
 	/*
 	this.base = {
 		description : null,
 		reason : null,
 		date : null,
 		place : null,

 		personalInformations : {
 			current : {
 				position : null,
 				role : null,
 				place : null
 			},
 			previous : {
 				position : null,
 				role : null,
 				place : null
 			}
 		},
 			
		alreadyReported : false,
		relatedReports : []
 	};
	*/
 		// generic info fields 

 		this.createDate = new Date().getTime();

 		this.description  = null;
 		this.reason  = null;
 		this.datePeriod  = null;
 		this.place  = null;

 		this.personalInformations = {
 			current : {
 				position : null,
 				role : null,
 				place : null
 			},
 			previous : {
 				position : null,
 				role : null,
 				place : null
 			}
 		};
 		

		this.alreadyReported  = false;
		this.relatedReports  = [{}];

		// additional info fields 
		this.additional = null;
		this.authors = [{}];
		this.deponents = [{}];

};




