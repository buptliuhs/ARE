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

var g_prj_id = "";
var g_sub_id = "";
var g_date = "";

var colors = [ 'blue', 'red', 'purple', 'orange', 'green', 'orangered', 'crimson', 'black' ];

function formatAct(v) {
  switch (v) {
  case -4:
    return "N/A";
  case -3:
    return "Nwr";
  case -2:
    return "Inv";
  case -1:
    return "Lie";
  case 0:
    return "Sit";
  case 1:
    return "Std";
  case 2:
    return "Shf";
  case 3:
    return "Wlk";
  }
}

function parseActInt(i) {
  switch (i) {
  case 3:
    return "WALKING";
  case 2:
    return "SHUFFLING";
  case 1:
    return "STANDING";
  case 0:
    return "SITTING";
  case -1:
    return "LYING";
  case -2:
    return "INVERTED";
  case -3:
    return "NONWEAR";
  }
  return "UNDEFINED";
}

function do_ready() {
}

function do_start() {
}

function do_end() {
}

$(document).ready(function() {
  $("#sel_prj").change(function() {
    $("#sel_prj option:selected").each(prj_changed);
  });

  $("#sel_sub").change(function() {
    $("#sel_sub option:selected").each(sub_changed);
  });

  $("#sel_date").change(function() {
    $("#sel_date option:selected").each(date_changed);
  });

  initPrj();

  check_progress();

  do_ready();
});

function start() {
  is_under_processing = true;
  $("#timer_spinner").show();

  $("#sel_prj").attr("disabled", true);
  $("#sel_sub").attr("disabled", true);
  $("#sel_date").attr("disabled", true);

  do_start();
}

function end() {
  is_under_processing = false;
  $("#timer_spinner").hide();

  $("#sel_prj").attr("disabled", false);
  $("#sel_sub").attr("disabled", false);
  $("#sel_date").attr("disabled", false);

  do_end();
}

function date_changed() {
  start();

  clearDiagrams();

  g_date = $(this).val();
  $("#duration").html($(this).attr("duration"));

  do_date_changed();
}

function initDate(prjId, subId) {
  $("#sel_date").empty();

  var url = $.ctx + "/init_report_list.action";
  $.post(url, {
    "project_id" : prjId,
    "subject_id" : subId
  }, function(data) {
    data = JSON.parse(data);
    var num = 0;
    for ( var o in data) {
      var c = data[o];
      var text = c.text;
      var report_date = c.report_date;
      var duration = c.duration;
      $("#sel_date").append($('<option>', {
        value : report_date,
        text : text,
        project_id : prjId,
        subject_id : subId,
        duration : duration
      }));
      num++;
    }
    if (num == 0)
      end();
    else
      $("#sel_date option:selected").each(date_changed);
  });
}

function clearSub() {
  $("#sub_dob").html("");
  $("#sub_weight").html("");
  $("#sub_height").html("");
  $("#sub_gender").html("");
  $("#sub_device_name").html("");
  $("#duration").html("");
}

function do_sub_changed() {
  initDate(g_prj_id, g_sub_id);
}

function sub_changed() {
  start();

  clearDiagrams();

  $("#sub_dob").html($(this).attr("sub_dob"));
  $("#sub_height").html($(this).attr("sub_height"));
  $("#sub_weight").html($(this).attr("sub_weight"));
  $("#sub_gender").html($(this).attr("sub_gender"));
  $("#sub_device_name").html($(this).attr("sub_device_name"));
  $("#duration").html($(this).attr("duration"));

  g_sub_id = $(this).val();

  do_sub_changed();
}

function initSub(prjId) {
  $("#sel_sub").empty();
  var url = $.ctx + "/init_sub_list.action";
  $.post(url, {
    "project_id" : prjId
  }, function(data) {
    data = JSON.parse(data);
    var num = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var name = c.name;
      var dob = c.dob;
      var weight = c.weight;
      var height = c.height;
      var gender = c.gender;
      var device_name = c.device_name;
      var duration = c.duration;
      $("#sel_sub").append($('<option>', {
        value : id,
        text : name,
        sub_dob : dob,
        sub_height : height,
        sub_weight : weight,
        sub_gender : gender,
        sub_device_name : device_name,
        duration : duration
      }));
      num++;
    }
    if (num == 0)
      end();
    else
      $("#sel_sub option:selected").each(sub_changed);
  });
}

function initPrj() {
  $("#sel_prj").empty();
  var url = $.ctx + "/init_prj_list.action";
  $.post(url, function(data) {
    data = JSON.parse(data);
    var num = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var name = c.name;
      $("#sel_prj").append($('<option>', {
        value : id,
        text : name
      }));
      num++;
    }
    if (num == 0)
      end();
    else
      $("#sel_prj option:selected").each(prj_changed);
  });
}

function prj_changed() {
  start();

  $("#sel_sub").empty();
  clearSub();
  $("#sel_date").empty();
  clearDiagrams();

  g_prj_id = $(this).val();
  initSub(g_prj_id);
}

function addAnnotation(data) {
  for (var j = data.getNumberOfColumns(); j > 1; --j) {
    data.insertColumn(j, {
      type : 'string',
      role : 'annotation'
    });
    for (var i = 0; i < data.getNumberOfRows(); ++i) {
      var v = data.getValue(i, j - 1);
      data.setValue(i, j, v.toString());
    }
  }
}
