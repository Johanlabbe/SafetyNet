package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.SafetynetAlertsApplication;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalRecordController.class)
@ContextConfiguration(classes = SafetynetAlertsApplication.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedicalRecordService service;

    private MedicalRecord mr(String fn, String ln, String birth) {
        MedicalRecord m = new MedicalRecord();
        m.setFirstName(fn);
        m.setLastName(ln);
        m.setBirthdate(birth);
        return m;
    }

    @Test
    @DisplayName("POST /medicalRecord → ajout réussi")
    void whenAddMedicalRecord_thenOk() throws Exception {
        MedicalRecord m = mr("A", "One", "01/01/2000");
        doNothing().when(service).addMedicalRecord(any(MedicalRecord.class));

        mvc.perform(post("/medicalRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(m)))
           .andExpect(status().isOk())
           .andExpect(content().string("Dossier médical ajouté."));
    }

    @Test
    @DisplayName("GET /medicalRecord → liste de dossiers")
    void whenGetAllMedicalRecords_thenReturnList() throws Exception {
        MedicalRecord m1 = mr("A","One","01/01/1990");
        MedicalRecord m2 = mr("B","Two","01/01/2005");
        when(service.getAllMedicalRecords()).thenReturn(Arrays.asList(m1, m2));

        mvc.perform(get("/medicalRecord")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[0].firstName", is("A")))
           .andExpect(jsonPath("$[1].firstName", is("B")));
    }
}
