package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository repository;

    @Mock
    private DataPersistenceService persistenceService;

    @InjectMocks
    private MedicalRecordService service;

    private MedicalRecord mr(String fn, String ln) {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName(fn);
        m.setLastName(ln);
        return m;
    }

    @Test
    @DisplayName("addMedicalRecord → save + updateDataFile")
    void addMedicalRecord() {
        MedicalRecord rec = mr("A","One");
        service.addMedicalRecord(rec);

        verify(repository).save(rec);
        verify(persistenceService).updateDataFile();
    }

    @Test
    @DisplayName("getAllMedicalRecords → délégation findAll")
    void getAllMedicalRecords() {
        MedicalRecord r1 = mr("X","Y"), r2 = mr("M","N");
        when(repository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<MedicalRecord> all = service.getAllMedicalRecords();
        assertEquals(2, all.size());
        assertTrue(all.contains(r1) && all.contains(r2));
    }
}
