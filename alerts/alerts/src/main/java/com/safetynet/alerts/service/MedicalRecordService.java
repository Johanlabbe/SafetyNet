package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repository;

    public MedicalRecordService(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    public void addMedicalRecord(MedicalRecord mr) {
        repository.save(mr);
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return repository.findAll();
    }
}
