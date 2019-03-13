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
package org.springframework.boot.autoconfigure.web.embedded;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link NettyWebServerFactoryCustomizer}.
 *
 * @author Brian Clozel
 */
public class NettyWebServerFactoryCustomizerTests {
    private MockEnvironment environment;

    private ServerProperties serverProperties;

    private NettyWebServerFactoryCustomizer customizer;

    @Test
    public void deduceUseForwardHeaders() {
        this.environment.setProperty("DYNO", "-");
        NettyReactiveWebServerFactory factory = Mockito.mock(NettyReactiveWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(true);
    }

    @Test
    public void defaultUseForwardHeaders() {
        NettyReactiveWebServerFactory factory = Mockito.mock(NettyReactiveWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(false);
    }

    @Test
    public void setUseForwardHeaders() {
        this.serverProperties.setUseForwardHeaders(true);
        NettyReactiveWebServerFactory factory = Mockito.mock(NettyReactiveWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(true);
    }
}

