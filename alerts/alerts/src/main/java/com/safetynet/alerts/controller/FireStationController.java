package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final FireStationService service;

    public FireStationController(FireStationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> addFireStation(@RequestBody FireStation fs) {
        service.addFireStation(fs);
        return ResponseEntity.ok("Caserne ajoutée.");
    }

    @GetMapping
    public ResponseEntity<List<FireStation>> getAllFireStations() {
        return ResponseEntity.ok(service.getAllFireStations());
    }
}
