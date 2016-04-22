(function(){

	var net = {
		//host :'http://185.58.116.136:8080/WBCP-web-1.0-SNAPSHOT/rs'
		//host :'/WBCP/rs'
		host :'http://localhost:8080/WBCP/rs'	
	};

	
	app.net = net;

	net.login = function(credentials, http, callback){
		
		var url =  net.host + "/auth/login";
		console.log("LOGIN!!");

		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data: 'loginEmail=' + encodeURIComponent(credentials.loginEmail) + 
							'&loginPassword=' + encodeURIComponent(credentials.loginPassword) + 
								'&privateKeyBase64=' + encodeURIComponent(credentials.privateKeyBase64)
		}
		
		console.log("Request:", req);

		http(req).
		success(function(data, status, headers, config) {  
		  
		  var token = headers()['authorizationtoken'];
			
			app.auth.setAuthToken(token);

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

		var req = {
			method: 'GET',
			url: url,
			headers: {'AuthorizationToken': app.auth.getAuthToken()}
		}
				
		console.log("getUserByEmail - request: ", req);

		http.get(req )
		  .success(function(data, status, headers, config) {
			  
			  console.log("success:", data);
			  
			  return callback(null, data);
			  
		  })
		  .error(function(data, status, headers, config) {
			
			 return callback("error");
		  });
	};

	app.net.getAuthor = function(messageId, http, callback) {

		var url =  net.host + "/message/getAuthor/" + messageId;
				
		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				'AuthorizationToken': app.auth.getAuthToken()
		    }
		}
		
		console.log("Request:", req);

		http(req).
		success(function(data, status, headers, config) {  
		 		  
		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
		
			 return callback(data);
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
				'Content-Type': 'application/json',
				'AuthorizationToken': app.auth.getAuthToken()
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
				'Content-Type': 'application/json',
				'AuthorizationToken': app.auth.getAuthToken()
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
		
			 hideLoader();

			 return callback(data);
		});
	};

	net.searchOrganization = function(keyword, http, callback){

		var url =  net.host + "/organization/fullTextSearch/" + keyword;

		var req = {
			method: 'GET',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				'AuthorizationToken': app.auth.getAuthToken()
		    }
		}

		http(req).
			success(function(data, status, headers, config) {  
			  return callback(null, data);
			}).
			error(function(data, status, headers, config) {
				return callback(data);
			});


	};
	 /* return $http.get( app.net.host + '/organization/fullTextSearch/' + val)
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
*/
	net.serachUserByOrganization = function(item, http, callback) {

		var url =  net.host + "/user/searchByOrganization/" + item.id;

		var req = {
			method: 'POST',
			url: url,
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				'AuthorizationToken': app.auth.getAuthToken()
		    },
			data: userSession 
		}

		http.get(url )
		  .success(function(data, status, headers, config) {
			  
			  console.log("success:", data);
			  
				return callback(null, data);
			  
		  })
		  .error(function(data, status, headers, config) {
			
			 return callback("error");
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

