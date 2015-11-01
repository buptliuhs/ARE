<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>
<%@ include file="/login_check.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="css/bootstrap-datepicker3.min.css">
<link rel="stylesheet" href="css/bootstrap-select.min.css">
<script src="js/bootstrap-datepicker.min.js"></script>
<script src="js/bootstrap-select.min.js"></script>
<script src="device.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container container-under-nav">
    <h3>
      List of Devices
      <button id="add_device_button" type="button" class="btn btn-primary">Add</button>
    </h3>
    <div class="row">
      <div class="col-sm-12">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Index</th>
              <th>Device Type</th>
              <th>Device ID</th>
              <th>Allocated To (Project / Subject)</th>
            </tr>
          </thead>
          <tbody id="maincontent"></tbody>
        </table>
      </div>
    </div>
  </div>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
