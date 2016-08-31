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



if(!console)
    console = {};

if(console.log == undefined)
    console.log = function(){};


app.config(['$routeProvider', '$httpProvider',
	function($routeProvider, $httpProvider) {

        $httpProvider.interceptors.push('APIInterceptor');

		$routeProvider.
			when('/home', {
				templateUrl: 'partials/home.html',
				controller: 'HomeController'
			}).
			when('/report-list', {
				templateUrl: 'partials/report-list.html',
				controller: 'ReportListController'
			}).
			when('/report/:reportUuid?', {
				templateUrl: 'partials/report.html',
				controller: function($scope, $routeParams) {

                    if(app.auth.getCurrentUser() == null) {
                        return $window.location.href = "#/login";
                    }

                    $scope.user = app.auth.getCurrentUser().user;

                    var mode = ['add','view'];
                    var reportUuid = $routeParams.reportUuid;

                    $scope.mode = (reportUuid != null) ? mode[1] : mode[0];

                    console.log("MODE:", $scope.mode);
                }
			}).
 			when('/register', {
				templateUrl: 'partials/register.html',
				controller: 'RegisterController'
			}).
			when('/login', {
				templateUrl: 'partials/login.html',
				controller: 'LoginController'
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

app.factory('APIInterceptor', function ($rootScope, $q) {
    return {
        'request': function(config) {
            return config;
        },
        'responseError': function(response) {
            if (response.status === 401) {
                $rootScope.$broadcast('event.unauthorized');
            }

            return $q.reject(response);
        }
    };
});

app.run(function ($rootScope, $window) {

    $rootScope.$on('event.unauthorized', function() {
        $window.location.href = "#/login";
    });

	$rootScope.$on('$routeChangeStart', function (event, toState, toParams) {

		var requireLogin = toState.requireLogin;

		if (requireLogin && app.auth.getCurrentUser() == null) {
            event.preventDefault();
            $window.location.href = "#/login";
		}
	});


    $rootScope.logout = function() {
        app.data.cleanData();
        app.auth.cleanCurrentUser();
        $window.location.href = "#/home";
    };

});


moment.tz.add("Europe/Berlin|CET CEST CEMT|-10 -20 -30|01010101010101210101210101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010|-2aFe0 11d0 1iO0 11A0 1o00 11A0 Qrc0 6i00 WM0 1fA0 1cM0 1cM0 1cM0 kL0 Nc0 m10 WM0 1ao0 1cp0 dX0 jz0 Dd0 1io0 17c0 1fA0 1a00 1ehA0 1a00 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1fA0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1fA0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1cM0 1fA0 1o00 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00 11A0 1qM0 WM0 1qM0 WM0 1qM0 WM0 1qM0 11A0 1o00 11A0 1o00");

