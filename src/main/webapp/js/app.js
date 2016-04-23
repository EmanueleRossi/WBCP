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
var app = angular.module('whistleBlowingApp', [
        'ngRoute','ui.bootstrap', 'angular-loading-bar', 'angularUUID2', 'ngAnimate'    ]);


//app.DEBUG = true;



if(!console)
    console = {};

if(console.log == undefined)
    console.log = function(){};

app.directive('dropzone', function () {
  return function (scope, element, attrs) {


    var config = scope[attrs.dropzone];

    // create a Dropzone for the element with the given options

    // file drag hover
    function FileDragHover(e) {
        e.stopPropagation();
        e.preventDefault();
        e.target.className = (e.type == "dragover" ? "hover" : "");
    }


    // file selection
    function FileSelectHandler(e, callback) {

      // cancel event and hover styling
      FileDragHover(e);


      // fetch FileList object
      var files = e.target.files || e.dataTransfer.files;

      for (var i = 0, f; f = files[i]; i++) {
        if(typeof config.readAsText  == 'function')
          readAsTextFile(files[i], config.readAsText)
        
        if(typeof config.readAsDataURL  == 'function')
          readAsDataURL(files[i], config.readAsDataURL)
        
      }
      
    }

    function readAsTextFile(file, callback) {
        var reader = new FileReader();
        
        reader.onload = function(e) {
          callback({
            name: file.name,
            size: file.size,
            type: file.type,
            content: e.target.result
          });
        }
        
        reader.readAsText(file);
    }

    function readAsDataURL(file, callback) {
        var reader = new FileReader();
        reader.onload = function(e) {
         callback({
            name: file.name,
            size: file.size,
            type: file.type,
            content: e.target.result
          });
        }
        reader.readAsDataURL(file);
    };

    var fileselect = jQuery("#fileselect")[0],
       filedrag = jQuery("#filedrag")[0] ;
      submitbutton = jQuery("#submitbutton")[0];

      if(fileselect)
        fileselect.addEventListener("change", FileSelectHandler, false);

      filedrag.addEventListener("dragenter", FileDragHover, false);
      filedrag.addEventListener("dragover", FileDragHover, false);
      filedrag.addEventListener("dragleave", FileDragHover, false);
      filedrag.addEventListener("drop", FileSelectHandler, false);
      filedrag.style.display = "block";
      
      if(submitbutton)
     submitbutton.style.display = "none";
  };
});


app.directive('moDateInput', function ($window) {
		return {
				require:'^ngModel',
				restrict:'A',
				link:function (scope, elm, attrs, ctrl) {
						var moment = $window.moment;
						var dateFormat = attrs.moMediumDate;
						attrs.$observe('moDateInput', function (newValue) {
								if (dateFormat == newValue || !ctrl.$modelValue) return;
								dateFormat = newValue;
								ctrl.$modelValue = new Date(ctrl.$setViewValue);
						});

						ctrl.$formatters.unshift(function (modelValue) {
								if (!dateFormat || !modelValue) return "";
								var retVal = moment(modelValue).format(dateFormat);
								return retVal;
						});

						ctrl.$parsers.unshift(function (viewValue) {
								var date = moment(viewValue, dateFormat);
								return (date && date.isValid() && date.year() > 1950 ) ? date.toDate() : "";
						});
				}
		};
});

app.directive('appDatetime', function ($window) {
		return {
				restrict: 'A',
				require: 'ngModel',
				link: function (scope, element, attrs, ngModel) {
						var moment = $window.moment;

						ngModel.$formatters.push(formatter);
						ngModel.$parsers.push(parser);

						element.on('change', function (e) {
								var element = e.target;
								element.value = formatter(ngModel.$modelValue);
						});

						function parser(value) {
								var m = moment(value);
								var valid = m.isValid();
								ngModel.$setValidity('datetime', valid);
								if (valid) return m.valueOf();
								else return value;
						}

						function formatter(value) {
								var m = moment(value);
								var valid = m.isValid();
								if (valid) return m.format("LLLL");
								else return value;

						}

				} //link
		};

}); //appDatetime

app.config(['$routeProvider',
	function($routeProvider) {

		$routeProvider.
			when('/home', {
				templateUrl: 'partials/home.html',
				controller: 'HomeController',
				requireLogin: false
			}).
			when('/report-list', {
				templateUrl: 'partials/report-list.html',
				controller: 'ReportListController',
				requireLogin: true
			}).
			when('/report/:reportUuid?', {
				templateUrl: 'partials/report.html',
				controller: 'ReportController',
				requireLogin: false
			}).
 			when('/register', {
				templateUrl: 'partials/register.html',
				controller: 'RegisterController',
				requireLogin: false
			}).
			when('/login', {
				templateUrl: 'partials/login.html',
				controller: 'LoginController',
				requireLogin: false
			}).
			otherwise({
				redirectTo: '/home',
			});
	}]);

 
app.directive("compareTo", function() {
    return {
        require: "ngModel",
        scope: {
            otherModelValue: "=compareTo"
        },
        link: function(scope, element, attributes, ngModel) {
             
            ngModel.$validators.compareTo = function(modelValue) {
              
                return (modelValue == null) || (scope.otherModelValue == null) || (modelValue == scope.otherModelValue);
            };
 
            scope.$watch("otherModelValue", function() {
                ngModel.$validate();
            });
        }
    };
});

app.directive('fiscalcode', ['$http', function ($http) {
  return {
    require: 'ngModel',
    link: function (scope, elem, attrs, ctrl) {
      elem.on('blur', function (evt) {
        scope.$apply(function () {
          

			ctrl.$setValidity('fiscalcodeUnique', true);
          	ctrl.$setValidity('fiscalcodeFormat', true);

          	ctrl.$pending = {'fiscalcodeUnique': false};

          	if(elem.val().length == 0 ) 
         		return;
		
			if(!elem.val().match( '^[a-zA-Z]{6}[0-9]{2}[abcdehlmprstABCDEHLMPRST]{1}[0-9]{2}([a-zA-Z]{1}[0-9]{3})[a-zA-Z]{1}$'))
			{
				ctrl.$setValidity('fiscalcodeFormat', false);
				return;
			}
                	
          	
          	ctrl.$pending = { 'fiscalcodeUnique' : true};

        	setTimeout(function(){
        		ctrl.$pending = { 'fiscalcodeUnique' : false};
        		
        		ctrl.$setValidity('fiscalcodeUnique', true);
	        	

        		scope.$apply();
        	}, 2000);
        	/*
        	
          $http({ 
            method: 'POST', 
            url: 'backendServices/checkUsername.php', 
            data: { 
              fiscalcode:elem.val()
            } 
          }).success(function(data, status, headers, config) {
            ctrl.$setValidity('fiscalcodeUnique', data.status);
          });
           */
        });
      });
    }
  }
	}
]);


app.directive('emailaddress', ['$http', function ($http) {
  return {
    require: 'ngModel',
    link: function (scope, elem, attrs, ctrl) {
      elem.on('blur', function (evt) {
        scope.$apply(function () {
          
			ctrl.$setValidity('emailaddressUnique', true);
          	ctrl.$setValidity('emailaddressFormat', true);

          	ctrl.$pending = {'emailaddressUnique': false};

          	if(elem.val().length == 0 ) 
         		return;
		
			if(!elem.val().match('^\s@]+$'))
			{
				ctrl.$setValidity('emailaddressFormat', false);
				return;
			}
                	
          	
          	ctrl.$pending = { 'emailaddressUnique' : true};

        	setTimeout(function(){
        		ctrl.$pending = { 'emailaddressUnique' : false};
        		
        		ctrl.$setValidity('emailaddressUnique', true);
	        	

        		scope.$apply();
        	}, 2000);
        	/*
        	
          $http({ 
            method: 'POST', 
            url: 'backendServices/checkUsername.php', 
            data: { 
              fiscalcode:elem.val()
            } 
          }).success(function(data, status, headers, config) {
            ctrl.$setValidity('fiscalcodeUnique', data.status);
          });
           */
        });
      });
    }
  }
	}
]);


app.directive("hasSelection", function() {
    return {
        require: "ngModel",
        
        link: function(scope, element, attributes, ngModel) {
           	
            ngModel.$validators.hasSelection = function(modelValue) {
                return (modelValue != null);
            };
        }
    };
});


app.filter('cut', function () {
      return function (value, wordwise, max, tail) {
          if (!value) return '';

          max = parseInt(max, 10);
          if (!max) return value;
          if (value.length <= max) return value;

          value = value.substr(0, max);
          if (wordwise) {
              var lastspace = value.lastIndexOf(' ');
              if (lastspace != -1) {
                  value = value.substr(0, lastspace);
              }
          }

          return value + (tail || ' …');
      };
  });

app.filter('text', function () {
    return function(text) {
        return text.replace(/\n/g, '<br/>');
    }
});

app.filter('reportStatusValue', function () {
    return function(val) {

        if(val && app.util.constants.statusOptions)
        {
          var st = app.util.constants.statusOptions.find(function(op){ return op.value == val.value;});
          if(st != null)
            return st.name;
        }

        return "Non letta";
    }
});
app.filter('reportStatusIcon', function () {
    return function(val) {

        if(val && val.value != null)
          return '/WBCP/img/icon-status-' + val.value + '.png';
        else
          return null;
    }
});

app.filter('filesize', function () {
    return function(val) {

        if(val != null)
        {
          if(val > 1024*1024)
             return (Math.round(val / 1024) / 1024) + " MB"; 
    
          if(val > 1024)
            return Math.round(val / 1024) + " KB"; 

          return (val * 1000) / 1000 + " KB"; 
        } 
      } 
});

app.filter('commentsCount', function () {
    return function(val) {

        if(val && val.length != null)
          return val.length == 1 ? '1 discussione' :  val.length + ' discussioni';
        
        return null;
    }
});


app.run(function ($rootScope, $window) {

	$rootScope.$on('$routeChangeStart', function (event, toState, toParams) {
		
		//console.log("routeChangeStart: ",  toState);
		
		/*
		if(toState.redirectTo == "/home")
		{
			if (app.auth.getCurrentUser() != null)
				return $window.location.href = "#/appointments";	
		}
		 */
	
		var requireLogin = toState.requireLogin;

		if (requireLogin && app.auth.getCurrentUser() == null) {
				event.preventDefault();
		
				$window.location.href = "#/login";
		}
	});

});

 app.run( function ($rootScope, $window) {
    $rootScope.logout = function() {
        app.data.cleanData();
        app.auth.cleanCurrentUser();
        $window.location.href = "#/home";
    };
});

moment.tz.add("Europe/Berlin|CET CEST CEMT|-10 -20 -30|01010101010101210101210101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010|-2aFe0 11d0 1iO0 11A0 1o00 11A0 Qrc0 6i00 WM0 1fA0 1cM0 1cM0 1cM0 kL0 Nc0 m10 WM0 1ao0 1cp0 dX0 jz0 Dd0 1io0 17c0 1fA0 1a00 1ehA0 1a00 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1fA0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1fA0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1fA0 1o00 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00");

