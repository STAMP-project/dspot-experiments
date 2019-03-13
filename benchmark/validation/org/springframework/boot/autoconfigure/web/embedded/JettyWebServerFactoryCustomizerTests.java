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


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.eclipse.jetty.server.NCSARequestLog;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link JettyWebServerFactoryCustomizer}.
 *
 * @author Brian Clozel
 * @author Phillip Webb
 */
public class JettyWebServerFactoryCustomizerTests {
    private MockEnvironment environment;

    private ServerProperties serverProperties;

    private JettyWebServerFactoryCustomizer customizer;

    @Test
    public void deduceUseForwardHeaders() {
        this.environment.setProperty("DYNO", "-");
        ConfigurableJettyWebServerFactory factory = Mockito.mock(ConfigurableJettyWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(true);
    }

    @Test
    public void defaultUseForwardHeaders() {
        ConfigurableJettyWebServerFactory factory = Mockito.mock(ConfigurableJettyWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(false);
    }

    @Test
    public void accessLogCanBeCustomized() throws IOException {
        File logFile = File.createTempFile("jetty_log", ".log");
        String timezone = TimeZone.getDefault().getID();
        bind("server.jetty.accesslog.enabled=true", ("server.jetty.accesslog.filename=" + (logFile.getAbsolutePath().replace("\\", "\\\\"))), "server.jetty.accesslog.file-date-format=yyyy-MM-dd", "server.jetty.accesslog.retention-period=42", "server.jetty.accesslog.append=true", "server.jetty.accesslog.extended-format=true", "server.jetty.accesslog.date-format=HH:mm:ss", "server.jetty.accesslog.locale=en_BE", ("server.jetty.accesslog.time-zone=" + timezone), "server.jetty.accesslog.log-cookies=true", "server.jetty.accesslog.log-server=true", "server.jetty.accesslog.log-latency=true");
        JettyWebServer server = customizeAndGetServer();
        NCSARequestLog requestLog = getNCSARequestLog(server);
        assertThat(requestLog.getFilename()).isEqualTo(logFile.getAbsolutePath());
        assertThat(requestLog.getFilenameDateFormat()).isEqualTo("yyyy-MM-dd");
        assertThat(requestLog.getRetainDays()).isEqualTo(42);
        assertThat(requestLog.isAppend()).isTrue();
        assertThat(requestLog.isExtended()).isTrue();
        assertThat(requestLog.getLogDateFormat()).isEqualTo("HH:mm:ss");
        assertThat(requestLog.getLogLocale()).isEqualTo(new Locale("en", "BE"));
        assertThat(requestLog.getLogTimeZone()).isEqualTo(timezone);
        assertThat(requestLog.getLogCookies()).isTrue();
        assertThat(requestLog.getLogServer()).isTrue();
        assertThat(requestLog.getLogLatency()).isTrue();
    }

    @Test
    public void accessLogCanBeEnabled() {
        bind("server.jetty.accesslog.enabled=true");
        JettyWebServer server = customizeAndGetServer();
        NCSARequestLog requestLog = getNCSARequestLog(server);
        assertThat(requestLog.getFilename()).isNull();
        assertThat(requestLog.isAppend()).isFalse();
        assertThat(requestLog.isExtended()).isFalse();
        assertThat(requestLog.getLogCookies()).isFalse();
        assertThat(requestLog.getLogServer()).isFalse();
        assertThat(requestLog.getLogLatency()).isFalse();
    }

    @Test
    public void setUseForwardHeaders() {
        this.serverProperties.setUseForwardHeaders(true);
        ConfigurableJettyWebServerFactory factory = Mockito.mock(ConfigurableJettyWebServerFactory.class);
        this.customizer.customize(factory);
        Mockito.verify(factory).setUseForwardHeaders(true);
    }

    @Test
    public void customizeMaxHttpHeaderSize() {
        bind("server.max-http-header-size=2048");
        JettyWebServer server = customizeAndGetServer();
        List<Integer> requestHeaderSizes = getRequestHeaderSizes(server);
        assertThat(requestHeaderSizes).containsOnly(2048);
    }

    @Test
    public void customMaxHttpHeaderSizeIgnoredIfNegative() {
        bind("server.max-http-header-size=-1");
        JettyWebServer server = customizeAndGetServer();
        List<Integer> requestHeaderSizes = getRequestHeaderSizes(server);
        assertThat(requestHeaderSizes).containsOnly(8192);
    }

    @Test
    public void customMaxHttpHeaderSizeIgnoredIfZero() {
        bind("server.max-http-header-size=0");
        JettyWebServer server = customizeAndGetServer();
        List<Integer> requestHeaderSizes = getRequestHeaderSizes(server);
        assertThat(requestHeaderSizes).containsOnly(8192);
    }
}

