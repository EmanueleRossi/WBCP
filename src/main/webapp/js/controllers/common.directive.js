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
angular.module('whistleBlowingApp')

    .directive('viewReport', function($compile){
        return {
            scope : {
                report : '='
            },
            restrict: 'E',
            template: '<sending-dialog data-sending="sending" on-close="closeDialog()"></sending-dialog>',
            link: function (scope, element, attrs, controller) {
                if(scope.report) {
                    element.append('<div ng-include="\'partials/templates/viewReport.' + scope.report.mtemplate + '.tmpl.html\'"></div>');
                    $compile(element.contents())(scope);
                }
            },
            controller: 'ViewReportController'
        };
    })

    .directive('addReport', function($compile){
        return {
            scope : {
                report : '='
            },
            restrict: 'E',
            template: '<sending-dialog data-sending="sending" on-close="closeDialog()"></sending-dialog>',
            link: function (scope, element, attrs, controller) {
                scope.$watch('report', function(){console.log("addReport $watch");
                    if(scope.report) {
                       element.append('<div ng-include="\'partials/templates/addReport.' + scope.report.mtemplate + '.tmpl.html\'"></div>');
                       $compile(element.contents())(scope);
                    }
                });
            },
            controller: 'AddReportController'
        };
    })
    
    .directive('sendingDialog', function($compile){
        return {
            scope : {
                sending : '=',
                onClose : '&'
            },
            restrict: 'E',
            templateUrl: 'partials/common/sendReportDialog.tmpl.html'
        };
    })

    .directive('attachment', function(){
        return {
            scope : {
                attachment : '=',
                remove : '&'
            },
            restrict: 'E',
            templateUrl: 'partials/common/attachment.tmpl.html',
            controller : function($scope) {

                console.log("att:", $scope.attachment);

                $scope.config = {

                    readAsDataURL : function(content) {

                        if(content == null)
                            return;

                        $scope.$apply(function(){

                            $scope.errors = {};

                            /*  if(content.size != 3168) TODO: check sul max file size
                             {
                             $scope.errors = {
                             message : "Chiave non valida",
                             details : "il file selezionato non contiene una chiave privata valida"
                             };

                             return;
                             }
                             */

                            $scope.attachment.content = content.content;
                            $scope.attachment.filename =content.name;
                            $scope.attachment.size = content.size;
                            $scope.attachment.type =  content.type;
                        });
                    }
                };

                $scope.removeAttachment = function(){
                    //$scope.attachment = null;

                    $scope.remove({attach : $scope.attachment});
                }
            }
        };
    })

    .directive('dropzone', function () {
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

            var fileselect = jQuery("#fileselect",element)[0],
                filedrag = jQuery("#filedrag",element)[0],
            submitbutton = jQuery("#submitbutton", element)[0];

            console.log("filedrag:", filedrag);

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
})


.directive('moDateInput', function ($window) {
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
})

.directive('appDatetime', function ($window) {
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
});