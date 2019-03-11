package io.dropwizard.client;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.util.Resources;
import java.net.URI;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class JerseyIgnoreRequestUserAgentHeaderFilterTest {
    public static final DropwizardAppExtension<Configuration> APP_RULE = new DropwizardAppExtension(JerseyIgnoreRequestUserAgentHeaderFilterTest.TestApplication.class, Resources.getResource("yaml/jerseyIgnoreRequestUserAgentHeaderFilterTest.yml").getPath());

    private final URI testUri = URI.create(("http://localhost:" + (JerseyIgnoreRequestUserAgentHeaderFilterTest.APP_RULE.getLocalPort())));

    private JerseyClientBuilder clientBuilder;

    private JerseyClientConfiguration clientConfiguration;

    @Test
    public void clientIsSetRequestIsNotSet() {
        clientConfiguration.setUserAgent(Optional.of("ClientUserAgentHeaderValue"));
        assertThat(clientBuilder.using(clientConfiguration).build("ClientName").target(((testUri) + "/user_agent")).request().get(String.class)).isEqualTo("ClientUserAgentHeaderValue");
    }

    @Test
    public void clientIsNotSetRequestIsSet() {
        assertThat(clientBuilder.build("ClientName").target(((testUri) + "/user_agent")).request().header("User-Agent", "RequestUserAgentHeaderValue").get(String.class)).isEqualTo("RequestUserAgentHeaderValue");
    }

    @Test
    public void clientIsNotSetRequestIsNotSet() {
        assertThat(clientBuilder.build("ClientName").target(((testUri) + "/user_agent")).request().get(String.class)).isEqualTo("ClientName");
    }

    @Test
    public void clientIsSetRequestIsSet() {
        clientConfiguration.setUserAgent(Optional.of("ClientUserAgentHeaderValue"));
        assertThat(clientBuilder.build("ClientName").target(((testUri) + "/user_agent")).request().header("User-Agent", "RequestUserAgentHeaderValue").get(String.class)).isEqualTo("RequestUserAgentHeaderValue");
    }

    @Path("/")
    public static class TestResource {
        @GET
        @Path("user_agent")
        public String getReturnUserAgentHeader(@HeaderParam("User-Agent")
        String userAgentHeader) {
            return userAgentHeader;
        }
    }

    public static class TestApplication extends Application<Configuration> {
        public static void main(String[] args) throws Exception {
            new JerseyIgnoreRequestUserAgentHeaderFilterTest.TestApplication().run(args);
        }

        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
            environment.jersey().register(JerseyIgnoreRequestUserAgentHeaderFilterTest.TestResource.class);
        }
    }
}

