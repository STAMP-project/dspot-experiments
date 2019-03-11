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


import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.MissingValueException;
import io.helidon.config.spi.ConfigSource;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link MapConfigSource}.
 */
public class MapConfigSourceTest {
    @Test
    public void testDescription() throws MalformedURLException {
        ConfigSource configSource = ConfigSources.create(CollectionsHelper.mapOf()).build();
        MatcherAssert.assertThat(configSource.description(), Is.is("MapConfig[map]"));
    }

    @Test
    public void testString() {
        Map<String, String> map = CollectionsHelper.mapOf("app.name", "app-name");
        Config config = Config.builder().sources(ConfigSources.create(map)).build();
        MatcherAssert.assertThat(config.get("app.name").asString().get(), Is.is("app-name"));
    }

    @Test
    public void testInt() {
        Map<String, String> map = CollectionsHelper.mapOf("app.port", "8080");
        Config config = Config.builder().sources(ConfigSources.create(map)).build();
        MatcherAssert.assertThat(config.get("app").get("port").asInt().get(), Is.is(8080));
        MatcherAssert.assertThat(config.get("app.port").asInt().get(), Is.is(8080));
    }

    @Test
    public void testMissingValue() {
        Map<String, String> map = CollectionsHelper.mapOf();
        Assertions.assertThrows(MissingValueException.class, () -> {
            Config config = Config.builder().sources(ConfigSources.create(map)).build();
            config.get("app.port").asInt().get();
        });
    }

    @Test
    public void testTraverse() {
        Map<String, String> map = CollectionsHelper.mapOf("app.name", "app-name", "app.port", "8080", "security", "on");
        Config config = Config.builder().sources(ConfigSources.create(map)).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.traverse().count(), Is.is(4L));
        MatcherAssert.assertThat(config.traverse().map(Config::key).map(Config.Key::toString).collect(Collectors.toList()), Matchers.containsInAnyOrder("security", "app", "app.name", "app.port"));
    }

    @Test
    public void testChildren() {
        Map<String, String> map = CollectionsHelper.mapOf("app.name", "app-name", "app.port", "8080");
        Config config = Config.builder().sources(ConfigSources.create(map)).build().get("app");
        MatcherAssert.assertThat(config.asNodeList().get().size(), Is.is(2));
        MatcherAssert.assertThat(config.asNodeList().get().stream().map(Config::key).map(Config.Key::toString).collect(Collectors.toList()), Matchers.containsInAnyOrder("app.name", "app.port"));
    }

    @Test
    public void testMapToCustomClass() {
        Map<String, String> map = CollectionsHelper.mapOf("app.name", "app-name");
        Config config = Config.builder().sources(ConfigSources.create(map)).build();
        MatcherAssert.assertThat(config.get("app.name").asString().map(MapConfigSourceTest.Name::fromString).map(MapConfigSourceTest.Name::getName), Is.is(Optional.of("app-name")));
    }

    private static class Name {
        private String name;

        private Name(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        static MapConfigSourceTest.Name fromString(String name) {
            return new MapConfigSourceTest.Name(name);
        }
    }
}

