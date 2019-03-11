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


import OverrideSource.OverrideData;
import io.helidon.common.reactive.Flow;
import io.helidon.config.ConfigException;
import io.helidon.config.OverrideSources;
import io.helidon.config.PollingStrategies;
import io.helidon.config.internal.ClasspathOverrideSource.ClasspathBuilder;
import io.helidon.config.spi.OverrideSource;
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


/**
 * Tests {@link ClasspathOverrideSource}.
 */
public class ClasspathOverrideSourceTest {
    @Test
    public void testDescriptionMandatory() {
        OverrideSource overrideSource = OverrideSources.classpath("overrides.properties").build();
        MatcherAssert.assertThat(overrideSource.description(), Is.is("ClasspathOverride[overrides.properties]"));
    }

    @Test
    public void testDescriptionOptional() {
        OverrideSource overrideSource = OverrideSources.classpath("overrides.properties").optional().build();
        MatcherAssert.assertThat(overrideSource.description(), Is.is("ClasspathOverride[overrides.properties]?"));
    }

    @Test
    public void testLoadNotExists() {
        ClasspathOverrideSource overrideSource = ((ClasspathOverrideSource) (OverrideSources.classpath("application.unknown").changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, overrideSource::load);
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Cannot load data from mandatory source"));
    }

    @Test
    public void testLoadExists() {
        OverrideSource overrideSource = OverrideSources.classpath("io/helidon/config/overrides.properties").build();
        Optional<OverrideSource.OverrideData> objectNode = overrideSource.load();
        MatcherAssert.assertThat(objectNode, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(objectNode.isPresent(), Is.is(true));
    }

    @Test
    public void testBuilder() {
        OverrideSource overrideSource = OverrideSources.classpath("overrides.properties").build();
        MatcherAssert.assertThat(overrideSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testBuilderWithMediaType() {
        OverrideSource overrideSource = OverrideSources.classpath("io/helidon/config/overrides.properties").build();
        MatcherAssert.assertThat(overrideSource, CoreMatchers.notNullValue());
    }

    @Test
    public void testBuilderPollingStrategyNotExistingResource() {
        ClasspathBuilder builder = ((ClasspathBuilder) (OverrideSources.classpath("not-exists").pollingStrategy(ClasspathOverrideSourceTest.TestingPathPollingStrategy::new)));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            MatcherAssert.assertThat(builder.pollingStrategyInternal(), Is.is(PollingStrategies.nop()));
        });
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Could not find a filesystem path for resource 'not-exists'"));
    }

    @Test
    public void testBuilderPollingStrategyExistingResource() throws URISyntaxException {
        ClasspathBuilder builder = ((ClasspathBuilder) (OverrideSources.classpath("io/helidon/config/overrides.properties").pollingStrategy(ClasspathOverrideSourceTest.TestingPathPollingStrategy::new)));
        MatcherAssert.assertThat(builder.pollingStrategyInternal(), Matchers.instanceOf(ClasspathOverrideSourceTest.TestingPathPollingStrategy.class));
        MatcherAssert.assertThat(((ClasspathOverrideSourceTest.TestingPathPollingStrategy) (builder.pollingStrategyInternal())).getPath(), Is.is(ClasspathSourceHelper.resourcePath("io/helidon/config/overrides.properties")));
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

