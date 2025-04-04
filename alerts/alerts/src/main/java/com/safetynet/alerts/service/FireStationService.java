package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FireStationService {

    private final FireStationRepository repository;

    public FireStationService(FireStationRepository repository) {
        this.repository = repository;
    }

    public void addFireStation(FireStation fs) {
        repository.save(fs);
    }

    public List<FireStation> getAllFireStations() {
        return repository.findAll();
    }
}
