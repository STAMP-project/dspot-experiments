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


import io.helidon.config.ConfigException;
import io.helidon.config.ConfigSources;
import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.test.infra.TemporaryFolderExt;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;


/**
 * Tests {@link DirectoryConfigSource}.
 */
public class DirectoryConfigSourceTest {
    @RegisterExtension
    static TemporaryFolderExt folder = TemporaryFolderExt.build();

    @Test
    public void testDescriptionMandatory() {
        ConfigSource configSource = ConfigSources.directory("secrets").build();
        MatcherAssert.assertThat(configSource.description(), Is.is("DirectoryConfig[secrets]"));
    }

    @Test
    public void testDescriptionOptional() {
        ConfigSource configSource = ConfigSources.directory("secrets").optional().build();
        MatcherAssert.assertThat(configSource.description(), Is.is("DirectoryConfig[secrets]?"));
    }

    @Test
    public void testLoadNoDirectory() {
        DirectoryConfigSource configSource = ((DirectoryConfigSource) (ConfigSources.directory("unknown").changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.dataStamp().get(), Is.is(Instant.MAX));
        ConfigException ex = Assertions.assertThrows(ConfigException.class, configSource::load);
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));
        MatcherAssert.assertThat(ex.getMessage(), CoreMatchers.startsWith("Cannot load data from mandatory source"));
    }

    @Test
    public void testLoadNoDirectoryOptional() {
        DirectoryConfigSource configSource = ((DirectoryConfigSource) (ConfigSources.directory("unknown").optional().changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.dataStamp().get(), Is.is(Instant.MAX));
        Optional<ObjectNode> loaded = configSource.load();
        MatcherAssert.assertThat(loaded, Is.is(Optional.empty()));
    }

    @Test
    public void testLoadEmptyDirectory() throws IOException {
        DirectoryConfigSource configSource = ((DirectoryConfigSource) (ConfigSources.directory(DirectoryConfigSourceTest.folder.newFolder().getAbsolutePath()).changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.dataStamp().get(), Matchers.both(Matchers.greaterThan(Instant.now().minusSeconds(3))).and(Matchers.lessThan(Instant.now().plusSeconds(3))));
        ObjectNode objectNode = configSource.load().get();
        MatcherAssert.assertThat(objectNode.entrySet(), Matchers.hasSize(0));
    }

    @Test
    public void testLoadDirectory() throws IOException {
        File folder = this.folder.newFolder();
        Files.write(Files.createFile(new File(folder, "username").toPath()), "libor".getBytes());
        Files.write(Files.createFile(new File(folder, "password").toPath()), "^ery$ecretP&ssword".getBytes());
        DirectoryConfigSource configSource = ((DirectoryConfigSource) (ConfigSources.directory(folder.getAbsolutePath()).changesExecutor(Runnable::run).changesMaxBuffer(1).build()));
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.dataStamp().get(), Matchers.both(Matchers.greaterThan(Instant.now().minusSeconds(3))).and(Matchers.lessThan(Instant.now().plusSeconds(3))));
        ObjectNode objectNode = configSource.load().get();
        MatcherAssert.assertThat(objectNode.entrySet(), Matchers.hasSize(2));
        MatcherAssert.assertThat(objectNode.get("username"), ValueNodeMatcher.valueNode("libor"));
        MatcherAssert.assertThat(objectNode.get("password"), ValueNodeMatcher.valueNode("^ery$ecretP&ssword"));
    }
}

