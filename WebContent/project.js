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
  initPrj();

  check_progress();

  $("#add_project_button").on('click', addPrj);
});

function initPrj() {
  var url = $.ctx + "/init_prj_list.action";
  $.post(url, function(data) {
    data = JSON.parse(data);
    var opt = "";
    var index = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var name = c.name;
      var description = c.description;
      opt += "<tr><td>" + (++index) + "</td><td>" + name + "</td><td>" + description + "</td><td><a href=\"javascript:viewPrj(" + id
          + ")\"> View </a> / " + "<a href=\"javascript:editPrj(" + id + ",'" + name + "','" + description + "')\"> Edit </a> / "
          + "<a href=\"javascript:deletePrj(" + id + ", '" + name + "')\"> Delete </a>" + "</td></tr>";
    }
    $("#maincontent").empty().html(opt);
  });
}

function viewPrj(id) {
  window.location.href = $.ctx + "/subject.jsp?project_id=" + id;
}

function editPrj(id, name, description) {
  bootbox.dialog({
    title : "Edit a project",
    message : '<div class="row"> ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" value="' + name + '" class="form-control input-md"> ' + '</div> ' + '</div> '
        + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="description">Description</label> '
        + '<div class="col-sm-6"> ' + '<input id="description" name="description" type="text" value="' + description
        + '" class="form-control input-md"> ' + '</div> ' + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var name = $('#name').val();
          var description = $('#description').val();
          if (name == "" || description == "")
            return;
          var url = $.ctx + "/edit_prj.action";
          $.post(url, {
            id : id,
            name : name,
            description : description
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initPrj();
          });
        }
      }
    }
  });
}

function deletePrj(id, name) {
  bootbox.confirm({
    // size : "small",
    title : "Need Confirmation",
    message : "Delete project: " + name + " ?",
    callback : function(result) {
      if (result) {
        var url = $.ctx + "/delete_prj.action";
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
          initPrj();
        });
      }
    }
  });
}

function addPrj() {
  bootbox.dialog({
    title : "Add a project",
    message : '<div class="row">  ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" placeholder="Project name" class="form-control input-md"> ' + '</div> ' + '</div> '
        + '<div class="form-group"> ' + '<label class="col-sm-4 control-label" for="description">Description</label> '
        + '<div class="col-sm-6"> '
        + '<input id="description" name="description" type="text" placeholder="Project description" class="form-control input-md"> '
        + '</div> ' + '</div> ' + '</form>' + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "Save" : {
        className : "btn-success",
        callback : function() {
          var name = $('#name').val();
          var description = $('#description').val();
          if (name == "" || description == "") {
            bootbox.alert({
              // size : "small",
              title : "Failed to add new project!",
              message : "Please fill in all details!"
            });
            return;
          }
          var url = $.ctx + "/add_prj.action";
          $.post(url, {
            name : name,
            description : description
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initPrj();
          });
        }
      }
    }
  });
}
