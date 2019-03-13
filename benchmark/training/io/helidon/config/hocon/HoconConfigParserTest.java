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
package io.helidon.config.hocon;


import HoconConfigParser.MEDIA_TYPE_APPLICATION_JSON;
import com.typesafe.config.ConfigException.NotResolved;
import com.typesafe.config.ConfigException.UnresolvedSubstitution;
import com.typesafe.config.ConfigResolveOptions;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigMappingException;
import io.helidon.config.ConfigSources;
import io.helidon.config.ConfigValues;
import io.helidon.config.MissingValueException;
import io.helidon.config.hocon.internal.HoconConfigParser;
import io.helidon.config.spi.ConfigNode;
import io.helidon.config.spi.ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigParser;
import io.helidon.config.spi.ConfigParser.Content;
import io.helidon.config.spi.ConfigParserException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link HoconConfigParser}.
 */
public class HoconConfigParserTest {
    @Test
    public void testResolveEnabled() {
        ConfigParser parser = HoconConfigParserBuilder.buildDefault();
        ObjectNode node = parser.parse(((HoconConfigParserTest.StringContent) (() -> "" + ((("aaa = 1 \n" + "bbb = ${aaa} \n") + "ccc = \"${aaa}\" \n") + "ddd = ${?zzz}"))));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(node.get("aaa"), valueNode("1"));
        MatcherAssert.assertThat(node.get("bbb"), valueNode("1"));
        MatcherAssert.assertThat(node.get("ccc"), valueNode("${aaa}"));
    }

    @Test
    public void testResolveDisabled() {
        ConfigParserException cpe = Assertions.assertThrows(ConfigParserException.class, () -> {
            ConfigParser parser = HoconConfigParserBuilder.create().disableResolving().build();
            parser.parse(((HoconConfigParserTest.StringContent) (() -> "" + ((("aaa = 1 \n" + "bbb = ${aaa} \n") + "ccc = \"${aaa}\" \n") + "ddd = ${?zzz}"))));
        });
        MatcherAssert.assertThat(cpe.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("Cannot read from source", "substitution not resolved", "${aaa}")));
        MatcherAssert.assertThat(cpe.getCause(), Matchers.instanceOf(NotResolved.class));
    }

    @Test
    public void testResolveEnabledEnvVar() {
        ConfigParser parser = HoconConfigParserBuilder.buildDefault();
        ObjectNode node = parser.parse(((HoconConfigParserTest.StringContent) (() -> "env-var = ${HOCON_TEST_PROPERTY}")));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(node.get("env-var"), valueNode("This Is My ENV VARS Value."));
    }

    @Test
    public void testResolveEnabledEnvVarDisabled() {
        ConfigParserException cpe = Assertions.assertThrows(ConfigParserException.class, () -> {
            ConfigParser parser = HoconConfigParserBuilder.create().resolveOptions(ConfigResolveOptions.noSystem()).build();
            parser.parse(((HoconConfigParserTest.StringContent) (() -> "env-var = ${HOCON_TEST_PROPERTY}")));
        });
        MatcherAssert.assertThat(cpe.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("Cannot read from source", "not resolve substitution ", "${HOCON_TEST_PROPERTY}")));
        MatcherAssert.assertThat(cpe.getCause(), Matchers.instanceOf(UnresolvedSubstitution.class));
    }

    @Test
    public void testEmpty() {
        HoconConfigParser parser = new HoconConfigParser();
        ObjectNode node = parser.parse(((HoconConfigParserTest.StringContent) (() -> "")));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(0));
    }

    @Test
    public void testSingleValue() {
        HoconConfigParser parser = new HoconConfigParser();
        ObjectNode node = parser.parse(((HoconConfigParserTest.StringContent) (() -> "aaa = bbb")));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(node.get("aaa"), valueNode("bbb"));
    }

    @Test
    public void testStringListValue() {
        HoconConfigParser parser = new HoconConfigParser();
        ObjectNode node = parser.parse(((HoconConfigParserTest.StringContent) (() -> "aaa = [ bbb, ccc, ddd ]")));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(1));
        List<ConfigNode> aaa = ((ListNode) (node.get("aaa")));
        MatcherAssert.assertThat(aaa, Matchers.hasSize(3));
        MatcherAssert.assertThat(aaa.get(0), valueNode("bbb"));
        MatcherAssert.assertThat(aaa.get(1), valueNode("ccc"));
        MatcherAssert.assertThat(aaa.get(2), valueNode("ddd"));
    }

    @Test
    public void testComplexValue() {
        HoconConfigParser parser = new HoconConfigParser();
        ObjectNode node = parser.parse(((HoconConfigParserTest.StringContent) (() -> "" + ((("aaa =  \"bbb\"\n" + "arr = [ bbb, 13, true, 3.14159 ] \n") + "obj1 = { aaa = bbb, ccc = false } \n") + "arr2 = [ aaa, false, { bbb = 3.14159, c = true }, { ooo { ppp { xxx = yyy }}} ]"))));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(4));
        MatcherAssert.assertThat(node.get("aaa"), valueNode("bbb"));
        MatcherAssert.assertThat(get("aaa"), valueNode("bbb"));
        MatcherAssert.assertThat(get("ccc"), valueNode("false"));
        // arr
        List<ConfigNode> arr = ((ListNode) (node.get("arr")));
        MatcherAssert.assertThat(arr, Matchers.hasSize(4));
        MatcherAssert.assertThat(arr.get(0), valueNode("bbb"));
        MatcherAssert.assertThat(arr.get(1), valueNode("13"));
        MatcherAssert.assertThat(arr.get(2), valueNode("true"));
        MatcherAssert.assertThat(arr.get(3), valueNode("3.14159"));
        // arr2
        List<ConfigNode> arr2 = ((ListNode) (node.get("arr2")));
        MatcherAssert.assertThat(arr2, Matchers.hasSize(4));
        MatcherAssert.assertThat(arr2.get(0), valueNode("aaa"));
        MatcherAssert.assertThat(arr2.get(1), valueNode("false"));
        // arr2[2]
        final Map<String, ConfigNode> arr2_2 = ((ObjectNode) (arr2.get(2)));
        MatcherAssert.assertThat(arr2_2.entrySet(), Matchers.hasSize(2));
        MatcherAssert.assertThat(arr2_2.get("bbb"), valueNode("3.14159"));
        MatcherAssert.assertThat(arr2_2.get("c"), valueNode("true"));
        // arr2[3]
        final Map<String, ConfigNode> arr2_3 = ((ObjectNode) (arr2.get(3)));
        MatcherAssert.assertThat(arr2_3.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(get("xxx"), valueNode("yyy"));
    }

    /**
     * This is same test as in {@code config} module, {@code ConfigTest} class, method {@code testConfigKeyEscapedNameComplex}.
     */
    @Test
    public void testConfigKeyEscapedNameComplex() {
        String JSON = "" + ((((((((("{\n" + "    \"oracle.com\": {\n") + "        \"prop1\": \"val1\",\n") + "        \"prop2\": \"val2\"\n") + "    },\n") + "    \"oracle\": {\n") + "        \"com\": \"1\",\n") + "        \"cz\": \"2\"\n") + "    }\n") + "}\n");
        Config config = Config.builder(ConfigSources.create(JSON, MEDIA_TYPE_APPLICATION_JSON)).addParser(new HoconConfigParser()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableParserServices().disableMapperServices().disableFilterServices().build();
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

    @Test
    public void testGetSupportedMediaTypes() {
        HoconConfigParser parser = new HoconConfigParser();
        MatcherAssert.assertThat(parser.supportedMediaTypes(), Matchers.is(Matchers.not(Matchers.empty())));
    }

    @Test
    public void testCustomTypeMapping() {
        Config config = Config.builder(ConfigSources.create(HoconConfigParserTest.AppType.DEF, MEDIA_TYPE_APPLICATION_JSON)).addParser(new HoconConfigParser()).addMapper(HoconConfigParserTest.AppType.class, new HoconConfigParserTest.AppTypeMapper()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableParserServices().disableMapperServices().disableFilterServices().build();
        HoconConfigParserTest.AppType app = config.get("app").as(HoconConfigParserTest.AppType.class).get();
        MatcherAssert.assertThat("greeting", app.getGreeting(), Matchers.is(HoconConfigParserTest.AppType.GREETING));
        MatcherAssert.assertThat("name", app.getName(), Matchers.is(HoconConfigParserTest.AppType.NAME));
        MatcherAssert.assertThat("page-size", app.getPageSize(), Matchers.is(HoconConfigParserTest.AppType.PAGE_SIZE));
        MatcherAssert.assertThat("basic-range", app.getBasicRange(), Matchers.is(HoconConfigParserTest.AppType.BASIC_RANGE));
    }

    // 
    // helper
    // 
    @FunctionalInterface
    private interface StringContent extends Content {
        @Override
        default String mediaType() {
            return HoconConfigParser.MEDIA_TYPE_APPLICATION_HOCON;
        }

        @Override
        default Reader asReadable() {
            return new StringReader(getContent());
        }

        String getContent();
    }

    public static class AppType {
        private static final String GREETING = "Hello";

        private static final String NAME = "Demo";

        private static final int PAGE_SIZE = 20;

        private static final List<Integer> BASIC_RANGE = CollectionsHelper.listOf((-20), 20);

        static final String DEF = ((((((((((((((("" + ("app {\n" + "  greeting = \"")) + (HoconConfigParserTest.AppType.GREETING)) + "\"\n") + "  name = \"") + (HoconConfigParserTest.AppType.NAME)) + "\"\n") + "  page-size = ") + (HoconConfigParserTest.AppType.PAGE_SIZE)) + "\n") + "  basic-range = [ ") + (HoconConfigParserTest.AppType.BASIC_RANGE.get(0))) + ", ") + (HoconConfigParserTest.AppType.BASIC_RANGE.get(1))) + " ]\n") + "  storagePassphrase = \"${AES=thisIsEncriptedPassphrase}\"") + "}";

        private String greeting;

        private String name;

        private int pageSize;

        private List<Integer> basicRange;

        private String storagePassphrase;

        public AppType(String name, String greeting, int pageSize, List<Integer> basicRange, String storagePassphrase) {
            this.name = name;
            this.greeting = greeting;
            this.pageSize = pageSize;
            this.basicRange = copyBasicRange(basicRange);
            this.storagePassphrase = storagePassphrase;
        }

        private List<Integer> copyBasicRange(List<Integer> source) {
            return source != null ? new ArrayList<>(source) : Collections.emptyList();
        }

        public String getGreeting() {
            return greeting;
        }

        public String getName() {
            return name;
        }

        public int getPageSize() {
            return pageSize;
        }

        public List<Integer> getBasicRange() {
            return basicRange;
        }

        public String getStoragePassphrase() {
            return storagePassphrase;
        }
    }

    private static class AppTypeMapper implements Function<Config, HoconConfigParserTest.AppType> {
        @Override
        public HoconConfigParserTest.AppType apply(Config config) throws ConfigMappingException, MissingValueException {
            HoconConfigParserTest.AppType app = new HoconConfigParserTest.AppType(config.get("name").asString().get(), config.get("greeting").asString().get(), config.get("page-size").asInt().get(), config.get("basic-range").asList(Integer.class).get(), config.get("storagePassphrase").asString().get());
            return app;
        }
    }
}

