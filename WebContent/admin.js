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

var ADMIN_USER = "admin";

$(document).ready(function() {
  if ($.username != ADMIN_USER)
    $("#add_user_button").hide();

  initUser();

  check_progress();
});

function initUser() {
  var url = $.ctx + "/init_user_list.action";
  $.post(url, function(data) {
    data = JSON.parse(data);
    var opt = "";
    var index = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var username = c.username;
      var role = c.role;
      var enabled = (c.enabled == 1) ? "Enabled" : "Disabled";
      var style = (c.enabled == 1) ? "style='color:blue'" : "style='color:red'";
      opt += "<tr>";
      opt += "<td>" + (++index) + "</td><td>" + username + "</td><td>" + role + "</td><td " + style + ">" + enabled
          + "</td><td><a href=\"javascript:editUser(" + id + ",'" + username + "')\"> Edit </a>";
      if ($.username == ADMIN_USER && username != ADMIN_USER)
        if (c.enabled == 1)
          opt += " / " + "<a href=\"javascript:disableUser(" + id + ", '" + username + "')\"> Disable </a>";
        else
          opt += " / " + "<a href=\"javascript:enableUser(" + id + ", '" + username + "')\"> Enable </a>";
      opt += "</td>";
      opt += "</tr>";
    }
    $("#maincontent").empty().html(opt);
  });
}

function disableUser(id, name) {
  var url = $.ctx + "/disable_user.action";
  $.post(url, {
    id : id,
    username : name
  }, function(data) {
    data = JSON.parse(data);
    if (data["result"] != 0)
      bootbox.alert({
        // size : "small",
        title : "Oops!",
        message : data["message"]
      });
    initUser();
  });
}

function enableUser(id, name) {
  var url = $.ctx + "/enable_user.action";
  $.post(url, {
    id : id,
    username : name
  }, function(data) {
    data = JSON.parse(data);
    if (data["result"] != 0)
      bootbox.alert({
        // size : "small",
        title : "Oops!",
        message : data["message"]
      });
    initUser();
  });
}

function editUser(id, name) {
  bootbox.dialog({
    title : "Edit a user",
    message : '<div class="row"> ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" readonly value="' + name + '" class="form-control input-md"> ' + '</div> ' + '</div> '
        + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="password">Password</label> '
        + '<div class="col-sm-6"> '
        + '<input id="password" name="password" type="password" placeholder="Password" class="form-control input-md"> ' + '</div> '
        + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var username = $('#name').val();
          var password = $('#password').val();
          if (username == "" || password == "")
            return;
          var url = $.ctx + "/edit_user.action";
          $.post(url, {
            id : id,
            username : username,
            password : password
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initUser();
          });
        }
      }
    }
  });
}

function addUser() {
  bootbox.dialog({
    title : "Add a user",
    message : '<div class="row">  ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" placeholder="User name" class="form-control input-md"> ' + '</div> ' + '</div> '
        + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="password">Password</label> '
        + '<div class="col-sm-6"> '
        + '<input id="password" name="password" type="password" placeholder="Password" class="form-control input-md"> ' + '</div> '
        + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var username = $('#name').val();
          var password = $('#password').val();
          if (username == "" || password == "")
            return;
          var url = $.ctx + "/add_user.action";
          $.post(url, {
            username : username,
            password : password
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initUser();
          });
        }
      }
    }
  });
}
