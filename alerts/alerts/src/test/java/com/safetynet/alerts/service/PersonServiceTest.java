package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository repository;

    @Mock
    private DataPersistenceService persistenceService;

    @InjectMocks
    private PersonService service;

    private Person p(String fn, String ln) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        return p;
    }

    @Test
    @DisplayName("addPerson: nouvel utilisateur → save + updateDataFile")
    void addPerson_success() {
        Person person = p("A", "One");
        when(repository.exists("A", "One")).thenReturn(false);

        service.addPerson(person);

        verify(repository).save(person);
        verify(persistenceService).updateDataFile();
    }

    @Test
    @DisplayName("addPerson: personne existe → IllegalArgumentException")
    void addPerson_alreadyExists() {
        Person person = p("A", "One");
        when(repository.exists("A", "One")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.addPerson(person)
        );
        assertEquals("Cette personne existe déjà.", ex.getMessage());

        verify(repository, never()).save(any());
        verify(persistenceService, never()).updateDataFile();
    }

    @Test
    @DisplayName("updatePerson: existant → save + updateDataFile")
    void updatePerson_success() {
        Person person = p("B", "Two");
        when(repository.exists("B", "Two")).thenReturn(true);

        service.updatePerson(person);

        verify(repository).save(person);
        verify(persistenceService).updateDataFile();
    }

    @Test
    @DisplayName("updatePerson: non trouvé → IllegalArgumentException")
    void updatePerson_notFound() {
        Person person = p("B", "Two");
        when(repository.exists("B", "Two")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.updatePerson(person)
        );
        assertEquals("Personne non trouvée.", ex.getMessage());

        verify(repository, never()).save(any());
        verify(persistenceService, never()).updateDataFile();
    }

    @Test
    @DisplayName("deletePerson: existant → delete + updateDataFile")
    void deletePerson_success() {
        when(repository.exists("C", "Three")).thenReturn(true);

        service.deletePerson("C", "Three");

        verify(repository).delete("C", "Three");
        verify(persistenceService).updateDataFile();
    }

    @Test
    @DisplayName("deletePerson: non trouvé → IllegalArgumentException")
    void deletePerson_notFound() {
        when(repository.exists("C", "Three")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.deletePerson("C", "Three")
        );
        assertEquals("Impossible de supprimer : personne non trouvée.", ex.getMessage());

        verify(repository, never()).delete(anyString(), anyString());
        verify(persistenceService, never()).updateDataFile();
    }

    @Test
    @DisplayName("getAllPersons & findByAddress délèguent au repository")
    void listAndFind() {
        Person a = p("X","Y");
        when(repository.findAll()).thenReturn(Collections.singletonList(a));
        when(repository.findByAddress("addr")).thenReturn(Arrays.asList(a));

        List<Person> all = service.getAllPersons();
        List<Person> byAddr = service.findByAddress("addr");

        assertEquals(1, all.size());
        assertEquals(a, all.get(0));
        assertEquals(1, byAddr.size());
        assertEquals(a, byAddr.get(0));
    }
}
