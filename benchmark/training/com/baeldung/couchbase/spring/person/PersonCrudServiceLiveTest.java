package com.baeldung.couchbase.spring.person;


import com.baeldung.couchbase.spring.IntegrationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PersonCrudServiceLiveTest extends IntegrationTest {
    private static final String CLARK_KENT = "Clark Kent";

    private static final String SMALLVILLE = "Smallville";

    private static final String CLARK_KENT_ID = "Person:ClarkKent";

    private Person clarkKent;

    @Autowired
    private PersonCrudService personService;

    @Test
    public final void givenRandomPerson_whenCreate_thenPersonPersisted() {
        Person person = randomPerson();
        personService.create(person);
        String id = person.getId();
        Assert.assertNotNull(personService.read(id));
    }

    @Test
    public final void givenClarkKentId_whenRead_thenReturnsClarkKent() {
        Person person = personService.read(PersonCrudServiceLiveTest.CLARK_KENT_ID);
        Assert.assertNotNull(person);
    }

    @Test
    public final void givenNewHometown_whenUpdate_thenNewHometownPersisted() {
        Person expected = randomPerson();
        personService.create(expected);
        String updatedHomeTown = RandomStringUtils.randomAlphabetic(12);
        expected.setHomeTown(updatedHomeTown);
        personService.update(expected);
        Person actual = personService.read(expected.getId());
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getHomeTown(), actual.getHomeTown());
    }

    @Test
    public final void givenRandomPerson_whenDelete_thenPersonNotInBucket() {
        Person person = randomPerson();
        personService.create(person);
        String id = person.getId();
        personService.delete(id);
        Assert.assertNull(personService.read(id));
    }
}

