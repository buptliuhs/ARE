// Copyright 2015 Tony (Huansheng) Liu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

$(document).ready(function() {
  initDevice();

  check_progress();

  $("#add_device_button").on('click', addDevice);
});

var g_device_type = urlArgs().dtn;
var g_device_type_id = urlArgs().dt;

function initDevice() {
  var url = $.ctx + "/init_device_list.action";
  $.post(url, {
    "device_type" : g_device_type_id
  }, function(data) {
    // console.log(data);
    data = JSON.parse(data);
    // console.log(data);
    var opt = "";
    var index = 0;
    for ( var o in data) {
      var c = data[o];
      var device_type = c.device_type;
      var device_name = c.device_name;
      var allocated = c.allocated;
      // console.log(device_id);
      opt += "<tr><td>" + (++index) + "</td><td>" + device_type + "</td><td>" + device_name + "</td><td>" + allocated + "</td></tr>";
    }
    $("#maincontent").empty().html(opt);
  });
}

function addDevice() {
  bootbox.dialog({
    title : "Add a device",
    message : '<div class="row">  ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="device_id">Device Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="device_name" name="device_name" type="text" placeholder="Device Name" class="form-control input-md"> ' + '</div> '
        + '</div> ' + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="device_type">Device Type</label> '
        + '<div class="col-sm-6"> ' + '<input id="device_type" name="device_type" type="text" value="' + g_device_type
        + '" class="form-control input-md" readonly> ' + '</div> ' + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var device_name = $('#device_name').val();
          if (device_name == "") {
            bootbox.alert({
              // size : "small",
              title : "Failed to add new device!",
              message : "Please fill in all details!"
            });
            return;
          }
          var url = $.ctx + "/add_device.action";
          $.post(url, {
            device_name : device_name,
            device_type : g_device_type_id
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initDevice();
          });
        }
      }
    }
  });
}
