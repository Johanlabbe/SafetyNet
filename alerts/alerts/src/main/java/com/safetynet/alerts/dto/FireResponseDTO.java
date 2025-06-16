package com.safetynet.alerts.dto;

import java.util.List;

public class FireResponseDTO {
    private String fireStationNumber;
    private List<FireAddressPersonDTO> persons;

    public String getFireStationNumber() {
        return fireStationNumber;
    }
    public void setFireStationNumber(String fireStationNumber) {
        this.fireStationNumber = fireStationNumber;
    }
    public List<FireAddressPersonDTO> getPersons() {
        return persons;
    }
    public void setPersons(List<FireAddressPersonDTO> persons) {
        this.persons = persons;
    }
}
