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
package org.springframework.boot.autoconfigure.web.embedded;


import UndertowOptions.MAX_HEADER_SIZE;
import UndertowOptions.NO_REQUEST_TIMEOUT;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import java.io.File;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.mock.env.MockEnvironment;
import org.xnio.OptionMap;


/**
 * Tests for {@link UndertowWebServerFactoryCustomizer}.
 *
 * @author Brian Clozel
 * @author Phillip Webb
 * @author Artsiom Yudovin
 */
public class UndertowWebServerFactoryCustomizerTests {
    private MockEnvironment environment;

    private ServerProperties serverProperties;

    private UndertowWebServerFactoryCustomizer customizer;

    @Test
    public void customizeUndertowAccessLog() {
        bind("server.undertow.accesslog.enabled=true", "server.undertow.accesslog.pattern=foo", "server.undertow.accesslog.prefix=test_log", "server.undertow.accesslog.suffix=txt", "server.undertow.accesslog.dir=test-logs", "server.undertow.accesslog.rotate=false");
        ConfigurableUndertowWebServerFactory factory = Mockito.mock(ConfigurableUndertowWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setAccessLogEnabled(true);
        Mockito.verify(factory).setAccessLogPattern("foo");
        Mockito.verify(factory).setAccessLogPrefix("test_log");
        Mockito.verify(factory).setAccessLogSuffix("txt");
        Mockito.verify(factory).setAccessLogDirectory(new File("test-logs"));
        Mockito.verify(factory).setAccessLogRotate(false);
    }

    @Test
    public void deduceUseForwardHeaders() {
        this.environment.setProperty("DYNO", "-");
        ConfigurableUndertowWebServerFactory factory = Mockito.mock(ConfigurableUndertowWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(true);
    }

    @Test
    public void defaultUseForwardHeaders() {
        ConfigurableUndertowWebServerFactory factory = Mockito.mock(ConfigurableUndertowWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(false);
    }

    @Test
    public void setUseForwardHeaders() {
        this.serverProperties.setUseForwardHeaders(true);
        ConfigurableUndertowWebServerFactory factory = Mockito.mock(ConfigurableUndertowWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(true);
    }

    @Test
    public void customizeMaxHttpHeaderSize() {
        bind("server.max-http-header-size=2048");
        Builder builder = Undertow.builder();
        ConfigurableUndertowWebServerFactory factory = mockFactory(builder);
        this.customizer.customize(factory);
        OptionMap map = getMap();
        assertThat(map.get(MAX_HEADER_SIZE).intValue()).isEqualTo(2048);
    }

    @Test
    public void customMaxHttpHeaderSizeIgnoredIfNegative() {
        bind("server.max-http-header-size=-1");
        Builder builder = Undertow.builder();
        ConfigurableUndertowWebServerFactory factory = mockFactory(builder);
        this.customizer.customize(factory);
        OptionMap map = getMap();
        assertThat(map.contains(MAX_HEADER_SIZE)).isFalse();
    }

    @Test
    public void customMaxHttpHeaderSizeIgnoredIfZero() {
        bind("server.max-http-header-size=0");
        Builder builder = Undertow.builder();
        ConfigurableUndertowWebServerFactory factory = mockFactory(builder);
        this.customizer.customize(factory);
        OptionMap map = getMap();
        assertThat(map.contains(MAX_HEADER_SIZE)).isFalse();
    }

    @Test
    public void customConnectionTimeout() {
        bind("server.connection-timeout=100");
        Builder builder = Undertow.builder();
        ConfigurableUndertowWebServerFactory factory = mockFactory(builder);
        this.customizer.customize(factory);
        OptionMap map = getMap();
        assertThat(map.contains(NO_REQUEST_TIMEOUT)).isTrue();
        assertThat(map.get(NO_REQUEST_TIMEOUT)).isEqualTo(100);
    }
}

