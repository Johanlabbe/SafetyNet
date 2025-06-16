package com.safetynet.alerts.service;

import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;

import java.util.List;

@Service
public class FireStationService {

    private final FireStationRepository repository;
    private final DataPersistenceService persistenceService;

    public FireStationService(FireStationRepository repository, DataPersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public void addFireStation(FireStation fs) {
        repository.save(fs);
        persistenceService.updateDataFile();
    }

    public List<FireStation> getAllFireStations() {
        return repository.findAll();
    }
}

