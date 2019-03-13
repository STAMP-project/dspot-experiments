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
package io.helidon.config.tests;


import io.helidon.config.Config;
import io.helidon.config.ConfigValues;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Provides complete Config API test that is supposed to be used to test appropriate config parsers.
 */
public abstract class AbstractComplexConfigTest {
    @Test
    public void testStringInRoot() {
        testString("text-");
    }

    @Test
    public void testStringInObject() {
        testString("text.");
    }

    @Test
    public void testBoolInRoot() {
        testBool("bool-");
    }

    @Test
    public void testBoolInObject() {
        testBool("bool.");
    }

    @Test
    public void testIntInRoot() {
        testInt("int-");
    }

    @Test
    public void testIntInObject() {
        testInt("int.");
    }

    @Test
    public void testLongInRoot() {
        testLong("long-");
    }

    @Test
    public void testLongInObject() {
        testLong("long.");
    }

    @Test
    public void testDoubleInRoot() {
        testDouble("double-");
    }

    @Test
    public void testDoubleInObject() {
        testDouble("double.");
    }

    @Test
    public void testUriInRoot() {
        testUri("uri-");
    }

    @Test
    public void testUriInObject() {
        testUri("uri.");
    }

    @Test
    public void testMixedListInRoot() {
        testMixedList("mixed-");
    }

    @Test
    public void testMixedListInObject() {
        testMixedList("mixed.");
    }

    // 
    // defaults
    // 
    @Test
    public void testStringDefault() {
        Config node = getMissingConfig();
        String defaultValue = "default-value";
        String expected = defaultValue;
        MatcherAssert.assertThat(node.asString().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(String.class).orElse(defaultValue), Matchers.is(expected));
    }

    @Test
    public void testStringListDefault() {
        Config node = getMissingConfig();
        List<String> defaultValue = Arrays.asList("def-1", "def-2", "def-3");
        String[] expected = defaultValue.toArray(new String[0]);
        MatcherAssert.assertThat(node.asList(String.class).orElse(defaultValue), Matchers.contains(expected));
        MatcherAssert.assertThat(node.asList(( aConfig) -> aConfig.asString().get()).orElse(defaultValue), Matchers.contains(expected));
    }

    @Test
    public void testBooleanDefault() {
        Config node = getMissingConfig();
        Boolean defaultValue = true;
        Boolean expected = defaultValue;
        MatcherAssert.assertThat(node.asBoolean().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Boolean.class).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asString().as(ConfigMappers::toBoolean).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Boolean.class).orElse(defaultValue), Matchers.is(expected));
    }

    @Test
    public void testBooleanListDefault() {
        Config node = getMissingConfig();
        List<Boolean> defaultValue = Arrays.asList(true, false, true);
        Boolean[] expected = defaultValue.toArray(new Boolean[0]);
        MatcherAssert.assertThat(node.asList(Boolean.class).orElse(defaultValue), Matchers.contains(expected));
    }

    @Test
    public void testIntDefault() {
        Config node = getMissingConfig();
        int expected = 42;
        int defaultValue = expected;
        MatcherAssert.assertThat(node.asInt().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Integer.class).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asString().as(ConfigMappers::toInt).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asInt().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Integer.class).orElse(defaultValue), Matchers.is(expected));
    }

    @Test
    public void testIntListDefault() {
        Config node = getMissingConfig();
        List<Integer> defaultValue = Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
        Integer[] expected = defaultValue.toArray(new Integer[0]);
        MatcherAssert.assertThat(node.asList(Integer.class).orElse(defaultValue), Matchers.contains(expected));
    }

    @Test
    public void testLongDefault() {
        Config node = getMissingConfig();
        long expected = 42;
        long defaultValue = expected;
        MatcherAssert.assertThat(node.asLong().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Long.class).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asString().as(ConfigMappers::toLong).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asLong().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Long.class).orElse(defaultValue), Matchers.is(expected));
    }

    @Test
    public void testLongListDefault() {
        Config node = getMissingConfig();
        List<Long> defaultValue = Arrays.asList(Long.MIN_VALUE, 0L, Long.MAX_VALUE);
        Long[] expected = defaultValue.toArray(new Long[0]);
        MatcherAssert.assertThat(node.asList(Long.class).orElse(defaultValue), Matchers.contains(expected));
    }

    @Test
    public void testDoubleDefault() {
        Config node = getMissingConfig();
        double expected = -1234.5678;
        double defaultValue = expected;
        MatcherAssert.assertThat(node.asDouble().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Double.class).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asString().as(ConfigMappers::toDouble).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asDouble().orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(Double.class).orElse(defaultValue), Matchers.is(expected));
    }

    @Test
    public void testDoubleListDefault() {
        Config node = getMissingConfig();
        List<Double> defaultValue = Arrays.asList((-1234.5678), 0.0, 1234.5678);
        Double[] expected = defaultValue.toArray(new Double[0]);
        MatcherAssert.assertThat(node.asList(Double.class).orElse(defaultValue), Matchers.contains(expected));
    }

    @Test
    public void testUriDefault() {
        Config node = getMissingConfig();
        URI expected = URI.create("http://localhost");
        URI defaultValue = expected;
        MatcherAssert.assertThat(node.as(URI.class).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asString().as(ConfigMappers::toUri).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.as(URI.class).orElse(defaultValue), Matchers.is(expected));
        MatcherAssert.assertThat(node.asString().as(ConfigMappers::toUri).orElse(defaultValue), Matchers.is(expected));
    }

    @Test
    public void testUriListDefault() {
        Config node = getMissingConfig();
        List<URI> defaultValue = Arrays.asList(URI.create("http://localhost"), URI.create("http://localhost"));
        URI[] expected = defaultValue.toArray(new URI[0]);
        MatcherAssert.assertThat(node.asList(URI.class).orElse(defaultValue), Matchers.contains(expected));
    }

    @Test
    public void testMixedListDefault() {
        Config node = getMissingConfig();
        List<Config> defaultValue = Arrays.asList(Config.create(), Config.create());
        Config[] expected = defaultValue.toArray(new Config[0]);
        MatcherAssert.assertThat(node.asNodeList().orElse(defaultValue), Matchers.contains(expected));
        MatcherAssert.assertThat(node.asList(Config.class).orElse(defaultValue), Matchers.contains(expected));
    }

    // 
    // children
    // 
    @Test
    public void testChildren() {
        Set<String> expectedKeys = new HashSet<>();
        expectedKeys.add("tree.single1");
        expectedKeys.add("tree.array1");
        expectedKeys.add("tree.object1");
        List<String> unexpectedKeys = getConfig().get("tree").asNodeList().get().stream().filter(( node) -> !(expectedKeys.remove(node.key().toString()))).map(Config::key).map(Config.Key::toString).collect(Collectors.toList());
        MatcherAssert.assertThat("Unvisited keys during traversing.", expectedKeys, Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat("Unexpected keys during traversing.", unexpectedKeys, Matchers.is(Matchers.empty()));
    }

    // 
    // traverse
    // 
    @Test
    public void testTraverse() {
        Set<String> expectedKeys = new HashSet<>();
        expectedKeys.add("tree.single1");
        expectedKeys.add("tree.array1");
        expectedKeys.add("tree.array1.0");
        expectedKeys.add("tree.array1.1");
        expectedKeys.add("tree.array1.2");
        expectedKeys.add("tree.object1");
        expectedKeys.add("tree.object1.single2");
        expectedKeys.add("tree.object1.array2");
        expectedKeys.add("tree.object1.array2.0");
        expectedKeys.add("tree.object1.array2.1");
        expectedKeys.add("tree.object1.array2.2");
        List<String> unexpectedKeys = getConfig().get("tree").traverse().filter(( node) -> !(expectedKeys.remove(node.key().toString()))).map(Config::key).map(Config.Key::toString).collect(Collectors.toList());
        MatcherAssert.assertThat("Unvisited keys during traversing.", expectedKeys, Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat("Unexpected keys during traversing.", unexpectedKeys, Matchers.is(Matchers.empty()));
    }

    // 
    // escaped key name
    // 
    /**
     * This is same test as in {@code config} module, {@code ConfigTest} class, method {@code testConfigKeyEscapedNameComplex}.
     */
    @Test
    public void testConfigKeyEscapedNameComplex() {
        Config config = getConfig().get("escaped").detach();
        // key
        MatcherAssert.assertThat(config.get("oracle~1com.prop1").asString(), Matchers.is(ConfigValues.simpleValue("val1")));
        MatcherAssert.assertThat(config.get("oracle~1com.prop2").asString(), Matchers.is(ConfigValues.simpleValue("val2")));
        MatcherAssert.assertThat(config.get("oracle.com").asString(), Matchers.is(ConfigValues.simpleValue("1")));
        MatcherAssert.assertThat(config.get("oracle.cz").asString(), Matchers.is(ConfigValues.simpleValue("2")));
        // name
        MatcherAssert.assertThat(config.get("oracle~1com").name(), Matchers.is("oracle.com"));
        MatcherAssert.assertThat(config.get("oracle~1com.prop1").name(), Matchers.is("prop1"));
        MatcherAssert.assertThat(config.get("oracle~1com.prop2").name(), Matchers.is("prop2"));
        MatcherAssert.assertThat(config.get("oracle").name(), Matchers.is("oracle"));
        MatcherAssert.assertThat(config.get("oracle.com").name(), Matchers.is("com"));
        MatcherAssert.assertThat(config.get("oracle.cz").name(), Matchers.is("cz"));
        // child nodes
        List<Config> children = config.asNodeList().get();
        MatcherAssert.assertThat(children, Matchers.hasSize(2));
        MatcherAssert.assertThat(children.stream().map(Config::name).collect(Collectors.toSet()), Matchers.containsInAnyOrder("oracle.com", "oracle"));
        // traverse
        Set<String> keys = config.traverse().map(Config::key).map(Config.Key::toString).collect(Collectors.toSet());
        MatcherAssert.assertThat(keys, Matchers.hasSize(6));
        MatcherAssert.assertThat(keys, Matchers.containsInAnyOrder("oracle~1com", "oracle~1com.prop1", "oracle~1com.prop2", "oracle", "oracle.com", "oracle.cz"));
        // map
        Map<String, String> map = config.asMap().get();
        MatcherAssert.assertThat(map.keySet(), Matchers.hasSize(4));
        MatcherAssert.assertThat(map.get("oracle~1com.prop1"), Matchers.is("val1"));
        MatcherAssert.assertThat(map.get("oracle~1com.prop2"), Matchers.is("val2"));
        MatcherAssert.assertThat(map.get("oracle.com"), Matchers.is("1"));
        MatcherAssert.assertThat(map.get("oracle.cz"), Matchers.is("2"));
    }
}

