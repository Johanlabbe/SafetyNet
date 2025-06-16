package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MedicalRecordRepositoryTest {

    private MedicalRecordRepository repo;

    private MedicalRecord mr(String fn, String ln, String birth) {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName(fn);
        m.setLastName(ln);
        m.setBirthdate(birth);
        return m;
    }

    @BeforeEach
    void setUp() {
        repo = new MedicalRecordRepository();
    }

    @Test
    @DisplayName("save & findByName & clear")
    void crudOperations() {
        assertThat(repo.findAll()).isEmpty();

        MedicalRecord r1 = mr("A","One","01/01/2000");
        repo.save(r1);

        // findAll
        List<MedicalRecord> all = repo.findAll();
        assertThat(all).hasSize(1).contains(r1);

        // findByName
        MedicalRecord fetched = repo.findByName("A","One");
        assertThat(fetched).isEqualTo(r1);

        // clear
        repo.clear();
        assertThat(repo.findAll()).isEmpty();
    }
}
