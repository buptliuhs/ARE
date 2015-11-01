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

function formatEELabel(v) {
  return v.toFixed(0).toString();
}

function formatEE(v) {
  return v.toFixed(2).toString();
}

function formatAngleLabel(v) {
  return v.toFixed(0).toString();
}

function formatAngle(v) {
  return v.toFixed(1).toString();
}

function handleResponseRange(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  g_range_data = response.getDataTable();
  var height = $("#chart_div_range").height();
  var width = $("#chart_div_range").width();
  g_dyg_range = new Dygraph(document.getElementById("chart_div_range"), g_range_data, {
    title : 'Overall Activity - 1 Hz',
    ylabel : 'Act',
    y2label : 'SMA',
    legend : 'always',
    interactionModel : Dygraph.Interaction.defaultModel,
    showRangeSelector : true,
    height : height * 0.95,
    width : width * 0.95,
    valueRange : [ -4, 4 ],
    drawCallback : function() {
      console.log("draw range chart...");
      if (!g_selectRange)
        end();
    },
    // labels : [ "Time", "Act", "SMA", "SMA-H", "SMA-L" ],
    labels : [ "Time", "Act", "SMA" ],
    labelsDivStyles : {
      'font-family' : '"Courier New", Courier, serif'
    },
    // This series is for intermediate result display
    series : {
      'SMA' : {
        axis : 'y2'
      // },
      // 'SMA-H' : {
      // axis : 'y2'
      // },
      // 'SMA-L' : {
      // axis : 'y2'
      }
    },
    clickCallback : rangeSelected,
    axes : {
      x : {
        axisLabelFormatter : formatSecond,
        valueFormatter : formatSecond
      },
      y : {
        axisLabelFormatter : formatAct,
        valueFormatter : formatAct
      },
      y2 : {
        axisLabelFormatter : formatEELabel,
        valueFormatter : formatEE
      }
    }
  });
}

function drawRange() {
  query = new google.visualization.Query('report/activity/range?type=1&prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
  query.send(handleResponseRange);
}

function handleResponseAct(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  var length = data.getNumberOfRows();
  var start_time = data.getValue(0, 0);
  var end_time = data.getValue(length - 1, 0);
  $("#chart_div_act").addClass("act_chart");
  var height = $("#chart_div_act").height();
  var width = $("#chart_div_act").width();
  g_dyg_act = new Dygraph(document.getElementById("chart_div_act"), data, {
    title : 'Activity & Signal Magnitude Areas (SMA) (' + formatMilliSecondToSecond(start_time) + ' &#8656; ' + formatSecond(g_time)
        + ' &#8658; ' + formatMilliSecondToSecond(end_time) + ') - 100 Hz',
    ylabel : 'Act',
    y2label : 'SMA',
    legend : 'always',
    interactionModel : Dygraph.Interaction.defaultModel,
    showRangeSelector : true,
    height : height * 0.95,
    width : width * 0.95,
    drawCallback : function(g, is_initial) {
      console.log("draw act chart...");
    },
    labels : [ "Time", "Act", "SMA", "SMA-H", "SMA-L" ],
    labelsDivStyles : {
      'font-family' : '"Courier New", Courier, serif'
    },
    clickCallback : function(e, x, points) {
      console.log("x = " + x);
    },
    // This series is for intermediate result display
    series : {
      'SMA' : {
        axis : 'y2'
      },
      'SMA-H' : {
        axis : 'y2'
      },
      'SMA-L' : {
        axis : 'y2'
      }
    },
    axes : {
      x : {
        // axisLabelFormatter : formatMilliSecondToSecond,
        axisLabelFormatter : formatMilliSecond2,
        valueFormatter : formatMilliSecond
      },
      y : {
        valueRange : [ -3, 3 ],
        axisLabelFormatter : formatAct,
        valueFormatter : formatAct
      },
      y2 : {
        axisLabelFormatter : formatEELabel,
        valueFormatter : formatEE
      }
    }
  });

  gs.push(g_dyg_act);

  console.log("Add act: gs=" + gs);
}

function drawRes() {
  query = new google.visualization.Query('report/activity/result?type=1&prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&time=' + g_time);
  query.send(handleResponseAct);
}

function handleResponseRaw(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  g_raw_data = data;
  var length = data.getNumberOfRows();
  var start_time = data.getValue(0, 0);
  var end_time = data.getValue(length - 1, 0);
  $("#chart_div_raw").addClass("signal_chart");
  var height = $("#chart_div_raw").height();
  var width = $("#chart_div_raw").width();

  g_dyg_signal = new Dygraph(document.getElementById("chart_div_raw"), data, {
    title : 'Signal & Angle to Vertical Plane (' + formatMilliSecondToSecond(start_time) + ' &#8656; ' + formatSecond(g_time) + ' &#8658; '
        + formatMilliSecondToSecond(end_time) + ') - 100 Hz',
    ylabel : 'Acc (g)',
    y2label : 'Angle (&deg;)',
    legend : 'always',
    interactionModel : Dygraph.Interaction.defaultModel,
    showRangeSelector : true,
    height : height * 0.95,
    width : width * 0.95,
    maxNumberWidth : 4,
    sigFigs : 2,
    drawCallback : function() {
      console.log("draw signal chart...");
    },
    clickCallback : function(e, x, points) {
      console.log("x = " + x);
    },
    labels : [ "Time", "Ax", "Ay", "Az", "Angle" ],
    labelsDivStyles : {
      'font-family' : '"Courier New", Courier, serif'
    },
    // This series is for intermediate result display
    series : {
      'Angle' : {
        axis : 'y2'
      }
    },
    axes : {
      x : {
        // axisLabelFormatter : formatMilliSecondToSecond,
        axisLabelFormatter : formatMilliSecond2,
        valueFormatter : formatX
      },
      y : {
        valueRange : [ -3, 3 ],
        axisLabelFormatter : formatAcc,
        valueFormatter : formatAcc
      },
      y2 : {
        valueRange : [ 0, 180 ],
        axisLabelFormatter : formatAngleLabel,
        valueFormatter : formatAngle
      }
    }
  });
  gs.push(g_dyg_signal);

  console.log("Add act: gs=" + gs);
}

function drawRaw() {
  query = new google.visualization.Query('report/activity/signal?type=1&prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&time=' + g_time);
  query.send(handleResponseRaw);
}

function clickUnmatchedRecord() {
  $(this).siblings().removeClass("selected");
  $(this).toggleClass("selected");

  var tds = $(this).find("td");
  var td = tds.first();
  var second = parseInt(td.text());
  td = td.next();
  var time = td.text();
  td = td.next();
  var actA = td.text();
  td = td.next();
  var actB = td.text();
  console.log("Clicking...: " + second + ", " + time + ", " + actA + ", " + actB);

  g_time = second;
  // update range
  updateRange(g_time);
}

var g_data_table = null;

function displayUnmatchedRecords(validation_result) {
  if (g_data_table === null) {
  } else {
    g_data_table.destroy();
    g_data_table = null;
  }

  opt = "";
  for ( var o in validation_result) {
    var item = validation_result[o];
    // console.log(item);
    opt += "<tr>";
    opt += "<td>" + item.second + "</td>";
    opt += "<td>" + item.time + "</td>";
    opt += "<td>" + parseActInt(item.actA) + "</td>";
    opt += "<td>" + parseActInt(item.actB) + "</td>";
    opt += "</tr>";

  }
  // console.log(opt);
  $("#unmatched_table_body").empty().html(opt);
  $('#unmatched_table_body tr').on('click', clickUnmatchedRecord);

  g_data_table = $('#unmatched_table').DataTable({
    "scrollY" : "200px",
    "scrollCollapse" : true,
    "paging" : true
  });
}

function validate(event) {
  event.preventDefault();
  if (g_prj_id == "" || g_sub_id == "" || g_date == "")
    return;
  var fileInput = $("#fileToUpload");
  var fileName = fileInput.val();
  if (fileName == "") {
    console.log("No file selected");
    return;
  }
  console.log("validate");
  start();

  var formData = new FormData($(this)[0]);

  // console.log(formData);
  $.ajax({
    url : 'ValidationServlet?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date,
    type : 'POST',
    data : formData,
    async : true,
    cache : false,
    contentType : false,
    processData : false,
    success : function(data) {
      // console.log(data);
      data = JSON.parse(data);
      console.log(data);
      if (data["result"] != 0)
        bootbox.alert({
          // size : "small",
          title : "Oops!",
          message : data["message"]
        });
      else {
        console.log(data["message"]);
        $("#percentage").text(data["percentage"] + " %");
        $("#validation_result").show();

        displayUnmatchedRecords(data["validation_result"]);
      }
      end();
    }
  });

  // clear
  fileInput.replaceWith(fileInput.val('').clone(true));
  // remove
  $(".file-input-name").remove();
}

function do_ready() {
  $("#validation_form").submit(validate);
  $('input[type=file]').bootstrapFileInput();
  $('.file-inputs').bootstrapFileInput();
  // console.log("do_ready");

  start();
  $("#backward_button").click(backward_button_click);
  $("#forward_button").click(forward_button_click);
  $("#time_button").on('click', time_button);

  init();
}

function clearDiagrams() {
  $(".data-div").hide();

  $("#duration").html("");
  $("#chart_div_range").html("");
  $("#validation_result").hide();

  clearSignalAndActDiagrams();
}

function do_start() {
  $("#backward_button").attr("disabled", true);
  $("#forward_button").attr("disabled", true);
  $("#time_button").attr("disabled", true);
  $("#validate_button").attr("disabled", true);
}

function do_end() {
  $("#backward_button").attr("disabled", false);
  $("#forward_button").attr("disabled", false);
  $("#time_button").attr("disabled", false);
  $("#validate_button").attr("disabled", false);
}
