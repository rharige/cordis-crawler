/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.nl.vu.ni.cordiscrawler.common.Constants;
import org.nl.vu.ni.cordiscrawler.common.Utils;
import org.nl.vu.ni.cordiscrawler.io.DataIO;


/**
 *
 * @author Ravi
 */
public class Crawl {
    static Logger log = Logger.getLogger(Crawl.class.getName());
    private ArrayList rcnList = null;
    /**
     * Fetches basic project details from the CORDIS portal
     * The response of each call is stored in XML file at %root%/feed directory. 
     * Approx 90 files are created.
     */
    public void fetchProjectFeed(){
        
        //total projects are < 94000 as of today
        for(int i=0;i<94;i++){
            
            //set start & end offset
            int start=(i*1000);
            if(start == 0) {
                start=1;
            }
            int end=(i*1000)+999;
            
            
            System.err.println(start+" - "+end);
            Utils.parseProjectsFeed(Constants.cordis_base+"&collection=EN_PROJ&start="+start+"&end="+end+"&sort=all&ENGINE_ID=CORDIS_ENGINE_ID&SEARCH_TYPE_ID=CORDIS_SEARCH_ID&typeResp=xml","pFeed"+i+".xml");
            
            try{
                Thread.sleep(Constants.API_DELAY);
            }catch(Exception e){
                
            }
        }
        System.out.println("Done!");
    }
    
    private ArrayList initRCNList(){
        if(rcnList == null){
            rcnList = DataIO.getInstance(Constants.DB_INSTANCE).getRCNdata();
        }
        System.out.println(rcnList.size());
        return rcnList;
    }
    
    private void storeProjectFeed(int INSTANCE_TYPE){
        try {
            DataIO.getInstance(INSTANCE_TYPE).writeProjectFeed();
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    public static void main(String asp[]){
        //step 1: download all the basic project details
        Crawl c = new Crawl();
//        c.fetchProjectFeed();
        
        //step 2: load the downloaded data to database for easier access
//        c.storeProjectFeed(Constants.DB_INSTANCE);
        
        //step 3: init arraylist with RCN number of all the projects, to process them sequentially
        c.initRCNList();
        
        //step 4: fetch complete project details for each 
        c.scrapeProjectData(Constants.DB_INSTANCE);
    }

    private void scrapeProjectData(int INSTANCE_TYPE) {
        
        
        ArrayList<Project> al = new ArrayList<Project>();
        ArrayList<String> failList = new ArrayList<String>();
        File stopFile = new File(Constants.stop_file);
        
        boolean stop_flag = false;
        loop: for(int i=getStartIndex();i<rcnList.size() && !stop_flag ;i++){
            String rcn = rcnList.get(i).toString();
            System.out.println(i+"/"+93770+" : "+rcn);
            try {
                
                Document tempDoc = fetchProjectData(rcn);
                Project proj = processProjectData(tempDoc);
                al.add(proj);
                
            } catch (Exception ex) {
                failList.add("io:"+rcn);
                log.error("(IO) Failed RCN:"+rcn, ex);
            }
            
            //flush to database
            if(((i+1)%Constants.FLUSH_INTERVAL) == 0){
                DataIO.getInstance(INSTANCE_TYPE).storeProjectData(al);
                Utils.logArrayList(failList,Constants.FILE_FAIL_LIST);
                failList.clear();
                al.clear();
                if(stopFile.exists()) {
                    log.info("stop file detected @"+i+" rcn:"+rcn);
                    saveIndex(i);
                    stop_flag = true;
                }
            }
            
            //delay
            try{
                Thread.sleep(Constants.API_DELAY);
            }catch(Exception e){
                
            }
        }
        if(al.size()>0){
            DataIO.getInstance(INSTANCE_TYPE).storeProjectData(al);
        }
        //store failed RCNs in file to try querying them later
        Utils.logArrayList(failList,Constants.FILE_FAIL_LIST);
        System.out.println(":processing done:");
        
    }

    private Document fetchProjectData(String rcn) throws IOException {
                
                //headers
                String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
                int timeout = 120*1000;
                String connection = "keep-alive";
                String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0";
                
                Map data = new HashMap();
                data.put("doc_fetcher", "PROJECTS_FETCHER_ID");
                data.put("reference", rcn);
                data.put("template", "projects_template.xml");
                
                //Get the cookies
                Connection.Response response = Jsoup.connect("http://cordis.europa.eu/newsearch/index.cfm?page=docview&collection=EN_PROJ&reference="+rcn)
                .method(Connection.Method.GET).execute();
                Map<String, String> cookies = response.cookies();
                String noCache = System.currentTimeMillis()+"";
                
                //Connection request formation
                String url = "http://cordis.europa.eu/newsearch/support/readDocument.cfc?method=getDocument&noCache="+noCache;
                String referer = "http://cordis.europa.eu/newsearch/index.cfm?page=docview&collection=EN_PROJ&reference="+rcn;
                Document doc = Jsoup.connect(url).method(org.jsoup.Connection.Method.POST).data(data).header("Referer",referer)
                        .cookies(cookies)
                        .header("Accept",accept)
                        .header("Connection", connection)
                        .header("User-Agent", agent)
                        .timeout(timeout)
                        .post();
//                System.out.println(doc);
                return doc;
        }
    
    @SuppressWarnings("empty-statement")
    private Project processProjectData(Document doc) throws Exception {
        Project project = new Project();
        
        Elements elements = doc.select("span[id~=tag_RCN]");
        try {
            String rcn = elements.get(0).text();
            int rcnbr = Integer.parseInt(rcn);
            project.setRCN(rcn);
        } catch (Exception e) {
            throw new Exception("Invalid RCN response from server");
        }

        elements = doc.select("span[id~=tag_startdate]");
        if (elements.size() == 1) {
            String startdate = elements.get(0).text();
            project.setStartDate(startdate);
        }

        elements = doc.select("span[id~=tag_titleHL]");
        if (elements.size() == 1) {
            String title = elements.get(0).text();
            project.setTitle(title);
        }

        elements = doc.select("span[id~=tag_duration]");
        if (elements.size() == 1) {
            String duration = elements.get(0).text();
            project.setDuration(duration);
        }

        elements = doc.select("span[id~=tag_enddate]");
        if (elements.size() == 1) {
            String enddate = elements.get(0).text();
            project.setEndDate(enddate);
        }

        elements = doc.select("span[id~=tag_achievements]");
        if (elements.size() == 1) {
            String achievements = elements.get(0).text();
            project.setAchievements(achievements);
        }

        elements = doc.select("span[id~=tag_generalinfo]");
        if (elements.size() == 1) {
            String information = elements.get(0).text();
            project.setGeneralInfo(information);
        }

        elements = doc.select("span[id~=tag_objective]");
        if (elements.size() == 1) {
            String objectives = elements.get(0).text();
            project.setObjectives(objectives);
        }

        elements = doc.select("span[id~=tag_cost]");
        if (elements.size() == 1) {
            String cost = elements.get(0).text().split(" ")[0];
            project.setCost(cost);
        }

        elements = doc.select("span[id~=tag_funding]");
        if (elements.size() == 1) {
            String funding = elements.get(0).text().split(" ")[0];
            project.setFunding(funding);
        };

        elements = doc.select("span[id~=tag_status]");
        if (elements.size() == 1) {
            String status = elements.get(0).text();
            project.setStatus(status);
        }

        elements = doc.select("span[id~=tag_programmeacronym]");
        if (elements.size() == 1) {
            String programmeacronym = elements.get(0).text();
            project.setProgramAcronym(programmeacronym);
        }

        elements = doc.select("span[id~=tag_projectacronym]");
        if (elements.size() == 1) {
            String projectAcronym = elements.get(0).text();
            project.setProjectAcronym(projectAcronym);
        }


        elements = doc.select("span[id~=tag_reference]");
        if (elements.size() == 1) {
            String projectReference = elements.get(0).text();
            project.setProjectReference(projectReference);
        }

        elements = doc.select("span[id~=tag_contracttype]");
        if (elements.size() == 1) {
            String Contract = elements.get(0).text();
            project.setContractType(Contract);
        }

        elements = doc.select("span[id~=tag_subject]");
        if (elements.size() == 1) {
            String subs = elements.get(0).text();
            project.setSubject(subs);
        }

        elements = doc.select("span[id~=tag_subprogramme]");
        if (elements.size() == 1) {
            String tag_subprogramme = elements.get(0).text();
            project.setSubProgramme(tag_subprogramme);
        }

        Organization org = new Organization(project.getRCN());

        elements = doc.select("span[id~=tag_organizationname]");
        if (elements.size() == 1) {
            String name = elements.get(0).text();
            org.setName(name);
        }

        elements = doc.select("span[id~=tag_orgtype]");
        if (elements.size() == 1) {
            String type = elements.get(0).text();
            org.setType(type);
        }

        elements = doc.select("span[id~=tag_address]");
        if (elements.size() == 1) {
            String address = elements.get(0).text();
            org.setAddress(address);
        }

        elements = doc.select("span[id~=tag_region]");
        if (elements.size() == 1) {
            String region = elements.get(0).text();
            org.setRegion(region);
        }

        elements = doc.select("span[id~=tag_url]");
        if (elements.size() == 1) {
            String url = elements.get(0).text();
            org.setUrl(url);
        }

        elements = doc.select("span[id~=tag_contact]");
        if (elements.size() > 1) {
            String contact = elements.get(0).text();
            org.setContact(contact);
        }

        elements = doc.select("span[id~=tag_email]");
        if (elements.size() == 1) {
            String email = elements.get(0).text();
            org.setEmail(email);
        }

        elements = doc.select("span[id~=tag_contact_fax]");
        if (elements.size() == 1) {
            String fax = elements.get(0).text();
            org.setFax(fax);
        }

        elements = doc.select("span[id~=tag_contact_tel]");
        if (elements.size() == 1) {
            String tel = elements.get(0).text();
            org.setTelephone(tel);
        }

        project.setMainContractor(org);

        elements = doc.select("span[id~=tag_participants]");
        for (Element element : elements) {
            List<Node> child = element.childNodes();
            for (Node n : child) {

                if (n.nodeName().equals("table")) {
                    Organization pOrg = new Organization(project.getRCN());
                    Document docx = Jsoup.parse(n.outerHtml());
                    if (docx.select("tr").isEmpty()) {
                        continue;
                    }

                    Elements ele = docx.select("span[id~=organization]");
                    if (ele.size() >= 1) {
                        String pname = ele.get(0).text();
                        pOrg.setName(pname);
                    }

                    ele = docx.select("span[id~=contact]");
                    if (ele.size() == 1) {
                        String pcontact = ele.get(0).text();
                        pOrg.setContact(pcontact);
                    }

                    ele = docx.select("span[id~=address]");
                    if (ele.size() == 1) {
                        String paddress = ele.get(0).text();
                        pOrg.setAddress(paddress);
                    }

                    ele = docx.select("span[id~=organizationtype]");
                    if (ele.size() == 1) {
                        String porganization_type = ele.get(0).text();
                        pOrg.setType(porganization_type);
                    }

                    ele = docx.select("span[id~=url]");
                    if (ele.size() == 1) {
                        String purl = ele.get(0).text();
                        pOrg.setUrl(purl);
                    }

                    ele = docx.select("span[id~=region]");
                    if (ele.size() == 1) {
                        String pregion = ele.get(0).text();
                        pOrg.setRegion(pregion);
                    }


                    ele = docx.select("span[id~=email]");
                    if (ele.size() == 1) {
                        String pemail = ele.get(0).text();
                        pOrg.setEmail(pemail);
                    }

                    ele = docx.select("span[id~=fax]");
                    if (ele.size() == 1) {
                        String pfax = ele.get(0).text();
                        pOrg.setFax(pfax);
                    }

                    ele = docx.select("span[id~=tel]");
                    if (ele.size() == 1) {
                        String ptel = ele.get(0).text();
                        pOrg.setTelephone(ptel);
                    }

                    project.addParticipant(pOrg);
                }
            }
        }
        return project;
    }

    private int getStartIndex() {
        int res = 0;
        try{
            Properties p = Utils.getProps(Constants.FILE_INDEX);
            String ind = p.getProperty("stopindex");
            res = Integer.parseInt(ind);
            res++;
        }catch(Exception e){
            log.error(e);
        }
        return res;
    }

    private void saveIndex(int i) {
        Properties p = new Properties();
        p.setProperty("stopindex",i+"");
        Utils.saveProps(p,Constants.FILE_INDEX);
    }
}
