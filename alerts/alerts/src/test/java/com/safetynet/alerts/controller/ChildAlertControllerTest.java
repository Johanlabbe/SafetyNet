package com.safetynet.alerts.controller;

import com.safetynet.alerts.SafetynetAlertsApplication;
// import com.safetynet.alerts.dto.ChildAlertDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChildAlertController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class ChildAlertControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PersonRepository personRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

//     @Autowired
//     private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /childAlert?address inconnue → 404")
    void whenAddressNotFound_then404() throws Exception {
        Mockito.when(personRepository.findByAddress("rue Inconnue"))
               .thenReturn(Collections.emptyList());

        mvc.perform(get("/childAlert")
                .param("address", "rue Inconnue"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /childAlert?address valid → liste d'enfants + cohabitants")
    void whenChildrenPresent_thenReturnList() throws Exception {
        Person child = new Person();
        child.setFirstName("Jean");
        child.setLastName("Dupont");
        child.setAddress("rue A");

        Person adult = new Person();
        adult.setFirstName("Marie");
        adult.setLastName("Dupont");
        adult.setAddress("rue A");

        Mockito.when(personRepository.findByAddress("rue A"))
               .thenReturn(Arrays.asList(child, adult));

        MedicalRecord mrChild = new MedicalRecord();
                      mrChild.setFirstName("Jean");
                      mrChild.setLastName("Dupont");
                      mrChild.setBirthdate("01/05/2014");
                      mrChild.setMedications(null);
                      mrChild.setAllergies(null);
        Mockito.when(medicalRecordRepository.findByName("Jean", "Dupont"))
               .thenReturn(mrChild);
        MedicalRecord mrAdult = new MedicalRecord();
                      mrAdult.setFirstName("Marie");
                      mrAdult.setLastName("Dupont");
                      mrAdult.setBirthdate("01/01/1980");
                      mrAdult.setMedications(null);
                      mrAdult.setAllergies(null);
        Mockito.when(medicalRecordRepository.findByName("Marie", "Dupont"))
               .thenReturn(mrAdult);

        mvc.perform(get("/childAlert")
                .param("address", "rue A")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(1)))
           .andExpect(jsonPath("$[0].firstName", is("Jean")))
           .andExpect(jsonPath("$[0].lastName", is("Dupont")))
           .andExpect(jsonPath("$[0].age", is(11)))
           .andExpect(jsonPath("$[0].householdMembers", hasSize(1)))
           .andExpect(jsonPath("$[0].householdMembers[0]", is("Marie Dupont")));
    }

    @Test
    @DisplayName("Si date invalide → age par défaut 0 et filtre enfant")
    void whenInvalidDate_thenAgeZero() throws Exception {
        Person kid = new Person();
        kid.setFirstName("Bob");
        kid.setLastName("Smith");
        kid.setAddress("rue B");
        Mockito.when(personRepository.findByAddress("rue B"))
               .thenReturn(Collections.singletonList(kid));
               MedicalRecord mrChild = new MedicalRecord();
               mrChild.setFirstName("Bob");
               mrChild.setLastName("Smith");
               mrChild.setBirthdate(null);
               mrChild.setMedications(null);
               mrChild.setAllergies(null);
        Mockito.when(medicalRecordRepository.findByName("Bob", "Smith"))
               .thenReturn(mrChild);

        mvc.perform(get("/childAlert")
                .param("address", "rue B"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].age", is(0)));
    }
}
