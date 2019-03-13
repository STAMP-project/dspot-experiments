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
package org.springframework.boot.autoconfigure.web.reactive;


import java.net.InetAddress;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.Ssl;


/**
 * Tests for {@link ReactiveWebServerFactoryCustomizer}.
 *
 * @author Brian Clozel
 * @author Yunkun Huang
 */
public class ReactiveWebServerFactoryCustomizerTests {
    private ServerProperties properties = new ServerProperties();

    private ReactiveWebServerFactoryCustomizer customizer;

    @Test
    public void testCustomizeServerPort() {
        ConfigurableReactiveWebServerFactory factory = Mockito.mock(ConfigurableReactiveWebServerFactory.class);
        this.properties.setPort(9000);
        this.customizer.customize(factory);
        Mockito.verify(factory).setPort(9000);
    }

    @Test
    public void testCustomizeServerAddress() {
        ConfigurableReactiveWebServerFactory factory = Mockito.mock(ConfigurableReactiveWebServerFactory.class);
        InetAddress address = Mockito.mock(InetAddress.class);
        this.properties.setAddress(address);
        this.customizer.customize(factory);
        Mockito.verify(factory).setAddress(address);
    }

    @Test
    public void testCustomizeServerSsl() {
        ConfigurableReactiveWebServerFactory factory = Mockito.mock(ConfigurableReactiveWebServerFactory.class);
        Ssl ssl = Mockito.mock(Ssl.class);
        this.properties.setSsl(ssl);
        this.customizer.customize(factory);
        Mockito.verify(factory).setSsl(ssl);
    }
}

