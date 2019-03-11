package io.dropwizard.server;


import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests that the {@link JerseyEnvironment#getUrlPattern()} is set by the following priority order:
 * <ol>
 *     <li>YAML defined value</li>
 *     <li>{@link io.dropwizard.Application#run(Configuration, Environment)} defined value</li>
 *     <li>Default value defined by {@link DropwizardResourceConfig#urlPattern}</li>
 * </ol>
 */
public class AbstractServerFactoryTest {
    private final JerseyContainerHolder holder = Mockito.mock(JerseyContainerHolder.class);

    private final DropwizardResourceConfig config = new DropwizardResourceConfig();

    private final JerseyEnvironment jerseyEnvironment = new JerseyEnvironment(holder, config);

    private final Environment environment = Mockito.mock(Environment.class, Mockito.RETURNS_DEEP_STUBS);

    private final AbstractServerFactoryTest.TestServerFactory serverFactory = new AbstractServerFactoryTest.TestServerFactory();

    private static final String DEFAULT_PATTERN = "/*";

    private static final String RUN_SET_PATTERN = "/set/from/run/*";

    private static final String YAML_SET_PATTERN = "/set/from/yaml/*";

    @Test
    public void usesYamlDefinedPattern() {
        setJerseyRootPath(AbstractServerFactoryTest.YAML_SET_PATTERN);
        jerseyEnvironment.setUrlPattern(AbstractServerFactoryTest.RUN_SET_PATTERN);
        serverFactory.build(environment);
        assertThat(jerseyEnvironment.getUrlPattern()).isEqualTo(AbstractServerFactoryTest.YAML_SET_PATTERN);
    }

    @Test
    public void usesRunDefinedPatternWhenNoYaml() {
        jerseyEnvironment.setUrlPattern(AbstractServerFactoryTest.RUN_SET_PATTERN);
        serverFactory.build(environment);
        assertThat(jerseyEnvironment.getUrlPattern()).isEqualTo(AbstractServerFactoryTest.RUN_SET_PATTERN);
    }

    @Test
    public void usesDefaultPatternWhenNoneSet() {
        serverFactory.build(environment);
        assertThat(jerseyEnvironment.getUrlPattern()).isEqualTo(AbstractServerFactoryTest.DEFAULT_PATTERN);
    }

    /**
     * Test implementation of {@link AbstractServerFactory} used to run {@link #createAppServlet}, which triggers the
     * setting of {@link JerseyEnvironment#setUrlPattern(String)}.
     */
    public static class TestServerFactory extends AbstractServerFactory {
        @Override
        public Server build(Environment environment) {
            // mimics the current default + simple server factory build() methods
            ThreadPool threadPool = createThreadPool(environment.metrics());
            Server server = buildServer(environment.lifecycle(), threadPool);
            createAppServlet(server, environment.jersey(), environment.getObjectMapper(), environment.getValidator(), environment.getApplicationContext(), environment.getJerseyServletContainer(), environment.metrics());
            return server;
        }

        @Override
        public void configure(Environment environment) {
            // left blank intentionally
        }
    }
}

