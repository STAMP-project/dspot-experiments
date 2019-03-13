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
package org.springframework.boot.web.embedded.netty;


import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactoryTests;
import org.springframework.boot.web.server.PortInUseException;
import reactor.netty.http.server.HttpServer;


/**
 * Tests for {@link NettyReactiveWebServerFactory}.
 *
 * @author Brian Clozel
 */
public class NettyReactiveWebServerFactoryTests extends AbstractReactiveWebServerFactoryTests {
    @Test
    public void exceptionIsThrownWhenPortIsAlreadyInUse() {
        AbstractReactiveWebServerFactory factory = getFactory();
        factory.setPort(0);
        this.webServer = factory.getWebServer(new AbstractReactiveWebServerFactoryTests.EchoHandler());
        this.webServer.start();
        factory.setPort(this.webServer.getPort());
        assertThatExceptionOfType(PortInUseException.class).isThrownBy(factory.getWebServer(new AbstractReactiveWebServerFactoryTests.EchoHandler())::start).satisfies(this::portMatchesRequirement);
    }

    @Test
    public void nettyCustomizers() {
        NettyReactiveWebServerFactory factory = getFactory();
        NettyServerCustomizer[] customizers = new NettyServerCustomizer[2];
        for (int i = 0; i < (customizers.length); i++) {
            customizers[i] = Mockito.mock(NettyServerCustomizer.class);
            BDDMockito.given(customizers[i].apply(ArgumentMatchers.any(HttpServer.class))).will(( invocation) -> invocation.getArgument(0));
        }
        factory.setServerCustomizers(Arrays.asList(customizers[0], customizers[1]));
        this.webServer = factory.getWebServer(new AbstractReactiveWebServerFactoryTests.EchoHandler());
        InOrder ordered = Mockito.inOrder(((Object[]) (customizers)));
        for (NettyServerCustomizer customizer : customizers) {
            ordered.verify(customizer).apply(ArgumentMatchers.any(HttpServer.class));
        }
    }

    @Test
    public void useForwardedHeaders() {
        NettyReactiveWebServerFactory factory = getFactory();
        factory.setUseForwardHeaders(true);
        assertForwardHeaderIsUsed(factory);
    }
}

