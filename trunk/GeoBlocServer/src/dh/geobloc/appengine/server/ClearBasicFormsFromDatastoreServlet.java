package dh.geobloc.appengine.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClearBasicFormsFromDatastoreServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {			
    		String query = "select from " + BasicForm.class.getName();
    		List<BasicForm> forms = (List<BasicForm>) pm.newQuery(query).execute();
    		pm.deletePersistentAll(forms);
		}
		finally {
			pm.close();
		}
		resp.sendRedirect("/main.jsp");
	}
}
