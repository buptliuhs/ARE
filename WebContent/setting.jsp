<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>
<%@ include file="/login_check.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="setting.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container container-under-nav">
    <h3>List of Settings (Parameters of Algorithm)</h3>
    <div class="row">
      <div class="col-sm-12">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Index</th>
              <th>Name</th>
              <th>Description</th>
              <th>Value</th>
              <th>Actions</th>
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
