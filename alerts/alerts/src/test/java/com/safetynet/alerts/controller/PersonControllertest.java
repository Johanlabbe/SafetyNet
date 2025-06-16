package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.SafetynetAlertsApplication;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonService service;

    @Test
    @DisplayName("POST /person → 400 si la personne existe déjà")
    void whenAddPersonDuplicate_thenBadRequest() throws Exception {
        Person p = new Person();
        p.setFirstName("Jean");
        p.setLastName("Dupont");
        doThrow(new IllegalArgumentException("Cette personne existe déjà."))
            .when(service).addPerson(any(Person.class));

        mvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
           .andExpect(status().isBadRequest())
           .andExpect(content().string("Cette personne existe déjà."));
    }
}
