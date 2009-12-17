/**
 * 
 */
package com.google.listeners;

import java.util.ArrayList;

/**
 * @author Dinesh Harjani (goldrunner192287@gmail.com)
 *
 */
public interface IStandardTaskListener {
	void downloadingComplete(Object result);
    void progressUpdate(int progress, int total);
}
