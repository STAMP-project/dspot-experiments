package org.baeldung.spring.data.couchbase2b.service;


import com.couchbase.client.java.document.json.JsonObject;
import java.util.List;
import org.baeldung.spring.data.couchbase.model.Person;
import org.baeldung.spring.data.couchbase2b.MultiBucketLiveTest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PersonServiceImplLiveTest extends MultiBucketLiveTest {
    static final String typeField = "_class";

    static final String john = "John";

    static final String smith = "Smith";

    static final String johnSmithId = (("person:" + (PersonServiceImplLiveTest.john)) + ":") + (PersonServiceImplLiveTest.smith);

    static final Person johnSmith = new Person(PersonServiceImplLiveTest.johnSmithId, PersonServiceImplLiveTest.john, PersonServiceImplLiveTest.smith);

    static final JsonObject jsonJohnSmith = JsonObject.empty().put(PersonServiceImplLiveTest.typeField, Person.class.getName()).put("firstName", PersonServiceImplLiveTest.john).put("lastName", PersonServiceImplLiveTest.smith).put("created", DateTime.now().getMillis());

    static final String foo = "Foo";

    static final String bar = "Bar";

    static final String foobarId = (("person:" + (PersonServiceImplLiveTest.foo)) + ":") + (PersonServiceImplLiveTest.bar);

    static final Person foobar = new Person(PersonServiceImplLiveTest.foobarId, PersonServiceImplLiveTest.foo, PersonServiceImplLiveTest.bar);

    static final JsonObject jsonFooBar = JsonObject.empty().put(PersonServiceImplLiveTest.typeField, Person.class.getName()).put("firstName", PersonServiceImplLiveTest.foo).put("lastName", PersonServiceImplLiveTest.bar).put("created", DateTime.now().getMillis());

    @Autowired
    private PersonServiceImpl personService;

    @Test
    public void whenFindingPersonByJohnSmithId_thenReturnsJohnSmith() {
        final Person actualPerson = personService.findOne(PersonServiceImplLiveTest.johnSmithId);
        Assert.assertNotNull(actualPerson);
        Assert.assertNotNull(actualPerson.getCreated());
        Assert.assertEquals(PersonServiceImplLiveTest.johnSmith, actualPerson);
    }

    @Test
    public void whenFindingAllPersons_thenReturnsTwoOrMorePersonsIncludingJohnSmithAndFooBar() {
        final List<Person> resultList = personService.findAll();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(resultContains(resultList, PersonServiceImplLiveTest.johnSmith));
        Assert.assertTrue(resultContains(resultList, PersonServiceImplLiveTest.foobar));
        Assert.assertTrue(((resultList.size()) >= 2));
    }

    @Test
    public void whenFindingByFirstNameJohn_thenReturnsOnlyPersonsNamedJohn() {
        final String expectedFirstName = PersonServiceImplLiveTest.john;
        final List<Person> resultList = personService.findByFirstName(expectedFirstName);
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(allResultsContainExpectedFirstName(resultList, expectedFirstName));
    }

    @Test
    public void whenFindingByLastNameSmith_thenReturnsOnlyPersonsNamedSmith() {
        final String expectedLastName = PersonServiceImplLiveTest.smith;
        final List<Person> resultList = personService.findByLastName(expectedLastName);
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(allResultsContainExpectedLastName(resultList, expectedLastName));
    }
}

