package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PersonInfoController.class);

    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PersonInfoController(PersonRepository personRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * GET /personInfo?firstName={firstName}&lastName={lastName}
     *
     * Récupère les informations détaillées d’une personne : nom, prénom, adresse,
     * âge,
     * email, ainsi que ses antécédents médicaux (médicaments et allergies).
     *
     * @param firstName prénom de la personne (obligatoire)
     * @param lastName  nom de famille de la personne (obligatoire)
     * @return liste de PersonInfoDTO ou 404 si non trouvée
     */
    @GetMapping
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByLastName(@RequestParam("lastName") String lastName) {
        logger.debug("Requête GET /personInfo pour : {}", lastName);
        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());

        if (persons.isEmpty()) {
            logger.info("Aucune information trouvée pour : {}", lastName);
            return ResponseEntity.notFound().build();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        List<PersonInfoDTO> result = persons.stream().map(person -> {
            PersonInfoDTO dto = new PersonInfoDTO();
            dto.setFirstName(person.getFirstName());
            dto.setLastName(person.getLastName());
            dto.setAddress(person.getAddress());
            dto.setEmail(person.getEmail());

            MedicalRecord mr = medicalRecordRepository.findByName(person.getFirstName(), person.getLastName());
            int age = 0;
            if (mr != null && mr.getBirthdate() != null) {
                try {
                    LocalDate birthDate = LocalDate.parse(mr.getBirthdate(), formatter);
                    age = Period.between(birthDate, LocalDate.now()).getYears();
                } catch (Exception e) {
                    logger.warn("Impossible de parser la date de naissance pour {} {} : {}",
                            person.getFirstName(), person.getLastName(), e.getMessage());
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

        logger.info("{} enregistrement(s) renvoyé(s) pour : {}", result.size(), lastName);
        return ResponseEntity.ok(result);
    }
}
