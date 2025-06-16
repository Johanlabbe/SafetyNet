package com.safetynet.alerts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private static final Logger logger = LoggerFactory.getLogger(CommunityEmailController.class);

    private final PersonRepository personRepository;

    /**
     * Crée une instance de CommunityEmailController.
     *
     * @param personRepository repository pour accéder aux données Person
     */
    public CommunityEmailController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * GET  /communityEmail?city={city}
     * <p>
     * Récupère les adresses email (uniques) de toutes les personnes
     * dont le champ `city` correspond, sans tenir compte de la casse.
     *
     * @param city le nom de la ville à interroger (obligatoire)
     * @return ResponseEntity contenant un Set d’emails, ou 404 si aucun email trouvé
     */
    @GetMapping
    public ResponseEntity<Set<String>> getCommunityEmailsByCity(@RequestParam("city") String city) {
        logger.debug("Requête de récupération des emails pour la ville : {}", city);

        Set<String> emails = personRepository.findAll().stream()
                .filter(p -> p.getCity() != null && p.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .filter(email -> email != null && !email.isEmpty())
                .collect(Collectors.toSet());

        if (emails.isEmpty()) {
            logger.info("Aucun email trouvé pour la ville : {}", city);
            return ResponseEntity.notFound().build();
        }

        logger.info("{} emails trouvés pour la ville {}", emails.size(), city);
        return ResponseEntity.ok(emails);
    }
}


