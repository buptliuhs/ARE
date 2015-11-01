<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/html_include.jsp"%>
<%@ include file="/login_check.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="css/jquery.fileupload.css">
<link rel="stylesheet" href="css/jquery.fileupload-ui.css">
<link rel="stylesheet" href="css/bootstrap-select.min.css">
<script src="js/vendor/jquery.ui.widget.js"></script>
<script src="js/tmpl.min.js"></script>
<script src="js/load-image.all.min.js"></script>
<script src="js/jquery.iframe-transport.js"></script>
<script src="js/jquery.fileupload.js"></script>
<script src="js/jquery.fileupload-process.js"></script>
<script src="js/jquery.fileupload-image.js"></script>
<script src="js/jquery.fileupload-audio.js"></script>
<script src="js/jquery.fileupload-video.js"></script>
<script src="js/jquery.fileupload-validate.js"></script>
<script src="js/jquery.fileupload-ui.js"></script>
<script src="js/bootstrap-select.min.js"></script>
<script src="util.js"></script>
<script src="fileupload.js"></script>
</head>
<body>
  <div id="nav_header"><jsp:include page="header.jsp"></jsp:include></div>
  <div class="container-fluid container-under-nav">
    <jsp:include page="sub_header.jsp"></jsp:include>
    <h3>
      Please upload raw data <b class="text-warning">(CAUTION: KEEP THIS PAGE OPEN WHILE UPLOADING)</b>
    </h3>
    <br>
    <blockquote>
      <ul id="notice_list">
      </ul>
    </blockquote>
    <br>
    <!-- The file upload form used as target for the file upload widget -->
    <form id="fileupload" method="POST" enctype="multipart/form-data">
      <div class="row fileupload-buttonbar">
        <div class="col-sm-5">
          <!-- The fileinput-button span is used to style the file input field as button -->
          <span class="btn btn-success fileinput-button"> <i class="glyphicon glyphicon-plus"></i> <span>Add files...</span> <input
            id="file_selector" type="file" accept="*" name="files[]" multiple>
          </span>
          <button type="submit" class="btn btn-primary start">
            <i class="glyphicon glyphicon-upload"></i> <span>Start upload</span>
          </button>
          <button type="reset" class="btn btn-warning cancel">
            <i class="glyphicon glyphicon-ban-circle"></i> <span>Cancel upload</span>
          </button>
          <!-- 
          <button type="button" class="btn btn-danger delete">
            <i class="glyphicon glyphicon-trash"></i> <span>Delete</span>
          </button>
          <input type="checkbox" class="toggle">
           -->
          <!-- The global file processing state -->
          <span class="fileupload-process"></span>
        </div>
        <!-- The global progress state -->
        <div class="col-sm-5 fileupload-progress fade">
          <!-- The global progress bar -->
          <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
            <div class="progress-bar progress-bar-success" style="width: 0%;"></div>
          </div>
          <!-- The extended global progress state -->
          <div class="progress-extended">&nbsp;</div>
        </div>
        <div class="col-sm-2">
          <h4>
            Number of files: <span id="number_of_files">-</span>
          </h4>
        </div>
      </div>
      <!-- The table listing the files available for upload/download -->
      <table role="presentation" class="table table-striped">
        <thead>
          <tr>
            <th>Status</th>
            <th>Name</th>
            <th>Size</th>
            <th>Duration</th>
            <th>Algorithm Applied</th>
            <th>Processed At</th>
            <th>Time Used</th>
            <th># of Jobs</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody id="files" class="files"></tbody>
      </table>
    </form>
    <br>
  </div>
  <!-- The template to display files available for upload -->
  <script id="template-upload" type="text/x-tmpl">
      {% for (var i=0, file; file=o.files[i]; i++) { %}
        <tr class="template-upload fade">
          <td>
            <span class="fa fa-question"></span>
          </td>
          <td>
            <p class="name">{%=file.name%}</p>
            <strong class="error text-danger"></strong>
          </td>
          <td>
            <p class="size">Processing...</p>
            <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
          </td>
          <td>
              {% if (file.duration) { %}
                <span>{%=file.duration%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.algorithm) { %}
                <span>{%=file.algorithm%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.stime) { %}
                <span>{%=file.stime%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.time) { %}
                <span>{%=file.time%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.jobs) { %}
                <span>{%=file.jobs%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
            {% if (!i && !o.options.autoUpload) { %}
              <button class="btn btn-primary start" disabled>
                <i class="glyphicon glyphicon-upload"></i>
                <span>Start</span>
              </button>
            {% } %}
            {% if (!i) { %}
              <button class="btn btn-warning cancel">
                <i class="glyphicon glyphicon-ban-circle"></i>
                <span>Cancel</span>
              </button>
            {% } %}
          </td>
        </tr>
      {% } %}
  </script>
  <!-- The template to display files available for download -->
  <script id="template-download" type="text/x-tmpl">
      {% for (var i=0, file; file=o.files[i]; i++) { %}
        <tr class="template-download fade">
          <td>
              {% if (file.pending) { %}
                <span class="throbber-loader"></span>
              {% } else if (file.error) { %}
                <span class="fa fa-warning"></span>
              {% } else { %}
                <span class="fa fa-thumbs-o-up"></span>
              {% } %}
          </td>
          <td>
            <p class="name">
              {% if (file.url) { %}
                <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}">{%=file.name%}</a>
              {% } else { %}
                <span>{%=file.name%}</span>
              {% } %}
            </p>
            {% if (file.error) { %}
              <div><span class="label label-danger">Error</span> {%=file.error%}</div>
            {% } %}
          </td>
          <td>
            <span class="size">{%=o.formatFileSize(file.size)%}</span>
          </td>
          <td>
              {% if (file.duration) { %}
                <span>{%=file.duration%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.algorithm) { %}
                <span>{%=file.algorithm%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.stime) { %}
                <span>{%=file.stime%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.time) { %}
                <span>{%=file.time%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
              {% if (file.jobs) { %}
                <span>{%=file.jobs%}</span>
              {% } else { %}
                <span></span>
              {% } %}
          </td>
          <td>
            {% if (file.rerunUrl) { %}
              {% if (file.deleteUrl) { %}
                <button class="btn btn-danger delete" data-type="{%=file.deleteType%}" data-url="{%=file.deleteUrl%}" data-file="{%=file.name%}"
                {% if (file.pending) { %}
                  disabled>
                {% } else { %}
                  >
                {% } %}
                  <i class="glyphicon glyphicon-trash"></i>
                  <span>Delete</span>
                </button>
              {% } %}
              <button class="btn btn-primary" onclick="reprocess(event, '{%=file.rerunUrl%}')"
              {% if (file.pending) { %}
                disabled>
              {% } else { %}
                >
              {% } %}
                <i class="glyphicon glyphicon-refresh"></i>
                <span>Rerun</span>
              </button>
            {% } else { %}
              <button class="btn btn-warning cancel">
                <i class="glyphicon glyphicon-ban-circle"></i>
                <span>Cancel</span>
              </button>
            {% } %}
          </td>
        </tr>
      {% } %}
  </script>
  <div id="footer"><jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
