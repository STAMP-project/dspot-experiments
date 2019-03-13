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
package io.helidon.config;


import ConfigSources.CompositeBuilder;
import ConfigSources.MapBuilder;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.test.infra.RestoreSystemPropertiesExt;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;


/**
 * Tests {@link ConfigSources}.
 */
@ExtendWith(RestoreSystemPropertiesExt.class)
public class ConfigSourcesTest {
    private static final String TEST_SYS_PROP_NAME = "this_is_my_property-ConfigSourcesTest";

    private static final String TEST_SYS_PROP_VALUE = "This Is My SYS PROPS Value.";

    @Test
    public void testEmptyDescription() {
        MatcherAssert.assertThat(ConfigSources.empty().description(), Matchers.is("Empty"));
    }

    @Test
    public void testEmptyLoad() {
        MatcherAssert.assertThat(ConfigSources.empty().load(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testEmptyIsAlwaysTheSameInstance() {
        MatcherAssert.assertThat(ConfigSources.empty(), Matchers.sameInstance(ConfigSources.empty()));
    }

    @Test
    public void testFromConfig() throws InterruptedException, MalformedURLException {
        Map<String, String> source = CollectionsHelper.mapOf("object.leaf", "value");
        ConfigSource originConfigSource = ConfigSources.create(source).build();
        Config originConfig = Config.builder(originConfigSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        ConfigSource configSource = ConfigSources.create(originConfig);
        Config copy = Config.builder(configSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(ConfigDiff.from(originConfig, copy).isEmpty(), Matchers.is(true));
    }

    @Test
    public void testPrefix() {
        MatcherAssert.assertThat(Config.create(ConfigSources.prefixed("security", ConfigSources.create(CollectionsHelper.mapOf("credentials.username", "libor")))).get("security.credentials.username").asString(), Matchers.is(ConfigValues.simpleValue("libor")));
    }

    @Test
    public void testPrefixDescription() {
        ConfigSource source = ConfigSources.create(CollectionsHelper.mapOf("credentials.username", "libor")).build();
        MatcherAssert.assertThat(ConfigSources.prefixed("security", source).description(), Matchers.is(("prefixed[security]:" + (source.description()))));
    }

    @Test
    public void testMapBuilderSupplierGetOnce() {
        ConfigSources.MapBuilder builder = ConfigSources.create(CollectionsHelper.mapOf());
        ConfigSource configSource = builder.get();
        MatcherAssert.assertThat(configSource, Matchers.sameInstance(builder.get()));
    }

    @Test
    public void testCompositeBuilderSupplierGetOnce() {
        ConfigSources.CompositeBuilder builder = ConfigSources.create();
        ConfigSource configSource = builder.get();
        MatcherAssert.assertThat(configSource, Matchers.sameInstance(builder.get()));
    }

    @Test
    public void testLoadNoSource() {
        ConfigSource source = ConfigSources.load().build();
        source.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(source.load(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testLoadSingleSource() {
        System.setProperty(ConfigSourcesTest.TEST_SYS_PROP_NAME, ConfigSourcesTest.TEST_SYS_PROP_VALUE);
        ConfigSource meta1 = ConfigSources.create(ObjectNode.builder().addList("sources", ListNode.builder().addObject(ObjectNode.builder().addValue("type", "system-properties").build()).build()).build());
        ConfigSource source = ConfigSources.load(meta1).build();
        source.init(Mockito.mock(ConfigContext.class));
        ObjectNode objectNode = source.load().get();
        MatcherAssert.assertThat(objectNode.get(ConfigSourcesTest.TEST_SYS_PROP_NAME), ValueNodeMatcher.valueNode(ConfigSourcesTest.TEST_SYS_PROP_VALUE));
    }

    @Test
    public void testLoadMultipleSource() {
        System.setProperty(ConfigSourcesTest.TEST_SYS_PROP_NAME, ConfigSourcesTest.TEST_SYS_PROP_VALUE);
        // meta1's `sources` property is used
        ConfigSource meta1 = ConfigSources.create(ObjectNode.builder().addList("sources", ListNode.builder().addObject(ObjectNode.builder().addValue("type", "classpath").addObject("properties", ObjectNode.builder().addValue("resource", "io/helidon/config/application.properties").build()).build()).build()).build());
        // meta2's `sources` property is ignored
        ConfigSource meta2 = ConfigSources.create(ObjectNode.builder().addList("sources", ListNode.builder().addObject(ObjectNode.builder().addValue("type", "system-properties").build()).build()).build());
        // meta1 has precedence over meta2
        ConfigSource source = ConfigSources.load(meta1, meta2).build();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser("text/x-java-properties")).thenReturn(Optional.of(ConfigParsers.properties()));
        source.init(context);
        ObjectNode objectNode = source.load().get();
        MatcherAssert.assertThat(objectNode.get(ConfigSourcesTest.TEST_SYS_PROP_NAME), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(objectNode.get("key1"), ValueNodeMatcher.valueNode("val1"));
    }
}

