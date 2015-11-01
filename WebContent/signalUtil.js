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

google.load("visualization", "1", {
  packages : [ "corechart" ]
});

var gs = [];

var NO_SIG = -4;
var SECONDS_OF_DAY = 24 * 60 * 60;
var SIZE_OF_WINDOW = 5;

var g_time = null;

var g_dyg_range = null;
var g_dyg_act = null;
var g_dyg_signal = null;

var g_range_data = null;
var g_raw_data = null;

var synced = false;

function formatSecond(v) {
  h = parseInt(v / 3600);
  m = parseInt((v - h * 3600) / 60);
  s = parseInt(v - h * 3600 - m * 60);
  return ((h < 10) ? "0" : "") + h + ":" + ((m < 10) ? "0" : "") + m + ":" + ((s < 10) ? "0" : "") + s;
}

function formatMilliSecond(v) {
  var ss = (v % 100);
  var p = ((ss < 10) ? "0" : "") + ss + "0";
  return formatSecond(v / 100) + "." + p;
}

function formatMilliSecond2(v) {
  return formatMilliSecond(v).substring(3, 11);
}

function formatMilliSecondToSecond(v) {
  return formatSecond(v / 100);
}

function formatAcc(v) {
  return v.toString();
}

function svm(x, y, z) {
  return Math.sqrt(x * x + y * y + z * z).toFixed(4);
}

function formatX(v) {
  return formatMilliSecond(v);
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
    title : 'Signal (' + formatMilliSecondToSecond(start_time) + ' &#8656; ' + formatSecond(g_time) + ' &#8658; '
        + formatMilliSecondToSecond(end_time) + ') - 100 Hz',
    ylabel : 'Acc (g)',
    legend : 'always',
    interactionModel : Dygraph.Interaction.defaultModel,
    showRangeSelector : true,
    height : height * 0.95,
    width : width * 0.95,
    maxNumberWidth : 4,
    sigFigs : 2,
    valueRange : [ -3, 3 ],
    drawCallback : function() {
      console.log("draw signal chart...");
    },
    clickCallback : function(e, x, points) {
      console.log("x = " + x);
    },
    labels : [ "Time", "Ax", "Ay", "Az" ],
    labelsDivStyles : {
      'font-family' : '"Courier New", Courier, serif'
    },
    axes : {
      x : {
        // axisLabelFormatter : formatMilliSecondToSecond,
        axisLabelFormatter : formatMilliSecond2,
        valueFormatter : formatX
      },
      y : {
        axisLabelFormatter : formatAcc,
        valueFormatter : formatAcc
      }
    }
  });
  gs.push(g_dyg_signal);

  console.log("Add act: gs=" + gs);
}

function drawRaw() {
  query = new google.visualization.Query('report/activity/signal?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date + '&time='
      + g_time);
  query.send(handleResponseRaw);
}

function actOfSecond(x) {
  return g_range_data.getValue(x, 1);
}

function getPrePoint(x, c) {
  var x1 = x;
  var count = c;
  while (x1 >= 0 && actOfSecond(x1) != NO_SIG && count > 0) {
    count--;
    x1--;
  }
  // console.log("x1 = " + x1);
  if ((x1 < 0) || (actOfSecond(x1) == NO_SIG))
    x1++;
  // console.log(x1 + " <-- " + x);
  return x1;
}

function getNextPoint(x, c) {
  var x1 = x;
  var count = c;
  while (x1 <= SECONDS_OF_DAY - 1 && actOfSecond(x1) != NO_SIG && count > 0) {
    count--;
    x1++;
  }
  // console.log("x1 = " + x1);
  if ((x1 > SECONDS_OF_DAY - 1) || (actOfSecond(x1) == NO_SIG))
    x1--;
  // console.log(x + " --> " + x1);
  return x1;
}

function highlightRange(x1, x2) {
  // Highlight selected range
  console.log("highlightRange: [" + x1 + ", " + x2 + "]");
  g_selectRange = true;
  g_dyg_range.updateOptions({
    underlayCallback : function(canvas, area, g) {
      var bottom_left = g.toDomCoords(x1, 0);
      var top_right = g.toDomCoords(x2, 0);
      var left = bottom_left[0];
      var right = top_right[0];

      canvas.fillStyle = "#99FFFF";
      canvas.fillRect(left, area.y, right - left, area.h);
    }
  });
}

var rangeStart = 0;
var rangeEnd = 0;

function addAnnotation(g, name, x, func) {
  // console.log("name = " + name);
  var ann = {
    series : name,
    xval : x,
    shortText : "X",
    text : func(x),
    attachAtBottom : true
  };
  var anns = [];
  anns.push(ann);
  g.setAnnotations(anns);
}

function updateRange(x) {
  console.log("updateRange: " + x);

  start();

  rangeStart = getPrePoint(x, SIZE_OF_WINDOW / 2 * 60);
  rangeEnd = getNextPoint(x, SIZE_OF_WINDOW / 2 * 60);
  console.log("start: " + rangeStart + ", end: " + rangeEnd);
  highlightRange(rangeStart, rangeEnd);

  addAnnotation(g_dyg_range, "Act", x, formatSecond);

  clearSignalAndActDiagrams();

  drawRaw();
  drawRes();

  synced = false;

  setTimeout(sync, 1000);
}

var is_under_processing = false;

function isUnderProcessing() {
  console.log("is_under_processing: " + is_under_processing);
  return is_under_processing;
}

function do_start() {
  $("#backward_button").attr("disabled", true);
  $("#forward_button").attr("disabled", true);
  $("#time_button").attr("disabled", true);
}

function do_end() {
  $("#backward_button").attr("disabled", false);
  $("#forward_button").attr("disabled", false);
  $("#time_button").attr("disabled", false);
}

function rangeSelected(e, x, points) {
  console.log("x = " + x);
  if (isUnderProcessing())
    return;

  if (e.detail > 1) {
    console.log("Skip non-single click mouse event.");
    return;
  }
  var yval = points[0].yval;
  console.log(x + "->" + yval);
  if (yval == NO_SIG) {
    console.log("NO act");
    return;
  }

  g_time = x;
  // console.log("g_time = " + g_time);

  updateRange(g_time);
}

var g_selectRange = false;

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
    labels : [ "Time", "Act" ],
    labelsDivStyles : {
      'font-family' : '"Courier New", Courier, serif'
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
      }
    }
  });
}

function drawRange() {
  query = new google.visualization.Query('report/activity/range?type=0&prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
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
    title : 'Activity (' + formatMilliSecondToSecond(start_time) + ' &#8656; ' + formatSecond(g_time) + ' &#8658; '
        + formatMilliSecondToSecond(end_time) + ') - 100 Hz',
    ylabel : 'Act',
    legend : 'always',
    interactionModel : Dygraph.Interaction.defaultModel,
    showRangeSelector : true,
    height : height * 0.95,
    width : width * 0.95,
    valueRange : [ -3, 3 ],
    drawCallback : function(g, is_initial) {
      console.log("draw act chart...");
    },
    labels : [ "Time", "Act" ],
    labelsDivStyles : {
      'font-family' : '"Courier New", Courier, serif'
    },
    clickCallback : function(e, x, points) {
      console.log("x = " + x);
    },
    axes : {
      x : {
        // axisLabelFormatter : formatMilliSecondToSecond,
        axisLabelFormatter : formatMilliSecond2,
        valueFormatter : formatMilliSecond
      },
      y : {
        axisLabelFormatter : formatAct,
        valueFormatter : formatAct
      }
    }
  });

  gs.push(g_dyg_act);

  console.log("Add act: gs=" + gs);
}

function drawRes() {
  query = new google.visualization.Query('report/activity/result?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date + '&time='
      + g_time);
  query.send(handleResponseAct);
}

function clearDiagrams() {
  $(".data-div").hide();

  $("#duration").html("");
  $("#chart_div_range").html("");

  clearSignalAndActDiagrams();
}

function clearSignalAndActDiagrams() {
  gs = [];
  $("#chart_div_raw").removeClass("signal_chart");
  $("#chart_div_raw").html("");
  $("#chart_div_act").removeClass("act_chart");
  $("#chart_div_act").html("");
}

function sync() {
  if (synced) {
    console.log("Already synced");
    return;
  }
  // console.log("gs=" + gs);
  if (gs.length == 2) {
    console.log("Synchronizing...");
    Dygraph.synchronize(gs, {
      selection : false,
      zoom : true
    });
    synced = true;

    addAnnotation(g_dyg_act, "Act", g_time * 100, formatMilliSecond);
    addAnnotation(g_dyg_signal, "Ax", g_time * 100, formatMilliSecond);
    end();
  } else {
    console.log("Unable to sync, wait another 1 second and sync again.");
    setTimeout(sync, 1000);
  }
}

function do_date_changed() {
  $(".data-div").show();

  g_selectRange = false;
  drawRange();
}

function backward_button_click() {
  if (g_time == null)
    return;

  console.log("backward_button is clicked: " + g_time);
  var new_g_time = getPrePoint(g_time, SIZE_OF_WINDOW * 60);
  // console.log("new_g_time = " + new_g_time);
  if (g_time == new_g_time)
    return;
  g_time = new_g_time;
  updateRange(g_time);
}

function forward_button_click() {
  if (g_time == null)
    return;

  console.log("forward_button is clicked: " + g_time);
  var new_g_time = getNextPoint(g_time, SIZE_OF_WINDOW * 60);
  // console.log("new_g_time = " + new_g_time);
  if (g_time == new_g_time)
    return;
  g_time = new_g_time;
  updateRange(g_time);
}

function init() {
  var url = $.ctx + "/get_config.action";
  $.post(url, {
    "name" : "SIZE_OF_WINDOW",
  }, function(data) {
    data = JSON.parse(data);
    SIZE_OF_WINDOW = parseInt(data.value);
    console.log("SIZE_OF_WINDOW = " + SIZE_OF_WINDOW);
  });

}

function time_button() {
  g_val = $("#g_time").val();
  if (g_val.length < 5)
    return;
  if (g_val.length < 8)
    g_val += ":00";
  console.log("g_val=" + g_val);
  var ss = g_val.split(":");
  var t = parseInt(ss[0]) * 3600 + parseInt(ss[1]) * 60 + parseInt(ss[2]);
  // console.log("t=" + t);
  if (actOfSecond(t) == NO_SIG) {
    console.log("NO act");
    bootbox.alert({
      title : "Oops!",
      message : "NO data for " + g_val + ".<br>Please change the time and try again!"
    });
    $("#g_time").val("");
    return;
  }
  g_time = t;
  updateRange(t);
}
function do_ready() {
  start();
  $("#backward_button").click(backward_button_click);
  $("#forward_button").click(forward_button_click);
  $("#time_button").on('click', time_button);
  init();
}
