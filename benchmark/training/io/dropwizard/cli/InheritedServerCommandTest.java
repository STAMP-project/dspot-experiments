package io.dropwizard.cli;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.JarLocation;
import java.io.IOException;
import java.util.Optional;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class InheritedServerCommandTest {
    private static class ApiCommand extends ServerCommand<Configuration> {
        protected ApiCommand(final Application<Configuration> application) {
            super(application, "api", "Runs the Dropwizard application as an API HTTP server");
        }
    }

    private static class MyApplication extends Application<Configuration> {
        @Override
        public void initialize(final Bootstrap<Configuration> bootstrap) {
            bootstrap.addCommand(new InheritedServerCommandTest.ApiCommand(this));
            super.initialize(bootstrap);
        }

        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    private final InheritedServerCommandTest.MyApplication application = new InheritedServerCommandTest.MyApplication();

    private final InheritedServerCommandTest.ApiCommand command = new InheritedServerCommandTest.ApiCommand(application);

    private final Server server = new Server(0);

    private final Environment environment = Mockito.mock(Environment.class);

    private final Namespace namespace = Mockito.mock(Namespace.class);

    private final ServerFactory serverFactory = Mockito.mock(ServerFactory.class);

    private final Configuration configuration = Mockito.mock(Configuration.class);

    @Test
    public void hasAName() throws Exception {
        assertThat(getName()).isEqualTo("api");
    }

    @Test
    public void hasADescription() throws Exception {
        assertThat(getDescription()).isEqualTo("Runs the Dropwizard application as an API HTTP server");
    }

    @Test
    public void buildsAndRunsAConfiguredServer() throws Exception {
        command.run(environment, namespace, configuration);
        assertThat(server.isStarted()).isTrue();
    }

    @Test
    public void usesDefaultConfigPath() throws Exception {
        class SingletonConfigurationFactory implements ConfigurationFactory<Configuration> {
            @Override
            public Configuration build(final ConfigurationSourceProvider provider, final String path) throws ConfigurationException, IOException {
                return configuration;
            }

            @Override
            public Configuration build() throws ConfigurationException, IOException {
                throw new AssertionError("Didn't use the default config path variable");
            }
        }
        Mockito.when(configuration.getLoggingFactory()).thenReturn(Mockito.mock(LoggingFactory.class));
        final Bootstrap<Configuration> bootstrap = new Bootstrap(application);
        bootstrap.setConfigurationFactoryFactory(( klass, validator, objectMapper, propertyPrefix) -> new SingletonConfigurationFactory());
        bootstrap.addCommand(new ConfiguredCommand<Configuration>("test", "a test command") {
            @Override
            protected void run(final Bootstrap<Configuration> bootstrap, final Namespace namespace, final Configuration configuration) throws Exception {
                assertThat(namespace.getString("file")).isNotEmpty().isEqualTo("yaml/server.yml");
            }

            @Override
            protected Argument addFileArgument(final Subparser subparser) {
                return super.addFileArgument(subparser).setDefault("yaml/server.yml");
            }
        });
        final JarLocation location = Mockito.mock(JarLocation.class);
        Mockito.when(location.toString()).thenReturn("dw-thing.jar");
        Mockito.when(location.getVersion()).thenReturn(Optional.of("1.0.0"));
        Cli cli = new Cli(location, bootstrap, System.out, System.err);
        cli.run("test");
    }
}

