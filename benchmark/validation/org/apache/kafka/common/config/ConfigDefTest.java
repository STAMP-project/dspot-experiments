/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.config;


import ConfigDef.CompositeValidator;
import ConfigDef.NO_DEFAULT_VALUE;
import ConfigDef.ValidList;
import Importance.HIGH;
import Importance.LOW;
import Importance.MEDIUM;
import Password.HIDDEN;
import SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import SslConfigs.SSL_KEY_PASSWORD_CONFIG;
import SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG;
import Type.BOOLEAN;
import Type.CLASS;
import Type.DOUBLE;
import Type.INT;
import Type.LIST;
import Type.LONG;
import Type.PASSWORD;
import Type.STRING;
import Width.NONE;
import Width.SHORT;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.kafka.common.config.ConfigDef.Range;
import org.apache.kafka.common.config.ConfigDef.ValidString;
import org.apache.kafka.common.config.types.Password;
import org.junit.Assert;
import org.junit.Test;


public class ConfigDefTest {
    @Test
    public void testBasicTypes() {
        ConfigDef def = new ConfigDef().define("a", INT, 5, Range.between(0, 14), HIGH, "docs").define("b", LONG, HIGH, "docs").define("c", STRING, "hello", HIGH, "docs").define("d", LIST, HIGH, "docs").define("e", DOUBLE, HIGH, "docs").define("f", CLASS, HIGH, "docs").define("g", BOOLEAN, HIGH, "docs").define("h", BOOLEAN, HIGH, "docs").define("i", BOOLEAN, HIGH, "docs").define("j", PASSWORD, HIGH, "docs");
        Properties props = new Properties();
        props.put("a", "1   ");
        props.put("b", 2);
        props.put("d", " a , b, c");
        props.put("e", 42.5);
        props.put("f", String.class.getName());
        props.put("g", "true");
        props.put("h", "FalSE");
        props.put("i", "TRUE");
        props.put("j", "password");
        Map<String, Object> vals = def.parse(props);
        Assert.assertEquals(1, vals.get("a"));
        Assert.assertEquals(2L, vals.get("b"));
        Assert.assertEquals("hello", vals.get("c"));
        Assert.assertEquals(Arrays.asList("a", "b", "c"), vals.get("d"));
        Assert.assertEquals(42.5, vals.get("e"));
        Assert.assertEquals(String.class, vals.get("f"));
        Assert.assertEquals(true, vals.get("g"));
        Assert.assertEquals(false, vals.get("h"));
        Assert.assertEquals(true, vals.get("i"));
        Assert.assertEquals(new Password("password"), vals.get("j"));
        Assert.assertEquals(HIDDEN, vals.get("j").toString());
    }

    @Test(expected = ConfigException.class)
    public void testInvalidDefault() {
        new ConfigDef().define("a", INT, "hello", HIGH, "docs");
    }

    @Test
    public void testNullDefault() {
        ConfigDef def = new ConfigDef().define("a", INT, null, null, null, "docs");
        Map<String, Object> vals = def.parse(new Properties());
        Assert.assertEquals(null, vals.get("a"));
    }

    @Test(expected = ConfigException.class)
    public void testMissingRequired() {
        new ConfigDef().define("a", INT, HIGH, "docs").parse(new HashMap<String, Object>());
    }

    @Test
    public void testParsingEmptyDefaultValueForStringFieldShouldSucceed() {
        new ConfigDef().define("a", STRING, "", ConfigDef.Importance.HIGH, "docs").parse(new HashMap<String, Object>());
    }

    @Test(expected = ConfigException.class)
    public void testDefinedTwice() {
        new ConfigDef().define("a", STRING, HIGH, "docs").define("a", INT, HIGH, "docs");
    }

    @Test
    public void testBadInputs() {
        testBadInputs(INT, "hello", "42.5", 42.5, Long.MAX_VALUE, Long.toString(Long.MAX_VALUE), new Object());
        testBadInputs(LONG, "hello", "42.5", ((Long.toString(Long.MAX_VALUE)) + "00"), new Object());
        testBadInputs(DOUBLE, "hello", new Object());
        testBadInputs(STRING, new Object());
        testBadInputs(LIST, 53, new Object());
        testBadInputs(BOOLEAN, "hello", "truee", "fals");
        testBadInputs(CLASS, "ClassDoesNotExist");
    }

    @Test(expected = ConfigException.class)
    public void testInvalidDefaultRange() {
        new ConfigDef().define("name", INT, (-1), Range.between(0, 10), HIGH, "docs");
    }

    @Test(expected = ConfigException.class)
    public void testInvalidDefaultString() {
        new ConfigDef().define("name", STRING, "bad", ValidString.in("valid", "values"), HIGH, "docs");
    }

    @Test
    public void testNestedClass() {
        // getName(), not getSimpleName() or getCanonicalName(), is the version that should be able to locate the class
        Map<String, Object> props = Collections.<String, Object>singletonMap("name", ConfigDefTest.NestedClass.class.getName());
        new ConfigDef().define("name", CLASS, HIGH, "docs").parse(props);
    }

    @Test
    public void testValidators() {
        testValidators(INT, Range.between(0, 10), 5, new Object[]{ 1, 5, 9 }, new Object[]{ -1, 11, null });
        testValidators(STRING, ValidString.in("good", "values", "default"), "default", new Object[]{ "good", "values", "default" }, new Object[]{ "bad", "inputs", null });
        testValidators(LIST, ValidList.in("1", "2", "3"), "1", new Object[]{ "1", "2", "3" }, new Object[]{ "4", "5", "6" });
        testValidators(STRING, new ConfigDef.NonNullValidator(), "a", new Object[]{ "abb" }, new Object[]{ null });
        testValidators(STRING, CompositeValidator.of(new ConfigDef.NonNullValidator(), ValidString.in("a", "b")), "a", new Object[]{ "a", "b" }, new Object[]{ null, -1, "c" });
        testValidators(STRING, new ConfigDef.NonEmptyStringWithoutControlChars(), "defaultname", new Object[]{ "test", "name", "test/test", "test\u1234", "\u1324name\\", "/+%>&):??<&()?-", "+1", "\ud83d\ude01", "\uf3b1", "     test   \n\r", "\n  hello \t" }, new Object[]{ "nontrailing\nnotallowed", "as\u0001cii control char", "tes\rt", "test\btest", "1\t2", "" });
    }

    @Test
    public void testSslPasswords() {
        ConfigDef def = new ConfigDef();
        SslConfigs.addClientSslSupport(def);
        Properties props = new Properties();
        props.put(SSL_KEY_PASSWORD_CONFIG, "key_password");
        props.put(SSL_KEYSTORE_PASSWORD_CONFIG, "keystore_password");
        props.put(SSL_TRUSTSTORE_PASSWORD_CONFIG, "truststore_password");
        Map<String, Object> vals = def.parse(props);
        Assert.assertEquals(new Password("key_password"), vals.get(SSL_KEY_PASSWORD_CONFIG));
        Assert.assertEquals(HIDDEN, vals.get(SSL_KEY_PASSWORD_CONFIG).toString());
        Assert.assertEquals(new Password("keystore_password"), vals.get(SSL_KEYSTORE_PASSWORD_CONFIG));
        Assert.assertEquals(HIDDEN, vals.get(SSL_KEYSTORE_PASSWORD_CONFIG).toString());
        Assert.assertEquals(new Password("truststore_password"), vals.get(SSL_TRUSTSTORE_PASSWORD_CONFIG));
        Assert.assertEquals(HIDDEN, vals.get(SSL_TRUSTSTORE_PASSWORD_CONFIG).toString());
    }

    @Test
    public void testNullDefaultWithValidator() {
        final String key = "enum_test";
        ConfigDef def = new ConfigDef();
        def.define(key, STRING, NO_DEFAULT_VALUE, ValidString.in("ONE", "TWO", "THREE"), HIGH, "docs");
        Properties props = new Properties();
        props.put(key, "ONE");
        Map<String, Object> vals = def.parse(props);
        Assert.assertEquals("ONE", vals.get(key));
    }

    @Test
    public void testGroupInference() {
        List<String> expected1 = Arrays.asList("group1", "group2");
        ConfigDef def1 = new ConfigDef().define("a", INT, HIGH, "docs", "group1", 1, SHORT, "a").define("b", INT, HIGH, "docs", "group2", 1, SHORT, "b").define("c", INT, HIGH, "docs", "group1", 2, SHORT, "c");
        Assert.assertEquals(expected1, def1.groups());
        List<String> expected2 = Arrays.asList("group2", "group1");
        ConfigDef def2 = new ConfigDef().define("a", INT, HIGH, "docs", "group2", 1, SHORT, "a").define("b", INT, HIGH, "docs", "group2", 2, SHORT, "b").define("c", INT, HIGH, "docs", "group1", 2, SHORT, "c");
        Assert.assertEquals(expected2, def2.groups());
    }

    @Test
    public void testParseForValidate() {
        Map<String, Object> expectedParsed = new HashMap<>();
        expectedParsed.put("a", 1);
        expectedParsed.put("b", null);
        expectedParsed.put("c", null);
        expectedParsed.put("d", 10);
        Map<String, ConfigValue> expected = new HashMap<>();
        String errorMessageB = "Missing required configuration \"b\" which has no default value.";
        String errorMessageC = "Missing required configuration \"c\" which has no default value.";
        ConfigValue configA = new ConfigValue("a", 1, Collections.<Object>emptyList(), Collections.<String>emptyList());
        ConfigValue configB = new ConfigValue("b", null, Collections.<Object>emptyList(), Arrays.asList(errorMessageB, errorMessageB));
        ConfigValue configC = new ConfigValue("c", null, Collections.<Object>emptyList(), Arrays.asList(errorMessageC));
        ConfigValue configD = new ConfigValue("d", 10, Collections.<Object>emptyList(), Collections.<String>emptyList());
        expected.put("a", configA);
        expected.put("b", configB);
        expected.put("c", configC);
        expected.put("d", configD);
        ConfigDef def = new ConfigDef().define("a", INT, HIGH, "docs", "group", 1, SHORT, "a", Arrays.asList("b", "c"), new ConfigDefTest.IntegerRecommender(false)).define("b", INT, HIGH, "docs", "group", 2, SHORT, "b", new ConfigDefTest.IntegerRecommender(true)).define("c", INT, HIGH, "docs", "group", 3, SHORT, "c", new ConfigDefTest.IntegerRecommender(true)).define("d", INT, HIGH, "docs", "group", 4, SHORT, "d", Arrays.asList("b"), new ConfigDefTest.IntegerRecommender(false));
        Map<String, String> props = new HashMap<>();
        props.put("a", "1");
        props.put("d", "10");
        Map<String, ConfigValue> configValues = new HashMap<>();
        for (String name : def.configKeys().keySet()) {
            configValues.put(name, new ConfigValue(name));
        }
        Map<String, Object> parsed = def.parseForValidate(props, configValues);
        Assert.assertEquals(expectedParsed, parsed);
        Assert.assertEquals(expected, configValues);
    }

    @Test
    public void testValidate() {
        Map<String, ConfigValue> expected = new HashMap<>();
        String errorMessageB = "Missing required configuration \"b\" which has no default value.";
        String errorMessageC = "Missing required configuration \"c\" which has no default value.";
        ConfigValue configA = new ConfigValue("a", 1, Arrays.<Object>asList(1, 2, 3), Collections.<String>emptyList());
        ConfigValue configB = new ConfigValue("b", null, Arrays.<Object>asList(4, 5), Arrays.asList(errorMessageB, errorMessageB));
        ConfigValue configC = new ConfigValue("c", null, Arrays.<Object>asList(4, 5), Arrays.asList(errorMessageC));
        ConfigValue configD = new ConfigValue("d", 10, Arrays.<Object>asList(1, 2, 3), Collections.<String>emptyList());
        expected.put("a", configA);
        expected.put("b", configB);
        expected.put("c", configC);
        expected.put("d", configD);
        ConfigDef def = new ConfigDef().define("a", INT, HIGH, "docs", "group", 1, SHORT, "a", Arrays.asList("b", "c"), new ConfigDefTest.IntegerRecommender(false)).define("b", INT, HIGH, "docs", "group", 2, SHORT, "b", new ConfigDefTest.IntegerRecommender(true)).define("c", INT, HIGH, "docs", "group", 3, SHORT, "c", new ConfigDefTest.IntegerRecommender(true)).define("d", INT, HIGH, "docs", "group", 4, SHORT, "d", Arrays.asList("b"), new ConfigDefTest.IntegerRecommender(false));
        Map<String, String> props = new HashMap<>();
        props.put("a", "1");
        props.put("d", "10");
        List<ConfigValue> configs = def.validate(props);
        for (ConfigValue config : configs) {
            String name = config.name();
            ConfigValue expectedConfig = expected.get(name);
            Assert.assertEquals(expectedConfig, config);
        }
    }

    @Test
    public void testValidateMissingConfigKey() {
        Map<String, ConfigValue> expected = new HashMap<>();
        String errorMessageB = "Missing required configuration \"b\" which has no default value.";
        String errorMessageC = "Missing required configuration \"c\" which has no default value.";
        String errorMessageD = "d is referred in the dependents, but not defined.";
        ConfigValue configA = new ConfigValue("a", 1, Arrays.<Object>asList(1, 2, 3), Collections.<String>emptyList());
        ConfigValue configB = new ConfigValue("b", null, Arrays.<Object>asList(4, 5), Arrays.asList(errorMessageB));
        ConfigValue configC = new ConfigValue("c", null, Arrays.<Object>asList(4, 5), Arrays.asList(errorMessageC));
        ConfigValue configD = new ConfigValue("d", null, Collections.emptyList(), Arrays.asList(errorMessageD));
        configD.visible(false);
        expected.put("a", configA);
        expected.put("b", configB);
        expected.put("c", configC);
        expected.put("d", configD);
        ConfigDef def = new ConfigDef().define("a", INT, HIGH, "docs", "group", 1, SHORT, "a", Arrays.asList("b", "c", "d"), new ConfigDefTest.IntegerRecommender(false)).define("b", INT, HIGH, "docs", "group", 2, SHORT, "b", new ConfigDefTest.IntegerRecommender(true)).define("c", INT, HIGH, "docs", "group", 3, SHORT, "c", new ConfigDefTest.IntegerRecommender(true));
        Map<String, String> props = new HashMap<>();
        props.put("a", "1");
        List<ConfigValue> configs = def.validate(props);
        for (ConfigValue config : configs) {
            String name = config.name();
            ConfigValue expectedConfig = expected.get(name);
            Assert.assertEquals(expectedConfig, config);
        }
    }

    @Test
    public void testValidateCannotParse() {
        Map<String, ConfigValue> expected = new HashMap<>();
        String errorMessageB = "Invalid value non_integer for configuration a: Not a number of type INT";
        ConfigValue configA = new ConfigValue("a", null, Collections.emptyList(), Arrays.asList(errorMessageB));
        expected.put("a", configA);
        ConfigDef def = new ConfigDef().define("a", INT, HIGH, "docs");
        Map<String, String> props = new HashMap<>();
        props.put("a", "non_integer");
        List<ConfigValue> configs = def.validate(props);
        for (ConfigValue config : configs) {
            String name = config.name();
            ConfigValue expectedConfig = expected.get(name);
            Assert.assertEquals(expectedConfig, config);
        }
    }

    @Test
    public void testCanAddInternalConfig() throws Exception {
        final String configName = "internal.config";
        final ConfigDef configDef = new ConfigDef().defineInternal(configName, STRING, "", LOW);
        final HashMap<String, String> properties = new HashMap<>();
        properties.put(configName, "value");
        final List<ConfigValue> results = configDef.validate(properties);
        final ConfigValue configValue = results.get(0);
        Assert.assertEquals("value", configValue.value());
        Assert.assertEquals(configName, configValue.name());
    }

    @Test
    public void testInternalConfigDoesntShowUpInDocs() throws Exception {
        final String name = "my.config";
        final ConfigDef configDef = new ConfigDef().defineInternal(name, STRING, "", LOW);
        Assert.assertFalse(configDef.toHtmlTable().contains("my.config"));
        Assert.assertFalse(configDef.toEnrichedRst().contains("my.config"));
        Assert.assertFalse(configDef.toRst().contains("my.config"));
    }

    @Test
    public void testDynamicUpdateModeInDocs() throws Exception {
        final ConfigDef configDef = new ConfigDef().define("my.broker.config", LONG, HIGH, "docs").define("my.cluster.config", LONG, HIGH, "docs").define("my.readonly.config", LONG, HIGH, "docs");
        final Map<String, String> updateModes = new HashMap<>();
        updateModes.put("my.broker.config", "per-broker");
        updateModes.put("my.cluster.config", "cluster-wide");
        final String html = configDef.toHtmlTable(updateModes);
        Set<String> configsInHtml = new HashSet<>();
        for (String line : html.split("\n")) {
            if (line.contains("my.broker.config")) {
                Assert.assertTrue(line.contains("per-broker"));
                configsInHtml.add("my.broker.config");
            } else
                if (line.contains("my.cluster.config")) {
                    Assert.assertTrue(line.contains("cluster-wide"));
                    configsInHtml.add("my.cluster.config");
                } else
                    if (line.contains("my.readonly.config")) {
                        Assert.assertTrue(line.contains("read-only"));
                        configsInHtml.add("my.readonly.config");
                    }


        }
        Assert.assertEquals(configDef.names(), configsInHtml);
    }

    @Test
    public void testNames() {
        final ConfigDef configDef = new ConfigDef().define("a", STRING, LOW, "docs").define("b", STRING, LOW, "docs");
        Set<String> names = configDef.names();
        Assert.assertEquals(new HashSet<>(Arrays.asList("a", "b")), names);
        // should be unmodifiable
        try {
            names.add("new");
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test(expected = ConfigException.class)
    public void testMissingDependentConfigs() {
        // Should not be possible to parse a config if a dependent config has not been defined
        final ConfigDef configDef = new ConfigDef().define("parent", STRING, HIGH, "parent docs", "group", 1, Width.LONG, "Parent", Collections.singletonList("child"));
        configDef.parse(Collections.emptyMap());
    }

    @Test
    public void testBaseConfigDefDependents() {
        // Creating a ConfigDef based on another should compute the correct number of configs with no parent, even
        // if the base ConfigDef has already computed its parentless configs
        final ConfigDef baseConfigDef = new ConfigDef().define("a", STRING, LOW, "docs");
        Assert.assertEquals(new HashSet(Arrays.asList("a")), baseConfigDef.getConfigsWithNoParent());
        final ConfigDef configDef = new ConfigDef(baseConfigDef).define("parent", STRING, HIGH, "parent docs", "group", 1, Width.LONG, "Parent", Collections.singletonList("child")).define("child", STRING, HIGH, "docs");
        Assert.assertEquals(new HashSet(Arrays.asList("a", "parent")), configDef.getConfigsWithNoParent());
    }

    private static class IntegerRecommender implements ConfigDef.Recommender {
        private boolean hasParent;

        public IntegerRecommender(boolean hasParent) {
            this.hasParent = hasParent;
        }

        @Override
        public List<Object> validValues(String name, Map<String, Object> parsedConfig) {
            List<Object> values = new LinkedList<>();
            if (!(hasParent)) {
                values.addAll(Arrays.asList(1, 2, 3));
            } else {
                values.addAll(Arrays.asList(4, 5));
            }
            return values;
        }

        @Override
        public boolean visible(String name, Map<String, Object> parsedConfig) {
            return true;
        }
    }

    @Test
    public void toRst() {
        final ConfigDef def = new ConfigDef().define("opt1", STRING, "a", ValidString.in("a", "b", "c"), HIGH, "docs1").define("opt2", INT, MEDIUM, "docs2").define("opt3", LIST, Arrays.asList("a", "b"), LOW, "docs3");
        final String expectedRst = "" + (((((((((((((((((((("``opt2``\n" + "  docs2\n") + "\n") + "  * Type: int\n") + "  * Importance: medium\n") + "\n") + "``opt1``\n") + "  docs1\n") + "\n") + "  * Type: string\n") + "  * Default: a\n") + "  * Valid Values: [a, b, c]\n") + "  * Importance: high\n") + "\n") + "``opt3``\n") + "  docs3\n") + "\n") + "  * Type: list\n") + "  * Default: a,b\n") + "  * Importance: low\n") + "\n");
        Assert.assertEquals(expectedRst, def.toRst());
    }

    @Test
    public void toEnrichedRst() {
        final ConfigDef def = new ConfigDef().define("opt1.of.group1", STRING, "a", ValidString.in("a", "b", "c"), HIGH, "Doc doc.", "Group One", 0, NONE, "..", Collections.<String>emptyList()).define("opt2.of.group1", INT, NO_DEFAULT_VALUE, MEDIUM, "Doc doc doc.", "Group One", 1, NONE, "..", Arrays.asList("some.option1", "some.option2")).define("opt2.of.group2", BOOLEAN, false, HIGH, "Doc doc doc doc.", "Group Two", 1, NONE, "..", Collections.<String>emptyList()).define("opt1.of.group2", BOOLEAN, false, HIGH, "Doc doc doc doc doc.", "Group Two", 0, NONE, "..", Collections.singletonList("some.option")).define("poor.opt", STRING, "foo", HIGH, "Doc doc doc doc.");
        final String expectedRst = "" + (((((((((((((((((((((((((((((((((((((((((("``poor.opt``\n" + "  Doc doc doc doc.\n") + "\n") + "  * Type: string\n") + "  * Default: foo\n") + "  * Importance: high\n") + "\n") + "Group One\n") + "^^^^^^^^^\n") + "\n") + "``opt1.of.group1``\n") + "  Doc doc.\n") + "\n") + "  * Type: string\n") + "  * Default: a\n") + "  * Valid Values: [a, b, c]\n") + "  * Importance: high\n") + "\n") + "``opt2.of.group1``\n") + "  Doc doc doc.\n") + "\n") + "  * Type: int\n") + "  * Importance: medium\n") + "  * Dependents: ``some.option1``, ``some.option2``\n") + "\n") + "Group Two\n") + "^^^^^^^^^\n") + "\n") + "``opt1.of.group2``\n") + "  Doc doc doc doc doc.\n") + "\n") + "  * Type: boolean\n") + "  * Default: false\n") + "  * Importance: high\n") + "  * Dependents: ``some.option``\n") + "\n") + "``opt2.of.group2``\n") + "  Doc doc doc doc.\n") + "\n") + "  * Type: boolean\n") + "  * Default: false\n") + "  * Importance: high\n") + "\n");
        Assert.assertEquals(expectedRst, def.toEnrichedRst());
    }

    @Test
    public void testConvertValueToStringBoolean() {
        Assert.assertEquals("true", ConfigDef.convertToString(true, BOOLEAN));
        Assert.assertNull(ConfigDef.convertToString(null, BOOLEAN));
    }

    @Test
    public void testConvertValueToStringShort() {
        Assert.assertEquals("32767", ConfigDef.convertToString(Short.MAX_VALUE, Type.SHORT));
        Assert.assertNull(ConfigDef.convertToString(null, Type.SHORT));
    }

    @Test
    public void testConvertValueToStringInt() {
        Assert.assertEquals("2147483647", ConfigDef.convertToString(Integer.MAX_VALUE, INT));
        Assert.assertNull(ConfigDef.convertToString(null, INT));
    }

    @Test
    public void testConvertValueToStringLong() {
        Assert.assertEquals("9223372036854775807", ConfigDef.convertToString(Long.MAX_VALUE, LONG));
        Assert.assertNull(ConfigDef.convertToString(null, LONG));
    }

    @Test
    public void testConvertValueToStringDouble() {
        Assert.assertEquals("3.125", ConfigDef.convertToString(3.125, DOUBLE));
        Assert.assertNull(ConfigDef.convertToString(null, DOUBLE));
    }

    @Test
    public void testConvertValueToStringString() {
        Assert.assertEquals("foobar", ConfigDef.convertToString("foobar", STRING));
        Assert.assertNull(ConfigDef.convertToString(null, STRING));
    }

    @Test
    public void testConvertValueToStringPassword() {
        Assert.assertEquals(HIDDEN, ConfigDef.convertToString(new Password("foobar"), PASSWORD));
        Assert.assertEquals("foobar", ConfigDef.convertToString("foobar", PASSWORD));
        Assert.assertNull(ConfigDef.convertToString(null, PASSWORD));
    }

    @Test
    public void testConvertValueToStringList() {
        Assert.assertEquals("a,bc,d", ConfigDef.convertToString(Arrays.asList("a", "bc", "d"), LIST));
        Assert.assertNull(ConfigDef.convertToString(null, LIST));
    }

    @Test
    public void testConvertValueToStringClass() throws ClassNotFoundException {
        String actual = ConfigDef.convertToString(ConfigDefTest.class, CLASS);
        Assert.assertEquals("org.apache.kafka.common.config.ConfigDefTest", actual);
        // Additionally validate that we can look up this class by this name
        Assert.assertEquals(ConfigDefTest.class, Class.forName(actual));
        Assert.assertNull(ConfigDef.convertToString(null, CLASS));
    }

    @Test
    public void testConvertValueToStringNestedClass() throws ClassNotFoundException {
        String actual = ConfigDef.convertToString(ConfigDefTest.NestedClass.class, CLASS);
        Assert.assertEquals("org.apache.kafka.common.config.ConfigDefTest$NestedClass", actual);
        // Additionally validate that we can look up this class by this name
        Assert.assertEquals(ConfigDefTest.NestedClass.class, Class.forName(actual));
    }

    private class NestedClass {}
}

