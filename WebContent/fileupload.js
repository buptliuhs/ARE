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

function clearDiagrams() {
  $("#files").html("");
}

function initFiles(prjId, subId) {
  $('#fileupload').fileupload({
    url : 'UploadServlet?project_id=' + prjId + "&subject_id=" + subId,
    sequentialUploads : false
  });
  // Load existing files:
  $('#fileupload').addClass('fileupload-processing');
  $.ajax({
    url : $('#fileupload').fileupload('option', 'url'),
    dataType : 'json',
    context : $('#fileupload')[0]
  }).always(function() {
    $(this).removeClass('fileupload-processing');
  }).done(function(result) {
    console.log(result);
    $(this).fileupload('option', 'done').call(this, $.Event('done'), {
      result : result
    });
    $("#number_of_files").html("" + result["files"].length);
  });
}

function getAlgorithmOptions() {
  var list = initAlgorithm();
  // console.log("Algorithm: " + list);
  var d_options = "";
  for ( var i = 0; i < list.length; ++i) {
    d_options += '<option value="' + list[i].class_name + '">' + list[i].name + '</option>';
  }
  // console.log(d_options);
  return d_options;
}

function reprocess(event, url) {
  console.log("reporcess: " + url);
  // console.log("event: " + event);
  event.preventDefault();

  bootbox.dialog({
    title : "Select an algorithm and rerun",
    message : '<div class="row"> ' + '<div class="col-sm-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> '
        + '<label class="col-sm-3 control-label" for="device_id">Algorithm</label> ' + '<div class="col-sm-9"> '
        + '<select id="algorithm" class="selectpicker">' + getAlgorithmOptions() + '</select>' + '</div> ' + '</div> ' + '</form>'
        + '</div> ' + '</div> ',
    buttons : {
      "Cancel" : {
        className : "btn-danger",
      },
      "GO!" : {
        className : "btn-success",
        callback : function() {
          var algorithm = $('#algorithm').val();
          url = url + "&algorithm=" + algorithm;
          $.get(url, function(data) {
            console.log("Reprocess requested!");
            // refresh file list
            start();
            clearDiagrams();
            do_sub_changed();
          });
        }
      }
    }
  });
  $('.selectpicker').selectpicker({
    width : 'auto'
  });
}
function do_sub_changed() {
  initFiles(g_prj_id, g_sub_id);
  initDeviceType(g_prj_id, g_sub_id);
  end();
}

function initAlgorithm() {
  var algorithm_list = [];
  var url = $.ctx + "/init_algorithm_list.action";
  $.ajax({
    type : 'POST',
    url : url,
    async : false
  }).done(function(data) {
    data = JSON.parse(data);
    for ( var o in data) {
      var c = data[o];
      algorithm_list.push(c);
    }
  });
  return algorithm_list;
}

function initDeviceType(prjId, subId) {
  var url = $.ctx + "/init_device_type_list.action";
  $.post(url, {
    "project_id" : prjId,
    "subject_id" : subId
  }, function(data) {
    data = JSON.parse(data);
    var opt = "";
    var suffix = "*";
    for ( var o in data) {
      var c = data[o];
      var name = c.name;
      var description = c.description;
      var sample = c.sample;
      suffix = c.suffix;
      opt += "<li>" + name + " (" + description + ") - Sample : " + sample + "</li>";
    }
    $("#notice_list").empty().html(opt);
    $("#file_selector")[0].accept = suffix;
  });
}

function do_ready() {
  $("#sel_date_div").hide();
}
