package com.safetynet.alerts.service;

import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repository;
    private final DataPersistenceService persistenceService;

    public MedicalRecordService(MedicalRecordRepository repository, DataPersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public void addMedicalRecord(MedicalRecord mr) {
        repository.save(mr);
        persistenceService.updateDataFile();
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return repository.findAll();
    }
}
