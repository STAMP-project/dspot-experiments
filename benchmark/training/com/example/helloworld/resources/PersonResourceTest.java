package com.example.helloworld.resources;


import Response.Status.NOT_FOUND;
import com.example.helloworld.core.Person;
import com.example.helloworld.db.PersonDAO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;


/**
 * Unit tests for {@link PersonResource}.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class PersonResourceTest {
    private static final PersonDAO DAO = Mockito.mock(PersonDAO.class);

    public static final ResourceExtension RULE = ResourceExtension.builder().addResource(new PersonResource(PersonResourceTest.DAO)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).build();

    private Person person;

    @Test
    public void getPersonSuccess() {
        Mockito.when(PersonResourceTest.DAO.findById(1L)).thenReturn(Optional.of(person));
        Person found = PersonResourceTest.RULE.target("/people/1").request().get(Person.class);
        assertThat(found.getId()).isEqualTo(person.getId());
        Mockito.verify(PersonResourceTest.DAO).findById(1L);
    }

    @Test
    public void getPersonNotFound() {
        Mockito.when(PersonResourceTest.DAO.findById(2L)).thenReturn(Optional.empty());
        final Response response = PersonResourceTest.RULE.target("/people/2").request().get();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(NOT_FOUND.getStatusCode());
        Mockito.verify(PersonResourceTest.DAO).findById(2L);
    }
}

