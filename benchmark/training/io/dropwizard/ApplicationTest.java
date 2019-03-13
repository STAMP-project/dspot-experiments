package io.dropwizard;


import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.File;
import org.junit.jupiter.api.Test;


public class ApplicationTest {
    private static class FakeConfiguration extends Configuration {}

    private static class FakeApplication extends Application<ApplicationTest.FakeConfiguration> {
        boolean fatalError = false;

        @Override
        public void run(ApplicationTest.FakeConfiguration configuration, Environment environment) {
        }

        @Override
        protected void onFatalError() {
            fatalError = true;
        }
    }

    private static class PoserApplication extends ApplicationTest.FakeApplication {}

    private static class WrapperApplication<C extends ApplicationTest.FakeConfiguration> extends Application<C> {
        private final Application<C> application;

        private WrapperApplication(Application<C> application) {
            this.application = application;
        }

        @Override
        public void initialize(Bootstrap<C> bootstrap) {
            this.application.initialize(bootstrap);
        }

        @Override
        public void run(C configuration, Environment environment) throws Exception {
            this.application.run(configuration, environment);
        }
    }

    @Test
    public void hasAReferenceToItsTypeParameter() throws Exception {
        assertThat(getConfigurationClass()).isSameAs(ApplicationTest.FakeConfiguration.class);
    }

    @Test
    public void canDetermineConfiguration() throws Exception {
        assertThat(getConfigurationClass()).isSameAs(ApplicationTest.FakeConfiguration.class);
    }

    @Test
    public void canDetermineWrappedConfiguration() throws Exception {
        final ApplicationTest.PoserApplication application = new ApplicationTest.PoserApplication();
        assertThat(getConfigurationClass()).isSameAs(ApplicationTest.FakeConfiguration.class);
    }

    @Test
    public void exitWithFatalErrorWhenCommandFails() throws Exception {
        final File configFile = File.createTempFile("dropwizard-invalid-config", ".yml");
        try {
            final ApplicationTest.FakeApplication application = new ApplicationTest.FakeApplication();
            application.run("server", configFile.getAbsolutePath());
            assertThat(application.fatalError).isTrue();
        } finally {
            configFile.delete();
        }
    }
}

