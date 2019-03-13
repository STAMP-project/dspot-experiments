package io.dropwizard.server;


import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;


public class SimpleServerFactoryTest {
    private SimpleServerFactory http;

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    private Validator validator = BaseValidator.newValidator();

    private Environment environment = new Environment("testEnvironment", objectMapper, validator, new MetricRegistry(), ClassLoader.getSystemClassLoader());

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(SimpleServerFactory.class);
    }

    @Test
    public void testGetAdminContext() {
        assertThat(http.getAdminContextPath()).isEqualTo("/secret");
    }

    @Test
    public void testGetApplicationContext() {
        assertThat(http.getApplicationContextPath()).isEqualTo("/service");
    }

    @Test
    public void testGetPort() {
        final HttpConnectorFactory connector = ((HttpConnectorFactory) (http.getConnector()));
        assertThat(connector.getPort()).isEqualTo(0);
    }

    @Test
    public void testBuild() throws Exception {
        environment.jersey().register(new SimpleServerFactoryTest.TestResource());
        environment.admin().addTask(new SimpleServerFactoryTest.TestTask());
        final Server server = http.build(environment);
        server.start();
        final int port = getLocalPort();
        assertThat(SimpleServerFactoryTest.httpRequest("GET", (("http://localhost:" + port) + "/service/test"))).isEqualTo("{\"hello\": \"World\"}");
        assertThat(SimpleServerFactoryTest.httpRequest("POST", (("http://localhost:" + port) + "/secret/tasks/hello?name=test_user"))).isEqualTo("Hello, test_user!");
        server.stop();
    }

    @Test
    public void testConfiguredEnvironment() {
        http.configure(environment);
        Assertions.assertEquals(http.getAdminContextPath(), environment.getAdminContext().getContextPath());
        Assertions.assertEquals(http.getApplicationContextPath(), environment.getApplicationContext().getContextPath());
    }

    @Test
    public void testDeserializeWithoutJsonAutoDetect() {
        final ObjectMapper objectMapper = Jackson.newObjectMapper().setVisibility(PropertyAccessor.ALL, NONE);
        assertThatCode(() -> new YamlConfigurationFactory<>(.class, BaseValidator.newValidator(), objectMapper, "dw").build(new File(Resources.getResource("yaml/simple_server.yml").toURI()))).doesNotThrowAnyException();
    }

    @Path("/test")
    @Produces("application/json")
    public static class TestResource {
        @GET
        public String get() throws Exception {
            return "{\"hello\": \"World\"}";
        }
    }

    public static class TestTask extends Task {
        public TestTask() {
            super("hello");
        }

        @Override
        public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
            final String name = parameters.getOrDefault("name", Collections.emptyList()).iterator().next();
            output.print((("Hello, " + name) + "!"));
            output.flush();
        }
    }
}

