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

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FloodStationsController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class FloodStationsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FireStationRepository fireStationRepository;

    @MockitoBean
    private PersonRepository personRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private FireStation fs(String addr, String station) {
        FireStation f = new FireStation();
        f.setAddress(addr);
        f.setStation(station);
        return f;
    }

    private Person person(String fn, String ln, String addr, String phone) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        p.setAddress(addr);
        p.setPhone(phone);
        return p;
    }

    private MedicalRecord mr(String fn, String ln, String birth,
                             java.util.List<String> meds,
                             java.util.List<String> allergies) {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName(fn);
        m.setLastName(ln);
        m.setBirthdate(birth);
        m.setMedications(meds);
        m.setAllergies(allergies);
        return m;
    }

    @Test
    @DisplayName("GET /flood/stations?stations inconnues → 404")
    void whenNoStations_then404() throws Exception {
        when(fireStationRepository.findAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/flood/stations")
                .param("stations", "1,2"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /flood/stations?stations valides → payload correct")
    void whenStationsExist_thenReturnHouseholds() throws Exception {
        FireStation s1 = fs("Addr1", "1");
        FireStation s2 = fs("Addr2", "2");
        when(fireStationRepository.findAll())
            .thenReturn(Arrays.asList(s1, s2));

        Person p1 = person("A", "One", "Addr1", "111");
        Person p2 = person("B", "Two", "Addr1", "222");
        Person p3 = person("C", "Three", "Addr2", "333");
        when(personRepository.findAll())
            .thenReturn(Arrays.asList(p1, p2, p3));

        when(medicalRecordRepository.findByName("A","One"))
            .thenReturn(mr("A","One","01/01/1990", null, null));
        when(medicalRecordRepository.findByName("B","Two"))
            .thenReturn(mr("B","Two","01/01/2000",
                          Arrays.asList("med"),
                          Arrays.asList("all")));
        when(medicalRecordRepository.findByName("C","Three"))
            .thenReturn(null);

        int ageA = Period.between(LocalDate.parse("01/01/1990", FORMATTER), LocalDate.now()).getYears();
        int ageB = Period.between(LocalDate.parse("01/01/2000", FORMATTER), LocalDate.now()).getYears();

        mvc.perform(get("/flood/stations")
                .param("stations", "1,2")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.households", hasSize(2)))

           .andExpect(jsonPath("$.households[0].address", is("Addr1")))
           .andExpect(jsonPath("$.households[0].residents", hasSize(2)))
           .andExpect(jsonPath("$.households[0].residents[0].firstName", is("A")))
           .andExpect(jsonPath("$.households[0].residents[0].lastName", is("One")))
           .andExpect(jsonPath("$.households[0].residents[0].phone", is("111")))
           .andExpect(jsonPath("$.households[0].residents[0].age", is(ageA)))
           .andExpect(jsonPath("$.households[0].residents[1].firstName", is("B")))
           .andExpect(jsonPath("$.households[0].residents[1].lastName", is("Two")))
           .andExpect(jsonPath("$.households[0].residents[1].phone", is("222")))
           .andExpect(jsonPath("$.households[0].residents[1].age", is(ageB)))
           .andExpect(jsonPath("$.households[0].residents[1].medications[0]", is("med")))
           .andExpect(jsonPath("$.households[0].residents[1].allergies[0]", is("all")))

           .andExpect(jsonPath("$.households[1].address", is("Addr2")))
           .andExpect(jsonPath("$.households[1].residents", hasSize(1)))
           .andExpect(jsonPath("$.households[1].residents[0].firstName", is("C")))
           .andExpect(jsonPath("$.households[1].residents[0].lastName", is("Three")))
           .andExpect(jsonPath("$.households[1].residents[0].phone", is("333")))
           .andExpect(jsonPath("$.households[1].residents[0].age", is(0)));
    }
}