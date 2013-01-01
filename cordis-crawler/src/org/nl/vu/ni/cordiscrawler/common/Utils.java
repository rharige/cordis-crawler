/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author Ravi
 */
public class Utils {
    static Logger log = Logger.getLogger(Utils.class.getName());
    public static void parseProjectsFeed(String urlStr, String fName) {
        try{
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer xform = tfactory.newTransformer();

            File myOutput = new File(Constants.DIR_FEED + File.separator + fName);
            xform.transform(new DOMSource(doc), new StreamResult(myOutput));
            
        }catch(Exception e){
            e.printStackTrace();
            log.error(e);
        }
    }
    
    public static void logArrayList(ArrayList<String> arr, String filePath){
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath,true);
            for(String str: arr) {
              writer.write(str+"\n");
            }
            writer.close();
        } catch (IOException ex) {
            log.error(ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }   
    
    public static boolean isValidEmail(String email) {
        if(email == null || email.trim().length()==0) {
            return false;
        }
        
        String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
	
        return matcher.matches();
 
	
    }

    public static void saveProps(Properties p, String FILE_INDEX) {
        try {
            File f = new File(FILE_INDEX);
            OutputStream out = new FileOutputStream(f);
            p.store(out, null);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public static Properties getProps(String FILE_INDEX) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(FILE_INDEX));
        } catch (IOException ex) {
            log.error(ex);
        }
        return prop;
    }

}
