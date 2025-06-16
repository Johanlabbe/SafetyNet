package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
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
class FireStationServiceTest {

    @Mock
    private FireStationRepository repository;

    @Mock
    private DataPersistenceService persistenceService;

    @InjectMocks
    private FireStationService service;

    @Test
    @DisplayName("addFireStation → save + updateDataFile")
    void addFireStation() {
        FireStation fs = new FireStation();
        fs.setAddress("X");
        fs.setStation("1");

        service.addFireStation(fs);

        verify(repository).save(fs);
        verify(persistenceService).updateDataFile();
    }

    @Test
    @DisplayName("getAllFireStations → délégation findAll")
    void getAllFireStations() {
        FireStation f1 = new FireStation(), f2 = new FireStation();
        when(repository.findAll()).thenReturn(Arrays.asList(f1,f2));

        List<FireStation> result = service.getAllFireStations();
        assertEquals(2, result.size());
        assertTrue(result.contains(f1));
        assertTrue(result.contains(f2));
    }
}
