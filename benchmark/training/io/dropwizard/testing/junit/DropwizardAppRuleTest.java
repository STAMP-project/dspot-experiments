package io.dropwizard.testing.junit;


import MediaType.TEXT_PLAIN;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.app.DropwizardTestApplication;
import io.dropwizard.testing.app.TestConfiguration;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import org.junit.ClassRule;
import org.junit.Test;


public class DropwizardAppRuleTest {
    @SuppressWarnings("deprecation")
    @ClassRule
    public static final DropwizardAppRule<TestConfiguration> RULE = new DropwizardAppRule(DropwizardTestApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"));

    @Test
    public void canGetExpectedResourceOverHttp() {
        final String content = ClientBuilder.newClient().target((("http://localhost:" + (DropwizardAppRuleTest.RULE.getLocalPort())) + "/test")).request().get(String.class);
        assertThat(content).isEqualTo("Yes, it's here");
    }

    @Test
    public void returnsConfiguration() {
        final TestConfiguration config = DropwizardAppRuleTest.RULE.getConfiguration();
        assertThat(config.getMessage()).isEqualTo("Yes, it's here");
    }

    @Test
    public void returnsApplication() {
        final DropwizardTestApplication application = DropwizardAppRuleTest.RULE.getApplication();
        assertThat(application).isNotNull();
    }

    @Test
    public void returnsEnvironment() {
        final Environment environment = DropwizardAppRuleTest.RULE.getEnvironment();
        assertThat(environment.getName()).isEqualTo("DropwizardTestApplication");
    }

    @Test
    public void canPerformAdminTask() {
        final String response = DropwizardAppRuleTest.RULE.client().target((("http://localhost:" + (DropwizardAppRuleTest.RULE.getAdminPort())) + "/tasks/hello?name=test_user")).request().post(Entity.entity("", TEXT_PLAIN), String.class);
        assertThat(response).isEqualTo("Hello has been said to test_user");
    }

    @Test
    public void canPerformAdminTaskWithPostBody() {
        final String response = DropwizardAppRuleTest.RULE.client().target((("http://localhost:" + (DropwizardAppRuleTest.RULE.getAdminPort())) + "/tasks/echo")).request().post(Entity.entity("Custom message", TEXT_PLAIN), String.class);
        assertThat(response).isEqualTo("Custom message");
    }

    @Test
    public void clientUsesJacksonMapperFromEnvironment() {
        assertThat(DropwizardAppRuleTest.RULE.client().target((("http://localhost:" + (DropwizardAppRuleTest.RULE.getLocalPort())) + "/message")).request().get(DropwizardTestApplication.MessageView.class).getMessage()).contains("Yes, it's here");
    }

    @Test
    public void clientSupportsPatchMethod() {
        assertThat(DropwizardAppRuleTest.RULE.client().target((("http://localhost:" + (DropwizardAppRuleTest.RULE.getLocalPort())) + "/echoPatch")).request().method("PATCH", Entity.text("Patch is working"), String.class)).contains("Patch is working");
    }
}

