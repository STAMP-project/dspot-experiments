package io.dropwizard.cli;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class CheckCommandTest {
    private static class MyApplication extends Application<Configuration> {
        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    private final CheckCommandTest.MyApplication application = new CheckCommandTest.MyApplication();

    private final CheckCommand<Configuration> command = new CheckCommand(application);

    @SuppressWarnings("unchecked")
    private final Bootstrap<Configuration> bootstrap = Mockito.mock(Bootstrap.class);

    private final Namespace namespace = Mockito.mock(Namespace.class);

    private final Configuration configuration = Mockito.mock(Configuration.class);

    @Test
    public void hasAName() throws Exception {
        assertThat(command.getName()).isEqualTo("check");
    }

    @Test
    public void hasADescription() throws Exception {
        assertThat(command.getDescription()).isEqualTo("Parses and validates the configuration file");
    }

    @Test
    public void doesNotInteractWithAnything() throws Exception {
        command.run(bootstrap, namespace, configuration);
        Mockito.verifyZeroInteractions(bootstrap, namespace, configuration);
    }
}

