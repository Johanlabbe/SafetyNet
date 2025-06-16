package com.safetynet.alerts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;



@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService service;

    /**
     * Constructeur avec injection du service métier.
     *
     * @param service service pour gérer les opérations sur Person
     */
    @Autowired
    public PersonController(PersonService service) {
        this.service = service;
    }

    /**
     * POST /person
     * <p>
     * Crée une nouvelle personne.
     *
     * @param person objet Person à ajouter (validé)
     * @return 201 Created avec en-tête Location pointant vers la ressource créée
     */
    @PostMapping
    public ResponseEntity<String> addPerson(@RequestBody Person person) {
        try {
            service.addPerson(person);
            return ResponseEntity.ok("Personne ajoutée avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * PUT /person
     * <p>
     * Met à jour une personne existante.
     *
     * @param person objet Person contenant les nouvelles valeurs (validé)
     * @return 204 No Content si la mise à jour réussit
     */
    @PutMapping
    public ResponseEntity<String> updatePerson(@RequestBody Person person) {
        try {
            service.updatePerson(person);
            return ResponseEntity.ok("Personne mise à jour avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /person?firstName={firstName}&lastName={lastName}
     * <p>
     * Supprime la personne identifiée par prénom et nom.
     *
     * @param firstName prénom de la personne
     * @param lastName  nom de la personne
     * @return 204 No Content si la suppression réussit
     */
    @DeleteMapping
    public ResponseEntity<String> deletePerson(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        try {
            service.deletePerson(firstName, lastName);
            return ResponseEntity.ok("Personne supprimée avec succès.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /person
     * <p>
     * Récupère la liste de toutes les personnes.
     *
     * @return 200 OK avec la liste des Person
     */
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(service.getAllPersons());
    }

}
