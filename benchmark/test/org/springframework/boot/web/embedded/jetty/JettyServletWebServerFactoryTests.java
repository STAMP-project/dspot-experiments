/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.web.embedded.jetty;


import java.net.InetAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactoryTests;


/**
 * Tests for {@link JettyServletWebServerFactory}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Henri Kerola
 */
public class JettyServletWebServerFactoryTests extends AbstractServletWebServerFactoryTests {
    @Test
    public void jettyConfigurations() throws Exception {
        JettyServletWebServerFactory factory = getFactory();
        Configuration[] configurations = new Configuration[4];
        Arrays.setAll(configurations, ( i) -> mock(.class));
        factory.setConfigurations(Arrays.asList(configurations[0], configurations[1]));
        factory.addConfigurations(configurations[2], configurations[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (configurations)));
        for (Configuration configuration : configurations) {
            ordered.verify(configuration).configure(ArgumentMatchers.any(WebAppContext.class));
        }
    }

    @Test
    public void jettyCustomizations() {
        JettyServletWebServerFactory factory = getFactory();
        JettyServerCustomizer[] configurations = new JettyServerCustomizer[4];
        Arrays.setAll(configurations, ( i) -> mock(.class));
        factory.setServerCustomizers(Arrays.asList(configurations[0], configurations[1]));
        factory.addServerCustomizers(configurations[2], configurations[3]);
        this.webServer = factory.getWebServer();
        InOrder ordered = Mockito.inOrder(((Object[]) (configurations)));
        for (JettyServerCustomizer configuration : configurations) {
            ordered.verify(configuration).customize(ArgumentMatchers.any(Server.class));
        }
    }

    @Test
    public void sessionTimeout() {
        JettyServletWebServerFactory factory = getFactory();
        factory.getSession().setTimeout(Duration.ofSeconds(10));
        assertTimeout(factory, 10);
    }

    @Test
    public void sessionTimeoutInMins() {
        JettyServletWebServerFactory factory = getFactory();
        factory.getSession().setTimeout(Duration.ofMinutes(1));
        assertTimeout(factory, 60);
    }

    @Test
    public void sslCiphersConfiguration() {
        Ssl ssl = new Ssl();
        ssl.setKeyStore("src/test/resources/test.jks");
        ssl.setKeyStorePassword("secret");
        ssl.setKeyPassword("password");
        ssl.setCiphers(new String[]{ "ALPHA", "BRAVO", "CHARLIE" });
        JettyServletWebServerFactory factory = getFactory();
        factory.setSsl(ssl);
        this.webServer = factory.getWebServer();
        this.webServer.start();
        JettyWebServer jettyWebServer = ((JettyWebServer) (this.webServer));
        ServerConnector connector = ((ServerConnector) (jettyWebServer.getServer().getConnectors()[0]));
        SslConnectionFactory connectionFactory = connector.getConnectionFactory(SslConnectionFactory.class);
        assertThat(connectionFactory.getSslContextFactory().getIncludeCipherSuites()).containsExactly("ALPHA", "BRAVO", "CHARLIE");
        assertThat(connectionFactory.getSslContextFactory().getExcludeCipherSuites()).isEmpty();
    }

    @Test
    public void stopCalledWithoutStart() {
        JettyServletWebServerFactory factory = getFactory();
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.stop();
        Server server = getServer();
        assertThat(server.isStopped()).isTrue();
    }

    @Test
    public void sslEnabledMultiProtocolsConfiguration() {
        JettyServletWebServerFactory factory = getFactory();
        factory.setSsl(getSslSettings("TLSv1.1", "TLSv1.2"));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        JettyWebServer jettyWebServer = ((JettyWebServer) (this.webServer));
        ServerConnector connector = ((ServerConnector) (jettyWebServer.getServer().getConnectors()[0]));
        SslConnectionFactory connectionFactory = connector.getConnectionFactory(SslConnectionFactory.class);
        assertThat(connectionFactory.getSslContextFactory().getIncludeProtocols()).containsExactly("TLSv1.1", "TLSv1.2");
    }

    @Test
    public void sslEnabledProtocolsConfiguration() {
        JettyServletWebServerFactory factory = getFactory();
        factory.setSsl(getSslSettings("TLSv1.1"));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        JettyWebServer jettyWebServer = ((JettyWebServer) (this.webServer));
        ServerConnector connector = ((ServerConnector) (jettyWebServer.getServer().getConnectors()[0]));
        SslConnectionFactory connectionFactory = connector.getConnectionFactory(SslConnectionFactory.class);
        assertThat(connectionFactory.getSslContextFactory().getIncludeProtocols()).containsExactly("TLSv1.1");
    }

    @Test
    public void wrappedHandlers() throws Exception {
        JettyServletWebServerFactory factory = getFactory();
        factory.setServerCustomizers(Collections.singletonList(( server) -> {
            Handler handler = server.getHandler();
            HandlerWrapper wrapper = new HandlerWrapper();
            wrapper.setHandler(handler);
            HandlerCollection collection = new HandlerCollection();
            collection.addHandler(wrapper);
            server.setHandler(collection);
        }));
        this.webServer = factory.getWebServer(exampleServletRegistration());
        this.webServer.start();
        assertThat(getResponse(getLocalUrl("/hello"))).isEqualTo("Hello World");
    }

    @Test
    public void basicSslClasspathKeyStore() throws Exception {
        testBasicSslWithKeyStore("classpath:test.jks");
    }

    @Test
    public void useForwardHeaders() throws Exception {
        JettyServletWebServerFactory factory = getFactory();
        factory.setUseForwardHeaders(true);
        assertForwardHeaderIsUsed(factory);
    }

    @Test
    public void defaultThreadPool() {
        JettyServletWebServerFactory factory = getFactory();
        factory.setThreadPool(null);
        assertThat(factory.getThreadPool()).isNull();
        this.webServer = factory.getWebServer();
        assertThat(((JettyWebServer) (this.webServer)).getServer().getThreadPool()).isNotNull();
    }

    @Test
    public void customThreadPool() {
        JettyServletWebServerFactory factory = getFactory();
        ThreadPool threadPool = Mockito.mock(ThreadPool.class);
        factory.setThreadPool(threadPool);
        this.webServer = factory.getWebServer();
        assertThat(((JettyWebServer) (this.webServer)).getServer().getThreadPool()).isSameAs(threadPool);
    }

    @Test
    public void startFailsWhenThreadPoolIsTooSmall() {
        JettyServletWebServerFactory factory = getFactory();
        factory.addServerCustomizers(( server) -> {
            QueuedThreadPool threadPool = server.getBean(.class);
            threadPool.setMaxThreads(2);
            threadPool.setMinThreads(2);
        });
        assertThatExceptionOfType(WebServerException.class).isThrownBy(factory.getWebServer()::start).withCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void specificIPAddressNotReverseResolved() throws Exception {
        JettyServletWebServerFactory factory = getFactory();
        InetAddress localhost = InetAddress.getLocalHost();
        factory.setAddress(InetAddress.getByAddress(localhost.getAddress()));
        this.webServer = factory.getWebServer();
        this.webServer.start();
        Connector connector = ((JettyWebServer) (this.webServer)).getServer().getConnectors()[0];
        assertThat(getHost()).isEqualTo(localhost.getHostAddress());
    }

    @Test
    public void specificIPAddressWithSslIsNotReverseResolved() throws Exception {
        JettyServletWebServerFactory factory = getFactory();
        InetAddress localhost = InetAddress.getLocalHost();
        factory.setAddress(InetAddress.getByAddress(localhost.getAddress()));
        Ssl ssl = new Ssl();
        ssl.setKeyStore("src/test/resources/test.jks");
        ssl.setKeyStorePassword("secret");
        ssl.setKeyPassword("password");
        factory.setSsl(ssl);
        this.webServer = factory.getWebServer();
        this.webServer.start();
        Connector connector = ((JettyWebServer) (this.webServer)).getServer().getConnectors()[0];
        assertThat(getHost()).isEqualTo(localhost.getHostAddress());
    }

    @Test
    public void faultyListenerCausesStartFailure() throws Exception {
        JettyServletWebServerFactory factory = getFactory();
        factory.addServerCustomizers(((JettyServerCustomizer) (( server) -> {
            Collection<WebAppContext> contexts = server.getBeans(.class);
            contexts.iterator().next().addEventListener(new ServletContextListener() {
                @Override
                public void contextInitialized(ServletContextEvent event) {
                    throw new RuntimeException();
                }

                @Override
                public void contextDestroyed(ServletContextEvent event) {
                }
            });
        })));
        assertThatExceptionOfType(WebServerException.class).isThrownBy(() -> {
            JettyWebServer jettyWebServer = ((JettyWebServer) (factory.getWebServer()));
            try {
                jettyWebServer.start();
            } finally {
                QueuedThreadPool threadPool = ((QueuedThreadPool) (jettyWebServer.getServer().getThreadPool()));
                assertThat(threadPool.isRunning()).isFalse();
            }
        });
    }
}

