package com.safetynet.alerts.repository;

import org.springframework.stereotype.Repository;

import com.safetynet.alerts.model.MedicalRecord;

import java.util.*;

@Repository
public class MedicalRecordRepository {

    private final Map<String, MedicalRecord> medicalRecordMap = new HashMap<>();

    /**
     * Sauvegarde ou met à jour un dossier médical.
     * <p>
     * Si un dossier existe déjà pour la même personne (même clé “prenom_nom”), il est remplacé.
     *
     * @param mr l’objet MedicalRecord à enregistrer (ne doit pas être null)
     */
    public void save(MedicalRecord mr) {
        medicalRecordMap.put(generateKey(mr.getFirstName(), mr.getLastName()), mr);
    }

    /**
     * Retourne la liste de tous les dossiers médicaux stockés.
     *
     * @return liste non null (potentiellement vide) de tous les MedicalRecord en mémoire
     */
    public List<MedicalRecord> findAll() {
        return new ArrayList<>(medicalRecordMap.values());
    }

    /**
     * Supprime l’ensemble des dossiers médicaux stockés en mémoire.
     * <p>
     * Utilisé notamment pour réinitialiser l’état lors des tests.
     */
    public void clear() {
        medicalRecordMap.clear();
    }

    /**
     * Génère la clé interne utilisée pour stocker et retrouver
     * un MedicalRecord à partir du prénom et du nom.
     *
     * @param firstName le prénom de la personne (non null)
     * @param lastName  le nom de la personne (non null)
     * @return clé unique au format "prenom_nom" en minuscules
     */
    private String generateKey(String firstName, String lastName) {
        return firstName.toLowerCase() + "_" + lastName.toLowerCase();
    }

    /**
     * Recherche un MedicalRecord par prénom et nom.
     *
     * @param firstName le prénom de la personne recherchée (non null)
     * @param lastName  le nom de la personne recherchée (non null)
     * @return le MedicalRecord correspondant, ou null si aucun dossier trouvé
     */
    public MedicalRecord findByName(String firstName, String lastName) {
        return medicalRecordMap.get(
            (firstName + "_" + lastName).toLowerCase()
        );
    }    
}
