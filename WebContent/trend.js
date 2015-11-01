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

function handleResponseTrendLine(response) {
  if (response.isError()) {
    console.log('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

  var data = response.getDataTable();
  // Too messy to display annotation in multiple-line chart
  // addAnnotation(data);

  var options = {
    vAxis : {
      title : "Mimutes",
      format : '#.#',
      viewWindow : {
        min : 0
      }
    },
    animation : {
      duration : 1000,
      easing : 'out',
      startup : true
    },
    colors : colors,
    legend : {
      position : 'top'
    },
    // curveType : 'function',
    pointShape : 'polygon',
    pointSize : 5,
    chartArea : {
      width : '80%',
      height : '75%'
    }
  };

  var chart = new google.visualization.LineChart(document.getElementById('chart_div_trend'));
  chart.draw(data, options);
}

function drawTrendChart() {
  query = new google.visualization.Query('report/weekly/trend?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
  query.send(handleResponseTrendLine);
}

function clearDiagrams() {
  $(".data-div").hide();

  $("#duration").html("");

  $("#chart_div_pie").html("");
  $("#chart_div_column").html("");
  $("#chart_div_trend").html("");
}

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
  data.addRow([ 'Total: ' + total.getValue(0, 1).toFixed(1) + ' minutes', 0 ]);

  var options = {
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
  query = new google.visualization.Query('report/weekly/overall?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
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
  for ( var i = 0; i < r; ++i)
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
  query = new google.visualization.Query('report/weekly/overall?prj_id=' + g_prj_id + '&sub_id=' + g_sub_id + '&date=' + g_date);
  query.send(handleResponseOverallColumn);
}

function do_date_changed() {
  $(".data-div").show();

  drawChartOverallPie();
  drawChartOverallColumn();
  drawTrendChart();

  end();
}

function do_ready() {
  $("#duration_div").hide();
}
