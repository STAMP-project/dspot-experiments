package spark.embeddedserver.jetty;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import spark.ExceptionMapper;
import spark.embeddedserver.EmbeddedServer;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;


public class EmbeddedJettyFactoryTest {
    private EmbeddedServer embeddedServer;

    @Test
    public void create() throws Exception {
        final JettyServerFactory jettyServerFactory = Mockito.mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = Mockito.mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = Mockito.mock(ExceptionMapper.class);
        final Routes routes = Mockito.mock(Routes.class);
        Server server = new Server();
        Mockito.when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);
        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6757, null, 100, 10, 10000);
        Mockito.verify(jettyServerFactory, Mockito.times(1)).create(100, 10, 10000);
        Mockito.verifyNoMoreInteractions(jettyServerFactory);
        Assert.assertTrue(getSessionCookieConfig().isHttpOnly());
    }

    @Test
    public void create_withThreadPool() throws Exception {
        final QueuedThreadPool threadPool = new QueuedThreadPool(100);
        final JettyServerFactory jettyServerFactory = Mockito.mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = Mockito.mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = Mockito.mock(ExceptionMapper.class);
        final Routes routes = Mockito.mock(Routes.class);
        Mockito.when(jettyServerFactory.create(threadPool)).thenReturn(new Server(threadPool));
        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(threadPool);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6758, null, 0, 0, 0);
        Mockito.verify(jettyServerFactory, Mockito.times(1)).create(threadPool);
        Mockito.verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withNullThreadPool() throws Exception {
        final JettyServerFactory jettyServerFactory = Mockito.mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = Mockito.mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = Mockito.mock(ExceptionMapper.class);
        final Routes routes = Mockito.mock(Routes.class);
        Mockito.when(jettyServerFactory.create(100, 10, 10000)).thenReturn(new Server());
        final EmbeddedJettyFactory embeddedJettyFactory = withThreadPool(null);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6759, null, 100, 10, 10000);
        Mockito.verify(jettyServerFactory, Mockito.times(1)).create(100, 10, 10000);
        Mockito.verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withoutHttpOnly() throws Exception {
        final JettyServerFactory jettyServerFactory = Mockito.mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = Mockito.mock(StaticFilesConfiguration.class);
        final Routes routes = Mockito.mock(Routes.class);
        Server server = new Server();
        Mockito.when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);
        final EmbeddedJettyFactory embeddedJettyFactory = withHttpOnly(false);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, false);
        embeddedServer.ignite("localhost", 6759, null, 100, 10, 10000);
        Assert.assertFalse(getSessionCookieConfig().isHttpOnly());
    }
}

