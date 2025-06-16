package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PersonInfoController(PersonRepository personRepository,
                                MedicalRecordRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfoByLastName(@RequestParam("lastName") String lastName) {
        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
                
        if (persons.isEmpty()) {
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
        
        return ResponseEntity.ok(result);
    }
}
