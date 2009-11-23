package dh.geobloc.appengine.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Simple class to wrap the PersistenceManager as a Singleton. Will be used a lot throughout the project. 
 * 
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */

public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}
