(function(){

	var LOCAL_STORAGE_USER_KEY = "WHISTLEBLOWING_CURRENTUSER";
	var LOCAL_STORAGE_TOKEN_KEY = "WHISTLEBLOWING_AUTH_TOKEN";

	var auth = {		
		currentUser : null,
		hasStorage : (typeof(Storage) !== "undefined"), 
		token : null
	};

	app.auth = auth;

	var MAX_AGE_STORAGE = 1000*60*60;

	auth.getCurrentUser = function()
	{
		if(auth.currentUser != null)
			return auth.currentUser;

		console.log("auth.hasLocalStorage: ", auth.hasLocalStorage());
		if(auth.hasLocalStorage())
    	{
    		var stored = localStorage.getItem(LOCAL_STORAGE_USER_KEY);
    		
		    if(stored) {

		    	var content = JSON.parse(stored);

		    	    		if((new Date().getTime() - content.timestamp) > MAX_AGE_STORAGE)
	    		{
	    			console.log("local storage old - it has been cleaned! ("+ MAX_AGE_STORAGE+ ")")
	    			auth.cleanCurrentUser();
	    		}
	    	
		    	else
		    		auth.currentUser = content.currentUser;  
	   		}

	   	}

    	return auth.currentUser;
	};


	auth.setCurrentUser = function(currentUser, remember)
	{
		auth.currentUser = currentUser;

		if(remember && auth.hasLocalStorage())
    	{
        	var obj = { currentUser : currentUser, timestamp: new Date().getTime() };
        	localStorage.setItem(LOCAL_STORAGE_USER_KEY, JSON.stringify(obj));	
      	}
	    
      	return auth.currentUser;
	};

	auth.cleanCurrentUser = function()
	{
		auth.currentUser = null;

  	if(auth.hasLocalStorage())
  		localStorage.removeItem(LOCAL_STORAGE_USER_KEY);
  }


	auth.getAuthToken = function()
	{
		if(auth.token != null)
			return auth.token;

		if(auth.hasLocalStorage())
    	{
    		var stored = localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY);
    		
		    if(stored) {

		    	var content = JSON.parse(stored);

		    	if((new Date().getTime() - content.timestamp) > MAX_AGE_STORAGE)
	    		{
	    			console.log("local storage old - it has been cleaned! ("+ MAX_AGE_STORAGE+ ")")
	    			auth.cleanAuthToken();
	    		}
	    	
		    	else
		    		auth.token = content.token;  
	   		}

	   	}

    	return auth.token;
	};


	auth.setAuthToken = function(token)
	{
		auth.token = token;

		if(auth.hasLocalStorage())
  	{
    	var obj = { token : token, timestamp: new Date().getTime() };
    	localStorage.setItem(LOCAL_STORAGE_TOKEN_KEY, JSON.stringify(obj));	
  	}
    
    return auth.token;
	};
		
	auth.cleanAuthToken = function()
	{
		auth.token = null;

  	if(auth.hasLocalStorage())
  		localStorage.removeItem(LOCAL_STORAGE_TOKEN_KEY);
  }


    auth.hasLocalStorage = function()
		{
    	 return auth.hasStorage;
    }
    
    auth.getCurrentUsername = function()
    {
    	if(auth.currentUser != null)
			return auth.currentUser.username;
    	
    	return null;
    }
    
    auth.getCurrentPassword = function()
    {
    	if(auth.currentUser != null)
			return auth.currentUser.password;
    	
    	return null;
    }

})()

