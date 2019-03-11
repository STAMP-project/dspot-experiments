package org.baeldung.spring.data.couchbase.service;


import com.couchbase.client.java.document.json.JsonObject;
import java.util.List;
import org.baeldung.spring.data.couchbase.IntegrationTest;
import org.baeldung.spring.data.couchbase.model.Person;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public abstract class PersonServiceLiveTest extends IntegrationTest {
    static final String typeField = "_class";

    static final String john = "John";

    static final String smith = "Smith";

    static final String johnSmithId = (("person:" + (PersonServiceLiveTest.john)) + ":") + (PersonServiceLiveTest.smith);

    static final Person johnSmith = new Person(PersonServiceLiveTest.johnSmithId, PersonServiceLiveTest.john, PersonServiceLiveTest.smith);

    static final JsonObject jsonJohnSmith = JsonObject.empty().put(PersonServiceLiveTest.typeField, Person.class.getName()).put("firstName", PersonServiceLiveTest.john).put("lastName", PersonServiceLiveTest.smith).put("created", DateTime.now().getMillis());

    static final String foo = "Foo";

    static final String bar = "Bar";

    static final String foobarId = (("person:" + (PersonServiceLiveTest.foo)) + ":") + (PersonServiceLiveTest.bar);

    static final Person foobar = new Person(PersonServiceLiveTest.foobarId, PersonServiceLiveTest.foo, PersonServiceLiveTest.bar);

    static final JsonObject jsonFooBar = JsonObject.empty().put(PersonServiceLiveTest.typeField, Person.class.getName()).put("firstName", PersonServiceLiveTest.foo).put("lastName", PersonServiceLiveTest.bar).put("created", DateTime.now().getMillis());

    PersonService personService;

    @Test
    public void whenFindingPersonByJohnSmithId_thenReturnsJohnSmith() {
        final Person actualPerson = personService.findOne(PersonServiceLiveTest.johnSmithId);
        Assert.assertNotNull(actualPerson);
        Assert.assertNotNull(actualPerson.getCreated());
        Assert.assertEquals(PersonServiceLiveTest.johnSmith, actualPerson);
    }

    @Test
    public void whenFindingAllPersons_thenReturnsTwoOrMorePersonsIncludingJohnSmithAndFooBar() {
        final List<Person> resultList = personService.findAll();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(resultContains(resultList, PersonServiceLiveTest.johnSmith));
        Assert.assertTrue(resultContains(resultList, PersonServiceLiveTest.foobar));
        Assert.assertTrue(((resultList.size()) >= 2));
    }

    @Test
    public void whenFindingByFirstNameJohn_thenReturnsOnlyPersonsNamedJohn() {
        final String expectedFirstName = PersonServiceLiveTest.john;
        final List<Person> resultList = personService.findByFirstName(expectedFirstName);
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(allResultsContainExpectedFirstName(resultList, expectedFirstName));
    }

    @Test
    public void whenFindingByLastNameSmith_thenReturnsOnlyPersonsNamedSmith() {
        final String expectedLastName = PersonServiceLiveTest.smith;
        final List<Person> resultList = personService.findByLastName(expectedLastName);
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertTrue(allResultsContainExpectedLastName(resultList, expectedLastName));
    }
}

