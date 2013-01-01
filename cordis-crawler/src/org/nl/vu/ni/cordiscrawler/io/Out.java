/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler.io;

import java.util.ArrayList;
import org.nl.vu.ni.cordiscrawler.Project;

/**
 *
 * @author Ravi
 */
public interface Out {
    public void init();
    public void writeProjectFeed(ArrayList params);
    public ArrayList getRCNlist();
    public void storeProjectData(ArrayList<Project> al);
}
