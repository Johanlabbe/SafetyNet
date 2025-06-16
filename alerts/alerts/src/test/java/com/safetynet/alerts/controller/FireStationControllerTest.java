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

@WebMvcTest(FireStationController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class FireStationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FireStationRepository fireStationRepository;

    @MockitoBean
    private PersonRepository personRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

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

    private MedicalRecord mr(String fn, String ln, String birthdate) {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName(fn);
        m.setLastName(ln);
        m.setBirthdate(birthdate);
        return m;
    }

    @Test
    @DisplayName("GET /firestation?stationNumber inconnue → 404")
    void whenNoStation_then404() throws Exception {
        when(fireStationRepository.findAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/firestation")
                .param("stationNumber", "999"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /firestation?stationNumber valid → liste personnes + compteurs")
    void whenStationExists_thenReturnPayload() throws Exception {
        FireStation s1 = fs("Addr1", "1");
        FireStation s2 = fs("Addr2", "1");
        when(fireStationRepository.findAll())
            .thenReturn(Arrays.asList(s1, s2));

        Person adult = person("Alice","Adult","Addr1","111-111");
        Person child = person("Bob","Child","Addr2","222-222");
        when(personRepository.findAll())
            .thenReturn(Arrays.asList(adult, child));

        when(medicalRecordRepository.findByName("Alice","Adult"))
            .thenReturn(mr("Alice","Adult","01/01/1990"));
        when(medicalRecordRepository.findByName("Bob","Child"))
            .thenReturn(mr("Bob","Child","01/01/2015"));

        mvc.perform(get("/firestation")
                .param("stationNumber", "1")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.persons", hasSize(2)))
           .andExpect(jsonPath("$.persons[0].firstName", is("Alice")))
           .andExpect(jsonPath("$.persons[0].lastName", is("Adult")))
           .andExpect(jsonPath("$.persons[0].address", is("Addr1")))
           .andExpect(jsonPath("$.persons[0].phone", is("111-111")))
           .andExpect(jsonPath("$.adultCount", is(1)))
           .andExpect(jsonPath("$.childCount", is(1)));
    }

    @Test
    @DisplayName("GET /firestation valid sans medicalRecord → tous adultes")
    void whenNoRecords_thenAllAdults() throws Exception {
        FireStation s = fs("X","10");
        when(fireStationRepository.findAll())
            .thenReturn(Collections.singletonList(s));

        Person p1 = person("Tom","NoRec","X","123");
        when(personRepository.findAll())
            .thenReturn(Collections.singletonList(p1));
        when(medicalRecordRepository.findByName("Tom","NoRec"))
            .thenReturn(null);

        mvc.perform(get("/firestation")
                .param("stationNumber", "10"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.persons", hasSize(1)))
           .andExpect(jsonPath("$.adultCount", is(1)))
           .andExpect(jsonPath("$.childCount", is(0)));
    }
}
