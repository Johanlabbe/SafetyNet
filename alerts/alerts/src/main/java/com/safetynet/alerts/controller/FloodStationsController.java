package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.dto.FloodStationsResponseDTO;
import com.safetynet.alerts.dto.HouseholdDTO;
import com.safetynet.alerts.dto.ResidentDTO;
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
@RequestMapping("/flood")
public class FloodStationsController {

    private final FireStationRepository fireStationRepository;
    private final PersonRepository personRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * Initialise le controller avec les repositories nécessaires.
     *
     * @param fireStationRepository   repository pour accéder aux données FireStation
     * @param personRepository        repository pour accéder aux données Person
     * @param medicalRecordRepository repository pour accéder aux données MedicalRecord
     */
    public FloodStationsController(FireStationRepository fireStationRepository,
                                   PersonRepository personRepository,
                                   MedicalRecordRepository medicalRecordRepository) {
        this.fireStationRepository = fireStationRepository;
        this.personRepository = personRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * GET  /flood/stations?stations={stationsParam}
     * <p>
     * Pour chacun des numéros de station fournis (séparés par des virgules),
     * récupère toutes les adresses couvertes, groupe les personnes par adresse
     * et retourne pour chaque foyer la liste des résidents avec :
     * <ul>
     *   <li>prénom, nom, téléphone ;</li>
     *   <li>âge calculé à partir de la date de naissance ;</li>
     *   <li>médicaments et allergies.</li>
     * </ul>
     *
     * @param stationsParam chaîne de numéros de station (ex. "1,2,3")
     * @return ResponseEntity contenant un FloodStationsResponseDTO
     *         ou 404 si aucun stationnement n’est trouvé
     */
    @GetMapping("/stations")
    public ResponseEntity<FloodStationsResponseDTO> getHouseholdsByStations(@RequestParam("stations") String stationsParam) {
        List<String> stationNumbers = Arrays.stream(stationsParam.split(","))
                                            .map(String::trim)
                                            .collect(Collectors.toList());
        
        List<FireStation> filteredStations = fireStationRepository.findAll().stream()
                .filter(fs -> stationNumbers.contains(fs.getStation()))
                .collect(Collectors.toList());
        
        if (filteredStations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Set<String> addresses = filteredStations.stream()
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());
        
        List<Person> persons = personRepository.findAll().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        Map<String, List<Person>> personsByAddress = persons.stream()
                .collect(Collectors.groupingBy(Person::getAddress));
        
        List<HouseholdDTO> households = personsByAddress.entrySet().stream().map(entry -> {
            String address = entry.getKey();
            List<Person> residents = entry.getValue();
            
            List<ResidentDTO> residentDTOs = residents.stream().map(person -> {
                ResidentDTO dto = new ResidentDTO();
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
            
            HouseholdDTO householdDTO = new HouseholdDTO();
            householdDTO.setAddress(address);
            householdDTO.setResidents(residentDTOs);
            return householdDTO;
        }).collect(Collectors.toList());
        
        FloodStationsResponseDTO response = new FloodStationsResponseDTO();
        response.setHouseholds(households);
        
        return ResponseEntity.ok(response);
    }
}
