package com.geobloc.appengine.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Not being used in this build; code from GeoBloc_Server1Servlet will be refactored here later.
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public class UploadBasicFormServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(UploadBasicFormServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = req.getParameter("content");
        Date date = new Date();
        /*
        BasicForm form = new BasicForm(user, content, date);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(form);
        } finally {
            pm.close();
        }
        */
        resp.sendRedirect("/guestbook.jsp");
    }
}
