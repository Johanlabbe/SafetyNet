package com.safetynet.alerts.controller;

import com.safetynet.alerts.SafetynetAlertsApplication;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FireController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class FireControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PersonRepository personRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

    @MockitoBean
    private FireStationRepository fireStationRepository;

    private Person person(String fn, String ln, String addr, String phone) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        p.setAddress(addr);
        p.setPhone(phone);
        return p;
    }

    private MedicalRecord record(String fn, String ln, String birth, 
                                 java.util.List<String> meds, 
                                 java.util.List<String> allergies) {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName(fn);
        mr.setLastName(ln);
        mr.setBirthdate(birth);
        mr.setMedications(meds);
        mr.setAllergies(allergies);
        return mr;
    }

    private FireStation station(String addr, String num) {
        FireStation fs = new FireStation();
        fs.setAddress(addr);
        fs.setStation(num);
        return fs;
    }

    @Test
    @DisplayName("GET /fire?address inconnue → 404")
    void whenNoPersons_then404() throws Exception {
        when(personRepository.findByAddress("Unknown")).thenReturn(Collections.emptyList());

        mvc.perform(get("/fire")
                .param("address", "Unknown"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /fire?address valid & station existante → 200 + DTO complet")
    void whenPersonsAndStation_thenOk() throws Exception {
        // two people at address
        Person p1 = person("John","Doe","123 Main St","111-222");
        Person p2 = person("Jane","Doe","123 Main St","333-444");
        when(personRepository.findByAddress("123 Main St"))
            .thenReturn(Arrays.asList(p1, p2));

        // their medical records
        // John né le 01/01/2000 → age calculé
        when(medicalRecordRepository.findByName("John","Doe"))
            .thenReturn(record("John","Doe","01/01/2000",
                               Arrays.asList("med1"), 
                               Arrays.asList("all1")));
        // Jane sans record → null
        when(medicalRecordRepository.findByName("Jane","Doe"))
            .thenReturn(null);

        // station assignée
        when(fireStationRepository.findAll())
            .thenReturn(Collections.singletonList(station("123 Main St","5")));

        mvc.perform(get("/fire")
                .param("address", "123 Main St")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.fireStationNumber", is("5")))
           .andExpect(jsonPath("$.persons", hasSize(2)))
           .andExpect(jsonPath("$.persons[0].age").isNotEmpty())
           .andExpect(jsonPath("$.persons[0].medications", hasSize(1)))
           .andExpect(jsonPath("$.persons[0].medications[0]", is("med1")))
           .andExpect(jsonPath("$.persons[0].allergies", hasSize(1)))
           .andExpect(jsonPath("$.persons[0].allergies[0]", is("all1")))
           .andExpect(jsonPath("$.persons[1].age", is(0)));
    }

    @Test
    @DisplayName("GET /fire?address valid sans station → station Non assigné")
    void whenNoStation_thenNonAssigne() throws Exception {
        Person p1 = person("Alice","Smith","XYZ","000-111");
        when(personRepository.findByAddress("XYZ"))
            .thenReturn(Collections.singletonList(p1));
        when(medicalRecordRepository.findByName("Alice","Smith"))
            .thenReturn(record("Alice","Smith","12/31/1990", null, null));
        // pas de station dans la liste
        when(fireStationRepository.findAll())
            .thenReturn(Collections.emptyList());

        mvc.perform(get("/fire")
                .param("address", "XYZ")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.fireStationNumber", is("Non assigné")))
           .andExpect(jsonPath("$.persons", hasSize(1)));
    }
}
