package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRepositoryTest {

    private PersonRepository repo;

    private Person p(String fn, String ln, String address, String city, String email) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        p.setAddress(address);
        p.setCity(city);
        p.setEmail(email);
        return p;
    }

    @BeforeEach
    void setUp() {
        repo = new PersonRepository();
    }

    @Test
    @DisplayName("save & findById & exists & delete")
    void crudOperations() {
        Person p1 = p("Jean","Dupont","rue A","Paris","jean@p.fr");
        assertThat(repo.exists("Jean","Dupont")).isFalse();

        repo.save(p1);
        assertThat(repo.exists("Jean","Dupont")).isTrue();

        Person fetched = repo.findById("Jean","Dupont");
        assertThat(fetched).isNotNull()
                           .extracting(Person::getEmail)
                           .isEqualTo("jean@p.fr");

        repo.delete("Jean","Dupont");
        assertThat(repo.exists("Jean","Dupont")).isFalse();
        assertThat(repo.findById("Jean","Dupont")).isNull();
    }

    @Test
    @DisplayName("findAll renvoie tout")
    void findAll() {
        Person p1 = p("A","One","addr1","C","e1");
        Person p2 = p("B","Two","addr2","C","e2");
        repo.save(p1);
        repo.save(p2);

        List<Person> all = repo.findAll();
        assertThat(all).hasSize(2).containsExactlyInAnyOrder(p1, p2);
    }

    @Test
    @DisplayName("findByAddress ignore la casse")
    void findByAddress() {
        Person p1 = p("X","Y","Rue Test","Z","x@y.z");
        Person p2 = p("M","N","rUE tEsT","Z","m@n.z");
        Person p3 = p("A","B","Autre","Z","a@b.z");
        repo.save(p1);
        repo.save(p2);
        repo.save(p3);

        List<Person> matched = repo.findByAddress("RUE TEST");
        assertThat(matched)
            .hasSize(2)
            .extracting(Person::getFirstName)
            .containsExactlyInAnyOrder("X","M");
    }

    @Test
    @DisplayName("clear vide entièrement la map")
    void clear() {
        repo.save(p("T","U","a","B","t@u.z"));
        assertThat(repo.findAll()).isNotEmpty();

        repo.clear();
        assertThat(repo.findAll()).isEmpty();
    }
}
