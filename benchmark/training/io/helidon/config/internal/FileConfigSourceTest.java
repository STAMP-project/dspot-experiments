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
import io.helidon.config.internal.FileConfigSource.FileBuilder;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigParser;
import io.helidon.config.spi.ConfigParserException;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.spi.PollingStrategy;
import io.helidon.config.test.infra.TemporaryFolderExt;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link FileConfigSource}.
 */
public class FileConfigSourceTest {
    private static final String TEST_MEDIA_TYPE = "my/media/type";

    private static final String RELATIVE_PATH_TO_RESOURCE = "/src/test/resources/";

    @RegisterExtension
    static TemporaryFolderExt folder = TemporaryFolderExt.build();

    @Test
    public void testDescriptionMandatory() {
        ConfigSource configSource = ConfigSources.file("application.conf").build();
        MatcherAssert.assertThat(configSource.description(), Is.is("FileConfig[application.conf]"));
    }

    @Test
    public void testDescriptionOptional() {
        ConfigSource configSource = ConfigSources.file("application.conf").optional().build();
        MatcherAssert.assertThat(configSource.description(), Is.is("FileConfig[application.conf]?"));
    }

    @Test
    public void testGetMediaTypeSet() {
        FileConfigSource configSource = ((FileConfigSource) (ConfigSources.file("application.conf").optional().mediaType(FileConfigSourceTest.TEST_MEDIA_TYPE).changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is(FileConfigSourceTest.TEST_MEDIA_TYPE));
    }

    @Test
    public void testGetMediaTypeGuessed() {
        FileConfigSource configSource = ((FileConfigSource) (ConfigSources.file("application.properties").optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is("text/x-java-properties"));
    }

    @Test
    public void testGetMediaTypeUnknown() {
        FileConfigSource configSource = ((FileConfigSource) (ConfigSources.file("application.unknown").optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        MatcherAssert.assertThat(configSource.mediaType(), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testLoadNotExists() {
        FileConfigSource configSource = ((FileConfigSource) (ConfigSources.file("application.unknown").changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            configSource.init(Mockito.mock(ConfigContext.class));
            configSource.load();
        });
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Cannot load data from mandatory source"));
    }

    @Test
    public void testLoadExists() {
        ConfigSource configSource = ConfigSources.file(((FileConfigSourceTest.getDir()) + "io/helidon/config/application.conf")).mediaType("application/hocon").build();
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
        Optional<ObjectNode> configNode = configSource.load();
        MatcherAssert.assertThat(configNode, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(configNode.isPresent(), Is.is(true));
    }

    @Test
    public void testBuilder() {
        ConfigSource configSource = ConfigSources.file("application.conf").build();
        MatcherAssert.assertThat(configSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testBuilderWithMediaType() {
        ConfigSource configSource = ConfigSources.file("application.conf").mediaType("application/hocon").build();
        MatcherAssert.assertThat(configSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testDataTimestamp() throws IOException {
        final String filename = "new-file";
        File file = FileConfigSourceTest.folder.newFile(filename);
        FileConfigSource fcs = new FileConfigSource(new FileBuilder(Paths.get(filename)), file.toPath());
        MatcherAssert.assertThat(fcs.dataStamp().isPresent(), Is.is(true));
        MatcherAssert.assertThat(fcs.dataStamp().get().length, Is.is(Matchers.greaterThan(0)));
    }

    @Test
    public void testBuilderPollingStrategy() {
        FileBuilder builder = ((FileBuilder) (ConfigSources.file("application.conf").pollingStrategy(FileConfigSourceTest.TestingPathPollingStrategy::new)));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), Matchers.instanceOf(FileConfigSourceTest.TestingPathPollingStrategy.class));
        MatcherAssert.assertThat(((FileConfigSourceTest.TestingPathPollingStrategy) (builder.pollingStrategyInternal())).getPath(), Is.is(Paths.get("application.conf")));
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

