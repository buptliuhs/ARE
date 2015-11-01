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
  packages : [ "corechart", "table" ]
});

function handleResponseOverallPie(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  // display total
  var total = google.visualization.data.group(data, [ {
    type : 'number',
    column : 0,
    modifier : function() {
      return 0;
    }
  } ], [ {
    type : 'number',
    column : 1,
    aggregation : google.visualization.data.sum
  } ]);
  var t = total.getValue(0, 1);
  var max = 24 * 60;
  t = (t > max) ? max : t;
  data.addRow([ 'Total: ' + t.toFixed(1) + ' minutes', 0 ]);

  var options = {
    // 'title' : 'PROPORTION (in hours)',
    sliceVisibilityThreshold : 0,
    chartArea : {
      width : '90%',
      height : '80%'
    },
    slices : {
      0 : {
        color : colors[0]
      },
      1 : {
        color : colors[1]
      },
      2 : {
        color : colors[2]
      },
      3 : {
        color : colors[3]
      },
      4 : {
        color : colors[4]
      },
      5 : {
        color : colors[5]
      },
      6 : {
        color : colors[6]
      },
      7 : {
        color : colors[7]
      }
    }
  };

  var chart = new google.visualization.PieChart(document.getElementById("chart_div_pie"));
  chart.draw(data, options);
}

function drawChartOverallPie() {
  query = new google.visualization.Query('report/daily/overall?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
  query.send(handleResponseOverallPie);
}

function handleResponseOverallColumn(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  addAnnotation(data);

  data.addColumn({
    type : 'string',
    role : 'style'
  });
  var c = data.getNumberOfColumns();
  var r = data.getNumberOfRows();
  for (var i = 0; i < r; ++i)
    data.setCell(i, c - 1, colors[i]);

  var options = {
    vAxis : {
      title : "Minutes",
      format : '#.#'
    },
    chartArea : {
      width : '80%',
      height : '80%'
    },
    animation : {
      duration : 1000,
      easing : 'out',
      startup : true
    },
    legend : {
      position : "none"
    }
  };
  var chart = new google.visualization.ColumnChart(document.getElementById("chart_div_column"));
  chart.draw(data, options);
}

function drawChartOverallColumn() {
  query = new google.visualization.Query('report/daily/overall?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
  query.send(handleResponseOverallColumn);
}

function handleResponseTable(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  // Draw table not using google table
  var num_of_cols = data.getNumberOfColumns();
  var num_of_rows = data.getNumberOfRows();
  var opt = "<tr>";
  for (var i = 0; i < num_of_cols; ++i) {
    opt += "<th>" + data.getColumnLabel(i) + "</th>";
  }
  opt += "</tr>";
  // console.log(opt);
  $("#summary_table_head").empty().html(opt);

  opt = "";
  for (var i = 0; i < num_of_rows; ++i) {
    opt += "<tr>";
    for (var j = 0; j < num_of_cols; ++j) {
      var r = "<td>" + data.getValue(i, j) + "</td>";
      opt += r;
    }
    opt += "</tr>";
  }
  // console.log(opt);
  $("#summary_table_body").empty().html(opt);
}

function drawChartTable() {
  query = new google.visualization.Query('report/daily/detailed?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
  query.send(handleResponseTable);
}

function handleResponseAct(response, title, color, chart_div) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  addAnnotation(data);

  data.addColumn({
    type : 'string',
    role : 'style'
  });
  var c = data.getNumberOfColumns();
  var r = data.getNumberOfRows();
  for (var i = 0; i < r; ++i)
    data.setCell(i, c - 1, color);

  var options = {
    title : title,
    vAxis : {
      title : "Minutes",
      format : '#',
      viewWindow : {
        max : g_view_type == "hour" ? "60" : "",
        min : 0
      }
    },
    chartArea : {
      width : g_view_type == "hour" ? '85%' : '75%',
      height : '75%'
    },
    animation : {
      duration : 1000,
      easing : 'out',
      startup : true
    },
    legend : {
      position : "none"
    }
  };
  var chart = new google.visualization.ColumnChart(document.getElementById(chart_div));
  chart.draw(data, options);
}

function handleResponseActLying(response) {
  handleResponseAct(response, 'Lying', colors[4], 'chart_div_lying');
}

function drawChartActLying() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=lie' + '&type=' + g_view_type);
  query.send(handleResponseActLying);
}

function handleResponseActSitting(response) {
  handleResponseAct(response, 'Sitting', colors[3], 'chart_div_sitting');
}

function drawChartActSitting() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=sit' + '&type=' + g_view_type);
  query.send(handleResponseActSitting);
}

function handleResponseActStanding(response) {
  handleResponseAct(response, 'Standing', colors[2], 'chart_div_standing');
}

function drawChartActStanding() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=std' + '&type=' + g_view_type);
  query.send(handleResponseActStanding);
}

function handleResponseActWalking(response) {
  handleResponseAct(response, 'Walking', colors[0], 'chart_div_walking');
}

function drawChartActWalking() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=wlk' + '&type=' + g_view_type);
  query.send(handleResponseActWalking);
}

function handleResponseActShuffling(response) {
  handleResponseAct(response, 'Shuffling', colors[1], 'chart_div_shuffling');
}

function drawChartActShuffling() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=shf' + '&type=' + g_view_type);
  query.send(handleResponseActShuffling);
}

function handleResponseActInverted(response) {
  handleResponseAct(response, 'Inverted', colors[5], 'chart_div_inverted');
}

function drawChartActInverted() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=inv' + '&type=' + g_view_type);
  query.send(handleResponseActInverted);
}

function handleResponseActNonwear(response) {
  handleResponseAct(response, 'Nonwear', colors[5], 'chart_div_nonwear');
}

function drawChartActNonwear() {
  query = new google.visualization.Query('report/daily/peract?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date
      + '&act_type=nwr' + '&type=' + g_view_type);
  query.send(handleResponseActNonwear);
}

function clearDiagrams() {
  $(".data-div").hide();

  $("#duration").html("");

  $("#chart_div_pie").html("");
  $("#chart_div_column").html("");
  $("#chart_div_table").html("");
  $("#chart_div_lying").html("");
  $("#chart_div_sitting").html("");
  $("#chart_div_standing").html("");
  $("#chart_div_walking").html("");
  $("#chart_div_shuffling").html("");
  // $("#chart_div_inverted").html("");
  $("#chart_div_nonwear").html("");

  $("#summary_table_head").html("");
  $("#summary_table_body").html("");
}

function do_date_changed() {
  $(".data-div").show();

  drawChartOverallPie();
  drawChartOverallColumn();
  drawChartTable();
  drawChartActLying();
  drawChartActSitting();
  drawChartActStanding();
  drawChartActWalking();
  drawChartActShuffling();
  // drawChartActInverted();
  drawChartActNonwear();

  end();
}

var g_view_type = "hour";
function hour_phase_switcher() {
  if (g_prj_id == "" || g_sub_id == "" || g_date == "")
    return;
  // console.log(g_view_type);
  if (g_view_type == "hour") {
    g_view_type = "phase";
    $(this).html('<i class="glyphicon glyphicon-eye-open"></i><span> View Hourly</span>');
  } else {
    g_view_type = "hour";
    $(this).html('<i class="glyphicon glyphicon-eye-open"></i><span> View Morning, Afternoon, Evening, Night</span>');
  }

  drawChartActLying();
  drawChartActSitting();
  drawChartActStanding();
  drawChartActWalking();
  drawChartActShuffling();
  // drawChartActInverted();
  drawChartActNonwear();
}

function download_report() {
  if (g_prj_id == "" || g_sub_id == "" || g_date == "")
    return;

  window.location.href = 'report/download?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date;
}

function do_ready() {
  $("#hour_phase_switcher").on('click', hour_phase_switcher);
  $("#download_report").on('click', download_report);
  // console.log("do_ready");
}
