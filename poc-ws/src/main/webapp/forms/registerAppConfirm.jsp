<%@ page import="javax.servlet.http.HttpServletRequest, com.pss.poc.ws.model.ConsumerRegistration" %>

<%
    ConsumerRegistration reg = (ConsumerRegistration)request.getAttribute("newClient");
    String basePath = request.getContextPath() + request.getServletPath();
    if (!basePath.endsWith("/")) {
        basePath += "/";
    } 
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Client Application Registration Confirmation</title>
    <STYLE TYPE="text/css">
	<!--
	  div.padded {  
         padding-left: 15em;  
      }   
	-->
</STYLE>
</head>
<body>
<div class="padded">

<h1>Client Application Registration Confirmation</h1>
<em></em>
<br/>
<p>
<big><big>
Please use the provided Client Identifier and Shared Secret when<br/>poc-ws service users as part of OAuth flows.
</big></big>
</p>
<br/>
<table>
        <tr>
            <td><big><big><big>Client Identifier:</big></big></big></td>
            <td>&nbsp;&nbsp;</td>
            <td><big><big><%= reg.getId() %></big></big></td>
        </tr>
        <tr>
            <td><big><big><big>Client Secret:</big></big></big></td>
            <td>&nbsp;&nbsp;</td>
            <td><big><big><%= reg.getSecret() %></big></big></td>
        </tr> 
</table>
<br/> 
 
<br/>
<p>
Please follow this <a href="<%= basePath %>forms/registerUser.jsp">link</a> to get a user registered with Social.com
</p>
</big></big>
</div>
</body>
</html>

