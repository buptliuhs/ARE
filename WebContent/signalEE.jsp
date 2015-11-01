<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>
<%@ include file="/login_check.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="css/jquery.dataTables.min.css">
<script src="https://www.google.com/jsapi"></script>
<script src="js/jquery.csv.js"></script>
<script src="js/dygraph-combined-dev.js"></script>
<script src="js/extras/synchronizer.js"></script>
<script src="js/bootstrap.file-input.js"></script>
<script src="js/jquery.dataTables.min.js"></script>
<script src="util.js"></script>
<script src="signalUtil.js"></script>
<script src="signalEE.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container-fluid container-under-nav">
    <jsp:include page="sub_header.jsp"></jsp:include>
    <hr>
    <div class="row container-fluid data-div">
      <div class="row container-fluid">
        <div class="col-sm-7">
          <blockquote>
            Upload your activity marking file (.xlsx) for validation (Click <a href="activityResultTemplate.xlsx">here</a> to download the
            template)
          </blockquote>
        </div>
        <div class="col-sm-5">
          <form id="validation_form" method="POST" enctype="multipart/form-data">
            <input type="file" name="fileToUpload" id="fileToUpload" title="Select a file to validate"
              accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
            <button type="submit" id="validate_button" class="btn btn-primary">
              <i class="glyphicon glyphicon-check"></i><span> Validate</span>
            </button>
          </form>
        </div>
      </div>
      <hr>
      <div id="validation_result" class="row container-fluid" style="display: none">
        <div class="row container-fluid">
          <div class="col-sm-12">
            <blockquote>
              Validation Result: <span id="percentage" style="color: blue"></span> records are matched. Please check below table for any
              unmatched records. Click to view details.
            </blockquote>
          </div>
        </div>
        <div class="row container-fluid unmatched_table_div">
          <div class="col-sm-12 div-unmatched-table-content">
            <table id="unmatched_table" class="table">
              <thead id="unmatched_table_head">
                <tr>
                  <th>Second #</th>
                  <th>Time</th>
                  <th>Recognised Activity</th>
                  <th>Your marking</th>
                </tr>
              </thead>
              <tfoot>
                <tr>
                  <th>Second #</th>
                  <th>Time</th>
                  <th>Recognised Activity</th>
                  <th>Your marking</th>
                </tr>
              </tfoot>
              <tbody id="unmatched_table_body">
              </tbody>
            </table>
          </div>
        </div>
        <div class="row container-fluid">
          <hr>
        </div>
      </div>
      <div class="row container-fluid">
        <div class="col-sm-6 button_wrapper">
          <button id="backward_button" class="btn btn-primary">
            <span class="glyphicon glyphicon-backward"></span>
          </button>
          <button id="forward_button" class="btn btn-primary">
            <span class="glyphicon glyphicon-forward"></span>
          </button>
        </div>
        <div class="col-sm-6 button_wrapper">
          <div>
            <input id="g_time" type="time" step="1"></input> <span><i class="glyphicon glyphicon-time"></i></span>
            <button type="submit" id="time_button" class="btn btn-primary">
              <i class="glyphicon glyphicon-check"></i><span> Go!</span>
            </button>
          </div>
        </div>
      </div>
      <br>
      <div class="row container-fluid">
        <div class="col-sm-12" id="chart_div_range"></div>
      </div>
      <hr>
      <div class="row container-fluid">
        <div class="col-sm-12" id="chart_div_act"></div>
      </div>
      <hr>
      <div class="row container-fluid">
        <div class="col-sm-12" id="chart_div_raw"></div>
      </div>
    </div>
  </div>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
