package com.safetynet.alerts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.dto.FireStationResponseDTO;
import com.safetynet.alerts.dto.PersonDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private static final Logger logger = LoggerFactory.getLogger(FireController.class);

    private final FireStationRepository fireStationRepository;
    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * Injecte les repositories nécessaires.
     *
     * @param fireStationRepository   pour accéder aux données FireStation
     * @param personRepository        pour accéder aux données Person
     * @param medicalRecordRepository pour accéder aux données MedicalRecord
     */
    public FireStationController(FireStationRepository fireStationRepository,
            PersonRepository personRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.fireStationRepository = fireStationRepository;
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * GET /firestation?stationNumber={stationNumber}
     * <p>
     * Pour le numéro de station fourni, récupère :
     * <ul>
     * <li>toutes les adresses couvertes par cette station ;</li>
     * <li>tous les résidents de ces adresses (via PersonDTO) ;</li>
     * <li>le nombre d’adultes et d’enfants, calculé à partir des
     * MedicalRecord.</li>
     * </ul>
     *
     * @param stationNumber le numéro de la station à interroger (obligatoire)
     * @return ResponseEntity contenant un FireStationResponseDTO avec la liste des
     *         PersonDTO
     *         et les comptes d’adultes/enfants, ou 404 si la station n’existe pas
     */
    @GetMapping(params = "stationNumber")
    public ResponseEntity<FireStationResponseDTO> getPersonsByStation(@RequestParam("stationNumber") String stationNumber) {
        logger.debug("Requête getPersonsByAddress pour l'adresse : {}", stationNumber);
        List<FireStation> stations = fireStationRepository.findAll()
                .stream()
                .filter(fs -> stationNumber.equals(fs.getStation()))
                .collect(Collectors.toList());

        if (stations.isEmpty()) {
            logger.info("Aucune station trouvée avec le numéro : {}", stationNumber);
            return ResponseEntity.notFound().build();
        }

        Set<String> addresses = stations.stream()
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());

        List<PersonDTO> personDTOs = persons.stream().map(p -> {
            PersonDTO dto = new PersonDTO();
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setAddress(p.getAddress());
            dto.setPhone(p.getPhone());
            return dto;
        }).collect(Collectors.toList());

        int adultCount = 0;
        int childCount = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (Person p : persons) {
            MedicalRecord mr = medicalRecordRepository.findByName(p.getFirstName(), p.getLastName());
            if (mr != null && mr.getBirthdate() != null) {
                try {
                    LocalDate birthDate = LocalDate.parse(mr.getBirthdate(), formatter);
                    int age = Period.between(birthDate, LocalDate.now()).getYears();
                    if (age <= 18) {
                        childCount++;
                    } else {
                        adultCount++;
                    }
                } catch (Exception e) {
                    logger.warn("Impossible de parser la date de naissance pour {} {} : {}",
                            p.getFirstName(), p.getLastName(), e.getMessage());
                    adultCount++;
                }
            } else {
                adultCount++;
            }
        }
        logger.info("{} résidents trouvés ({} adultes, {} enfants) pour la station {}", 
                    personDTOs.size(), adultCount, childCount, stationNumber);
                    
        FireStationResponseDTO response = new FireStationResponseDTO();
        response.setPersons(personDTOs);
        response.setAdultCount(adultCount);
        response.setChildCount(childCount);

        return ResponseEntity.ok(response);
    }
}
