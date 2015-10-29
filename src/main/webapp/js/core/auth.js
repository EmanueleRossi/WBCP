(function(){

	var auth = {
		localStorageKey : "WHISTLEBLOWING_CURRENTUSER",
		currentUser : null,
		hasStorage : (typeof(Storage) !== "undefined")
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
    		var stored = localStorage.getItem(auth.localStorageKey);
    		
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
        	localStorage.setItem(auth.localStorageKey, JSON.stringify(obj));	
      	}
	    
      	return auth.currentUser;
	};
	
		auth.cleanCurrentUser = function()
		{
			auth.currentUser = null;

    	if(auth.hasLocalStorage())
    		localStorage.removeItem(auth.localStorageKey);
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

