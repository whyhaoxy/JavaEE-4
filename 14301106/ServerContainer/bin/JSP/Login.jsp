<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Login</title>
</head>
<body>
    <%
    if (request.getParameter("username") == null) {%>
        <form action='?' method='get'><p>Your name: <input type='text' name='username'></p></form>
    <%}else {%>
        <p>Hello,  <%=request.getParameter("username")%> !</p>
    <%}%>
</body>
</html>

