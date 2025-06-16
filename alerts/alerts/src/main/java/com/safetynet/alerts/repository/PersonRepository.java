package com.safetynet.alerts.repository;

import org.springframework.stereotype.Repository;

import com.safetynet.alerts.model.Person;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PersonRepository {

    private final Map<String, Person> personMap = new HashMap<>();

    /**
     * Sauvegarde ou met à jour une personne en mémoire.
     * <p>
     * Si une personne existe déjà pour la même clé “prenom_nom”, elle est remplacée.
     *
     * @param person l’objet Person à enregistrer (ne doit pas être null)
     */
    public void save(Person person) {
        String key = generateKey(person.getFirstName(), person.getLastName());
        personMap.put(key, person);
    }

    /**
     * Recherche une personne par prénom et nom.
     *
     * @param firstName le prénom de la personne recherchée (non null)
     * @param lastName  le nom de la personne recherchée (non null)
     * @return la Person correspondante, ou null si aucune trouvée
     */
    public Person findById(String firstName, String lastName) {
        return personMap.get(generateKey(firstName, lastName));
    }

    /**
     * Supprime une personne identifiée par prénom et nom.
     * <p>
     * Ne fait rien si la personne n’existe pas.
     *
     * @param firstName le prénom de la personne à supprimer (non null)
     * @param lastName  le nom de la personne à supprimer (non null)
     */
    public void delete(String firstName, String lastName) {
        personMap.remove(generateKey(firstName, lastName));
    }

    /**
     * Vérifie si une personne existe pour le prénom et nom donnés.
     *
     * @param firstName le prénom à vérifier (non null)
     * @param lastName  le nom à vérifier (non null)
     * @return true si une Person avec cette clé existe, false sinon
     */
    public boolean exists(String firstName, String lastName) {
        return personMap.containsKey(generateKey(firstName, lastName));
    }

    /**
     * Génère la clé interne utilisée pour stocker et retrouver
     * une Person à partir du prénom et du nom.
     *
     * @param firstName le prénom de la personne (non null)
     * @param lastName  le nom de la personne (non null)
     * @return clé unique au format "prenom_nom" en minuscules
     */
    private String generateKey(String firstName, String lastName) {
        return firstName.toLowerCase() + "_" + lastName.toLowerCase();
    }

    /**
     * Retourne la liste de toutes les personnes stockées.
     *
     * @return liste non null (potentiellement vide) de toutes les Person en mémoire
     */
    public List<Person> findAll() {
        return new ArrayList<>(personMap.values());
    }

    /**
     * Supprime toutes les personnes stockées en mémoire.
     * <p>
     * Utilisé notamment pour réinitialiser l’état lors des tests.
     */
    public void clear() {
        personMap.clear();
    }

    /**
     * Recherche toutes les personnes habitant à l’adresse donnée.
     *
     * @param address l’adresse à interroger (non null)
     * @return liste des Person dont le champ address correspond (ignore la casse)
     */
    public List<Person> findByAddress(String address) {
        return personMap.values().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .collect(Collectors.toList());
    }
}
