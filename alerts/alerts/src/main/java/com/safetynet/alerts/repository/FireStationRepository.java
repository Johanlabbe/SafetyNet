package com.safetynet.alerts.repository;

import org.springframework.stereotype.Repository;

import com.safetynet.alerts.model.FireStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FireStationRepository {

    private final Map<String, FireStation> fireStationMap = new HashMap<>();

    /**
     * Sauvegarde ou met à jour un FireStation en mémoire.
     * <p>
     * Si une station existe déjà pour l'adresse fournie, elle est remplacée.
     *
     * @param fireStation l'objet FireStation à enregistrer (ne doit pas être null)
     */
    public void save(FireStation fireStation) {
        fireStationMap.put(fireStation.getAddress(), fireStation);
    }

    /**
     * Retourne la liste de toutes les FireStation stockées.
     *
     * @return liste non null (potentiellement vide) de FireStation
     */
    public List<FireStation> findAll() {
        return new ArrayList<>(fireStationMap.values());
    }

    /**
     * Supprime toutes les entrées en mémoire.
     * <p>
     * Utilisé typiquement pour réinitialiser l'état lors des tests.
     */
    public void clear() {
        fireStationMap.clear();
    }
}
