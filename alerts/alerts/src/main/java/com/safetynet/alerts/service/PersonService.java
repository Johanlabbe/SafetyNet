package com.safetynet.alerts.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;



@Service
public class PersonService {

    private final PersonRepository repository;
    private final DataPersistenceService persistenceService;

    @Autowired
    public PersonService(PersonRepository repository, DataPersistenceService persistenceService) {
        this.repository = repository;
        this.persistenceService = persistenceService;
    }

    public void addPerson(Person person) {
        if (repository.exists(person.getFirstName(), person.getLastName())) {
            throw new IllegalArgumentException("Cette personne existe déjà.");
        }
        repository.save(person);
        persistenceService.updateDataFile();
    }

    public void updatePerson(Person person) {
        if (!repository.exists(person.getFirstName(), person.getLastName())) {
            throw new IllegalArgumentException("Personne non trouvée.");
        }
        repository.save(person);
        persistenceService.updateDataFile();
    }

    public void deletePerson(String firstName, String lastName) {
        if (!repository.exists(firstName, lastName)) {
            throw new IllegalArgumentException("Impossible de supprimer : personne non trouvée.");
        }
        repository.delete(firstName, lastName);
        persistenceService.updateDataFile();
    }

    public List<Person> getAllPersons() {
        return repository.findAll();
    }

    public List<Person> findByAddress(String address) {
        return repository.findByAddress(address);
    }
}
