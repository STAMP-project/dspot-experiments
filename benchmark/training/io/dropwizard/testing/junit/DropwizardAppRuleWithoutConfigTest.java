package io.dropwizard.testing.junit;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.ClassRule;
import org.junit.Test;


public class DropwizardAppRuleWithoutConfigTest {
    @SuppressWarnings("deprecation")
    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE = new DropwizardAppRule(DropwizardAppRuleWithoutConfigTest.TestApplication.class, null, ConfigOverride.config("server.applicationConnectors[0].port", "0"), ConfigOverride.config("server.adminConnectors[0].port", "0"));

    @Test
    public void runWithoutConfigFile() {
        Map<String, String> response = DropwizardAppRuleWithoutConfigTest.RULE.client().target((("http://localhost:" + (DropwizardAppRuleWithoutConfigTest.RULE.getLocalPort())) + "/test")).request().get(new javax.ws.rs.core.GenericType<Map<String, String>>() {});
        assertThat(response).containsOnly(entry("color", "orange"));
    }

    public static class TestApplication extends Application<Configuration> {
        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
            environment.jersey().register(new DropwizardAppRuleWithoutConfigTest.TestResource());
        }
    }

    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public static class TestResource {
        @GET
        public Response get() {
            return Response.ok(Collections.singletonMap("color", "orange")).build();
        }
    }
}

