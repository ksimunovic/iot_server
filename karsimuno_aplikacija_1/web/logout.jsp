<%-- 
    Document   : logout
    Created on : May 31, 2017, 9:54:51 AM
    Author     : Karlo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%@ page session="true"%>

        User '<%=request.getRemoteUser()%>' has been logged out.

        <% session.invalidate();%>

        <br/><br/>
        <a href="${pageContext.request.contextPath}">Click here to go to login</a>
    </body>
</html>
