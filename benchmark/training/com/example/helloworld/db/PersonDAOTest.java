package com.example.helloworld.db;


import com.example.helloworld.core.Person;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class PersonDAOTest {
    public DAOTestExtension daoTestRule = DAOTestExtension.newBuilder().addEntityClass(Person.class).build();

    private PersonDAO personDAO;

    @Test
    public void createPerson() {
        final Person jeff = daoTestRule.inTransaction(() -> personDAO.create(new Person("Jeff", "The plumber")));
        assertThat(jeff.getId()).isGreaterThan(0);
        assertThat(jeff.getFullName()).isEqualTo("Jeff");
        assertThat(jeff.getJobTitle()).isEqualTo("The plumber");
        assertThat(personDAO.findById(jeff.getId())).isEqualTo(Optional.of(jeff));
    }

    @Test
    public void findAll() {
        daoTestRule.inTransaction(() -> {
            personDAO.create(new Person("Jeff", "The plumber"));
            personDAO.create(new Person("Jim", "The cook"));
            personDAO.create(new Person("Randy", "The watchman"));
        });
        final List<Person> persons = personDAO.findAll();
        assertThat(persons).extracting("fullName").containsOnly("Jeff", "Jim", "Randy");
        assertThat(persons).extracting("jobTitle").containsOnly("The plumber", "The cook", "The watchman");
    }

    @Test
    public void handlesNullFullName() {
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> daoTestRule.inTransaction(() -> personDAO.create(new Person(null, "The null"))));
    }
}

