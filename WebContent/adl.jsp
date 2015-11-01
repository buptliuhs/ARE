<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>
<%@ include file="/login_check.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://www.google.com/jsapi"></script>
<script src="js/bootstrap-select.min.js"></script>
<script src="util.js"></script>
<script src="adl.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container-fluid container-under-nav">
    <jsp:include page="sub_header.jsp"></jsp:include>
    <hr>
    <div class="row container-fluid data-div">
      <div class="row container-fluid">
        <div class="col-sm-6">
          <h3>Overall</h3>
        </div>
        <div class="col-sm-6">
          <button id="download_report" class="btn btn-primary">
            <i class="glyphicon glyphicon-download-alt"></i><span> Download Report</span>
          </button>
        </div>
      </div>
      <div class="row container-fluid">
        <div class="col-sm-6 chart-div-6" id="chart_div_pie"></div>
        <div class="col-sm-6 chart-div-6" id="chart_div_column"></div>
      </div>
      <hr>
      <div class="row container-fluid">
        <h3>Summary</h3>
        <div class="col-sm-12">
          <table class="table table-striped">
            <thead id="summary_table_head">
            </thead>
            <tbody id="summary_table_body">
            </tbody>
          </table>
        </div>
      </div>
      <hr>
      <div class="row container-fluid">
        <div class="col-sm-6">
          <h3>Per-Activity</h3>
        </div>
        <div class="col-sm-6">
          <button id="hour_phase_switcher" class="btn btn-primary">
            <i class="glyphicon glyphicon-eye-open"></i><span> View Morning, Afternoon, Evening, Night</span>
          </button>
        </div>
      </div>
      <div class="row container-fluid">
        <div class="col-sm-6 chart-div-act my-border" id="chart_div_walking"></div>
        <div class="col-sm-6 chart-div-act my-border" id="chart_div_shuffling"></div>
      </div>
      <div class="row container-fluid">
        <div class="col-sm-6 chart-div-act my-border" id="chart_div_standing"></div>
        <div class="col-sm-6 chart-div-act my-border" id="chart_div_sitting"></div>
      </div>
      <div class="row container-fluid">
        <div class="col-sm-6 chart-div-act my-border" id="chart_div_lying"></div>
        <!-- <div class="col-sm-6 chart-div-act my-border" id="chart_div_inverted"></div> -->
        <div class="col-sm-6 chart-div-act my-border" id="chart_div_nonwear"></div>
      </div>
    </div>
  </div>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
