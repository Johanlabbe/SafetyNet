package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository repository;

    @Autowired
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public void addPerson(Person person) {
        if (repository.exists(person.getFirstName(), person.getLastName())) {
            throw new IllegalArgumentException("Cette personne existe déjà.");
        }
        repository.save(person);
    }

    public void updatePerson(Person person) {
        if (!repository.exists(person.getFirstName(), person.getLastName())) {
            throw new IllegalArgumentException("Personne non trouvée.");
        }
        repository.save(person);
    }

    public void deletePerson(String firstName, String lastName) {
        if (!repository.exists(firstName, lastName)) {
            throw new IllegalArgumentException("Impossible de supprimer : personne non trouvée.");
        }
        repository.delete(firstName, lastName);
    }
}
