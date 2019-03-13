/**
 * Copyright 2012-2018 the original author or authors.
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
import java.util.Arrays;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactoryTests;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link JettyReactiveWebServerFactory} and {@link JettyWebServer}.
 *
 * @author Brian Clozel
 * @author Madhura Bhave
 */
public class JettyReactiveWebServerFactoryTests extends AbstractReactiveWebServerFactoryTests {
    @Test
    public void setNullServerCustomizersShouldThrowException() {
        JettyReactiveWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.setServerCustomizers(null)).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void addNullServerCustomizersShouldThrowException() {
        JettyReactiveWebServerFactory factory = getFactory();
        assertThatIllegalArgumentException().isThrownBy(() -> factory.addServerCustomizers(((JettyServerCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void jettyCustomizersShouldBeInvoked() {
        HttpHandler handler = Mockito.mock(HttpHandler.class);
        JettyReactiveWebServerFactory factory = getFactory();
        JettyServerCustomizer[] configurations = new JettyServerCustomizer[4];
        Arrays.setAll(configurations, ( i) -> mock(.class));
        factory.setServerCustomizers(Arrays.asList(configurations[0], configurations[1]));
        factory.addServerCustomizers(configurations[2], configurations[3]);
        this.webServer = factory.getWebServer(handler);
        InOrder ordered = Mockito.inOrder(((Object[]) (configurations)));
        for (JettyServerCustomizer configuration : configurations) {
            ordered.verify(configuration).customize(ArgumentMatchers.any(Server.class));
        }
    }

    @Test
    public void specificIPAddressNotReverseResolved() throws Exception {
        JettyReactiveWebServerFactory factory = getFactory();
        InetAddress localhost = InetAddress.getLocalHost();
        factory.setAddress(InetAddress.getByAddress(localhost.getAddress()));
        this.webServer = factory.getWebServer(Mockito.mock(HttpHandler.class));
        this.webServer.start();
        Connector connector = getServer().getConnectors()[0];
        assertThat(getHost()).isEqualTo(localhost.getHostAddress());
    }

    @Test
    public void useForwardedHeaders() {
        JettyReactiveWebServerFactory factory = getFactory();
        factory.setUseForwardHeaders(true);
        assertForwardHeaderIsUsed(factory);
    }

    @Test
    public void useServerResources() throws Exception {
        JettyResourceFactory resourceFactory = new JettyResourceFactory();
        resourceFactory.afterPropertiesSet();
        JettyReactiveWebServerFactory factory = getFactory();
        factory.setResourceFactory(resourceFactory);
        JettyWebServer webServer = ((JettyWebServer) (factory.getWebServer(new AbstractReactiveWebServerFactoryTests.EchoHandler())));
        webServer.start();
        Connector connector = webServer.getServer().getConnectors()[0];
        assertThat(connector.getByteBufferPool()).isEqualTo(resourceFactory.getByteBufferPool());
        assertThat(connector.getExecutor()).isEqualTo(resourceFactory.getExecutor());
        assertThat(connector.getScheduler()).isEqualTo(resourceFactory.getScheduler());
    }
}

