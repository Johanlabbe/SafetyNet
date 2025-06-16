package com.safetynet.alerts.controller;

import com.safetynet.alerts.SafetynetAlertsApplication;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
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

@WebMvcTest(PersonInfoController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class PersonInfoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PersonRepository personRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private Person person(String fn, String ln, String addr, String email) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        p.setAddress(addr);
        p.setEmail(email);
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
    @DisplayName("GET /personInfo?lastName inconnue → 404")
    void whenNoPerson_then404() throws Exception {
        when(personRepository.findAll())
            .thenReturn(Collections.emptyList());

        mvc.perform(get("/personInfo")
                .param("lastName", "Unknown"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /personInfo?lastName valide → liste de DTO")
    void whenLastNameExists_thenReturnList() throws Exception {
        Person p1 = person("John","Doe","A","john@d.fr");
        Person p2 = person("Jane","Doe","B","jane@d.fr");
        when(personRepository.findAll())
            .thenReturn(Arrays.asList(p1, p2));

        when(medicalRecordRepository.findByName("John","Doe"))
            .thenReturn(mr("John","Doe","01/01/1980",
                          Arrays.asList("m1"),
                          Arrays.asList("a1")));
        when(medicalRecordRepository.findByName("Jane","Doe"))
            .thenReturn(mr("Jane","Doe","01/01/2010", null, null));

        int age1 = Period.between(LocalDate.parse("01/01/1980", FORMATTER), LocalDate.now()).getYears();
        int age2 = Period.between(LocalDate.parse("01/01/2010", FORMATTER), LocalDate.now()).getYears();

        mvc.perform(get("/personInfo")
                .param("lastName", "doe")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[0].firstName", is("John")))
           .andExpect(jsonPath("$[0].lastName", is("Doe")))
           .andExpect(jsonPath("$[0].address", is("A")))
           .andExpect(jsonPath("$[0].email", is("john@d.fr")))
           .andExpect(jsonPath("$[0].age", is(age1)))
           .andExpect(jsonPath("$[0].medications[0]", is("m1")))
           .andExpect(jsonPath("$[0].allergies[0]", is("a1")))
           .andExpect(jsonPath("$[1].firstName", is("Jane")))
           .andExpect(jsonPath("$[1].age", is(age2)));
    }
}
