/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.internal;


import ConfigParser.Content;
import com.xebialabs.restito.server.StubServer;
import io.helidon.config.ConfigHelperTest;
import io.helidon.config.ConfigParsers;
import io.helidon.config.ConfigSources;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigParser;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests {@link UrlConfigSource} with running {@link StubServer Restito Server}.
 */
public class UrlConfigSourceServerMockTest {
    private static final String TEST_MEDIA_TYPE = "my/media/type";

    private static final String TEST_CONFIG = "test-key = test-value";

    private StubServer server;

    @Test
    public void testDataTimestamp() throws IOException {
        whenHttp(server).match(method(HEAD), uri("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.properties", server.getPort()))).build()));
        Optional<Instant> dataStamp = configSource.dataStamp();
        MatcherAssert.assertThat(dataStamp.get(), Is.is(Instant.parse("2017-06-10T10:14:02.00Z")));
        verifyHttp(server).once(method(HEAD), uri("/application.properties"));
    }

    @Test
    public void testDoNotReloadSameContent() throws IOException {
        // HEAD
        whenHttp(server).match(method(HEAD), uri("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        // GET
        whenHttp(server).match(get("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser("text/x-java-properties")).thenReturn(Optional.of(ConfigParsers.properties()));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.properties", server.getPort()))).build()));
        // INIT
        configSource.init(context);
        // HEAD & GET
        configSource.load();
        // just HEAD - same "Last-Modified"
        configSource.load();
        verifyHttp(server).times(2, method(HEAD), uri("/application.properties"));
        verifyHttp(server).once(method(Method.GET), uri("/application.properties"));
    }

    @Test
    public void testDoReloadChangedContent() throws IOException {
        // HEAD
        whenHttp(server).match(method(HEAD), uri("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        // GET
        whenHttp(server).match(get("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser("text/x-java-properties")).thenReturn(Optional.of(ConfigParsers.properties()));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.properties", server.getPort()))).build()));
        // INIT
        configSource.init(context);
        // HEAD & GET
        configSource.load();
        // HEAD
        whenHttp(server).match(method(HEAD), uri("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:03 GMT"), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        // HEAD & GET - "Last-Modified" changed
        configSource.load();
        verifyHttp(server).times(2, method(HEAD), uri("/application.properties"));
        verifyHttp(server).times(2, method(Method.GET), uri("/application.properties"));
    }

    @Test
    public void testContentMediaTypeSet() throws IOException {
        whenHttp(server).match(get("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.properties", server.getPort()))).mediaType(UrlConfigSourceServerMockTest.TEST_MEDIA_TYPE).build()));
        ConfigParser.Content content = configSource.content();
        MatcherAssert.assertThat(content.mediaType(), Is.is(UrlConfigSourceServerMockTest.TEST_MEDIA_TYPE));
        MatcherAssert.assertThat(ConfigHelperTest.readerToString(content.asReadable()), Is.is(UrlConfigSourceServerMockTest.TEST_CONFIG));
        verifyHttp(server).once(method(Method.GET), uri("/application.properties"));
    }

    @Test
    public void testContentMediaTypeFromResponse() throws IOException {
        whenHttp(server).match(get("/application.properties")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.properties", server.getPort()))).build()));
        ConfigParser.Content content = configSource.content();
        MatcherAssert.assertThat(content.mediaType(), Is.is(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES));
        MatcherAssert.assertThat(ConfigHelperTest.readerToString(content.asReadable()), Is.is(UrlConfigSourceServerMockTest.TEST_CONFIG));
        verifyHttp(server).once(method(Method.GET), uri("/application.properties"));
    }

    @Test
    public void testContentMediaTypeGuessed() throws IOException {
        whenHttp(server).match(get("/application.properties")).then(status(OK_200), contentType(UrlConfigSourceServerMockTest.TEST_MEDIA_TYPE), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.properties", server.getPort()))).build()));
        ConfigParser.Content content = configSource.content();
        MatcherAssert.assertThat(content.mediaType(), Is.is(UrlConfigSourceServerMockTest.TEST_MEDIA_TYPE));
        MatcherAssert.assertThat(ConfigHelperTest.readerToString(content.asReadable()), Is.is(UrlConfigSourceServerMockTest.TEST_CONFIG));
        verifyHttp(server).once(method(Method.GET), uri("/application.properties"));
    }

    @Test
    public void testContentMediaTypeUnknown() throws IOException {
        whenHttp(server).match(get("/application.unknown")).then(status(OK_200), stringContent(UrlConfigSourceServerMockTest.TEST_CONFIG));
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL(String.format("http://127.0.0.1:%d/application.unknown", server.getPort()))).build()));
        MatcherAssert.assertThat(configSource.content().mediaType(), Is.is(Matchers.nullValue()));
    }
}

