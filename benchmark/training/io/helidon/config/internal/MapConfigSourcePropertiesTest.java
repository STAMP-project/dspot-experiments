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


import ConfigNode.ObjectNode;
import PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES;
import io.helidon.config.Config;
import io.helidon.config.ConfigParsers;
import io.helidon.config.ConfigSources;
import io.helidon.config.MissingValueException;
import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigNode;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.test.infra.RestoreSystemPropertiesExt;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link MapConfigSource} from {@link Properties} method.
 */
@ExtendWith(RestoreSystemPropertiesExt.class)
public class MapConfigSourcePropertiesTest {
    private static final String TEST_SYS_PROP_NAME = "this_is_my_property-MapConfigSourcePropertiesTest";

    private static final String TEST_SYS_PROP_VALUE = "This Is My SYS PROPS Value.";

    @Test
    public void testFromProperties() {
        Properties props = new Properties();
        props.setProperty(MapConfigSourcePropertiesTest.TEST_SYS_PROP_NAME, MapConfigSourcePropertiesTest.TEST_SYS_PROP_VALUE);
        ConfigSource configSource = ConfigSources.create(props).build();
        ConfigNode.ObjectNode objectNode = configSource.load().get();
        MatcherAssert.assertThat(objectNode.get(MapConfigSourcePropertiesTest.TEST_SYS_PROP_NAME), ValueNodeMatcher.valueNode(MapConfigSourcePropertiesTest.TEST_SYS_PROP_VALUE));
    }

    @Test
    public void testFromPropertiesDescription() {
        Properties props = new Properties();
        ConfigSource configSource = ConfigSources.create(props).build();
        MatcherAssert.assertThat(configSource.description(), Is.is("MapConfig[properties]"));
    }

    @Test
    public void testString() {
        Properties properties = new Properties();
        properties.setProperty("app.name", "app-name");
        Config config = Config.builder().sources(ConfigSources.create(properties)).build();
        MatcherAssert.assertThat(config.get("app.name").asString().get(), CoreMatchers.is("app-name"));
    }

    @Test
    public void testInt() {
        Properties properties = new Properties();
        properties.setProperty("app.port", "8080");
        Config config = Config.builder().sources(ConfigSources.create(properties)).build();
        MatcherAssert.assertThat(config.get("app").get("port").asInt().get(), Is.is(8080));
        MatcherAssert.assertThat(config.get("app.port").asInt().get(), Is.is(8080));
    }

    @Test
    public void testMissingValue() {
        Assertions.assertThrows(MissingValueException.class, () -> {
            Properties properties = new Properties();
            Config config = Config.builder().sources(ConfigSources.create(properties)).build();
            config.get("app.port").asInt().get();
        });
    }

    @Test
    public void testTraverse() {
        Properties properties = new Properties();
        properties.setProperty("app.name", "app-name");
        properties.setProperty("app.port", "8080");
        properties.setProperty("security", "on");
        Config config = Config.builder().sources(ConfigSources.create(properties)).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.traverse().count(), Is.is(4L));
        MatcherAssert.assertThat(config.traverse().map(Config::key).map(Config.Key::toString).collect(Collectors.toList()), Matchers.containsInAnyOrder("security", "app", "app.name", "app.port"));
    }

    @Test
    public void testChildren() {
        Properties properties = new Properties();
        properties.setProperty("app.name", "app-name");
        properties.setProperty("app.port", "8080");
        Config config = Config.builder().sources(ConfigSources.create(properties)).build().get("app");
        MatcherAssert.assertThat(config.asNodeList().get().size(), Is.is(2));
        MatcherAssert.assertThat(config.asNodeList().get().stream().map(Config::key).map(Config.Key::toString).collect(Collectors.toList()), Matchers.containsInAnyOrder("app.name", "app.port"));
    }

    @Test
    public void testMapToCustomClass() {
        Properties properties = new Properties();
        properties.setProperty("app.name", "app-name");
        Config config = Config.builder().sources(ConfigSources.create(properties)).build();
        MatcherAssert.assertThat(config.get("app.name").asString().map(MapConfigSourcePropertiesTest.Name::fromString).map(MapConfigSourcePropertiesTest.Name::getName), Is.is(Optional.of("app-name")));
    }

    @Test
    public void testMapToArray() {
        Properties properties = new Properties();
        properties.setProperty("app.0", "zero");
        properties.setProperty("app.1", "one");
        Config config = Config.builder().sources(ConfigSources.create(properties)).build();
        MatcherAssert.assertThat(config.get("app").asNodeList().get().size(), CoreMatchers.is(2));
    }

    @Test
    public void testMapToArrayWithParser() {
        final String PROPS = "" + ((((((("uri-array.0=http://localhost\n" + "uri-array.1=http://localhost\n") + "uri-array.2=http://localhost\n") + "uri-localhost=http://localhost\n") + "uri.array.0=http://localhost\n") + "uri.array.1=http://localhost\n") + "uri.array.2=http://localhost\n") + "uri.localhost=http://localhost\n");
        Config config = Config.builder().sources(ConfigSources.create(PROPS, MEDIA_TYPE_TEXT_JAVA_PROPERTIES)).addParser(ConfigParsers.properties()).build();
        MatcherAssert.assertThat(config.get("uri").asNodeList().get().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(config.get("uri.array").asNodeList().get().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(config.get("uri-array").asNodeList().get().size(), CoreMatchers.is(3));
    }

    private static final class Name {
        private final String name;

        private Name(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        static MapConfigSourcePropertiesTest.Name fromString(String name) {
            return new MapConfigSourcePropertiesTest.Name(name);
        }
    }
}

