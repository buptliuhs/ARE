<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>
<body>
  <div id="nav_header">
    <%
    	if (session.getAttribute("username") == null
    			|| session.getAttribute("userid") == null) {
    %>
    <jsp:include page="login_header.jsp" />
    <%
    	} else {
    %>
    <jsp:include page="header.jsp" />
    <%
    	}
    %>
  </div>
  <div class="container-fluid container-under-nav">
    <div class="row container-fluid">
      <div class="col-sm-4">
        <div class="row container-fluid">
          <h3>Documentation</h3>
        </div>
        <div class="row container-fluid">
          <ul>
            <li><p>
                <a href="https://docs.google.com/document/d/1-2kVxhPp8_OsBb9UuuzugxbpIWVbNCsYB8F3I_Eo24k/edit?usp=sharing" target="_blank">User
                  Guide</a>
              </p></li>
          </ul>
        </div>
      </div>
      <div class="col-sm-4">
        <div class="row container-fluid">
          <h3>Developer</h3>
        </div>
        <div class="row container-fluid">
          <ul>
            <li>
              <p>
                <!-- <img src="http://www.gravatar.com/avatar/0cbfd967f0d52e8eda0009cb8e84fbec" />-->
                <a href="mailto:hliu482@aucklanduni.ac.nz?Subject=About ARE">Tony (Huansheng) Liu</a>
                &nbsp;
                <a target="_blank" href="https://nz.linkedin.com/pub/tony-huansheng-liu/4/433/a64">
                  <img src="https://static.licdn.com/scds/common/u/img/webpromo/btn_profile_bluetxt_80x15.png" width="80" height="15" border="0" alt="View Tony (Huansheng) Liu's profile on LinkedIn">
                </a>
              </p>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="row container-fluid">
      <div class="col-sm-4">
        <h3>Videos</h3>
      </div>
    </div>
    <div class="row container-fluid">
      <div class="col-sm-4">
        <h4>Introduction of the ARE</h4>
        <iframe src="https://www.youtube.com/embed/mtI2nl9AvQQ" frameborder="0" allowfullscreen></iframe>
      </div>
      <div class="col-sm-4">
        <h4>Part 1 of the ARE</h4>
        <iframe src="https://www.youtube.com/embed/pzyWk5WKQiQ" frameborder="0" allowfullscreen></iframe>
      </div>
    </div>
    <br>
    <div class="row container-fluid">
      <div class="col-sm-4">
        <h4>Part 2 of the ARE (Basic Mode of Signal Analysis)</h4>
        <iframe src="https://www.youtube.com/embed/K0_I1vOcgcw" frameborder="0" allowfullscreen></iframe>
      </div>
      <div class="col-sm-4">
        <h4>Part 2 of the ARE (Advanced Mode of Signal Analysis)</h4>
        <iframe src="https://www.youtube.com/embed/6VwDdkU22EY" frameborder="0" allowfullscreen></iframe>
      </div>
      <div class="col-sm-4">
        <h4>Part 3 of the ARE (Validation)</h4>
        <iframe src="https://www.youtube.com/embed/KgkokY1I1PA" frameborder="0" allowfullscreen></iframe>
      </div>
    </div>
  </div>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
