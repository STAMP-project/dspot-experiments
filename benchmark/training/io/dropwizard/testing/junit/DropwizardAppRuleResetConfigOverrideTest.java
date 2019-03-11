package io.dropwizard.testing.junit;


import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.app.TestApplication;
import io.dropwizard.testing.app.TestConfiguration;
import org.junit.Test;


public class DropwizardAppRuleResetConfigOverrideTest {
    @SuppressWarnings("deprecation")
    private final DropwizardAppRule<TestConfiguration> dropwizardAppRule = new DropwizardAppRule(TestApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"), "app-rule-reset", ConfigOverride.config("app-rule-reset", "message", "A new way to say Hooray!"));

    @Test
    public void test2() throws Exception {
        dropwizardAppRule.before();
        assertThat(System.getProperty("app-rule-reset.message")).isEqualTo("A new way to say Hooray!");
        assertThat(System.getProperty("app-rule-reset.extra")).isNull();
        dropwizardAppRule.after();
        System.setProperty("app-rule-reset.extra", "Some extra system property");
        dropwizardAppRule.before();
        assertThat(System.getProperty("app-rule-reset.message")).isEqualTo("A new way to say Hooray!");
        assertThat(System.getProperty("app-rule-reset.extra")).isEqualTo("Some extra system property");
        dropwizardAppRule.after();
        assertThat(System.getProperty("app-rule-reset.message")).isNull();
        assertThat(System.getProperty("app-rule-reset.extra")).isEqualTo("Some extra system property");
        System.clearProperty("app-rule-reset.extra");
    }
}

