<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="index.jsp"><img class="uoa-eng-logo" src="img/UoA_Engineering.jpg" /> Activity Recognition Engine</a>
    </div>
    <div>
      <ul class="nav navbar-nav">
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Reports<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="adl.jsp">Daily</a></li>
            <li><a href="trend.jsp">Weekly</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Data Mgmt<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="project.jsp">Projects</a></li>
            <li><a href="device_type.jsp">Devices</a></li>
            <li><a href="fileupload.jsp">Files</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Analysis<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="signal.jsp">Basic Mode</a></li>
            <li><a href="signalEE.jsp">Advanced Mode</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">System<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="admin.jsp">User</a></li>
            <li><a href="setting.jsp">Setting</a></li>
            <li><a href="sys_info.jsp">Statistics</a></li>
          </ul></li>
        <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Help<span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="help.jsp">Help</a></li>
          </ul></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li id="task_spinner" class="throbber-loader" style="display: none;"></li>
        <li><a href="#"><span class="glyphicon glyphicon-user"></span> <c:out value="${username}" /></a></li>
        <li onclick="javascript:logout()"><a href="#"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
      </ul>
    </div>
  </div>
</nav>
