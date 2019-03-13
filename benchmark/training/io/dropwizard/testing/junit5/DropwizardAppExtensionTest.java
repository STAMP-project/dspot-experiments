package io.dropwizard.testing.junit5;


import MediaType.TEXT_PLAIN;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.app.DropwizardTestApplication;
import io.dropwizard.testing.app.TestConfiguration;
import java.util.Optional;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardAppExtensionTest {
    public static final DropwizardAppExtension<TestConfiguration> EXTENSION = new DropwizardAppExtension(DropwizardTestApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"));

    @Test
    public void canGetExpectedResourceOverHttp() {
        final String content = ClientBuilder.newClient().target((("http://localhost:" + (DropwizardAppExtensionTest.EXTENSION.getLocalPort())) + "/test")).request().get(String.class);
        assertThat(content).isEqualTo("Yes, it's here");
    }

    @Test
    public void returnsConfiguration() {
        final TestConfiguration config = DropwizardAppExtensionTest.EXTENSION.getConfiguration();
        assertThat(config.getMessage()).isEqualTo("Yes, it's here");
    }

    @Test
    public void returnsApplication() {
        final DropwizardTestApplication application = DropwizardAppExtensionTest.EXTENSION.getApplication();
        Assertions.assertNotNull(application);
    }

    @Test
    public void returnsEnvironment() {
        final Environment environment = DropwizardAppExtensionTest.EXTENSION.getEnvironment();
        assertThat(environment.getName()).isEqualTo("DropwizardTestApplication");
    }

    @Test
    public void canPerformAdminTask() {
        final String response = DropwizardAppExtensionTest.EXTENSION.client().target((("http://localhost:" + (DropwizardAppExtensionTest.EXTENSION.getAdminPort())) + "/tasks/hello?name=test_user")).request().post(Entity.entity("", TEXT_PLAIN), String.class);
        assertThat(response).isEqualTo("Hello has been said to test_user");
    }

    @Test
    public void canPerformAdminTaskWithPostBody() {
        final String response = DropwizardAppExtensionTest.EXTENSION.client().target((("http://localhost:" + (DropwizardAppExtensionTest.EXTENSION.getAdminPort())) + "/tasks/echo")).request().post(Entity.entity("Custom message", TEXT_PLAIN), String.class);
        assertThat(response).isEqualTo("Custom message");
    }

    @Test
    public void clientUsesJacksonMapperFromEnvironment() {
        final Optional<String> message = DropwizardAppExtensionTest.EXTENSION.client().target((("http://localhost:" + (DropwizardAppExtensionTest.EXTENSION.getLocalPort())) + "/message")).request().get(DropwizardTestApplication.MessageView.class).getMessage();
        assertThat(message).hasValue("Yes, it's here");
    }

    @Test
    public void clientSupportsPatchMethod() {
        final String method = DropwizardAppExtensionTest.EXTENSION.client().target((("http://localhost:" + (DropwizardAppExtensionTest.EXTENSION.getLocalPort())) + "/echoPatch")).request().method("PATCH", Entity.text("Patch is working"), String.class);
        assertThat(method).isEqualTo("Patch is working");
    }
}

