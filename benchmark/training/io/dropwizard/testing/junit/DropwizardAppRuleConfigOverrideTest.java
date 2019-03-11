package io.dropwizard.testing.junit;


import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.app.TestApplication;
import io.dropwizard.testing.app.TestConfiguration;
import org.junit.ClassRule;
import org.junit.Test;


public class DropwizardAppRuleConfigOverrideTest {
    @SuppressWarnings("deprecation")
    @ClassRule
    public static final DropwizardAppRule<TestConfiguration> RULE = new DropwizardAppRule(TestApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"), "app-rule", ConfigOverride.config("app-rule", "message", "A new way to say Hooray!"), ConfigOverride.config("app-rule", "extra", () -> "supplied"), ConfigOverride.config("extra", () -> "supplied again"));

    @Test
    public void supportsConfigAttributeOverrides() {
        final String content = DropwizardAppRuleConfigOverrideTest.RULE.client().target((("http://localhost:" + (DropwizardAppRuleConfigOverrideTest.RULE.getLocalPort())) + "/test")).request().get(String.class);
        assertThat(content).isEqualTo("A new way to say Hooray!");
    }

    @Test
    public void supportsSuppliedConfigAttributeOverrides() throws Exception {
        assertThat(System.getProperty("app-rule.extra")).isEqualTo("supplied");
        assertThat(System.getProperty("dw.extra")).isEqualTo("supplied again");
    }
}

