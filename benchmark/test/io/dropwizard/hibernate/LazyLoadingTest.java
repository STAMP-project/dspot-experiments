package io.dropwizard.hibernate;


import FlushMode.COMMIT;
import HttpHeaders.CONTENT_TYPE;
import MediaType.APPLICATION_JSON;
import Response.Status.BAD_REQUEST;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.io.dropwizard.Application;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.util.Strings;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LazyLoadingTest {
    public static class TestConfiguration extends Configuration {
        DataSourceFactory dataSource = new DataSourceFactory();

        TestConfiguration(@JsonProperty("dataSource")
        DataSourceFactory dataSource) {
            this.dataSource = dataSource;
        }
    }

    public static class TestApplication extends io.dropwizard.Application<LazyLoadingTest.TestConfiguration> {
        final HibernateBundle<LazyLoadingTest.TestConfiguration> hibernate = new HibernateBundle<LazyLoadingTest.TestConfiguration>(Arrays.asList(Person.class, Dog.class), new SessionFactoryFactory()) {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(LazyLoadingTest.TestConfiguration configuration) {
                return configuration.dataSource;
            }
        };

        @Override
        public void initialize(Bootstrap<LazyLoadingTest.TestConfiguration> bootstrap) {
            bootstrap.addBundle(hibernate);
        }

        @Override
        public void run(LazyLoadingTest.TestConfiguration configuration, Environment environment) throws Exception {
            final SessionFactory sessionFactory = hibernate.getSessionFactory();
            initDatabase(sessionFactory);
            environment.jersey().register(new UnitOfWorkApplicationListener("hr-db", sessionFactory));
            environment.jersey().register(new LazyLoadingTest.DogResource(new LazyLoadingTest.DogDAO(sessionFactory)));
            environment.jersey().register(new PersistenceExceptionMapper());
            environment.jersey().register(new LazyLoadingTest.ConstraintViolationExceptionMapper());
        }

        private void initDatabase(SessionFactory sessionFactory) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.createNativeQuery("CREATE TABLE people (name varchar(100) primary key, email varchar(16), birthday timestamp with time zone)").executeUpdate();
                session.createNativeQuery("INSERT INTO people VALUES ('Coda', 'coda@example.com', '1979-01-02 00:22:00+0:00')").executeUpdate();
                session.createNativeQuery("CREATE TABLE dogs (name varchar(100) primary key, owner varchar(100), CONSTRAINT fk_owner FOREIGN KEY (owner) REFERENCES people(name))").executeUpdate();
                session.createNativeQuery("INSERT INTO dogs VALUES ('Raf', 'Coda')").executeUpdate();
                transaction.commit();
            }
        }
    }

    public static class TestApplicationWithDisabledLazyLoading extends LazyLoadingTest.TestApplication {
        @Override
        public void initialize(Bootstrap<LazyLoadingTest.TestConfiguration> bootstrap) {
            hibernate.setLazyLoadingEnabled(false);
            bootstrap.addBundle(hibernate);
        }
    }

    public static class DogDAO extends AbstractDAO<Dog> {
        DogDAO(SessionFactory sessionFactory) {
            super(sessionFactory);
        }

        Optional<Dog> findByName(String name) {
            return Optional.ofNullable(get(name));
        }

        Dog create(Dog dog) throws HibernateException {
            currentSession().setHibernateFlushMode(COMMIT);
            currentSession().save(Objects.requireNonNull(dog));
            return dog;
        }
    }

    @Path("/dogs/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public static class DogResource {
        private final LazyLoadingTest.DogDAO dao;

        DogResource(LazyLoadingTest.DogDAO dao) {
            this.dao = dao;
        }

        @GET
        @UnitOfWork(readOnly = true)
        public Optional<Dog> find(@PathParam("name")
        String name) {
            return dao.findByName(name);
        }

        @PUT
        @UnitOfWork
        public void create(Dog dog) {
            dao.create(dog);
        }
    }

    public static class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException e) {
            return Response.status(BAD_REQUEST).entity(new ErrorMessage(BAD_REQUEST.getStatusCode(), Strings.nullToEmpty(e.getCause().getMessage()))).build();
        }
    }

    private DropwizardTestSupport<?> dropwizardTestSupport = Mockito.mock(DropwizardTestSupport.class);

    private Client client = new JerseyClientBuilder().build();

    @Test
    public void serialisesLazyObjectWhenEnabled() throws Exception {
        setup(LazyLoadingTest.TestApplication.class);
        final Dog raf = client.target(((getUrlPrefix()) + "/dogs/Raf")).request(APPLICATION_JSON).get(Dog.class);
        assertThat(raf.getName()).isEqualTo("Raf");
        assertThat(raf.getOwner()).isNotNull();
        assertThat(Objects.requireNonNull(raf.getOwner()).getName()).isEqualTo("Coda");
    }

    @Test
    public void sendsNullWhenDisabled() throws Exception {
        setup(LazyLoadingTest.TestApplicationWithDisabledLazyLoading.class);
        final Dog raf = client.target(((getUrlPrefix()) + "/dogs/Raf")).request(APPLICATION_JSON).get(Dog.class);
        assertThat(raf.getName()).isEqualTo("Raf");
        assertThat(raf.getOwner()).isNull();
    }

    @Test
    public void returnsErrorsWhenEnabled() throws Exception {
        setup(LazyLoadingTest.TestApplication.class);
        final Dog raf = new Dog();
        raf.setName("Raf");
        // Raf already exists so this should cause a primary key constraint violation
        final Response response = client.target(((getUrlPrefix()) + "/dogs/Raf")).request().put(Entity.entity(raf, APPLICATION_JSON));
        assertThat(response.getStatusInfo()).isEqualTo(BAD_REQUEST);
        assertThat(response.getHeaderString(CONTENT_TYPE)).isEqualTo(APPLICATION_JSON);
        assertThat(response.readEntity(ErrorMessage.class).getMessage()).contains("unique constraint", "table: DOGS");
    }
}

