/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.configuration;


import org.apache.flink.util.InstantiationUtil;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class contains test for the configuration package. In particular, the serialization of {@link Configuration}
 * objects is tested.
 */
public class ConfigurationTest extends TestLogger {
    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final long TOO_LONG = (Integer.MAX_VALUE) + 10L;

    private static final double TOO_LONG_DOUBLE = Double.MAX_VALUE;

    /**
     * This test checks the serialization/deserialization of configuration objects.
     */
    @Test
    public void testConfigurationSerializationAndGetters() {
        try {
            final Configuration orig = new Configuration();
            orig.setString("mykey", "myvalue");
            orig.setInteger("mynumber", 100);
            orig.setLong("longvalue", 478236947162389746L);
            orig.setFloat("PI", 3.1415925F);
            orig.setDouble("E", Math.E);
            orig.setBoolean("shouldbetrue", true);
            orig.setBytes("bytes sequence", new byte[]{ 1, 2, 3, 4, 5 });
            orig.setClass("myclass", this.getClass());
            final Configuration copy = InstantiationUtil.createCopyWritable(orig);
            Assert.assertEquals("myvalue", copy.getString("mykey", "null"));
            Assert.assertEquals(100, copy.getInteger("mynumber", 0));
            Assert.assertEquals(478236947162389746L, copy.getLong("longvalue", 0L));
            Assert.assertEquals(3.1415925F, copy.getFloat("PI", 3.1415925F), 0.0);
            Assert.assertEquals(Math.E, copy.getDouble("E", 0.0), 0.0);
            Assert.assertEquals(true, copy.getBoolean("shouldbetrue", false));
            Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 5 }, copy.getBytes("bytes sequence", null));
            Assert.assertEquals(getClass(), copy.getClass("myclass", null, getClass().getClassLoader()));
            Assert.assertEquals(orig, copy);
            Assert.assertEquals(orig.keySet(), copy.keySet());
            Assert.assertEquals(orig.hashCode(), copy.hashCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConversions() {
        try {
            Configuration pc = new Configuration();
            pc.setInteger("int", 5);
            pc.setLong("long", 15);
            pc.setLong("too_long", ConfigurationTest.TOO_LONG);
            pc.setFloat("float", 2.1456776F);
            pc.setDouble("double", Math.PI);
            pc.setDouble("negative_double", (-1.0));
            pc.setDouble("zero", 0.0);
            pc.setDouble("too_long_double", ConfigurationTest.TOO_LONG_DOUBLE);
            pc.setString("string", "42");
            pc.setString("non_convertible_string", "bcdefg&&");
            pc.setBoolean("boolean", true);
            // as integer
            Assert.assertEquals(5, pc.getInteger("int", 0));
            Assert.assertEquals(5L, pc.getLong("int", 0));
            Assert.assertEquals(5.0F, pc.getFloat("int", 0), 0.0);
            Assert.assertEquals(5.0, pc.getDouble("int", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("int", true));
            Assert.assertEquals("5", pc.getString("int", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("int", ConfigurationTest.EMPTY_BYTES));
            // as long
            Assert.assertEquals(15, pc.getInteger("long", 0));
            Assert.assertEquals(15L, pc.getLong("long", 0));
            Assert.assertEquals(15.0F, pc.getFloat("long", 0), 0.0);
            Assert.assertEquals(15.0, pc.getDouble("long", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("long", true));
            Assert.assertEquals("15", pc.getString("long", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("long", ConfigurationTest.EMPTY_BYTES));
            // as too long
            Assert.assertEquals(0, pc.getInteger("too_long", 0));
            Assert.assertEquals(ConfigurationTest.TOO_LONG, pc.getLong("too_long", 0));
            Assert.assertEquals(((float) (ConfigurationTest.TOO_LONG)), pc.getFloat("too_long", 0), 10.0);
            Assert.assertEquals(((double) (ConfigurationTest.TOO_LONG)), pc.getDouble("too_long", 0), 10.0);
            Assert.assertEquals(false, pc.getBoolean("too_long", true));
            Assert.assertEquals(String.valueOf(ConfigurationTest.TOO_LONG), pc.getString("too_long", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("too_long", ConfigurationTest.EMPTY_BYTES));
            // as float
            Assert.assertEquals(0, pc.getInteger("float", 0));
            Assert.assertEquals(0L, pc.getLong("float", 0));
            Assert.assertEquals(2.1456776F, pc.getFloat("float", 0), 0.0);
            Assert.assertEquals(2.1456775, pc.getDouble("float", 0), 1.0E-7);
            Assert.assertEquals(false, pc.getBoolean("float", true));
            Assert.assertTrue(pc.getString("float", "0").startsWith("2.145677"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("float", ConfigurationTest.EMPTY_BYTES));
            // as double
            Assert.assertEquals(0, pc.getInteger("double", 0));
            Assert.assertEquals(0L, pc.getLong("double", 0));
            Assert.assertEquals(3.141592F, pc.getFloat("double", 0), 1.0E-6);
            Assert.assertEquals(Math.PI, pc.getDouble("double", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("double", true));
            Assert.assertTrue(pc.getString("double", "0").startsWith("3.1415926535"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("double", ConfigurationTest.EMPTY_BYTES));
            // as negative double
            Assert.assertEquals(0, pc.getInteger("negative_double", 0));
            Assert.assertEquals(0L, pc.getLong("negative_double", 0));
            Assert.assertEquals((-1.0F), pc.getFloat("negative_double", 0), 1.0E-6);
            Assert.assertEquals((-1), pc.getDouble("negative_double", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("negative_double", true));
            Assert.assertTrue(pc.getString("negative_double", "0").startsWith("-1"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("negative_double", ConfigurationTest.EMPTY_BYTES));
            // as zero
            Assert.assertEquals((-1), pc.getInteger("zero", (-1)));
            Assert.assertEquals((-1L), pc.getLong("zero", (-1)));
            Assert.assertEquals(0.0F, pc.getFloat("zero", (-1)), 1.0E-6);
            Assert.assertEquals(0.0, pc.getDouble("zero", (-1)), 0.0);
            Assert.assertEquals(false, pc.getBoolean("zero", true));
            Assert.assertTrue(pc.getString("zero", "-1").startsWith("0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("zero", ConfigurationTest.EMPTY_BYTES));
            // as too long double
            Assert.assertEquals(0, pc.getInteger("too_long_double", 0));
            Assert.assertEquals(0L, pc.getLong("too_long_double", 0));
            Assert.assertEquals(0.0F, pc.getFloat("too_long_double", 0.0F), 1.0E-6);
            Assert.assertEquals(ConfigurationTest.TOO_LONG_DOUBLE, pc.getDouble("too_long_double", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("too_long_double", true));
            Assert.assertEquals(String.valueOf(ConfigurationTest.TOO_LONG_DOUBLE), pc.getString("too_long_double", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("too_long_double", ConfigurationTest.EMPTY_BYTES));
            // as string
            Assert.assertEquals(42, pc.getInteger("string", 0));
            Assert.assertEquals(42L, pc.getLong("string", 0));
            Assert.assertEquals(42.0F, pc.getFloat("string", 0.0F), 1.0E-6);
            Assert.assertEquals(42.0, pc.getDouble("string", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("string", true));
            Assert.assertEquals("42", pc.getString("string", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("string", ConfigurationTest.EMPTY_BYTES));
            // as non convertible string
            Assert.assertEquals(0, pc.getInteger("non_convertible_string", 0));
            Assert.assertEquals(0L, pc.getLong("non_convertible_string", 0));
            Assert.assertEquals(0.0F, pc.getFloat("non_convertible_string", 0.0F), 1.0E-6);
            Assert.assertEquals(0.0, pc.getDouble("non_convertible_string", 0), 0.0);
            Assert.assertEquals(false, pc.getBoolean("non_convertible_string", true));
            Assert.assertEquals("bcdefg&&", pc.getString("non_convertible_string", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("non_convertible_string", ConfigurationTest.EMPTY_BYTES));
            // as boolean
            Assert.assertEquals(0, pc.getInteger("boolean", 0));
            Assert.assertEquals(0L, pc.getLong("boolean", 0));
            Assert.assertEquals(0.0F, pc.getFloat("boolean", 0.0F), 1.0E-6);
            Assert.assertEquals(0.0, pc.getDouble("boolean", 0), 0.0);
            Assert.assertEquals(true, pc.getBoolean("boolean", false));
            Assert.assertEquals("true", pc.getString("boolean", "0"));
            Assert.assertArrayEquals(ConfigurationTest.EMPTY_BYTES, pc.getBytes("boolean", ConfigurationTest.EMPTY_BYTES));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCopyConstructor() {
        try {
            final String key = "theKey";
            Configuration cfg1 = new Configuration();
            cfg1.setString(key, "value");
            Configuration cfg2 = new Configuration(cfg1);
            cfg2.setString(key, "another value");
            Assert.assertEquals("value", cfg1.getString(key, ""));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testOptionWithDefault() {
        Configuration cfg = new Configuration();
        cfg.setInteger("int-key", 11);
        cfg.setString("string-key", "abc");
        ConfigOption<String> presentStringOption = ConfigOptions.key("string-key").defaultValue("my-beautiful-default");
        ConfigOption<Integer> presentIntOption = ConfigOptions.key("int-key").defaultValue(87);
        Assert.assertEquals("abc", cfg.getString(presentStringOption));
        Assert.assertEquals("abc", cfg.getValue(presentStringOption));
        Assert.assertEquals(11, cfg.getInteger(presentIntOption));
        Assert.assertEquals("11", cfg.getValue(presentIntOption));
        // test getting default when no value is present
        ConfigOption<String> stringOption = ConfigOptions.key("test").defaultValue("my-beautiful-default");
        ConfigOption<Integer> intOption = ConfigOptions.key("test2").defaultValue(87);
        // getting strings with default value should work
        Assert.assertEquals("my-beautiful-default", cfg.getValue(stringOption));
        Assert.assertEquals("my-beautiful-default", cfg.getString(stringOption));
        // overriding the default should work
        Assert.assertEquals("override", cfg.getString(stringOption, "override"));
        // getting a primitive with a default value should work
        Assert.assertEquals(87, cfg.getInteger(intOption));
        Assert.assertEquals("87", cfg.getValue(intOption));
    }

    @Test
    public void testOptionWithNoDefault() {
        Configuration cfg = new Configuration();
        cfg.setInteger("int-key", 11);
        cfg.setString("string-key", "abc");
        ConfigOption<String> presentStringOption = ConfigOptions.key("string-key").noDefaultValue();
        Assert.assertEquals("abc", cfg.getString(presentStringOption));
        Assert.assertEquals("abc", cfg.getValue(presentStringOption));
        // test getting default when no value is present
        ConfigOption<String> stringOption = ConfigOptions.key("test").noDefaultValue();
        // getting strings for null should work
        Assert.assertNull(cfg.getValue(stringOption));
        Assert.assertNull(cfg.getString(stringOption));
        // overriding the null default should work
        Assert.assertEquals("override", cfg.getString(stringOption, "override"));
    }

    @Test
    public void testDeprecatedKeys() {
        Configuration cfg = new Configuration();
        cfg.setInteger("the-key", 11);
        cfg.setInteger("old-key", 12);
        cfg.setInteger("older-key", 13);
        ConfigOption<Integer> matchesFirst = ConfigOptions.key("the-key").defaultValue((-1)).withDeprecatedKeys("old-key", "older-key");
        ConfigOption<Integer> matchesSecond = ConfigOptions.key("does-not-exist").defaultValue((-1)).withDeprecatedKeys("old-key", "older-key");
        ConfigOption<Integer> matchesThird = ConfigOptions.key("does-not-exist").defaultValue((-1)).withDeprecatedKeys("foo", "older-key");
        ConfigOption<Integer> notContained = ConfigOptions.key("does-not-exist").defaultValue((-1)).withDeprecatedKeys("not-there", "also-not-there");
        Assert.assertEquals(11, cfg.getInteger(matchesFirst));
        Assert.assertEquals(12, cfg.getInteger(matchesSecond));
        Assert.assertEquals(13, cfg.getInteger(matchesThird));
        Assert.assertEquals((-1), cfg.getInteger(notContained));
    }

    @Test
    public void testFallbackKeys() {
        Configuration cfg = new Configuration();
        cfg.setInteger("the-key", 11);
        cfg.setInteger("old-key", 12);
        cfg.setInteger("older-key", 13);
        ConfigOption<Integer> matchesFirst = ConfigOptions.key("the-key").defaultValue((-1)).withFallbackKeys("old-key", "older-key");
        ConfigOption<Integer> matchesSecond = ConfigOptions.key("does-not-exist").defaultValue((-1)).withFallbackKeys("old-key", "older-key");
        ConfigOption<Integer> matchesThird = ConfigOptions.key("does-not-exist").defaultValue((-1)).withFallbackKeys("foo", "older-key");
        ConfigOption<Integer> notContained = ConfigOptions.key("does-not-exist").defaultValue((-1)).withFallbackKeys("not-there", "also-not-there");
        Assert.assertEquals(11, cfg.getInteger(matchesFirst));
        Assert.assertEquals(12, cfg.getInteger(matchesSecond));
        Assert.assertEquals(13, cfg.getInteger(matchesThird));
        Assert.assertEquals((-1), cfg.getInteger(notContained));
    }

    @Test
    public void testFallbackAndDeprecatedKeys() {
        final ConfigOption<Integer> fallback = ConfigOptions.key("fallback").defaultValue((-1));
        final ConfigOption<Integer> deprecated = ConfigOptions.key("deprecated").defaultValue((-1));
        final ConfigOption<Integer> mainOption = ConfigOptions.key("main").defaultValue((-1)).withFallbackKeys(fallback.key()).withDeprecatedKeys(deprecated.key());
        final Configuration fallbackCfg = new Configuration();
        fallbackCfg.setInteger(fallback, 1);
        Assert.assertEquals(1, fallbackCfg.getInteger(mainOption));
        final Configuration deprecatedCfg = new Configuration();
        deprecatedCfg.setInteger(deprecated, 2);
        Assert.assertEquals(2, deprecatedCfg.getInteger(mainOption));
        // reverse declaration of fallback and deprecated keys, fallback keys should always be used first
        final ConfigOption<Integer> reversedMainOption = ConfigOptions.key("main").defaultValue((-1)).withDeprecatedKeys(deprecated.key()).withFallbackKeys(fallback.key());
        final Configuration deprecatedAndFallBackConfig = new Configuration();
        deprecatedAndFallBackConfig.setInteger(fallback, 1);
        deprecatedAndFallBackConfig.setInteger(deprecated, 2);
        Assert.assertEquals(1, deprecatedAndFallBackConfig.getInteger(mainOption));
        Assert.assertEquals(1, deprecatedAndFallBackConfig.getInteger(reversedMainOption));
    }

    @Test
    public void testRemove() {
        Configuration cfg = new Configuration();
        cfg.setInteger("a", 1);
        cfg.setInteger("b", 2);
        ConfigOption<Integer> validOption = ConfigOptions.key("a").defaultValue((-1));
        ConfigOption<Integer> deprecatedOption = ConfigOptions.key("c").defaultValue((-1)).withDeprecatedKeys("d", "b");
        ConfigOption<Integer> unexistedOption = ConfigOptions.key("e").defaultValue((-1)).withDeprecatedKeys("f", "g", "j");
        Assert.assertEquals("Wrong expectation about size", cfg.keySet().size(), 2);
        Assert.assertTrue("Expected 'validOption' is removed", cfg.removeConfig(validOption));
        Assert.assertEquals("Wrong expectation about size", cfg.keySet().size(), 1);
        Assert.assertTrue("Expected 'existedOption' is removed", cfg.removeConfig(deprecatedOption));
        Assert.assertEquals("Wrong expectation about size", cfg.keySet().size(), 0);
        Assert.assertFalse("Expected 'unexistedOption' is not removed", cfg.removeConfig(unexistedOption));
    }

    @Test
    public void testShouldParseValidStringToEnum() {
        final ConfigOption<String> configOption = ConfigurationTest.createStringConfigOption();
        final Configuration configuration = new Configuration();
        configuration.setString(configOption.key(), ConfigurationTest.TestEnum.VALUE1.toString());
        final ConfigurationTest.TestEnum parsedEnumValue = configuration.getEnum(ConfigurationTest.TestEnum.class, configOption);
        Assert.assertEquals(ConfigurationTest.TestEnum.VALUE1, parsedEnumValue);
    }

    @Test
    public void testShouldParseValidStringToEnumIgnoringCase() {
        final ConfigOption<String> configOption = ConfigurationTest.createStringConfigOption();
        final Configuration configuration = new Configuration();
        configuration.setString(configOption.key(), ConfigurationTest.TestEnum.VALUE1.toString().toLowerCase());
        final ConfigurationTest.TestEnum parsedEnumValue = configuration.getEnum(ConfigurationTest.TestEnum.class, configOption);
        Assert.assertEquals(ConfigurationTest.TestEnum.VALUE1, parsedEnumValue);
    }

    @Test
    public void testThrowsExceptionIfTryingToParseInvalidStringForEnum() {
        final ConfigOption<String> configOption = ConfigurationTest.createStringConfigOption();
        final Configuration configuration = new Configuration();
        final String invalidValueForTestEnum = "InvalidValueForTestEnum";
        configuration.setString(configOption.key(), invalidValueForTestEnum);
        try {
            configuration.getEnum(ConfigurationTest.TestEnum.class, configOption);
            Assert.fail("Expected exception not thrown");
        } catch (IllegalArgumentException e) {
            final String expectedMessage = ((("Value for config option " + (configOption.key())) + " must be one of [VALUE1, VALUE2] (was ") + invalidValueForTestEnum) + ")";
            Assert.assertThat(e.getMessage(), Matchers.containsString(expectedMessage));
        }
    }

    enum TestEnum {

        VALUE1,
        VALUE2;}
}

