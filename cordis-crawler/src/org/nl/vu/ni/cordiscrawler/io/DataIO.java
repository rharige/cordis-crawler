/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler.io;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.nl.vu.ni.cordiscrawler.Project;
import org.nl.vu.ni.cordiscrawler.common.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ravi
 */
public class DataIO {

    
    private Out out = null;
    private static DataIO data = null;
    public static DataIO getInstance(int INSTANCE_TYPE) {
        if(data == null) {
            data = new DataIO();
        }
        if(INSTANCE_TYPE == Constants.DB_INSTANCE){
            data.out = DB.getInstance();
        }
        
        return data;
    }
    
    private DataIO(){
    
    }
    
    public void writeProjectFeed() throws Exception {
        SimpleDateFormat fromUser = new SimpleDateFormat("d MMM yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        File dir = new File(Constants.DIR_FEED);
        for (int i = 0; i < dir.listFiles().length; i++) {
            File fXmlFile = dir.listFiles()[i];
            System.out.println(i+": "+fXmlFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("item");
            
            loop:
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String t = getTagValue("url", eElement);
                    String title = getTagValue("title", eElement).replace("'", "\'");
                    int rcn = Integer.parseInt(t.substring(t.lastIndexOf("=") + 1));
                    String country = getTagValue("countrydesc", eElement);
                    String date = getTagValue("pubDate", eElement);
                    date = date.substring(date.indexOf(",") + 2, date.indexOf(",") + 2 + 11).trim();
                    String acry = getTagValue("programmeacronym", eElement);
                    date = myFormat.format(fromUser.parse(date)).trim();

                    ArrayList params = new ArrayList();
                    params.add(rcn);
                    params.add(title);
                    params.add(acry);
                    params.add(country);
                    params.add(date);
//                    System.out.println(params);
                    out.writeProjectFeed(params);
//                    System.exit(0);
                }
            }
        }
    }
    
    private String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null) {
            return "";
        }
        return nValue.getNodeValue();
  }

    public ArrayList getRCNdata() {
        return out.getRCNlist();
    }

    public void storeProjectData(ArrayList<Project> al) {
        out.storeProjectData(al);
    }
    
    
}
