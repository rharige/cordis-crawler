/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nl.vu.ni.cordiscrawler;

import java.util.ArrayList;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author Ravi
 */
public class Project {

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRCN() {
        return rcn;
    }

    public void setRCN(String rcn) {
        this.rcn = rcn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        char[] sep = {'.'};
        this.title = WordUtils.capitalizeFully(title, sep);
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(String generalInfo) {
        this.generalInfo = generalInfo;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProgramAcronym() {
        return programAcronym;
    }

    public void setProgramAcronym(String programAcronym) {
        this.programAcronym = programAcronym;
    }

    public String getProjectAcronym() {
        return projectAcronym;
    }

    public void setProjectAcronym(String projectAcronym) {
        this.projectAcronym = projectAcronym;
    }

    public String getProjectReference() {
        return projectReference;
    }

    public void setProjectReference(String projectReference) {
        this.projectReference = projectReference;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getSubProgramme() {
        return subProgramme;
    }

    public void setSubProgramme(String subProgramme) {
        this.subProgramme = subProgramme;
    }

    public Organization getMainContractor() {
        return mainContractor;
    }

    public void setMainContractor(Organization mainContractor) {
        this.mainContractor = mainContractor;
    }

    public ArrayList<Organization> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Organization> participants) {
        this.participants = participants;
    }
    public void addParticipant(Organization participant) {
        this.participants.add(participant);
    }
    
    
    @Override
    public String toString() {
        return "Project{" + "\n startDate=" + startDate + ",\n endDate=" + endDate + ",\n rcn=" + rcn + ",\n title=" + title + ",\n duration=" + duration + ",\n achievements=" + achievements + ",\n objectives=" + objectives + ",\n generalInfo=" + generalInfo + ",\n cost=" + cost + ",\n funding=" + funding + ",\n status=" + status + ",\n programAcronym=" + programAcronym + ",\n projectAcronym=" + projectAcronym + ",\n projectReference=" + projectReference + ",\n subject=" + subject + ",\n contractType=" + contractType + ",\n subProgramme=" + subProgramme + ",\n mainContractor=" + mainContractor + ",\n participants=" + participants + "\n}";
    }
    
    String startDate;
    String endDate;
    String rcn;
    String title;
    String duration;
    String achievements;
    String objectives;
    String generalInfo;
    String cost;
    String funding;
    String status;
    String programAcronym;
    String projectAcronym;
    String projectReference;
    String subject;
    String contractType;
    String subProgramme;
    
    Organization mainContractor;
    ArrayList<Organization> participants =  new ArrayList<Organization>();
    
    public ArrayList toArrayList(){
        ArrayList al = new ArrayList();
        al.add(rcn);
        al.add(title);
        al.add(duration);
        al.add(startDate);
        al.add(endDate);
        al.add(achievements);
        al.add(generalInfo);
        al.add(objectives);
        al.add(cost);
        al.add(funding);
        al.add(status);
        al.add(programAcronym);
        al.add(contractType);
        al.add(subject);
        al.add(projectReference);
        al.add(projectAcronym);
        al.add(subProgramme);
        return al;
    }
}
