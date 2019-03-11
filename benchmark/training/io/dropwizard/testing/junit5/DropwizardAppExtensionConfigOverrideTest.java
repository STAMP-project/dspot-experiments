package io.dropwizard.testing.junit5;


import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.app.TestApplication;
import io.dropwizard.testing.app.TestConfiguration;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class DropwizardAppExtensionConfigOverrideTest {
    public static final DropwizardAppExtension<TestConfiguration> EXTENSION = new DropwizardAppExtension(TestApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"), Optional.of("app-rule"), ConfigOverride.config("app-rule", "message", "A new way to say Hooray!"), ConfigOverride.config("app-rule", "extra", () -> "supplied"), ConfigOverride.config("extra", () -> "supplied again"));

    @Test
    public void supportsConfigAttributeOverrides() {
        final String content = DropwizardAppExtensionConfigOverrideTest.EXTENSION.client().target((("http://localhost:" + (DropwizardAppExtensionConfigOverrideTest.EXTENSION.getLocalPort())) + "/test")).request().get(String.class);
        assertThat(content).isEqualTo("A new way to say Hooray!");
    }

    @Test
    public void supportsSuppliedConfigAttributeOverrides() throws Exception {
        assertThat(System.getProperty("app-rule.extra")).isEqualTo("supplied");
        assertThat(System.getProperty("dw.extra")).isEqualTo("supplied again");
    }
}

