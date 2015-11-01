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

function initIdleDevice() {
  var idleDeviceList = [];
  var url = $.ctx + "/init_idle_device_list.action";
  $.ajax({
    type : 'POST',
    url : url,
    async : false
  }).done(function(data) {
    // console.log(data);
    data = JSON.parse(data);
    console.log(data);
    for ( var o in data) {
      var c = data[o];
      var device_type = c.device_type;
      var device_id = c.device_id;
      var device_name = c.device_name;

      // console.log(device_name);
      idleDeviceList.push({
        "device_id" : device_id,
        "device_type" : device_type,
        "device_name" : device_name
      });
      // console.log(idleDeviceList);
    }
  });
  return idleDeviceList;
}

function initSub() {
  var url = $.ctx + "/init_sub_list.action";
  $.post(url, {
    "project_id" : urlArgs().project_id
  }, function(data) {
    data = JSON.parse(data);
    var opt = "";
    var index = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var name = c.name;
      var project_name = c.project_name;
      var dob = c.dob;
      var gender = c.gender;
      var weight = c.weight;
      var height = c.height;
      var device_id = c.device_id;
      var device_name = c.device_name;
      opt += "<tr><td>" + (++index) + "</td><td>" + project_name + "</td><td>" + name + "</td><td>" + dob + "</td><td>" + gender
          + "</td><td>" + weight + "</td><td>" + height + "</td><td>" + device_name + "</td><td><a href=\"javascript:editSub(" + id + ",'"
          + name + "','" + dob + "','" + gender + "'," + weight + "," + height + "," + device_id + ",'" + device_name
          + "')\"> Edit </a> / " + "<a href=\"javascript:deleteSub(" + id + ",'" + name + "')\"> Delete </a>" + "</td></tr>";
    }
    $("#maincontent").empty().html(opt);
  });
}

function editSub(id, name, dob, gender, weight, height, device_id, device_name) {
  bootbox.dialog({
    title : "Edit a subject",
    message : '<div class="row"> ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" value="'
        + name
        + '" class="form-control input-md"> '
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="dob">DOB</label> '
        + '<div class="col-sm-6"> '
        + '<input id="dob" name="dob" class="form-control input-md datepicker" data-date-format="dd/mm/yyyy" value="'
        + dob
        + '">'
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="gender">Gender</label> '
        + '<div class="col-sm-6"> '
        + '<select id="gender" class="selectpicker">'
        + '<option value="0">Male</option>'
        + '<option value="1">Female</option>'
        + '<option value="2">Other</option>'
        + '</select>'
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="weight">Weight (kg)</label> '
        + '<div class="col-sm-6"> '
        + '<input id="weight" name="weight" type="number" min="1" max="250" value="'
        + weight
        + '" class="form-control input-md"> '
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="height">Height (cm)</label> '
        + '<div class="col-sm-6"> '
        + '<input id="height" name="height" type="number" min="1" max="250" value="'
        + height
        + '" class="form-control input-md"> '
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="device_id">Device ID</label> '
        + '<div class="col-sm-6"> '
        + '<input id="device_type" name="device_type" type="text" value="'
        + device_name
        + '" class="form-control input-md" readonly> ' + '</div> ' + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var name = $('#name').val();
          var dob = $('#dob').val();
          var gender = $('#gender').val();
          var weight = $('#weight').val();
          var height = $('#height').val();
          var device_id = $('#device_id').val();
          if (name == "" || dob == "" || gender == "" || weight == "" || height == "" || device_id == "")
            return;
          var url = $.ctx + "/edit_sub.action";
          $.post(url, {
            id : id,
            name : name,
            dob : dob,
            gender : gender,
            weight : weight,
            height : height,
            device_id : device_id
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initSub();
          });
        }
      }
    }
  });
  $('.datepicker').datepicker();
  $('.selectpicker').selectpicker();

  var g;
  if (gender == "Male")
    g = "0";
  else if (gender == "Female")
    g = "1";
  else
    g = "2";

  $("#gender").val(g);
  $("#gender").change();
}

function deleteSub(id, name) {
  bootbox.confirm({
    // size : "small",
    title : "Need Confirmation",
    message : "Delete subject: " + name + " ?",
    callback : function(result) {
      if (result) {
        var url = $.ctx + "/delete_sub.action";
        $.post(url, {
          id : id
        }, function(data) {
          data = JSON.parse(data);
          if (data["result"] != 0)
            bootbox.alert({
              // size : "small",
              title : "Oops!",
              message : data["message"]
            });
          initSub();
        });
      }
    }
  });
}

function getDeviceIDOptions() {
  var list = initIdleDevice();
  var d_options = "";
  for ( var i = 0; i < list.length; ++i) {
    d_options += '<option value="' + list[i].device_id + '">' + list[i].device_type + " - " + list[i].device_name + '</option>';
  }
  // console.log(d_options);
  return d_options;
}

function addSub() {
  bootbox.dialog({
    title : "Add a subject",
    message : '<div class="row"> ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" placeholder="Subject Name" class="form-control input-md"> ' + '</div> ' + '</div> '
        + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="dob">DOB</label> ' + '<div class="col-sm-6"> '
        + '<input id="dob" name="dob" class="form-control input-md datepicker" data-date-format="dd/mm/yyyy" placeholder="dd/mm/yyyy">'
        + '</div> ' + '</div> ' + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="gender">Gender</label> '
        + '<div class="col-sm-6"> ' + '<select id="gender" class="selectpicker">' + '<option value="0">Male</option>'
        + '<option value="1">Female</option>' + '<option value="2">Other</option>' + '</select>' + '</div> ' + '</div> '
        + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="weight">Weight (kg)</label> '
        + '<div class="col-sm-6"> '
        + '<input id="weight" name="weight" type="number" min="1" max="250" placeholder="Weight" class="form-control input-md"> '
        + '</div> ' + '</div> ' + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="height">Height (cm)</label> '
        + '<div class="col-sm-6"> '
        + '<input id="height" name="height" type="number" min="1" max="250" placeholder="Height" class="form-control input-md"> '
        + '</div> ' + '</div> ' + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="device_id">Device ID</label> '
        + '<div class="col-sm-6"> ' + '<select id="device_id" class="selectpicker">' + getDeviceIDOptions() + '</select>' + '</div> '
        + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var project_id = urlArgs().project_id;
          var name = $('#name').val();
          var dob = $('#dob').val();
          var gender = $('#gender').val();
          var weight = $('#weight').val();
          var height = $('#height').val();
          var device_id = $('#device_id').val();
          if (name == "" || dob == "" || gender == "" || weight == "" || height == "" || device_id == "" || device_id == null) {
            bootbox.alert({
              // size : "small",
              title : "Failed to add new subject!",
              message : "Please fill in all details!"
            });
            return;
          }
          var url = $.ctx + "/add_sub.action";
          $.post(url, {
            project_id : project_id,
            name : name,
            dob : dob,
            gender : gender,
            weight : weight,
            height : height,
            device_id : device_id
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initSub();
          });
        }
      }
    }
  });
  $(".datepicker").datepicker();
  $('.selectpicker').selectpicker();
}

$(document).ready(function() {
  initSub();

  check_progress();

  $("#add_subject_button").on('click', addSub);
});
