package com.example.firapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "firs")
public class FIR {
    @Id
    private String id;
    private String district;
    private String policeStation;
    private String act;
    private List<String> ipcSections; // e.g., ["1860", "373", "353", "420", "302"]
    private String firNumber;
    private Date dateTime;
    private String generalDiaryRef;
    private String infoType;
    private String placeOccurrence;
    private String complainantName;
    private Date complainantDob;
    private String complainantNationality;
    private String complainantAadhaar;
    private String complainantOccupation;
    private String complainantMobile;
    private String complainantAddress;
    private String suspectName;
    private String suspectAddress;
    private String enquiryOfficerName;
    private String enquiryOfficerRank;

    public FIR() {
    }

    public FIR(String id, String district, String policeStation, String act, List<String> ipcSections, String firNumber, Date dateTime, String generalDiaryRef, String infoType, String placeOccurrence, String complainantName, Date complainantDob, String complainantNationality, String complainantAadhaar, String complainantOccupation, String complainantMobile, String complainantAddress, String suspectName, String suspectAddress, String enquiryOfficerName, String enquiryOfficerRank) {
        this.id = id;
        this.district = district;
        this.policeStation = policeStation;
        this.act = act;
        this.ipcSections = ipcSections;
        this.firNumber = firNumber;
        this.dateTime = dateTime;
        this.generalDiaryRef = generalDiaryRef;
        this.infoType = infoType;
        this.placeOccurrence = placeOccurrence;
        this.complainantName = complainantName;
        this.complainantDob = complainantDob;
        this.complainantNationality = complainantNationality;
        this.complainantAadhaar = complainantAadhaar;
        this.complainantOccupation = complainantOccupation;
        this.complainantMobile = complainantMobile;
        this.complainantAddress = complainantAddress;
        this.suspectName = suspectName;
        this.suspectAddress = suspectAddress;
        this.enquiryOfficerName = enquiryOfficerName;
        this.enquiryOfficerRank = enquiryOfficerRank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public List<String> getIpcSections() {
        return ipcSections;
    }

    public void setIpcSections(List<String> ipcSections) {
        this.ipcSections = ipcSections;
    }

    public String getFirNumber() {
        return firNumber;
    }

    public void setFirNumber(String firNumber) {
        this.firNumber = firNumber;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getGeneralDiaryRef() {
        return generalDiaryRef;
    }

    public void setGeneralDiaryRef(String generalDiaryRef) {
        this.generalDiaryRef = generalDiaryRef;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getPlaceOccurrence() {
        return placeOccurrence;
    }

    public void setPlaceOccurrence(String placeOccurrence) {
        this.placeOccurrence = placeOccurrence;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public Date getComplainantDob() {
        return complainantDob;
    }

    public void setComplainantDob(Date complainantDob) {
        this.complainantDob = complainantDob;
    }

    public String getComplainantNationality() {
        return complainantNationality;
    }

    public void setComplainantNationality(String complainantNationality) {
        this.complainantNationality = complainantNationality;
    }

    public String getComplainantAadhaar() {
        return complainantAadhaar;
    }

    public void setComplainantAadhaar(String complainantAadhaar) {
        this.complainantAadhaar = complainantAadhaar;
    }

    public String getComplainantOccupation() {
        return complainantOccupation;
    }

    public void setComplainantOccupation(String complainantOccupation) {
        this.complainantOccupation = complainantOccupation;
    }

    public String getComplainantMobile() {
        return complainantMobile;
    }

    public void setComplainantMobile(String complainantMobile) {
        this.complainantMobile = complainantMobile;
    }

    public String getComplainantAddress() {
        return complainantAddress;
    }

    public void setComplainantAddress(String complainantAddress) {
        this.complainantAddress = complainantAddress;
    }

    public String getSuspectName() {
        return suspectName;
    }

    public void setSuspectName(String suspectName) {
        this.suspectName = suspectName;
    }

    public String getSuspectAddress() {
        return suspectAddress;
    }

    public void setSuspectAddress(String suspectAddress) {
        this.suspectAddress = suspectAddress;
    }

    public String getEnquiryOfficerName() {
        return enquiryOfficerName;
    }

    public void setEnquiryOfficerName(String enquiryOfficerName) {
        this.enquiryOfficerName = enquiryOfficerName;
    }

    public String getEnquiryOfficerRank() {
        return enquiryOfficerRank;
    }

    public void setEnquiryOfficerRank(String enquiryOfficerRank) {
        this.enquiryOfficerRank = enquiryOfficerRank;
    }
}