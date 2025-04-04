package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MedicalRecordRepository {

    private final Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();

    public void save(MedicalRecord mr) {
        medicalRecordMap.put(generateKey(mr.getFirstName(), mr.getLastName()), mr);
    }

    public List<MedicalRecord> findAll() {
        return new ArrayList<>(medicalRecordMap.values());
    }

    public void clear() {
        medicalRecordMap.clear();
    }

    private String generateKey(String firstName, String lastName) {
        return firstName.toLowerCase() + "_" + lastName.toLowerCase();
    }
}
