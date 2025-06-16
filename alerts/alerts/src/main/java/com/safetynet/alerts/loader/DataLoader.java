package com.safetynet.alerts.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DataWrapper;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;

@Component
public class DataLoader {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private FireStationRepository fireStationRepository;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @PostConstruct
    public void loadData() {
        logger.debug("Début du chargement des données depuis data.json.");

        try {
            personRepository.clear();
            fireStationRepository.clear();
            medicalRecordRepository.clear();
            logger.debug("Les repositories ont été vidés.");

            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();
            logger.debug("Lecture du fichier data.json en cours...");

            DataWrapper dataWrapper = mapper.readValue(inputStream, DataWrapper.class);

            List<Person> persons = dataWrapper.getPersons();
            logger.info("Chargement de {} personnes", persons.size());
            for (Person p : persons) {
                personRepository.save(p);
                logger.debug("Personne sauvegardée : {} {}", p.getFirstName(), p.getLastName());
            }

            List<MedicalRecord> medicalRecords = dataWrapper.getMedicalrecords();
            logger.info("Chargement de {} dossiers médicaux", medicalRecords.size());
            for (MedicalRecord mr : medicalRecords) {
                medicalRecordRepository.save(mr);
                Person person = personRepository.findById(mr.getFirstName(), mr.getLastName());
                if (person != null) {
                    person.addMedicalRecord(mr);
                    logger.debug("Dossier médical associé à : {} {}", mr.getFirstName(), mr.getLastName());
                } else {
                    logger.warn("Aucune personne trouvée pour le dossier médical de : {} {}", mr.getFirstName(), mr.getLastName());
                }
            }

            List<FireStation> fireStations = dataWrapper.getFirestations();
            logger.info("Chargement de {} casernes", fireStations.size());
            for (FireStation fs : fireStations) {
                fireStationRepository.save(fs);
                List<Person> personsAtAddress = personRepository.findByAddress(fs.getAddress());
                for (Person p : personsAtAddress) {
                    fs.addPerson(p);
                }
                logger.debug("Caserne '{}' associée à {} personnes", fs.getStation(), personsAtAddress.size());
            }
            
            logger.info("Données chargées (data.json) avec succès.");

        } catch (Exception e) {
            logger.error("Erreur lors du chargement des données : {}", e.getMessage(), e);
        }
    }
}
