package com.baeldung.eclipselink.springdata.repo;


import DirtiesContext.ClassMode;
import com.baeldung.eclipselink.springdata.model.Person;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class PersonsRepositoryIntegrationTest {
    @Autowired
    private PersonsRepository personsRepository;

    @Test
    public void givenPerson_whenSave_thenAddOnePersonToDB() {
        personsRepository.save(new Person());
        MatcherAssert.assertThat(personsRepository.findAll().spliterator().getExactSizeIfKnown(), IsEqual.equalTo(1L));
    }

    @Test
    public void givenPersons_whenSearch_thenFindOk() {
        Person person1 = new Person();
        person1.setFirstName("Adam");
        Person person2 = new Person();
        person2.setFirstName("Dave");
        personsRepository.save(person1);
        personsRepository.save(person2);
        Person foundPerson = personsRepository.findByFirstName("Adam");
        MatcherAssert.assertThat(foundPerson.getFirstName(), IsEqual.equalTo("Adam"));
        MatcherAssert.assertThat(foundPerson.getId(), IsNull.notNullValue());
    }
}

