package com.example.helloworld.resources;


import MediaType.APPLICATION_JSON_TYPE;
import Response.Status.OK;
import com.example.helloworld.core.Person;
import com.example.helloworld.db.PersonDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link PeopleResource}.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class PeopleResourceTest {
    private static final PersonDAO PERSON_DAO = Mockito.mock(PersonDAO.class);

    public static final ResourceExtension RESOURCES = ResourceExtension.builder().addResource(new PeopleResource(PeopleResourceTest.PERSON_DAO)).build();

    private ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    private Person person;

    @Test
    public void createPerson() throws JsonProcessingException {
        Mockito.when(PeopleResourceTest.PERSON_DAO.create(ArgumentMatchers.any(Person.class))).thenReturn(person);
        final Response response = PeopleResourceTest.RESOURCES.target("/people").request(APPLICATION_JSON_TYPE).post(Entity.entity(person, APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(OK);
        Mockito.verify(PeopleResourceTest.PERSON_DAO).create(personCaptor.capture());
        assertThat(personCaptor.getValue()).isEqualTo(person);
    }

    @Test
    public void listPeople() throws Exception {
        final List<Person> people = Collections.singletonList(person);
        Mockito.when(PeopleResourceTest.PERSON_DAO.findAll()).thenReturn(people);
        final List<Person> response = PeopleResourceTest.RESOURCES.target("/people").request().get(new javax.ws.rs.core.GenericType<List<Person>>() {});
        Mockito.verify(PeopleResourceTest.PERSON_DAO).findAll();
        assertThat(response).containsAll(people);
    }
}

