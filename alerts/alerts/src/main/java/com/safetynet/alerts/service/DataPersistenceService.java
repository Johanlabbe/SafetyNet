package com.safetynet.alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DataWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class DataPersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(DataPersistenceService.class);
    
    private final PersonRepository personRepository;
    private final FireStationRepository fireStationRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final ObjectMapper objectMapper;

    public DataPersistenceService(PersonRepository personRepository,
                                  FireStationRepository fireStationRepository,
                                  MedicalRecordRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.fireStationRepository = fireStationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Construit l'objet DataWrapper à partir des données des repositories et l'écrit dans data.json
     */
    public void updateDataFile() {
        DataWrapper dataWrapper = new DataWrapper();
        dataWrapper.setPersons(personRepository.findAll());
        dataWrapper.setFirestations(fireStationRepository.findAll());
        dataWrapper.setMedicalrecords(medicalRecordRepository.findAll());
        
        try {
            File file = new File("alerts/alerts/src/ressources/data.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, dataWrapper);
            logger.info("Fichier data.json mis à jour avec succès.");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du fichier data.json : {}", e.getMessage(), e);
        }
    }
}
