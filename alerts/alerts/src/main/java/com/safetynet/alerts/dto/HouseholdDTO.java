package com.safetynet.alerts.dto;

import java.util.List;

public class HouseholdDTO {
    private String address;
    private List<ResidentDTO> residents;

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public List<ResidentDTO> getResidents() {
        return residents;
    }
    public void setResidents(List<ResidentDTO> residents) {
        this.residents = residents;
    }
}
