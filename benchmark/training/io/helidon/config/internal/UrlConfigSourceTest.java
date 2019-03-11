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


import io.helidon.common.reactive.Flow;
import io.helidon.config.ConfigException;
import io.helidon.config.ConfigSources;
import io.helidon.config.RetryPolicies;
import io.helidon.config.internal.UrlConfigSource.UrlBuilder;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.spi.PollingStrategy;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link UrlConfigSource}.
 */
public class UrlConfigSourceTest {
    private static final String TEST_MEDIA_TYPE = "my/media/type";

    @Test
    public void testDescriptionMandatory() throws MalformedURLException {
        ConfigSource configSource = ConfigSources.url(new URL("http://config-service/application.json")).build();
        MatcherAssert.assertThat(configSource.description(), Is.is("UrlConfig[http://config-service/application.json]"));
    }

    @Test
    public void testDescriptionOptional() throws MalformedURLException {
        ConfigSource configSource = ConfigSources.url(new URL("http://config-service/application.json")).optional().build();
        MatcherAssert.assertThat(configSource.description(), Is.is("UrlConfig[http://config-service/application.json]?"));
    }

    @Test
    public void testGetMediaTypeSet() throws MalformedURLException {
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL("http://config-service/application.json")).optional().mediaType(UrlConfigSourceTest.TEST_MEDIA_TYPE).changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is(UrlConfigSourceTest.TEST_MEDIA_TYPE));
    }

    @Test
    public void testGetMediaTypeGuessed() throws MalformedURLException {
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL("http://config-service/application.json")).optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testGetMediaTypeUnknown() throws MalformedURLException {
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL("http://config-service/application.unknown")).optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testLoadNotExists() throws MalformedURLException {
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL("http://config-service/application.unknown")).changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            configSource.init(Mockito.mock(ConfigContext.class));
            configSource.load();
        });
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Cannot load data from mandatory source"));
    }

    @Test
    public void testLoadNotExistsWithRetries() throws MalformedURLException {
        UrlConfigSource configSource = ((UrlConfigSource) (ConfigSources.url(new URL("http://config-service/application.unknown")).changesExecutor(Runnable::run).changesMaxBuffer(1).retryPolicy(RetryPolicies.repeat(2).delay(Duration.ofMillis(10)).delayFactor(2).overallTimeout(Duration.ofSeconds(1))).build()));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            configSource.init(Mockito.mock(ConfigContext.class));
            configSource.load();
        });
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Cannot load data from mandatory source"));
    }

    @Test
    public void testBuilderPollingStrategy() throws MalformedURLException {
        URL url = new URL("http://config-service/application.unknown");
        UrlBuilder builder = ((UrlBuilder) (ConfigSources.url(url).pollingStrategy(UrlConfigSourceTest.TestingPathPollingStrategy::new)));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), Matchers.instanceOf(UrlConfigSourceTest.TestingPathPollingStrategy.class));
        MatcherAssert.assertThat(((UrlConfigSourceTest.TestingPathPollingStrategy) (builder.pollingStrategyInternal())).getUrl(), Is.is(url));
    }

    private static class TestingPathPollingStrategy implements PollingStrategy {
        private final URL url;

        public TestingPathPollingStrategy(URL url) {
            this.url = url;
            MatcherAssert.assertThat(url, CoreMatchers.notNullValue());
        }

        @Override
        public Flow.Publisher<PollingEvent> ticks() {
            return Flow.Subscriber::onComplete;
        }

        public URL getUrl() {
            return url;
        }
    }
}

