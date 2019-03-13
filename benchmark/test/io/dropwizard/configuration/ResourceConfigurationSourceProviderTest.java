package io.dropwizard.configuration;


import org.junit.jupiter.api.Test;


public class ResourceConfigurationSourceProviderTest {
    private final ConfigurationSourceProvider provider = new ResourceConfigurationSourceProvider();

    @Test
    public void readsFileContents() throws Exception {
        assertForWheeContent("example.txt");
        assertForWheeContent("io/dropwizard/configuration/not-root-example.txt");
        assertForWheeContent("/io/dropwizard/configuration/not-root-example.txt");
    }
}

