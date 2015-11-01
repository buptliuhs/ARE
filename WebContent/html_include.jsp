<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="userid" value="<%=session.getAttribute(\"userid\")%>" />
<c:set var="username" value="<%=session.getAttribute(\"username\")%>" />
<c:set var="roleid" value="<%=session.getAttribute(\"roleid\")%>" />

<title>Activity Recognition Engine</title>
<link rel="shortcut icon" href="img/favicon.ico">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/spinners.css" type="text/css">
<script src="check_progress.js"></script>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/bootbox.min.js"></script>

<script type="text/javascript">
    function logout() {
        $.post($.ctx + "/logout.action", {}, function(data) {
            window.location.href = $.ctx + "/login.jsp";
        });
    }
    function urlArgs() {
        var args = {};
        var query = location.search.substring(1);
        var pairs = query.split("&");
        for ( var i = 0; i < pairs.length; i++) {
            var pos = pairs[i].indexOf('=');
            if (pos == -1)
                continue;
            var name = pairs[i].substring(0, pos);
            var value = pairs[i].substring(pos + 1);
            value = decodeURIComponent(value);
            args[name] = value;
        }
        ;
        return args;
    };

    jQuery.ctx = '${ctx}';
    jQuery.userid = '${userid}';
    jQuery.username = '${username}';
    jQuery.roleid = '${roleid}';
    //jQuery.debug=true;
    jQuery.debug = false;
</script>
