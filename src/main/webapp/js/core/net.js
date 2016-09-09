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
(function(){

	var net = {
		host: '/WBCP/rs'
	}

	app.net = net;

	net.login = function(credentials, http, callback){
		
		var url =  net.host + "/auth/login";

		showLoader();
		
		var req = {
			method: 'POST',
			url: url,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data: 'loginEmail=' + encodeURIComponent(credentials.loginEmail) + 
							'&loginPassword=' + encodeURIComponent(credentials.loginPassword) + 
								'&privateKeyBase64=' + encodeURIComponent(credentials.privateKeyBase64)
		}

		http(req).
		success(function(data, status, headers, config) {  

			var token = headers()['authorizationtoken'];

			if(token != null)
			{
				app.auth.setAuthToken(token);

				hideLoader();

				return callback(null, data);

			}
			else
			{
				return callback({
					message: "Errore di autenticazione. Token non valido."
				});
			}


		 
		}).
		error(function(data, status, headers, config) {
			
			 hideLoader();

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

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();

		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
		
			 hideLoader();

			 return callback(data);
		});
	};


	net.sendReport = function(data, http, callback){
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

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();
		 
		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {

			 hideLoader();

			 return callback(data);
		});
	};

	net.searchReportByInstant = function(from, to, userSession, http, callback) {

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

		http(req).
		success(function(data, status, headers, config) {  
		  hideLoader();

		  return callback(null, data);
		 
		}).
		error(function(data, status, headers, config) {
		
			 hideLoader();

			 return callback(data,[]);
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

