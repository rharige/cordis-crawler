/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler;

import java.util.ArrayList;
import org.apache.commons.lang3.text.WordUtils;
import org.nl.vu.ni.cordiscrawler.common.Utils;

/**
 *
 * @author Ravi
 */
public class Organization {

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(Utils.isValidEmail(email)){
            this.email = email.toLowerCase();
        }
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if(contact!=null && contact.trim().length()>0 && !contact.toLowerCase().equals("n/a")){
            contact = contact.replace(",","");
            this.contact = WordUtils.capitalizeFully(contact);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = WordUtils.capitalizeFully(region);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = WordUtils.capitalizeFully(address);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name);
        
    }

    @Override
    public String toString() {
        return "Organization{" + "\n telephone=" + telephone + ",\n fax=" + fax + ",\n email=" + email + ",\n contact=" + contact + ",\n url=" + url + ",\n region=" + region + ",\n address=" + address + ",\n type=" + type + ",\n name=" + name + "\n}";
    }
    
    String telephone;
    String fax;
    String email;
    String contact;
    String url;
    String region;
    String address;
    String type;
    String name;
    String rcn;
    
    public Organization(String rcn) {
        this.rcn = rcn;
    }
    
    public ArrayList toArrayList(){
        ArrayList al = new ArrayList();
        
        //org details 0-7
        al.add(name);
        al.add(type);
        al.add(address);
        al.add(region);
        al.add(url);
        al.add(fax);
        al.add(telephone);
        
        //contact details 7-8
        al.add(contact);
        
        //rcn details 8-9
        al.add(rcn);
        
        return al;
    }
    
}
