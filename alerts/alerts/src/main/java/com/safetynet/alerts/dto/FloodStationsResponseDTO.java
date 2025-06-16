package com.safetynet.alerts.dto;

import java.util.List;

public class FloodStationsResponseDTO {
    private List<HouseholdDTO> households;

    public List<HouseholdDTO> getHouseholds() {
        return households;
    }
    public void setHouseholds(List<HouseholdDTO> households) {
        this.households = households;
    }
}
