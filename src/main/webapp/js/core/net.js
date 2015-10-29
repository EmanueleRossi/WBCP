(function(){

	var net = {
		//host :'http://185.58.116.136:8080/WBCP-web-1.0-SNAPSHOT/rs'
		host :'/WBCP-web-1.0-SNAPSHOT/rs'	
	};

	
	app.net = net;

	net.login = function(credentials, http, callback){
		
		var url =  net.host + "/auth/login";

		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json'
		    },
			data: credentials 
		}
		
		console.log("Request:", req);

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		 
		  console.log("success:", data);
		  console.log("status:", status);
		  
		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
			
			 hideLoader();

			console.log("error status:", status);
				
			if(status == 401)
			{
				return callback({
					message: "Errore di autenticazione",
					details: "le credenziali fornite non sono valide"
				});
			}

			 return callback("Errore");
		});

};

	net.localLogin = function(credentials, http, callback){
		showLoader();
		

		
		setTimeout(function(){
			 hideLoader();

			  return callback(null, {
			  			firstName: "Emanuele",
						lastName: "Rossi",
						taxCode: "RSSMNL77R02L781Y",
						email: "emanuele.rossi@gpi.it",
						token: "xxxx"
					});
		}, 2000);
	
	};

	net.getUserByEmail = function(email, http, callback){

		var url =  net.host + "/user/email/" + email;

		console.log("url:", url);
				
		http.get(url )
		  .success(function(data, status, headers, config) {
			  
			  console.log("success:", data);
			  
			  return callback(null, data);
			  
		  })
		  .error(function(data, status, headers, config) {
			
			 return callback("error");
		  });
	};

	net.createAccount = function(account, http, callback){
		
		var url =  net.host + "/user/create";
		

		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json'
		    },
			data: account 
		}
		
		console.log("Request:", req);

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		 
		  console.log("success:", data);
		  console.log("status:", status);
		  
		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
		
			console.log("error:", data);
			console.log("status:", status);
			
			 hideLoader();

			 return callback(data);
		});
	};


	net.sendReport = function(data, http, callback){
		console.log("sendReport");
	
		var url =  net.host + "/message/create";
		

		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json'
		    },
			data: data 
		}
		
		console.log("Request:", req);

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		 
		  console.log("success:", data);
		  console.log("status:", status);
		  
		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
		
			console.log("error:", data);
			console.log("status:", status);
			
			 hideLoader();

			 return callback(data);
		});
	};

	net.searchReportByInstant = function(from, to, userSession, http, callback) {
		
		console.log("searchReportByInstant");
	
		var url =  net.host + "/message/searchByInstant/" + from + "/" + to;
		
		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json'
		    },
			data: userSession 
		}
		
		console.log("Request:", req);

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		 
		 // console.log("success:", data);
		 // console.log("status:", status);
		  
		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
		
			console.log("error:", data);
			console.log("status:", status);
			
			 hideLoader();

			 return callback(data);
		});
	};

	net.serachUserByOrganization = function(item, http, callback) {

		var url =  net.host + "/user/searchByOrganization/" + item.id;

		console.log("url:", url);
				
		http.get(url )
		  .success(function(data, status, headers, config) {
			  
			  console.log("success:", data);
			  
				return callback(null, data);
			  
		  })
		  .error(function(data, status, headers, config) {
			
			 return callback("error");
		  });

	};	

	net.getAppointments = function (startdate, enddate, http, callback){
		
		console.log("startdate", startdate);
		console.log("enddate", enddate);
		
		
		var url =  net.host + "getAppointments?" +
					"username=" + app.auth.getCurrentUsername()  + 
					"&password=" + app.auth.getCurrentPassword() +
					"&startdate=" + moment(startdate.toISOString()).format('YYYY-MM-DD') +
					"&enddate=" + moment(enddate.toISOString()).format('YYYY-MM-DD');
		
		console.log("url:", url);

		showLoader();
				
		http.get(url )
		  .success(function(data, status, headers, config) {
			  
			  hideLoader();
			  console.log("success:", data);
			  
			  if(data.status = "OK")
			  {
				  return callback(null, data.results);
			  }
			  
		  })
		  .error(function(data, status, headers, config) {
			 hideLoader();
			 return callback(data.error_message);
		  });
		
		/*
		http.get(net.host + "appointments_1.json")
    		.success(function(res) {

    			//hideLoader();
    			console.log(res);

    			if(res && res.status == "OK")
    			{
    				if(typeof callback == 'function')
    					callback(null, res.results);
    			}
    		});
    	*/
	};	

	net.updateAppointment_ = function(appointment, http, callback)
	{
		showLoader();
		setTimeout(function(){
			hideLoader();
			callback(null, appointment);
		}, 2000);
	};
	
	net.updateAppointment = function(appointment, http, callback)
	{
		var url =  net.host + "updateAppointment";
				
		var data = "username=" 		+ app.auth.getCurrentUsername() +
				"&password=" 		+ app.auth.getCurrentPassword() +
				"&appointmentid=" 	+ appointment.appointmentid +
				"&date=" 			+ moment(appointment.date).format('YYYY-MM-DD') +
				"&starttime=" 		+ moment.tz(appointment.starttime, "Europe/Berlin").format() +
				"&endtime=" 		+ moment.tz(appointment.endtime, "Europe/Berlin").format() +
				"&fullday=" 		+ (appointment.fullday ? "true" : "false") +	
				"&appointmentstatus=" + appointment.appointmentstatus +	
				"&title=" 			+ (appointment.title || '') +	
				"&description=" 	+ (appointment.description || '') +	
				"&report=" 			+ (appointment.report || '') +	
				"&evaluation=" 		+ (appointment.evaluation || '') +	
				"&trip=" 			+ (appointment.trip || '') +	
				"&type=" 			+ (appointment.type || '') +	
				"&customerid=" 		+ appointment.customerid;
		
		console.log("data: ", data);
		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/x-www-form-urlencoded'
		    },
			data: data 
		}
		
		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		  console.log("success:", data);
		  
		  if(data.status = "OK")
		  {		
			  return callback(null, appointment);
		  }
		}).
		error(function(data, status, headers, config) {
			 hideLoader();
			 return callback(data.error_message);
		});
	};
	
	net.insertAppointment = function(appointment, http, callback)
	{
		var url =  net.host + "insertAppointment";
				
		var data = "username=" 			+ app.auth.getCurrentUsername() +
					"&password=" 		+ app.auth.getCurrentPassword() +
					"&date=" 			+ moment(appointment.date).format('YYYY-MM-DD') +
					"&starttime=" 		+ moment.tz(appointment.starttime, "Europe/Berlin").format() +
					"&endtime=" 		+ moment.tz(appointment.endtime, "Europe/Berlin").format() +
					"&fullday=" 		+ (appointment.fullday ? "true" : "false") +	
					"&appointmentstatus=" + appointment.appointmentstatus +	
					"&title=" 			+ (appointment.title || '') +	
					"&description=" 	+ (appointment.description || '') +	
					"&report=" 			+ (appointment.report || '') +	
					"&evaluation=" 		+ (appointment.evaluation || '') +	
					"&trip=" 			+ (appointment.trip || '') +	
					"&type="			+ (appointment.type || '') +	
					"&customerid=" 		+ appointment.customerid;
		
		console.log("data: ", data);
		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/x-www-form-urlencoded'
		    },
			data: data 
		}
		
		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		  console.log("success:", data);
		  
		  if(data.status = "OK")
		  {
			  appointment.appointmentid = data.appointmentid;
			  return callback(null, appointment);
		  }
		}).
		error(function(data, status, headers, config) {
			 hideLoader();
			 return callback(data.error_message);
		});
	};
	
	net.deleteAppointment = function(id, http, callback) {
		
		var url =  net.host + "deleteAppointment";
		
		var data = "username=" + app.auth.getCurrentUsername() +
					"&password=" + app.auth.getCurrentPassword() +
					"&appointmentid=" + id;
							
		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/x-www-form-urlencoded'
		    },
			data: data
		}
		
		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		  console.log("success:", data);
		  
		  if(data.status = "OK")
		  {
			  return callback(null, id);
		  }
		}).
		error(function(data, status, headers, config) {
			 hideLoader();
			 return callback(data.error_message);
		});
	};
	
	net.getCustomers = function(keyword, http, callback){
		
		var url =  net.host + "getCustomer?" +
				"username=" + app.auth.getCurrentUsername()  + 
				"&password=" + app.auth.getCurrentPassword() +
				"&customername=" + keyword;
		
		//showLoader();
			
		http.get(url )
		.success(function(data, status, headers, config) {
		  
		  //hideLoader();
		  console.log("success:", data);
		  
		  if(data.status = "OK")
		  {
			  return callback(null, data.results || []);
		  }
		  
		})
		.error(function(data, status, headers, config) {
			// hideLoader();
			 return callback(data.error_message);
		});
	};	



	function showLoader()
	{
	//	$('.modal').modal('show');
	}

	function hideLoader()
	{
	//	$('.modal').modal('hide');
	}
	
	function stringify(obj)
	{
		var str = "";
		for(var p in obj)
		{
			str += p + "=" + obj[p] + "&";
		}
		
		return str;
	}

})()

