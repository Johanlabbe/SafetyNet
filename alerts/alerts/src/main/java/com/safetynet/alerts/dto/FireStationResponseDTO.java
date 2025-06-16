package com.safetynet.alerts.dto;

import java.util.List;

public class FireStationResponseDTO {
    private List<PersonDTO> persons;
    private int adultCount;
    private int childCount;

    public List<PersonDTO> getPersons() {
        return persons;
    }
    public void setPersons(List<PersonDTO> persons) {
        this.persons = persons;
    }
    public int getAdultCount() {
        return adultCount;
    }
    public void setAdultCount(int adultCount) {
        this.adultCount = adultCount;
    }
    public int getChildCount() {
        return childCount;
    }
    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
}




