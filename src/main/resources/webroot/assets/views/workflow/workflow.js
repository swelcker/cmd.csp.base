'use strict';

angular
.module('app')
.controller('AppMyCtrl', AppMyCtrl)



AppMyCtrl.$inject =  ['$scope', '$http', '$templateCache'];
  function AppMyCtrl($scope, $http, $templateCache) {
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
