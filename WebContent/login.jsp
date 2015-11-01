<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="login.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="login_header.jsp"></jsp:include></div>
  <div class="container container-under-nav">
    <div class="row container-fluid">
      <h3>Sign in to use ARE</h3>
      <form id="login_form" class="form-inline" action="javascript:login()">
        <div class="input-group">
          <span class="input-group-addon"><i class="fa fa-user fa-fw"></i></span> <input type="text" class="form-control" id="username"
            name="username" placeholder="Username">
        </div>
        <div class="input-group">
          <span class="input-group-addon"><i class="fa fa-key fa-fw"></i></span> <input type="password" class="form-control" id="password"
            name="password" placeholder="Password">
        </div>
        <button type="submit" class="btn btn-primary">Sign in</button>
      </form>
    </div>
  </div>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
