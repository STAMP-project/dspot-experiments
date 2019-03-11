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
import io.helidon.config.ConfigHelper;
import io.helidon.config.ConfigSources;
import io.helidon.config.PollingStrategies;
import io.helidon.config.internal.ClasspathConfigSource.ClasspathBuilder;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigParser;
import io.helidon.config.spi.ConfigParserException;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.spi.PollingStrategy;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link ClasspathConfigSource}.
 */
public class ClasspathConfigSourceTest {
    private static final String TEST_MEDIA_TYPE = "my/media/type";

    @Test
    public void testDescriptionMandatory() {
        ConfigSource configSource = ConfigSources.classpath("application.conf").build();
        MatcherAssert.assertThat(configSource.description(), Matchers.is("ClasspathConfig[application.conf]"));
    }

    @Test
    public void testDescriptionOptional() {
        ConfigSource configSource = ConfigSources.classpath("application.conf").optional().build();
        MatcherAssert.assertThat(configSource.description(), Matchers.is("ClasspathConfig[application.conf]?"));
    }

    @Test
    public void testGetMediaTypeSet() {
        ClasspathConfigSource configSource = ((ClasspathConfigSource) (ConfigSources.classpath("application.conf").optional().mediaType(ClasspathConfigSourceTest.TEST_MEDIA_TYPE).changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Matchers.is(ClasspathConfigSourceTest.TEST_MEDIA_TYPE));
    }

    @Test
    public void testGetMediaTypeGuessed() {
        ClasspathConfigSource configSource = ((ClasspathConfigSource) (ConfigSources.classpath("application.properties").optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is("text/x-java-properties"));
    }

    @Test
    public void testGetMediaTypeUnknown() {
        ClasspathConfigSource configSource = ((ClasspathConfigSource) (ConfigSources.classpath("application.unknown").optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testLoadNotExists() {
        ClasspathConfigSource configSource = ((ClasspathConfigSource) (ConfigSources.classpath("application.unknown").changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            configSource.init(Mockito.mock(ConfigContext.class));
            configSource.load();
        });
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Cannot load data from mandatory source"));
    }

    @Test
    public void testLoadExists() {
        ConfigSource configSource = ConfigSources.classpath("io/helidon/config/application.conf").mediaType("application/hocon").build();
        configSource.init(( content) -> Optional.of(new ConfigParser() {
            @Override
            public Set<String> supportedMediaTypes() {
                return new HashSet<String>() {
                    {
                        add("application/hocon");
                    }
                };
            }

            @Override
            public ObjectNode parse(Content content) throws ConfigParserException {
                assertThat(content, notNullValue());
                assertThat(content.mediaType(), is("application/hocon"));
                try {
                    assertThat(((char) (ConfigHelper.createReader(content.asReadable()).read())), is('#'));
                } catch ( e) {
                    fail("Cannot read from source's reader");
                }
                return ObjectNode.empty();
            }
        }));
        Optional<ObjectNode> objectNode = configSource.load();
        MatcherAssert.assertThat(objectNode, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(objectNode.isPresent(), Matchers.is(true));
    }

    @Test
    public void testBuilder() {
        ConfigSource configSource = ConfigSources.classpath("application.conf").build();
        MatcherAssert.assertThat(configSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testBuilderWithMediaType() {
        ConfigSource configSource = ConfigSources.classpath("io/helidon/config/application.conf").mediaType("application/hocon").build();
        MatcherAssert.assertThat(configSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testBuilderPollingStrategyNotExistingResource() {
        ClasspathBuilder builder = ((ClasspathBuilder) (ConfigSources.classpath("not-exists").pollingStrategy(ClasspathConfigSourceTest.TestingPathPollingStrategy::new)));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            MatcherAssert.assertThat(builder.pollingStrategyInternal(), Matchers.is(PollingStrategies.nop()));
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Could not find a filesystem path for resource 'not-exists'"));
    }

    @Test
    public void testBuilderPollingStrategyExistingResource() throws URISyntaxException {
        ClasspathBuilder builder = ((ClasspathBuilder) (ConfigSources.classpath("io/helidon/config/application.conf").pollingStrategy(ClasspathConfigSourceTest.TestingPathPollingStrategy::new)));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), Matchers.instanceOf(ClasspathConfigSourceTest.TestingPathPollingStrategy.class));
        MatcherAssert.assertThat(((ClasspathConfigSourceTest.TestingPathPollingStrategy) (builder.pollingStrategyInternal())).getPath(), Matchers.is(ClasspathSourceHelper.resourcePath("io/helidon/config/application.conf")));
    }

    private static class TestingPathPollingStrategy implements PollingStrategy {
        private final Path path;

        public TestingPathPollingStrategy(Path path) {
            this.path = path;
            MatcherAssert.assertThat(path, CoreMatchers.notNullValue());
        }

        @Override
        public Flow.Publisher<PollingEvent> ticks() {
            return Flow.Subscriber::onComplete;
        }

        public Path getPath() {
            return path;
        }
    }
}

