package com.safetynet.alerts.model;

import java.util.ArrayList;
import java.util.List;

public class FireStation {
    private String address;
    private String station;

    private List<Person> persons = new ArrayList<>();

    public FireStation() {}

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getStation() {
        return station;
    }
    public void setStation(String station) {
        this.station = station;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void addPerson(Person person) {
        this.persons.add(person);
    }
}
