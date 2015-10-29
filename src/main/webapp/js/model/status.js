
var Status = function (uuid, reportUuid) {
 	this.mversion = 0.1;
 	this.contentType = "STATUS";
 	this.uuid = uuid;
 	
	this.createDate = new Date().getTime();
	this.reportUuid = reportUuid;

	this.value = 0;
	this.note = null;
};




