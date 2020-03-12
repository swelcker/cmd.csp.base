'use strict';

angular
.module('app')
.controller('AppIndexCtrl', AppIndexCtrl)
.controller('AppMetricsCtrl', AppMetricsCtrl)
.controller('AppPerfCtrl', AppPerfCtrl)
.controller('AppLogCtrl', AppLogCtrl)


AppIndexCtrl.$inject =  ['$scope', '$http', '$templateCache'];
  function AppIndexCtrl($scope, $http, $templateCache) {
    $scope.records = [];

    var fetchRegServices = () => {
      $http({
        method: 'GET',
        url: '/discovery',
        cache: $templateCache
      }).then(response => {
   	  
        $scope.records = response.data;
      }),((data, status, headers, config) => {
      });
    };

    fetchRegServices();

/*    setInterval(fetchRegServices, 60000);
*/  };

AppMetricsCtrl.$inject = ['$scope'];
  function AppMetricsCtrl($scope) {
	    $scope.metrics = [];
	      $scope.series = ["Published","Sent","Received"];
		  $scope.labels = [];
		  $scope.data = [[],[],[]];

	    
	var eventbus = new EventBus('/csp.bridge.js');

    eventbus.onopen = () => {
      eventbus.registerHandler('csp.metrics', (err, message) => {
        var res = message.body;
        if (res != null) {
        	$scope.$apply(function () {

	          $scope.metrics = res;
	          var seriesArray = $scope.series;
	          var time = (new Date()).getTime();
        
	          $scope.labels.push(time);
	         
	          $scope.data[0].push(res["cspMetrics.eventbus.messages.published"].meanRate);
	    	  $scope.data[1].push(res["cspMetrics.eventbus.messages.sent"].meanRate);
	    	  $scope.data[2].push(res["cspMetrics.eventbus.messages.received"].meanRate);
        	});
        }
      });
    }

  };


  
AppPerfCtrl.$inject = ['$scope'];
	  function AppPerfCtrl($scope) {
	    $scope.metrics = [];
	      $scope.series = ["Published","Sent","Received"];
		  $scope.labels = [];
		  $scope.data = [[],[],[]];



	    var eventbus = new EventBus('/csp.bridge.js');

	    eventbus.onopen = () => {
	      eventbus.registerHandler('csp.metrics', (err, message) => {
	        var res = message.body;
	        if (res != null) {
	        	$scope.$apply(function () {

		          $scope.metrics = res;
		          $scope.availablemetrics = res["availablemetrics"]
		          var seriesArray = $scope.series;
		          var time = (new Date()).getTime();
	        
		          $scope.labels.push(time);
		         
		          $scope.data[0].push(res["cspMetrics.eventbus.messages.published"].meanRate);
		    	  $scope.data[1].push(res["cspMetrics.eventbus.messages.sent"].meanRate);
		    	  $scope.data[2].push(res["cspMetrics.eventbus.messages.received"].meanRate);
	        	});
	        }
	      });
   	      eventbus.registerHandler('csp.pop', (err, message) => {
    	        var res = message.body;
    	        if (res != null) {
    	        	$scope.$apply(function () {
    		          $scope.pop = res;     	        
     	        	});
    	        }
    	      });
   	      eventbus.registerHandler('csp.imap', (err, message) => {
  	        var res = message.body;
  	        if (res != null) {
  	        	$scope.$apply(function () {
  		          $scope.imap = res;     	        
   	        	});
  	        }
  	      });
     	      eventbus.registerHandler('csp.pre', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.pre = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.pro', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.pro = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.pst', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.pst = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.arc', (err, message) => {
    	        var res = message.body;
    	        if (res != null) {
    	        	$scope.$apply(function () {
    		          $scope.arc = res;     	        
     	        	});
    	        }
    	      });
     	      eventbus.registerHandler('csp.bat', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.bat = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.wfl', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.wfl = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.fcr', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.fcr = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.wcr', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.wcr = res;     	        
       	        	});
      	        }
      	      });
     	      eventbus.registerHandler('csp.ftp', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.ftp = res;     	        
       	        	});
      	        }
      	      });
       	             	              	              	              	     
     	      eventbus.registerHandler('csp.smtp', (err, message) => {
      	        var res = message.body;
      	        if (res != null) {
      	        	$scope.$apply(function () {
      		          $scope.smtp = res;     	        
       	        	});
      	        }
      	      });       	      
	    }

	  };

AppLogCtrl.$inject = ['$scope'];
  function AppLogCtrl($scope) {
    $scope.logs = [];

    var eventbus = new EventBus('/csp.bridge.js');

    eventbus.onopen = () => {
      eventbus.registerHandler('csp.log.all', (err, message) => {
        if (message != null) {
          $scope.logs = $scope.logs.concat(message.body);
          $scope.$apply();
        }
      });
    }
  };

