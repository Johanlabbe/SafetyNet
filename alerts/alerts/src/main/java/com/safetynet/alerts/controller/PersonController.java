package com.safetynet.alerts.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
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
        logger.debug("Requête POST /person avec Person : {}", person);
        try {
            service.addPerson(person);
            logger.info("Personne ajoutée : {} {}", person.getFirstName(), person.getLastName());
            return ResponseEntity.ok("Personne ajoutée avec succès.");
        } catch (IllegalArgumentException e) {
            logger.warn("Échec ajout Personne {} {} : {}",
                    person.getFirstName(), person.getLastName(), e.getMessage());
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
        logger.debug("Requête PUT /person avec Person : {}", person);
        try {
            service.updatePerson(person);
            logger.info("Personne mise à jour : {} {}", person.getFirstName(), person.getLastName());
            return ResponseEntity.ok("Personne mise à jour avec succès.");
        } catch (IllegalArgumentException e) {
            logger.warn("Échec mise à jour Personne {} {} : {}",
                    person.getFirstName(), person.getLastName(), e.getMessage());
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
        logger.debug("Requête DELETE /person pour : {} {}", firstName, lastName);
        try {
            service.deletePerson(firstName, lastName);
            logger.info("Personne supprimée : {} {}", firstName, lastName);
            return ResponseEntity.ok("Personne supprimée avec succès.");
        } catch (IllegalArgumentException e) {
            logger.warn("Échec suppression Personne {} {} : {}",
                    firstName, lastName, e.getMessage());
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
        logger.debug("Requête GET /person");
        List<Person> persons = service.getAllPersons();
        logger.info("{} personnes récupérées", persons.size());
        
        return ResponseEntity.ok(persons);
    }
}
