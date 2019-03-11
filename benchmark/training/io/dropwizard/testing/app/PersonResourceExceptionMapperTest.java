package io.dropwizard.testing.app;


import MediaType.TEXT_PLAIN;
import Response.Status.BAD_REQUEST;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.util.Strings;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class PersonResourceExceptionMapperTest {
    private static final PeopleStore PEOPLE_STORE = Mockito.mock(PeopleStore.class);

    private static final ObjectMapper OBJECT_MAPPER = Jackson.newObjectMapper().registerModule(new GuavaModule());

    @SuppressWarnings("deprecation")
    @ClassRule
    public static final ResourceTestRule RESOURCES = ResourceTestRule.builder().addResource(new PersonResource(PersonResourceExceptionMapperTest.PEOPLE_STORE)).setRegisterDefaultExceptionMappers(false).addProvider(new PersonResourceExceptionMapperTest.MyJerseyExceptionMapper()).addProvider(new PersonResourceExceptionMapperTest.GenericExceptionMapper()).setMapper(PersonResourceExceptionMapperTest.OBJECT_MAPPER).build();

    @Test
    public void testDefaultConstraintViolation() {
        assertThat(PersonResourceExceptionMapperTest.RESOURCES.target("/person/blah/index").queryParam("ind", (-1)).request().get().readEntity(String.class)).isEqualTo("Invalid data");
    }

    @Test
    public void testDefaultJsonProcessingMapper() {
        assertThat(PersonResourceExceptionMapperTest.RESOURCES.target("/person/blah/runtime-exception").request().post(Entity.json("{ \"he: \"ho\"}")).readEntity(String.class)).startsWith("Something went wrong: Unexpected character");
    }

    @Test
    public void testDefaultExceptionMapper() {
        assertThat(PersonResourceExceptionMapperTest.RESOURCES.target("/person/blah/runtime-exception").request().post(Entity.json("{}")).readEntity(String.class)).isEqualTo("Something went wrong: I'm an exception!");
    }

    @Test
    public void testDefaultEofExceptionMapper() {
        assertThat(PersonResourceExceptionMapperTest.RESOURCES.target("/person/blah/eof-exception").request().get().readEntity(String.class)).isEqualTo("Something went wrong: I'm an eof exception!");
    }

    private static class MyJerseyExceptionMapper implements ExceptionMapper<JerseyViolationException> {
        @Override
        public Response toResponse(JerseyViolationException exception) {
            return Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity("Invalid data").build();
        }
    }

    private static class GenericExceptionMapper implements ExtendedExceptionMapper<Throwable> {
        @Override
        public boolean isMappable(Throwable throwable) {
            return !(throwable instanceof JerseyViolationException);
        }

        @Override
        public Response toResponse(Throwable exception) {
            return Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity(("Something went wrong: " + (Strings.nullToEmpty(exception.getMessage())))).build();
        }
    }
}

