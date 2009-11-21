<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="dh.geobloc.appengine.server.BasicForm" %>
<%@ page import="dh.geobloc.appengine.server.PMF;" %>


<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Hello App Engine</title>
  </head>

  <body>
    <h1>Hello, User!</h1>
    
    <%
    	UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
    	if (user != null) {
	%>
	<p>Hello, <%= user.getNickname() %>! (You can
	<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
	<%
    	} else {
	%>
	<p>Hello!
	<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
	to include your name with greetings you post.</p>
	<%
    	}
	%>
	
    <table>
      <tr>
        <td colspan="2" style="font-weight:bold;">Available Servlets:</td>        
      </tr>
      <tr>
        <td><a href="geobloc_server1?firstname=John"/>GeoBloc_Server1</td>
      </tr>
      <tr>
      	<td><a href="clear_basicforms"/>ClearBasicFormsFromDatastoreServlet</td>
      </tr>
    </table>
  </body>
  
  <%
  	if (user != null) {
  %>
  	<%
  		out.println("<p><a href=/main.jsp>Back to main</a></p>");
  		PersistenceManager pm = PMF.get().getPersistenceManager();
    	String query = "select from " + BasicForm.class.getName() + " order by date desc range 0,5";
    	List<BasicForm> forms = (List<BasicForm>) pm.newQuery(query).execute();
    	if (forms.isEmpty()) {
	%>
	<p>No forms have been uploaded yet.</p>
	<%
    	} else {
        	for (BasicForm form : forms) {
        		String key = KeyFactory.keyToString(form.getKey());
    	        if (form.getAuthor() == null) {
	%>
	<p>An anonymous person uploaded: -<%= form.getKey()%></p>
	<%
    	        } else {
	%>
	<p><b><%= form.getAuthor().getNickname() %></b> uploaded: - <a href="show_basicform?key=<%=key%>"> <%= form.getName() %>  </a></p>
	<%
    	        }
	%>
	<blockquote>at <%= form.getDate().toString() %> </blockquote>
	<%
        	}
    	}
    	pm.close();
	%>
  
  
  	<form name="filesForm" action="geobloc_server1" method="post" enctype="multipart/form-data">
    	File 1:<input type="file" name="file1"><br>
    	File 2:<input type="file" name="file2"><br>
    	File 3:<input type="file" name="file3"><br>
    	<input type="submit" name="Submit" value="Upload Files">
  	</form>
  <%
  	} else {
  %>
  		<p> Please 
  		<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>
		, so that you can upload files to the server..</p>
  <%
  	}
  %>
</html>
