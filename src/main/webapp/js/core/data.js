(function(){

	var data = {
		reports : null,
		comments : null,
		statuses : null
	};

	app.data = data;
	
	data.getReports = function(userSession, http, callback){

		//if(data.reports != null)
		//	return callback(null, data.reports);

		var from = "2015-01-01";
		var to = "2015-12-31";
		
		app.net.searchReportByInstant(from, to, userSession, http, function(err, results) {

		//app.net.getReports(username, http, function(err, results){
			
			if(err)
				return callback(err);
				
			if(results)
			{
				data.reports = [];
				data.comments = [];
				data.statuses = [];

				results.forEach(function(result){
					var payload = JSON.parse(result.payload);

					if(payload.contentType == "COMMENT")
						data.comments.push(payload);

					if(payload.contentType == "STATUS")
						data.statuses.push(payload);
					
					if(payload.contentType == "REPORT")
						data.reports.push(payload);
			
				});

			}
			
			return callback(null, data.reports);
		
		});	
	};

	

	data.getReport = function(uuid){

		if(data.reports != null)
				return data.reports.find(function(r){ return r.uuid == uuid;});

		return null;
	}

	data.getComments = function(reportUuid) {

		if(data.comments)
		{	
			var filtered = data.comments.filter(function(c) { return c.reportUuid == reportUuid;});		

			return filtered.sort(function(a, b) {return b.createDate - a.createDate;})

		}
		return null;
	};

	data.getStatuses = function(reportUuid) {

		if(data.statuses)
		{	
			var filtered = data.statuses.filter(function(s) { return s.reportUuid == reportUuid;});		

			return filtered.sort(function(a, b) {return b.createDate - a.createDate;})
		}

		return null;
	};

	data.getCurrentStatus = function(reportUuid) {

		if(data.statuses)
			data.getStatuses(reportUuid)[0];
		
		return null;
	};

	data.getReportAsync = function(index, callback){
	//	console.log("data.getAppointment ", data.appointments);

		if(data.reports == null)
			return callback("No data loaded!");

		if(data.reports[index] != null)
			return callback(null, data.reports[index]);

		/*
		for(i in data.reports)
		{
			var r = data.reports[i];
			//console.log(id + ': ', a);
			if(r.id == id)
				return callback(null, r);
		}
		*/
		callback("Not found!");
	};

	data.cleanData = function() {
		data.reports = null;
	};




})()

