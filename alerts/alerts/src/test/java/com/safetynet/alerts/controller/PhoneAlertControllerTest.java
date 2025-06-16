package com.safetynet.alerts.controller;

import com.safetynet.alerts.SafetynetAlertsApplication;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhoneAlertController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class PhoneAlertControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FireStationRepository fireStationRepository;

    @MockitoBean
    private PersonRepository personRepository;

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

    @Test
    @DisplayName("GET /phoneAlert?firestation inconnue → 404")
    void whenNoStation_then404() throws Exception {
        when(fireStationRepository.findAll())
            .thenReturn(Collections.emptyList());

        mvc.perform(get("/phoneAlert")
                .param("firestation", "9"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /phoneAlert?firestation valide → set de numéros")
    void whenStationExists_thenReturnPhones() throws Exception {
        FireStation s = fs("A","1");
        when(fireStationRepository.findAll())
            .thenReturn(Collections.singletonList(s));

        Person p1 = person("X","Y","A","111");
        Person p2 = person("M","N","A","222");
        when(personRepository.findAll())
            .thenReturn(Arrays.asList(p1, p2));

        mvc.perform(get("/phoneAlert")
                .param("firestation", "1")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$", containsInAnyOrder("111","222")));
    }
}
