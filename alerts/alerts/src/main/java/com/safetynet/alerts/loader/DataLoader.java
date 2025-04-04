package com.safetynet.alerts.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.util.DataWrapper;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    private PersonRepository personRepository;

    @PostConstruct
    public void loadData() {
        try {
            personRepository.clear(); // Nettoyage
    
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();
            DataWrapper dataWrapper = mapper.readValue(inputStream, DataWrapper.class);
    
            List<Person> persons = dataWrapper.getPersons();
            System.out.println("Chargement de " + persons.size() + " personnes");

            for (Person p : persons) {
                personRepository.save(p);
            }
    
            System.out.println("Données chargées (data.json)");
    
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des données : " + e.getMessage());
        }
    }
    
}
