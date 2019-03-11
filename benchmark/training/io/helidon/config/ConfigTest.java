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


import io.helidon.config.Config.Key;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigSourceTest;
import io.helidon.config.test.infra.RestoreSystemPropertiesExt;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * General {@link Config} tests.
 *
 * @see ConfigObjectImplTest
 * @see ConfigListImplTest
 * @see ConfigMissingImplTest
 * @see ConfigLeafImplTest
 */
@ExtendWith(RestoreSystemPropertiesExt.class)
public class ConfigTest {
    private static final String TEST_SYS_PROP_NAME = "this_is_my_property-ConfigTest";

    private static final String TEST_SYS_PROP_VALUE = "This Is My SYS PROPS Value.";

    private static final boolean LOG = false;

    private static final String OBJECT_VALUE_PREFIX = "object-";

    private static final String LIST_VALUE_PREFIX = "list-";

    @Test
    public void testCreateKeyNotSet() {
        testKeyNotSet(Config.create());
    }

    @Test
    public void testBuilderDefaultConfigSourceKeyNotSet() {
        testKeyNotSet(Config.builder().build());
    }

    @Test
    public void testCreateKeyFromSysProps() {
        System.setProperty(ConfigTest.TEST_SYS_PROP_NAME, ConfigTest.TEST_SYS_PROP_VALUE);
        testKeyFromSysProps(Config.create());
    }

    @Test
    public void testBuilderDefaultConfigSourceKeyFromSysProps() {
        System.setProperty(ConfigTest.TEST_SYS_PROP_NAME, ConfigTest.TEST_SYS_PROP_VALUE);
        testKeyFromSysProps(Config.builder().build());
    }

    @Test
    public void testCreateKeyFromEnvVars() {
        System.setProperty(ConfigSourceTest.TEST_ENV_VAR_NAME, "This value is not used, but from Env Vars, see pom.xml!");
        testKeyFromEnvVars(Config.create());
    }

    @Test
    public void testBuilderDefaultConfigSourceKeyFromEnvVars() {
        System.setProperty(ConfigSourceTest.TEST_ENV_VAR_NAME, "This value is not used, but from Env Vars, see pom.xml!");
        testKeyFromEnvVars(Config.builder().build());
    }

    @Test
    public void testKeyAndTypeAndGet() {
        Config config = ConfigTest.createTestConfig(3);
        testKeyAndTypeAndGet(config);
    }

    @Test
    public void testTraverseOnObjectNode() {
        Config config = ConfigTest.createTestConfig(3).get("object-1");
        // full traverse -> count
        List<Key> allSubKeys = config.traverse().peek(( node) -> log((((node.type()) + "\t") + (node.key())))).map(Config::key).collect(Collectors.toList());
        Assert.assertThat(allSubKeys, Matchers.hasSize(33));
        ConfigTest.log("--------");
        // traverse with predicate -> assert keys
        List<String> noListSubKeys = // do NOT go into LIST nodes
        config.traverse(( node) -> (node.type()) != Config.Type.LIST).peek(( node) -> log(((("\"" + (node.key())) + "\", // ") + (node.type())))).map(Config::key).map(Key::toString).collect(Collectors.toList());
        Assert.assertThat(noListSubKeys, // OBJECT
        // VALUE
        // OBJECT
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        Matchers.containsInAnyOrder("object-1.object-2", "object-1.object-2.double-3", "object-1.object-2.object-3", "object-1.object-2.long-3", "object-1.object-2.text-3", "object-1.object-2.bool-3", "object-1.object-2.int-3", "object-1.double-2", "object-1.bool-2", "object-1.long-2", "object-1.text-2", "object-1.int-2"));
    }

    @Test
    public void testTraverseOnListNode() {
        Config config = ConfigTest.createTestConfig(3).get("list-1");
        // full traverse -> count
        List<Key> allSubKeys = config.traverse().peek(( node) -> log((((node.type()) + "\t") + (node.key())))).map(Config::key).collect(Collectors.toList());
        Assert.assertThat(allSubKeys, Matchers.hasSize(33));
        ConfigTest.log("--------");
        // traverse with predicate -> assert keys
        List<String> noObjectSubKeys = // do NOT go into OBJECT nodes
        config.traverse(( node) -> (node.type()) != Config.Type.OBJECT).peek(( node) -> log(((("\"" + (node.key())) + "\", // ") + (node.type())))).map(Config::key).map(Key::toString).collect(Collectors.toList());
        Assert.assertThat(noObjectSubKeys, // VALUE
        // LIST
        // VALUE
        // LIST
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // LIST
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // VALUE
        // LIST
        // VALUE
        // VALUE
        // VALUE
        Matchers.containsInAnyOrder("list-1.0", "list-1.2", "list-1.2.0", "list-1.2.2", "list-1.2.3", "list-1.2.4", "list-1.2.5", "list-1.2.6", "list-1.2.7", "list-1.2.7.0", "list-1.2.7.1", "list-1.2.7.2", "list-1.3", "list-1.4", "list-1.5", "list-1.6", "list-1.7", "list-1.7.0", "list-1.7.1", "list-1.7.2"));
    }

    @Test
    public void testAsMapOnObjectNode() {
        Config config = ConfigTest.createTestConfig(3).get("object-1");
        // full map -> count
        Map<String, String> allLeafs = config.asMap().get();
        allLeafs.forEach(( key, value) -> ConfigTest.log(key));
        Assert.assertThat(allLeafs.keySet(), Matchers.hasSize(24));
        ConfigTest.log("--------");
        // sub-map -> assert keys
        Map<String, String> subLeafs = config.get("object-2").asMap().get();
        subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
        Assert.assertThat(subLeafs.keySet(), Matchers.containsInAnyOrder("object-1.object-2.bool-3", "object-1.object-2.int-3", "object-1.object-2.double-3", "object-1.object-2.str-list-3.2", "object-1.object-2.str-list-3.1", "object-1.object-2.str-list-3.0", "object-1.object-2.text-3", "object-1.object-2.long-3"));
    }

    @Test
    public void testAsMapOnListNode() {
        Config config = ConfigTest.createTestConfig(3).get("list-1");
        // full map -> count
        Map<String, String> allLeafs = config.asMap().get();
        allLeafs.forEach(( key, value) -> ConfigTest.log(key));
        Assert.assertThat(allLeafs.keySet(), Matchers.hasSize(24));
        ConfigTest.log("--------");
        // sub-map -> assert keys
        Map<String, String> subLeafs = config.get("2").asMap().get();
        subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
        Assert.assertThat(subLeafs.keySet(), Matchers.containsInAnyOrder("list-1.2.0", "list-1.2.3", "list-1.2.5", "list-1.2.4", "list-1.2.6", "list-1.2.7.1", "list-1.2.7.2", "list-1.2.7.0"));
    }

    @Test
    public void testAsPropertiesOnObjectNode() {
        Config config = ConfigTest.createTestConfig(3).get("object-1");
        // full properties -> count
        Properties allLeafs = config.as(Properties.class).get();
        allLeafs.forEach(( key, value) -> ConfigTest.log(key));
        Assert.assertThat(allLeafs.keySet(), Matchers.hasSize(24));
        ConfigTest.log("--------");
        // sub-properties -> assert keys
        Properties subLeafs = config.get("object-2").as(Properties.class).get();
        subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
        Assert.assertThat(subLeafs.keySet(), Matchers.containsInAnyOrder("object-1.object-2.bool-3", "object-1.object-2.int-3", "object-1.object-2.double-3", "object-1.object-2.str-list-3.2", "object-1.object-2.str-list-3.1", "object-1.object-2.str-list-3.0", "object-1.object-2.text-3", "object-1.object-2.long-3"));
    }

    @Test
    public void testAsPropertiesOnListNode() {
        Config config = ConfigTest.createTestConfig(3).get("list-1");
        // full properties -> count
        Properties allLeafs = config.as(Properties.class).get();
        allLeafs.forEach(( key, value) -> ConfigTest.log(key));
        Assert.assertThat(allLeafs.keySet(), Matchers.hasSize(24));
        ConfigTest.log("--------");
        // sub-properties -> assert keys
        Properties subLeafs = config.get("2").as(Properties.class).get();
        subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
        Assert.assertThat(subLeafs.keySet(), Matchers.containsInAnyOrder("list-1.2.0", "list-1.2.3", "list-1.2.5", "list-1.2.4", "list-1.2.6", "list-1.2.7.1", "list-1.2.7.2", "list-1.2.7.0"));
    }

    @Test
    public void testDetachOnObjectNode() {
        Config config = ConfigTest.createTestConfig(3).detach();
        // ///////////
        // 1ST DETACH
        {
            config = config.get("object-1").detach();
            {
                // detach & map
                Map<String, String> subLeafs = config.asMap().get();
                subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
                // expected size
                Assert.assertThat(subLeafs.keySet(), Matchers.hasSize(24));
                // no key prefixed by 'object-1'
                Assert.assertThat(subLeafs.keySet().stream().filter(( key) -> key.startsWith("object1")).collect(Collectors.toList()), Matchers.is(Matchers.empty()));
            }
            ConfigTest.log("--------");
            {
                // detach & traverse
                List<String> subKeys = config.traverse().peek(( node) -> log(((("\"" + (node.key())) + "\", // ") + (node.type())))).map(Config::key).map(Key::toString).collect(Collectors.toList());
                // expected size
                Assert.assertThat(subKeys, Matchers.hasSize(33));
                // no key prefixed by 'object-1'
                Assert.assertThat(subKeys.stream().filter(( key) -> key.startsWith("object1")).collect(Collectors.toList()), Matchers.is(Matchers.empty()));
            }
        }
        // ///////////
        // 2ND DETACH
        {
            config = config.get("object-2").detach();
            {
                // detach & map
                Map<String, String> subLeafs = config.asMap().get();
                subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
                Assert.assertThat(subLeafs.keySet(), Matchers.containsInAnyOrder("bool-3", "int-3", "double-3", "str-list-3.2", "str-list-3.1", "str-list-3.0", "text-3", "long-3"));
            }
            ConfigTest.log("--------");
            {
                // detach & traverse
                List<String> subKeys = config.traverse().peek(( node) -> log(((("\"" + (node.key())) + "\", // ") + (node.type())))).map(Config::key).map(Key::toString).collect(Collectors.toList());
                Assert.assertThat(subKeys, // VALUE
                // OBJECT
                // LIST
                // VALUE
                // VALUE
                // VALUE
                // VALUE
                // VALUE
                // VALUE
                // LIST
                // VALUE
                Matchers.containsInAnyOrder("double-3", "object-3", "str-list-3", "str-list-3.0", "str-list-3.1", "str-list-3.2", "long-3", "text-3", "bool-3", "list-3", "int-3"));
            }
        }
    }

    @Test
    public void testDetachOnListNode() {
        Config config = ConfigTest.createTestConfig(3).detach();
        // ///////////
        // 1ST DETACH
        {
            config = config.get("list-1").detach();
            {
                // detach & map
                Map<String, String> subLeafs = config.asMap().get();
                subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
                // expected size
                Assert.assertThat(subLeafs.keySet(), Matchers.hasSize(24));
                // no key prefixed by 'object-1'
                Assert.assertThat(subLeafs.keySet().stream().filter(( key) -> key.startsWith("list")).collect(Collectors.toList()), Matchers.is(Matchers.empty()));
            }
            ConfigTest.log("--------");
            {
                // detach & traverse
                List<String> subKeys = config.traverse().peek(( node) -> log(((("\"" + (node.key())) + "\", // ") + (node.type())))).map(Config::key).map(Key::toString).collect(Collectors.toList());
                // expected size
                Assert.assertThat(subKeys, Matchers.hasSize(33));
                // no key prefixed by 'object-1'
                Assert.assertThat(subKeys.stream().filter(( key) -> key.startsWith("list")).collect(Collectors.toList()), Matchers.is(Matchers.empty()));
            }
        }
        ConfigTest.log("========");
        // ///////////
        // 2ND DETACH
        {
            config = config.get("2").detach();
            {
                // detach & map
                Map<String, String> subLeafs = config.asMap().get();
                subLeafs.forEach(( key, value) -> ConfigTest.log((("\"" + key) + "\",")));
                Assert.assertThat(subLeafs.keySet(), Matchers.containsInAnyOrder("0", "3", "4", "5", "6", "7.0", "7.1", "7.2"));
            }
            ConfigTest.log("--------");
            {
                // detach & traverse
                List<String> subKeys = config.traverse().peek(( node) -> log(((("\"" + (node.key())) + "\", // ") + (node.type())))).map(Config::key).map(Key::toString).collect(Collectors.toList());
                Assert.assertThat(subKeys, // VALUE
                // OBJECT
                // LIST
                // VALUE
                // VALUE
                // VALUE
                // VALUE
                // LIST
                // VALUE
                // VALUE
                // VALUE
                Matchers.containsInAnyOrder("0", "1", "2", "3", "4", "5", "6", "7", "7.0", "7.1", "7.2"));
            }
        }
    }

    /**
     * Similar test is copied to in {@code config-hocon} module, {@code HoconConfigParserTest} class,
     * method {@code testConfigKeyEscapedNameComplex};
     * and in {@code integration-tests} module, {@code AbstractComplexConfigTest} class,
     * method {@code testConfigKeyEscapedNameComplex}.
     */
    @Test
    public void testConfigKeyEscapedNameComplex() {
        Config config = Config.builder(ConfigSources.create(ObjectNode.builder().addObject(Key.escapeName("oracle.com"), ObjectNode.builder().addValue("prop1", "val1").addValue("prop2", "val2").build()).addObject("oracle", ObjectNode.builder().addValue("com", "1").addValue("cz", "2").build()).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableParserServices().disableMapperServices().disableFilterServices().build();
        // key
        Assert.assertThat(config.get("oracle~1com.prop1").asString(), Matchers.is(ConfigValues.simpleValue("val1")));
        Assert.assertThat(config.get("oracle~1com.prop2").asString(), Matchers.is(ConfigValues.simpleValue("val2")));
        Assert.assertThat(config.get("oracle.com").asString(), Matchers.is(ConfigValues.simpleValue("1")));
        Assert.assertThat(config.get("oracle.cz").asString(), Matchers.is(ConfigValues.simpleValue("2")));
        // name
        Assert.assertThat(config.get("oracle~1com").name(), Matchers.is("oracle.com"));
        Assert.assertThat(config.get("oracle~1com.prop1").name(), Matchers.is("prop1"));
        Assert.assertThat(config.get("oracle~1com.prop2").name(), Matchers.is("prop2"));
        Assert.assertThat(config.get("oracle").name(), Matchers.is("oracle"));
        Assert.assertThat(config.get("oracle.com").name(), Matchers.is("com"));
        Assert.assertThat(config.get("oracle.cz").name(), Matchers.is("cz"));
        // child nodes
        List<Config> children = config.asNodeList().get();
        Assert.assertThat(children, Matchers.hasSize(2));
        Assert.assertThat(children.stream().map(Config::name).collect(Collectors.toSet()), Matchers.containsInAnyOrder("oracle.com", "oracle"));
        // traverse
        Set<String> keys = config.traverse().map(Config::key).map(Key::toString).collect(Collectors.toSet());
        Assert.assertThat(keys, Matchers.hasSize(6));
        Assert.assertThat(keys, Matchers.containsInAnyOrder("oracle~1com", "oracle~1com.prop1", "oracle~1com.prop2", "oracle", "oracle.com", "oracle.cz"));
        // map
        Map<String, String> map = config.asMap().get();
        Assert.assertThat(map.keySet(), Matchers.hasSize(4));
        Assert.assertThat(map.get("oracle~1com.prop1"), Matchers.is("val1"));
        Assert.assertThat(map.get("oracle~1com.prop2"), Matchers.is("val2"));
        Assert.assertThat(map.get("oracle.com"), Matchers.is("1"));
        Assert.assertThat(map.get("oracle.cz"), Matchers.is("2"));
    }

    @ExtendWith(RestoreSystemPropertiesExt.class)
    @Test
    public void testConfigKeyEscapeUnescapeName() {
        testConfigKeyEscapeUnescapeName("", "");
        testConfigKeyEscapeUnescapeName("~", "~0");
        testConfigKeyEscapeUnescapeName(".", "~1");
        testConfigKeyEscapeUnescapeName("~.", "~0~1");
        testConfigKeyEscapeUnescapeName(".~", "~1~0");
        testConfigKeyEscapeUnescapeName("qwerty", "qwerty");
        testConfigKeyEscapeUnescapeName(".qwerty~", "~1qwerty~0");
        testConfigKeyEscapeUnescapeName("${qwerty}", "${qwerty}");
        testConfigKeyEscapeUnescapeName("${qwe.rty}", "${qwe~1rty}");
        testConfigKeyEscapeUnescapeName("${qwe.rty}.asd", "${qwe~1rty}~1asd");
    }

    @Test
    public void testComplexNodesWithSimpleValues() {
        /* This method uses variants of the methods for creating test configs and 
        objects to assign values to the complex nodes (object- and list-type)
        and make sure the values are as expected.
         */
        final String valueQual = "xx";// to help avoid confusion between key and value

        final String obj1Value = ((ConfigTest.OBJECT_VALUE_PREFIX) + valueQual) + "-2";
        final String obj1_2Value = obj1Value + "-3";
        Config config = ConfigTest.createTestConfig(3, valueQual);
        testKeyAndTypeAndGet(config);
        Assert.assertThat(config.get("object-1").asString(), Matchers.is(ConfigValues.simpleValue(obj1Value)));
        Assert.assertThat(config.get("object-1.object-2").asString(), Matchers.is(ConfigValues.simpleValue(obj1_2Value)));
        Assert.assertThat(config.get("list-1").asString(), Matchers.is(ConfigValues.simpleValue((((ConfigTest.LIST_VALUE_PREFIX) + valueQual) + "-2"))));
    }
}

