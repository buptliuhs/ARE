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
<script src="trend.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container-fluid container-under-nav">
    <jsp:include page="sub_header.jsp"></jsp:include>
    <hr>
    <div class="row container-fluid data-div">
      <div class="row container-fluid">
        <h3>Overall</h3>
        <div class="col-sm-6 chart-div-6" id="chart_div_pie"></div>
        <div class="col-sm-6 chart-div-6" id="chart_div_column"></div>
      </div>
      <hr>
      <div class="row container-fluid">
        <h3>Trend</h3>
        <div class="col-sm-12" id="chart_div_trend"></div>
      </div>
      <hr>
    </div>
  </div>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
