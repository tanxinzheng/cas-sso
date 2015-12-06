<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.apache.shiro.subject.Subject,org.apache.shiro.SecurityUtils"%>
<!DOCTYPE html>
<html>
<head>
	<title>Shiro CAS Client</title>
	<meta charset="UTF-8" />
	<link rel="stylesheet" href="/resources/css/bootstrap.min.css" />
</head>
<body>
	<div class="container">
		<ul class="nav nav-tabs">
			<li><a href="/index.jsp">Call the /index.jsp page</a></li>
			<li class="active"><a href="/protected/index.jsp">You are on the /protected/index.jsp page</a></li>
			<!-- #### change with your own CAS server and your host name #### -->
			<li><a href="http://localhost:8080/cas/logout?service=http://localhost:8082">Call the CAS logout</a></li>
		</ul>
		<br>
		<% Subject subject = SecurityUtils.getSubject(); %>
		<h3>
			<p>principals : <%=subject.getPrincipals()%></p>
			<p>isAuthenticated : <%=subject.isAuthenticated()%></p>
		</h3>
	</div>
</body>
</html>
