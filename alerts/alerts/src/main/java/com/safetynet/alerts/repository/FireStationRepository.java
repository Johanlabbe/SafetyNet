package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.FireStation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FireStationRepository {

    private final Map<String, FireStation> fireStationMap = new HashMap<>();

    public void save(FireStation fireStation) {
        fireStationMap.put(fireStation.getAddress(), fireStation);
    }

    public List<FireStation> findAll() {
        return new ArrayList<>(fireStationMap.values());
    }

    public void clear() {
        fireStationMap.clear();
    }
}
