package io.dropwizard.setup;


import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.server.SimpleServerFactoryTest;
import io.dropwizard.validation.BaseValidator;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;


public class ExceptionMapperBinderTest {
    private SimpleServerFactory http;

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    private Validator validator = BaseValidator.newValidator();

    private Environment environment = new Environment("testEnvironment", objectMapper, validator, new MetricRegistry(), ClassLoader.getSystemClassLoader());

    @Test
    public void testOverrideDefaultExceptionMapper() throws Exception {
        environment.jersey().register(new ExceptionMapperBinderTest.TestValidationResource());
        environment.jersey().register(new ExceptionMapperBinderTest.MyJerseyExceptionMapper());
        final Server server = http.build(environment);
        server.start();
        final int port = getLocalPort();
        assertThat(SimpleServerFactoryTest.httpRequest("GET", (("http://localhost:" + port) + "/service/test"))).isEqualTo("alright!");
        server.stop();
    }

    private static class MyJerseyExceptionMapper implements ExceptionMapper<JerseyViolationException> {
        @Override
        public Response toResponse(JerseyViolationException e) {
            return Response.ok("alright!").build();
        }
    }

    @Path("/test")
    @Produces("application/json")
    private static class TestValidationResource {
        @GET
        public String get(@NotEmpty
        @QueryParam("foo")
        String foo) {
            return foo;
        }
    }
}

