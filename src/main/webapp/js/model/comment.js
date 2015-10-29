
var Comment = function (uuid, reportUuid) {
 	this.mversion = 0.1;
 	this.contentType = "COMMENT";
 	this.uuid = uuid;
 	
	this.createDate = new Date().getTime();
	this.reportUuid = reportUuid;

	this.description  = null;
};




