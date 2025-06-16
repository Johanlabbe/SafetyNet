package com.safetynet.alerts.util;

import java.util.List;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;



public class DataWrapper {
    private List<Person> persons;
    private List<FireStation> firestations;
    private List<MedicalRecord> medicalrecords;
    
    public List<Person> getPersons() {
        return persons;
    }
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
    public List<FireStation> getFirestations() {
        return firestations;
    }
    public void setFirestations(List<FireStation> firestations) {
        this.firestations = firestations;
    }
    public List<MedicalRecord> getMedicalrecords() {
        return medicalrecords;
    }
    public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
        this.medicalrecords = medicalrecords;
    }
}
