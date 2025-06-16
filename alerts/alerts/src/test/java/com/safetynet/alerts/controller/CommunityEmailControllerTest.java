package com.safetynet.alerts.controller;

import com.safetynet.alerts.SafetynetAlertsApplication;
import com.safetynet.alerts.model.Person;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityEmailController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class CommunityEmailControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PersonRepository personRepository;

    private Person p(String fn, String ln, String city, String email) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        p.setCity(city);
        p.setEmail(email);
        return p;
    }

    @Test
    @DisplayName("GET /communityEmail?city inconnue → 404")
    void whenNoPersons_then404() throws Exception {
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        mvc.perform(get("/communityEmail")
                .param("city", "Nowhere"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /communityEmail?city valid → set d'emails uniques")
    void whenSomeEmails_thenReturnSet() throws Exception {
        List<Person> data = Arrays.asList(
            p("A","One","Paris","a@p.fr"),
            p("B","Two","paris","b@p.fr"),   
            p("C","Three","Lyon","c@l.fr"),
            p("D","Four","Paris", ""),     
            p("E","Five","Paris", null),  
            p("F","Six","Paris","a@p.fr") 
        );
        when(personRepository.findAll()).thenReturn(data);

        mvc.perform(get("/communityEmail")
                .param("city", "PARIS")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[?(@ == 'a@p.fr')]").exists())
           .andExpect(jsonPath("$[?(@ == 'b@p.fr')]").exists());
    }

    @Test
    @DisplayName("GET /communityEmail?city présent mais pas d'emails valides → 404")
    void whenCityButNoValidEmails_then404() throws Exception {
        List<Person> data = Arrays.asList(
            p("X","Y","Nice",""),
            p("Z","W","nice", null)
        );
        when(personRepository.findAll()).thenReturn(data);

        mvc.perform(get("/communityEmail")
                .param("city", "Nice"))
           .andExpect(status().isNotFound());
    }
}
