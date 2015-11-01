<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>
<%@ include file="/login_check.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="https://www.google.com/jsapi"></script>
<script src="js/jquery.csv.js"></script>
<script src="js/dygraph-combined-dev.js"></script>
<script src="js/extras/synchronizer.js"></script>
<script src="util.js"></script>
<script src="signalUtil.js"></script>
<script src="signal.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container-fluid container-under-nav">
    <jsp:include page="sub_header.jsp"></jsp:include>
    <hr>
    <div class="row container-fluid data-div">
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
