<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
  <div id="sel_prj_div" class="col-sm-3">
    <table class="table">
      <tr>
        <th>Project</th>
      </tr>
      <tr>
        <td><select class="form-control" id="sel_prj">
        </select></td>
      </tr>
    </table>
  </div>
  <div id="sel_sub_div" class="col-sm-3">
    <table class="table">
      <tr>
        <th>Subject</th>
      </tr>
      <tr>
        <td><select class="form-control" id="sel_sub">
        </select></td>
      </tr>
    </table>
  </div>
  <div id="sel_date_div" class="col-sm-3">
    <table class="table">
      <tr>
        <th>Date</th>
      </tr>
      <tr>
        <td><select class="form-control" id="sel_date">
        </select></td>
      </tr>
    </table>
  </div>
  <div class="col-sm-3">
    <div id="timer_spinner" class="spinner-loader"></div>
  </div>
</div>
<div class="row">
  <div class="col-sm-2">
    <table class="table">
      <tr>
        <th>DOB</th>
      </tr>
      <tr>
        <td id="sub_dob"></td>
      </tr>
    </table>
  </div>
  <div class="col-sm-2">
    <table class="table">
      <tr>
        <th>Gender</th>
      </tr>
      <tr>
        <td id="sub_gender"></td>
      </tr>
    </table>
  </div>
  <div class="col-sm-2">
    <table class="table">
      <tr>
        <th>Height (cm)</th>
      </tr>
      <tr>
        <td id="sub_height"></td>
      </tr>
    </table>
  </div>
  <div class="col-sm-2">
    <table class="table">
      <tr>
        <th>Weight (kg)</th>
      </tr>
      <tr>
        <td id="sub_weight"></td>
      </tr>
    </table>
  </div>
  <div class="col-sm-2">
    <table class="table">
      <tr>
        <th>Device</th>
      </tr>
      <tr>
        <td id="sub_device_name"></td>
      </tr>
    </table>
  </div>
  <div id="duration_div" class="col-sm-2">
    <table class="table">
      <tr>
        <th>Monitored Time</th>
      </tr>
      <tr>
        <td id="duration"></td>
      </tr>
    </table>
  </div>
</div>
