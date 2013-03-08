/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler.io;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.nl.vu.ni.cordiscrawler.Organization;
import org.nl.vu.ni.cordiscrawler.Project;

import org.nl.vu.ni.cordiscrawler.common.Constants;
import org.nl.vu.ni.cordiscrawler.common.Utils;

/**
 *
 * @author Ravi
 */
public class DB implements Out{
    static Logger log = Logger.getLogger(DB.class.getName());
    
    static DB obj = null;
    Connection con = null;
    static Out getInstance() {
        if(obj == null){
            obj = new DB();
        }
        return obj;
    }
    
    private DB(){
        init();
    }
    @Override
    public void init() {
        try {
            Class.forName(Constants.db_driver);
            con = DriverManager.getConnection(Constants.db_url+Constants.db_name,Constants.db_user,Constants.db_password);
            
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    @Override
    public void writeProjectFeed(ArrayList param) {
        Object params[] = param.toArray();
        try {
            java.sql.PreparedStatement ps = con.prepareStatement(Constants.SQL_PROJECT_FEED);
            ps.setInt(1, Integer.parseInt(params[0].toString()));
            ps.setString(2, params[1].toString());
            ps.setString(3, params[2].toString());
            ps.setString(4, params[3].toString());
            ps.setDate(5, Date.valueOf(params[4].toString()));
            ps.executeUpdate();
        } catch (SQLException s) {
            System.out.println("SQL statement is not executed!");
            s.printStackTrace();
            log.error(s);
        }
    }
    
    @Override
    public ArrayList getRCNlist(){
         ArrayList al = new ArrayList();
        try {
                String sql = "SELECT * FROM project_feed";
                PreparedStatement prest = con.prepareStatement(sql);
                ResultSet rs = prest.executeQuery();
                while (rs.next()) {
                    String mov_name = rs.getString(1);
                    al.add(mov_name);
//                    System.out.println(mov_name);
                }
                prest.close();
            } catch (SQLException s) {
                System.out.println(">>"+con);
//                s.printStackTrace();
//                log.error(s);
            }
        return al;
    }

    @Override
    public void storeProjectData(ArrayList<Project> al) {
        ArrayList failedRCN = new ArrayList(); 
        loop: for(int i=0;i<al.size();i++){
            Project p = al.get(i);
            ArrayList pdata = p.toArrayList();
            try {
                storeProjectFields(pdata);
            } catch (Exception ex) {
                failedRCN.add("db:"+p.getRCN());
                log.error("(DB) Failed RCN:"+p.getRCN(),ex);
                continue loop;
            }
            storeOrganization(p.getMainContractor(),true);
            for(Organization o:p.getParticipants()){
                storeOrganization(o,false);
            }
        }
        Utils.logArrayList(failedRCN,Constants.FILE_FAIL_LIST);
    }

    private void storeProjectFields(ArrayList pdata) throws Exception {
        updateTable(pdata, Constants.SQL_PROJECTS);
    }

    private void storeOrganization(Organization mainContractor, boolean isMainContractor) {
        ArrayList<String> orgDetails = new ArrayList<String>(mainContractor.toArrayList().subList(0, 7));
        ArrayList<String> contactDetails = new ArrayList<String>(mainContractor.toArrayList().subList(7, 8));
        ArrayList<String> rcndata = new ArrayList<String>(mainContractor.toArrayList().subList(8, 9));
//        System.out.println(orgDetails);

        try {
            int org_id = updateTable(orgDetails, Constants.SQL_ORG);
            int person_id = -1;
            
            
            if(contactDetails.get(0) != null){
                person_id = updateTable(contactDetails, Constants.SQL_PERSON);
            }
            rcndata.add(person_id + "");
            rcndata.add(org_id + "");
            if (isMainContractor) {
                rcndata.add("0");
            } else {
                rcndata.add("1");
            }
            updateTable(rcndata, Constants.SQL_CONTRACT);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("storeOrganization@rcn:"+rcndata.get(0),ex);
        }


    }
    
    public int updateTable(ArrayList<String> fields, String SQL)throws Exception {
        int newKey = -1;
        try {
            PreparedStatement prest = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            for (int x = 0; x < fields.size(); x++) {
                prest.setString(x + 1, fields.get(x));
            }
            int count = prest.executeUpdate();
            if (count == 1) {
                final ResultSet rs = prest.getGeneratedKeys();
                if (rs.next()) {
                    newKey = rs.getInt(1);
                }
            }
            Thread.sleep(10);
        } catch (Exception s) {
//            s.printStackTrace();
            log.error("SQL statement is not executed! ["+SQL+"] ["+fields+"]");
            throw s;
        }

        return newKey;
    }
}
