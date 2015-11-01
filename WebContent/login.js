// Copyright 2015 Tony (Huansheng) Liu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

function login() {
  var username = $("#username").val();
  var password = $("#password").val();
  if (username.trim().length < 1 || password.length < 1) {
    return;
  }
  $.post($.ctx + "/login.action", {
    "username" : username,
    "password" : password
  }).done(function(data, status) {
    console.log(data);
    data = JSON.parse(data);
    if (data["result"] == 0) {
      window.location.href = $.ctx + "/index.jsp";
    } else
      bootbox.alert({
        // size : "small",
        title : "Oops!",
        message : data["message"]
      });
  });
}

$(document).ready(function() {
  if ($.username == "")
    console.log("User is NOT logged in");
  else {
    console.log("User is logged in: " + $.username + " [" + $.userid + "]");
    window.location.href = $.ctx + "/index.jsp";
  }
});
