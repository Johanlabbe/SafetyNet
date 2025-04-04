package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService service;

    @Autowired
    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> addPerson(@RequestBody Person person) {
        try {
            service.addPerson(person);
            return ResponseEntity.ok("Personne ajoutée avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<String> updatePerson(@RequestBody Person person) {
        try {
            service.updatePerson(person);
            return ResponseEntity.ok("Personne mise à jour avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deletePerson(
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        try {
            service.deletePerson(firstName, lastName);
            return ResponseEntity.ok("Personne supprimée avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
