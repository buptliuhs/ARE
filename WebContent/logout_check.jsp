<%@ page contentType="text/html; charset=utf-8"%>
<%
    if (session.getAttribute("username") != null & session.getAttribute("userid") != null)
    {
        response.sendRedirect("index.jsp");
        return;
    }
%>
