package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PersonRepository {

    private final Map<String, Person> personMap = new HashMap<>();

    public void save(Person person) {
        String key = generateKey(person.getFirstName(), person.getLastName());
        personMap.put(key, person);
    }

    public Person findById(String firstName, String lastName) {
        return personMap.get(generateKey(firstName, lastName));
    }

    public void delete(String firstName, String lastName) {
        personMap.remove(generateKey(firstName, lastName));
    }

    public boolean exists(String firstName, String lastName) {
        return personMap.containsKey(generateKey(firstName, lastName));
    }

    private String generateKey(String firstName, String lastName) {
        return firstName.toLowerCase() + "_" + lastName.toLowerCase();
    }

    public List<Person> findAll() {
        return new ArrayList<>(personMap.values());
    }

    public void clear() {
        personMap.clear();
    }    
}
