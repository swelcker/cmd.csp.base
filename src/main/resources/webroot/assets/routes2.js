angular
.module('app')
.config(['$stateProvider', '$urlRouterProvider', '$ocLazyLoadProvider', '$breadcrumbProvider', function($stateProvider, $urlRouterProvider, $ocLazyLoadProvider, $breadcrumbProvider) {

  $urlRouterProvider.otherwise('/dashboard');

  $ocLazyLoadProvider.config({
    // Set to true if you want to see what and when is dynamically loaded
    debug: true
  });

  $breadcrumbProvider.setOptions({
    prefixStateName: 'app.main',
    includeAbstract: true,
    template: '<li class="breadcrumb-item" ng-repeat="step in steps" ng-class="{active: $last}" ng-switch="$last || !!step.abstract"><a ng-switch-when="false" href="{{step.ncyBreadcrumbLink}}">{{step.ncyBreadcrumbLabel}}</a><span ng-switch-when="true">{{step.ncyBreadcrumbLabel}}</span></li>'
  });

  $stateProvider
  .state('app', {
    abstract: true,
    templateUrl: 'assets/views/common/layouts/full.html',
    //page title goes here
    ncyBreadcrumb: {
      label: 'Root',
      skip: true
    },
    resolve: {
      loadCSS: ['$ocLazyLoad', function($ocLazyLoad) {
        // you can lazy load CSS files
        return $ocLazyLoad.load([{
          serie: true,
          name: 'Font Awesome',
          files: ['assets/css/font-awesome.min.css']
        },{
          serie: true,
          name: 'Simple Line Icons',
          files: ['assets/css/simple-line-icons.min.css']
        }]);
      }],
      loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
        // you can lazy load files for an existing module
        return $ocLazyLoad.load([{
          serie: true,
          name: 'chart.js',
          files: [
            'assets/js/vendors/Chart.min.js',
            'assets/js/vendors/angular/angular-chart.min.js'
          ]
        }]);
      }],
    }
  })
  
  .state('app.main', {
    url: '/dashboard',
    templateUrl: 'assets/views/main.html',
    //page title goes here
    ncyBreadcrumb: {
      label: 'Home',
    },
    //page subtitle goes here
    params: { subtitle: 'Welcome to CSP-UI' },
    resolve: {
      loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
        // you can lazy load files for an existing module
        return $ocLazyLoad.load([
          {
            serie: true,
            name: 'chart.js',
            files: [
              'assets/js/vendors/Chart.min.js',
              'assets/js/vendors/angular/angular-chart.min.js'
            ]
          },
        ]);
      }],
      loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad) {
        // you can lazy load controllers
        return $ocLazyLoad.load({
          files: ['assets/views/monitor/monitor.js']
        });
      }]
    }
  })
  
  .state('app.workitems', {
    url: "/workitems",
    abstract: true,
    templateUrl: 'assets/views/workitems/workitems.html',
    ncyBreadcrumb: {
      label: 'Workitems'
    }
  })
  
  .state('app.templates', {
    url: "/templates",
    abstract: true,
    templateUrl: 'assets/views/templates/templates.html',
    ncyBreadcrumb: {
      label: 'Templates'
    }
  })
  
  .state('app.workflow', {
    url: "/workflow",
    abstract: true,
    templateUrl: 'assets/views/workflow/workflow.html',
    ncyBreadcrumb: {
      label: 'Workflow'
    }
  })
  
  .state('app.configuration', {
    url: "/configuration",
    abstract: true,
    templateUrl: 'assets/views/configuration/configuration.html',
    ncyBreadcrumb: {
      label: 'Configuration'
    }
  })
  
  .state('appSimple', {
    abstract: true,
    templateUrl: 'assets/views/common/layouts/simple.html',
    resolve: {
      loadCSS: ['$ocLazyLoad', function($ocLazyLoad) {
        // you can lazy load CSS files
        return $ocLazyLoad.load([{
          serie: true,
          name: 'Font Awesome',
          files: ['assets/css/font-awesome.min.css']
        },{
          serie: true,
          name: 'Simple Line Icons',
          files: ['assets/css/simple-line-icons.min.css']
        }]);
      }],
    }
  })
  // Additional Pages
  .state('appSimple.login', {
    url: '/login',
    templateUrl: 'assets/views/pages/login.html'
  })
  .state('appSimple.register', {
    url: '/register',
    templateUrl: 'assets/views/pages/register.html'
  })
  .state('appSimple.404', {
    url: '/404',
    templateUrl: 'assets/views/pages/404.html'
  })
  .state('appSimple.500', {
    url: '/500',
    templateUrl: 'assets/views/pages/500.html'
  })

  .state('app.monitor', {
	    url: "/monitor",
	    abstract: true,
	    template: '<ui-view></ui-view>',
	    ncyBreadcrumb: {
	      label: 'Monitor'
	    }
	  })
	  .state('app.monitor.verticles', {
	    url: '/verticles',
	    templateUrl: 'assets/views/monitor/verticles.html',
	    ncyBreadcrumb: {
	      label: 'Verticles'
	    },
	    resolve: {
	        loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad) {
	          // you can lazy load controllers
	          return $ocLazyLoad.load({
	            files: ['assets/views/monitor/monitor.js']
	          });
	        }]
	      }
	  })
	  .state('app.monitor.metrics', {
	    url: '/metrics',
	    templateUrl: 'assets/views/monitor/metrics.html',
	    ncyBreadcrumb: {
	      label: 'Metrics'
	    },
	    resolve: {
	        loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad) {
	          // you can lazy load controllers
	          return $ocLazyLoad.load({
	            files: ['assets/views/monitor/monitor.js']
	          });
	        }]
	      }
	  })
	  .state('app.monitor.performance', {
	    url: '/performance',
	    templateUrl: 'assets/views/monitor/performance.html',
	    ncyBreadcrumb: {
	      label: 'Performance'
	    },
	    resolve: {
	        loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad) {
	          // you can lazy load controllers
	          return $ocLazyLoad.load({
	            files: ['assets/views/monitor/monitor.js']
	          });
	        }]
	      }
	  })
	  .state('app.monitor.logs', {
	    url: '/logs',
	    templateUrl: 'assets/views/monitor/logs.html',
	    ncyBreadcrumb: {
	      label: 'Logs'
	    },
	    resolve: {
	        loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad) {
	          // you can lazy load controllers
	          return $ocLazyLoad.load({
	            files: [assets/views/monitor/monitor.js']
	          });
	        }]
	      }
	  })


}]);
