<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.geobloc.appengine.forms.BasicForm" %>
<%@ page import="com.geobloc.appengine.server.PMF;" %>

<%
/**
 * This jsp currently allows a signed in user (through a Google Account) to upload files and see the contents of
 * the uploaded files. However, the servlet which handles this request saves the files as text wrapped inside a 
 * BasicForm, so the server is only ready to receive XML files at the moment. 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
%>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Hello App Engine</title>
    <style type="text/css">
  		body {
    		color: purple;
    		background-color: white;
    		}
  	</style>
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
	to upload files and see a list with the most recent uploaded files.</p>
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
  		out.println("<p><a href=/main.jsp>Refresh</a></p>");
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
	<p>An anonymous person uploaded: - <a href="show_basicform?key=<%=key%>"> <%= form.getName() %>  </a></p>
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
		<p> Cannot display element until user signs in.</p>
  <%
  	}
  %>
</html>
