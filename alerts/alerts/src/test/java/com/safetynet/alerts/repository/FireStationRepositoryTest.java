package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.FireStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FireStationRepositoryTest {

    private FireStationRepository repo;

    private FireStation fs(String addr, String num) {
        FireStation f = new FireStation();
        f.setAddress(addr);
        f.setStation(num);
        return f;
    }

    @BeforeEach
    void setUp() {
        repo = new FireStationRepository();
    }

    @Test
    @DisplayName("save & findAll & clear")
    void crudOperations() {
        assertThat(repo.findAll()).isEmpty();

        FireStation f1 = fs("A","1");
        FireStation f2 = fs("B","2");
        repo.save(f1);
        repo.save(f2);

        List<FireStation> all = repo.findAll();
        assertThat(all).hasSize(2).containsExactlyInAnyOrder(f1, f2);

        repo.clear();
        assertThat(repo.findAll()).isEmpty();
    }
}
