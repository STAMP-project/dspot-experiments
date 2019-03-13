package io.dropwizard.cli;


import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.setup.Environment;
import java.io.IOException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ServerCommandTest {
    private static class MyApplication extends Application<Configuration> {
        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
        }
    }

    private final ServerCommandTest.MyApplication application = new ServerCommandTest.MyApplication();

    private final ServerCommand<Configuration> command = new ServerCommand(application);

    private final Server server = new Server(0) {
        @Override
        protected void doStop() throws Exception {
            super.doStop();
            if (ServerCommandTest.this.throwException) {
                System.out.println("throw NullPointerException, see Issue#1557");
                throw new NullPointerException();
            }
        }
    };

    private final Environment environment = Mockito.mock(Environment.class);

    private final Namespace namespace = Mockito.mock(Namespace.class);

    private final ServerFactory serverFactory = Mockito.mock(ServerFactory.class);

    private final Configuration configuration = Mockito.mock(Configuration.class);

    private boolean throwException = false;

    @Test
    public void hasAName() throws Exception {
        assertThat(command.getName()).isEqualTo("server");
    }

    @Test
    public void hasADescription() throws Exception {
        assertThat(command.getDescription()).isEqualTo("Runs the Dropwizard application as an HTTP server");
    }

    @Test
    public void hasTheApplicationsConfigurationClass() throws Exception {
        assertThat(command.getConfigurationClass()).isEqualTo(getConfigurationClass());
    }

    @Test
    public void buildsAndRunsAConfiguredServer() throws Exception {
        command.run(environment, namespace, configuration);
        assertThat(server.isStarted()).isTrue();
    }

    @Test
    public void stopsAServerIfThereIsAnErrorStartingIt() throws Exception {
        this.throwException = true;
        server.addBean(new AbstractLifeCycle() {
            @Override
            protected void doStart() throws Exception {
                throw new IOException("oh crap");
            }
        });
        try {
            command.run(environment, namespace, configuration);
            failBecauseExceptionWasNotThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e.getMessage()).isEqualTo("oh crap");
        }
        assertThat(server.isStarted()).isFalse();
        this.throwException = false;
    }
}

