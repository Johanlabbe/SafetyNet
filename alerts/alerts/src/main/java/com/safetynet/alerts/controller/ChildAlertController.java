package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.dto.ChildAlertDTO;
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
@RequestMapping("/childAlert")
public class ChildAlertController {

    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * Crée une instance de ChildAlertController.
     *
     * @param personRepository        repository pour accéder aux données Person
     * @param medicalRecordRepository repository pour accéder aux données MedicalRecord
     */
    public ChildAlertController(PersonRepository personRepository,
                                MedicalRecordRepository medicalRecordRepository) {
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * GET  /childAlert?address={address}
     * <p>
     * Récupère tous les enfants (<= 18 ans) habitant à l'adresse spécifiée,
     * et liste leurs autres membres de foyer.
     *
     * @param address l'adresse à interroger (obligatoire)
     * @return ResponseEntity contenant la liste des ChildAlertDTO, ou 404 if no persons at that address
     */
    @GetMapping
    public ResponseEntity<List<ChildAlertDTO>> getChildrenByAddress(@RequestParam("address") String address) {
        List<Person> personsAtAddress = personRepository.findByAddress(address);
        if (personsAtAddress.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        List<ChildAlertDTO> children = personsAtAddress.stream().map(person -> {
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
            return new Object[]{ person, age };
        })
        .filter(obj -> (int)obj[1] <= 18)
        .map(obj -> {
            Person child = (Person)obj[0];
            int age = (int)obj[1];
            
            ChildAlertDTO dto = new ChildAlertDTO();
            dto.setFirstName(child.getFirstName());
            dto.setLastName(child.getLastName());
            dto.setAge(age);
            
            List<String> householdMembers = personsAtAddress.stream()
                    .filter(p -> !(p.getFirstName().equals(child.getFirstName()) 
                                && p.getLastName().equals(child.getLastName())))
                    .map(p -> p.getFirstName() + " " + p.getLastName())
                    .collect(Collectors.toList());
                    
            dto.setHouseholdMembers(householdMembers);
            return dto;
        })
        .collect(Collectors.toList());
        
        return ResponseEntity.ok(children);
    }
}
