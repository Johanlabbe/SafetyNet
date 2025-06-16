package com.safetynet.alerts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;

import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService service;

    /**
     * Constructeur de MedicalRecordController.
     *
     * @param service service métier pour les MedicalRecord
     */
    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    /**
     * POST  /medicalRecord
     * <p>
     * Crée un nouveau dossier médical à partir du corps de la requête.
     *
     * @param mr le MedicalRecord à ajouter (JSON)
     * @return ResponseEntity avec le code HTTP 201 (Created) et l’URI de la ressource créée
     */
    @PostMapping
    public ResponseEntity<String> addMedicalRecord(@RequestBody MedicalRecord mr) {
        service.addMedicalRecord(mr);
        return ResponseEntity.ok("Dossier médical ajouté.");
    }

    /**
     * GET  /medicalRecord
     * <p>
     * Récupère la liste de tous les dossiers médicaux existants.
     *
     * @return ResponseEntity contenant la liste de MedicalRecord
     */
    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        return ResponseEntity.ok(service.getAllMedicalRecords());
    }
}
