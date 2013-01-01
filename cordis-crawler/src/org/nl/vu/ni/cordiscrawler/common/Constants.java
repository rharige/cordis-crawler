/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler.common;

/**
 *
 * @author Ravi
 */
public class Constants {
    
    //database connection variables
    public static String db_url = "jdbc:mysql://localhost:3306/";
    public static String db_name = "cordisx";
    public static String db_user = "root";
    public static String db_password = "root";
    public static String db_driver = "com.mysql.jdbc.Driver";
    public static String cordis_base = "http://cordis.europa.eu/newsearch/download.cfm?action=query";
    
    //Directory paths
    public static String DIR_OUT = "data";
    public static String DIR_FEED = "feed";
    
    //Log file paths
    public static String log_feed_network = "logs\\feed_network.txt";
    public static String log_feed_db = "logs\\feed_db.txt";
    public static String stop_file = "logs\\stop.txt";
    
    //misc
    public static long API_DELAY = 1000;
    public static int DB_INSTANCE = 0;
    
    //SQL Statements
    public static String SQL_PROJECT_FEED = "insert into project_feed(RCN,Title,ProgramAcronym,Country,FeedPubDate) values (?,?,?,?,?)";
    
    //Project document elements
    public static String DOC_RCN = "rcn";
    
    public static String FILE_FAIL_LIST = "logs\\failedRCNs.txt";
    public static String FILE_INDEX = "logs\\index.txt";
    public static int FLUSH_INTERVAL = 20;
    
    //SQL
    public static String SQL_CONTRACT          = "INSERT contractor(rcn,person_id,org_id,type) values (?,?,?,?)";
    public static String SQL_ORG               = "INSERT organization(name,type,address,region,website,fax,telephone) values (?,?,?,?,?,?,?)";
    public static String SQL_PERSON            = "INSERT person(name) values (?)";
    
    public static String SQL_PROJECTS          = "INSERT projects(rcn,title,duration,start_dt,end_dt,achievements,general_info,objectives,cost,funding,status,prog_acronym,contract,subjects,prog_reference,project_acronym,sub_programs) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static String SQL_UPDATE_ORG        = "UPDATE person SET city = ?, country = ? WHERE id = ?";
}
