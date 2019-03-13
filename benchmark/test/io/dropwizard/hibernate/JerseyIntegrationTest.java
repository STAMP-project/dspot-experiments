package io.dropwizard.hibernate;


import HttpHeaders.CONTENT_TYPE;
import MediaType.APPLICATION_JSON;
import Response.Status.BAD_REQUEST;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.logging.BootstrapLogging;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hibernate.SessionFactory;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;


public class JerseyIntegrationTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    public static class PersonDAO extends AbstractDAO<Person> {
        public PersonDAO(SessionFactory sessionFactory) {
            super(sessionFactory);
        }

        public Optional<Person> findByName(String name) {
            return Optional.ofNullable(get(name));
        }

        @Override
        public Person persist(Person entity) {
            return super.persist(entity);
        }
    }

    @Path("/people/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public static class PersonResource {
        private final JerseyIntegrationTest.PersonDAO dao;

        public PersonResource(JerseyIntegrationTest.PersonDAO dao) {
            this.dao = dao;
        }

        @GET
        @UnitOfWork(readOnly = true)
        public Optional<Person> find(@PathParam("name")
        String name) {
            return dao.findByName(name);
        }

        @PUT
        @UnitOfWork
        public void save(Person person) {
            dao.persist(person);
        }
    }

    @Nullable
    private SessionFactory sessionFactory;

    @Test
    public void findsExistingData() throws Exception {
        final Person coda = target("/people/Coda").request(APPLICATION_JSON).get(Person.class);
        assertThat(coda.getName()).isEqualTo("Coda");
        assertThat(coda.getEmail()).isEqualTo("coda@example.com");
        assertThat(coda.getBirthday()).isEqualTo(new org.joda.time.DateTime(1979, 1, 2, 0, 22, DateTimeZone.UTC));
    }

    @Test
    public void doesNotFindMissingData() throws Exception {
        try {
            target("/people/Poof").request(APPLICATION_JSON).get(Person.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(404);
        }
    }

    @Test
    public void createsNewData() throws Exception {
        final Person person = new Person();
        person.setName("Hank");
        person.setEmail("hank@example.com");
        person.setBirthday(new org.joda.time.DateTime(1971, 3, 14, 19, 12, DateTimeZone.UTC));
        target("/people/Hank").request().put(Entity.entity(person, APPLICATION_JSON));
        final Person hank = target("/people/Hank").request(APPLICATION_JSON).get(Person.class);
        assertThat(hank.getName()).isEqualTo("Hank");
        assertThat(hank.getEmail()).isEqualTo("hank@example.com");
        assertThat(hank.getBirthday()).isEqualTo(person.getBirthday());
    }

    @Test
    public void testSqlExceptionIsHandled() throws Exception {
        final Person person = new Person();
        person.setName("Jeff");
        person.setEmail("jeff.hammersmith@targetprocessinc.com");
        person.setBirthday(new org.joda.time.DateTime(1984, 2, 11, 0, 0, DateTimeZone.UTC));
        final Response response = target("/people/Jeff").request().put(Entity.entity(person, APPLICATION_JSON));
        assertThat(response.getStatusInfo()).isEqualTo(BAD_REQUEST);
        assertThat(response.getHeaderString(CONTENT_TYPE)).isEqualTo(APPLICATION_JSON);
        assertThat(response.readEntity(ErrorMessage.class).getMessage()).isEqualTo("Wrong email");
    }
}

