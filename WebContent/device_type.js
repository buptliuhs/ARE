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

$(document).ready(function() {
  initDeviceType();

  check_progress();
});

function initDeviceType() {
  var url = $.ctx + "/init_device_type_list.action";
  $.post(url, function(data) {
    data = JSON.parse(data);
    var opt = "";
    var index = 0;
    for ( var o in data) {
      var c = data[o];
      var id = c.id;
      var name = c.name;
      var description = c.description;
      opt += "<tr><td>" + (++index) + "</td><td>" + name + "</td><td>" + description + "</td><td><a href=\"javascript:viewDeviceType(" + id
          + ", '" + name + "')\"> View </a>" + "</td></tr>";
    }
    $("#maincontent").empty().html(opt);
  });
}

function viewDeviceType(id, name) {
  window.location.href = $.ctx + "/device.jsp?dt=" + id + "&dtn=" + name;
}
