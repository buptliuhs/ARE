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
  initSetting();

  check_progress();
});

function initSetting() {
  var url = $.ctx + "/init_setting_list.action";
  $.post(url, function(data) {
    data = JSON.parse(data);
    var opt = "";
    var index = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var name = c.name;
      var description = c.description;
      var value = c.value;
      opt += "<tr><td>" + (++index) + "</td><td>" + name + "</td><td>" + description + "</td><td>" + value
          + "</td><td><a href=\"javascript:editSetting(" + id + ",'" + name + "','" + description + "','" + value + "')\"> Edit </a>"
          + "</td></tr>";
    }
    $("#maincontent").empty().html(opt);
  });
}

function editSetting(id, name, description, value) {
  bootbox.dialog({
    title : "Edit a setting",
    message : '<div class="row"> ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="name">Name</label> ' + '<div class="col-sm-6"> '
        + '<input id="name" name="name" type="text" value="'
        + name
        + '" class="form-control input-md" readonly> '
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="description">Description</label> '
        + '<div class="col-sm-6"> '
        + '<input id="description" name="description" type="text" value="'
        + description
        + '" class="form-control input-md" readonly> '
        + '</div> '
        + '</div> '
        + '<div class="form-group"> '
        + '<label class="col-sm-4 control-label" for="description">Value</label> '
        + '<div class="col-sm-6"> '
        + '<input id="value" name="value" type="text" value="'
        + value
        + '" class="form-control input-md"> '
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
          var value = $('#value').val();
          if (name == "" || description == "" || value == "")
            return;
          var url = $.ctx + "/edit_setting.action";
          $.post(url, {
            id : id,
            name : name,
            description : description,
            value : value
          }, function(data) {
            data = JSON.parse(data);
            if (data["result"] != 0)
              bootbox.alert({
                // size : "small",
                title : "Oops!",
                message : data["message"]
              });
            initSetting();
          });
        }
      }
    }
  });
}
