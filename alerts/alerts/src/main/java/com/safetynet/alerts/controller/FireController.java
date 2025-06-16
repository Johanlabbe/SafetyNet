package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.dto.FireAddressPersonDTO;
import com.safetynet.alerts.dto.FireResponseDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fire")
public class FireController {

    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final FireStationRepository fireStationRepository;

    /**
     * Initialise le FireController avec les repositories nécessaires.
     *
     * @param personRepository         repository pour accéder aux Person
     * @param medicalRecordRepository  repository pour accéder aux MedicalRecord
     * @param fireStationRepository    repository pour accéder aux FireStation
     */
    public FireController(PersonRepository personRepository,
                          MedicalRecordRepository medicalRecordRepository,
                          FireStationRepository fireStationRepository) {
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.fireStationRepository = fireStationRepository;
    }

    /**
     * GET  /fire?address={address}
     * <p>
     * Récupère pour l'adresse spécifiée :
     * <ul>
     *   <li>la liste des personnes (nom, prénom, téléphone, âge, médicaments, allergies),</li>
     *   <li>le numéro de la station de pompiers desservant cette adresse.</li>
     * </ul>
     *
     * @param address l'adresse à interroger (obligatoire)
     * @return ResponseEntity contenant un FireResponseDTO si au moins une personne trouvée,
     *         ou 404 si aucun enregistrement pour cette adresse
     */
    @GetMapping
    public ResponseEntity<FireResponseDTO> getPersonsByAddress(@RequestParam("address") String address) {
        List<Person> personsAtAddress = personRepository.findByAddress(address);
        if (personsAtAddress.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        List<FireAddressPersonDTO> personDTOs = personsAtAddress.stream().map(person -> {
            FireAddressPersonDTO dto = new FireAddressPersonDTO();
            dto.setFirstName(person.getFirstName());
            dto.setLastName(person.getLastName());
            dto.setPhone(person.getPhone());

            MedicalRecord mr = medicalRecordRepository.findByName(person.getFirstName(), person.getLastName());
            int age = 0;
            if (mr != null && mr.getBirthdate() != null) {
                try {
                    LocalDate birthDate = LocalDate.parse(mr.getBirthdate(), formatter);
                    age = Period.between(birthDate, LocalDate.now()).getYears();
                } catch (Exception e) {
                    age = 0;
                }
            }
            dto.setAge(age);
            if (mr != null) {
                dto.setMedications(mr.getMedications());
                dto.setAllergies(mr.getAllergies());
            }
            return dto;
        }).collect(Collectors.toList());

        Optional<FireStation> fireStationOpt = fireStationRepository.findAll()
                .stream()
                .filter(fs -> fs.getAddress().equalsIgnoreCase(address))
                .findFirst();

        String fireStationNumber = fireStationOpt.map(FireStation::getStation).orElse("Non assigné");

        // Construire le DTO global de réponse
        FireResponseDTO responseDTO = new FireResponseDTO();
        responseDTO.setFireStationNumber(fireStationNumber);
        responseDTO.setPersons(personDTOs);

        return ResponseEntity.ok(responseDTO);
    }
}
