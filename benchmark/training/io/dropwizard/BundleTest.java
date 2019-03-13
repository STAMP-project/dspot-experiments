package io.dropwizard;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import org.junit.jupiter.api.Test;


public class BundleTest {
    @Test
    public void deprecatedBundleWillBeInitializedAndRun() throws Exception {
        final BundleTest.DeprecatedBundle deprecatedBundle = new BundleTest.DeprecatedBundle();
        assertThat(deprecatedBundle.wasInitialized()).isFalse();
        assertThat(deprecatedBundle.wasRun()).isFalse();
        final File configFile = File.createTempFile("bundle-test", ".yml");
        try {
            Files.write(configFile.toPath(), Collections.singleton("text: Test"));
            final BundleTest.TestApplication application = new BundleTest.TestApplication(deprecatedBundle);
            application.run("server", configFile.getAbsolutePath());
        } finally {
            configFile.delete();
        }
        assertThat(deprecatedBundle.wasInitialized()).isTrue();
        assertThat(deprecatedBundle.wasRun()).isTrue();
    }

    private static class TestConfiguration extends Configuration {
        @JsonProperty
        String text = "";

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    @SuppressWarnings("deprecation")
    private static class TestApplication extends Application<BundleTest.TestConfiguration> {
        Bundle bundle;

        public TestApplication(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public void initialize(Bootstrap<BundleTest.TestConfiguration> bootstrap) {
            bootstrap.addBundle(bundle);
        }

        @Override
        public void run(BundleTest.TestConfiguration configuration, Environment environment) {
        }
    }

    @SuppressWarnings("deprecation")
    private static class DeprecatedBundle implements Bundle {
        boolean wasInitialized = false;

        boolean wasRun = false;

        @Override
        public void initialize(Bootstrap<?> bootstrap) {
            wasInitialized = true;
        }

        @Override
        public void run(Environment environment) {
            wasRun = true;
        }

        public boolean wasInitialized() {
            return wasInitialized;
        }

        public boolean wasRun() {
            return wasRun;
        }
    }
}

