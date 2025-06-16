package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/phoneAlert")
public class PhoneAlertController {

    private final FireStationRepository fireStationRepository;
    private final PersonRepository personRepository;

    /**
     * Initialise le controller avec les repositories nécessaires.
     *
     * @param personRepository          repository pour accéder aux Person
     * @param medicalRecordRepository   repository pour accéder aux MedicalRecord
     */
    public PhoneAlertController(FireStationRepository fireStationRepository,
                                PersonRepository personRepository) {
        this.fireStationRepository = fireStationRepository;
        this.personRepository = personRepository;
    }

    /**
     * GET  /personInfo?lastName={lastName}
     * <p>
     * Récupère la liste des personnes dont le nom de famille
     * correspond (ignorant la casse), et renvoie leurs informations :
     * prénom, nom, adresse, email, âge, liste des médicaments et allergies.
     *
     * @param lastName le nom de famille à rechercher (obligatoire)
     * @return ResponseEntity contenant la liste des PersonInfoDTO
     *         ou 404 si aucune personne trouvée
     */
    @GetMapping
    public ResponseEntity<Set<String>> getPhoneNumbersByFireStation(@RequestParam("firestation") String firestationNumber) {
        List<FireStation> stations = fireStationRepository.findAll()
                .stream()
                .filter(fs -> firestationNumber.equals(fs.getStation()))
                .collect(Collectors.toList());

        if (stations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<String> addresses = stations.stream()
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        Set<String> phoneNumbers = personRepository.findAll().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(phoneNumbers);
    }
}
