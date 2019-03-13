/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.configuration.metatype.bnd.util;


import Meta.AD;
import NewEnv.Type;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.test.aspects.ReflectionUtilAdvice;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;
import java.util.Collections;
import java.util.Dictionary;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Tina Tian
 */
public class ConfigurableUtilTest {
    @ClassRule
    @Rule
    public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(AspectJNewEnvTestRule.INSTANCE, CodeCoverageAssertor.INSTANCE);

    @Test
    public void testBigString() {
        _testBigString(65535);
        _testBigString(65536);
    }

    @AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testClassInitializationFailure() throws Exception {
        Throwable throwable = new Throwable();
        ReflectionUtilAdvice.setDeclaredMethodThrowable(throwable);
        try {
            Class.forName(ConfigurableUtil.class.getName());
            Assert.fail();
        } catch (ExceptionInInitializerError eiie) {
            Assert.assertSame(throwable, eiie.getCause());
        }
    }

    @Test
    public void testCreateConfigurable() {
        // Test dictionary
        Dictionary<String, String> dictionary = new com.liferay.portal.kernel.util.HashMapDictionary();
        dictionary.put("testReqiredString", "testReqiredString1");
        _assertTestConfiguration(ConfigurableUtil.createConfigurable(ConfigurableUtilTest.TestConfiguration.class, dictionary), "testReqiredString1");
        // Test map
        _assertTestConfiguration(ConfigurableUtil.createConfigurable(ConfigurableUtilTest.TestConfiguration.class, Collections.singletonMap("testReqiredString", "testReqiredString2")), "testReqiredString2");
    }

    @Test
    public void testMisc() {
        // Exception
        try {
            ConfigurableUtil.createConfigurable(ConfigurableUtilTest.TestConfiguration.class, Collections.emptyMap());
            Assert.fail();
        } catch (RuntimeException re) {
            Assert.assertEquals(("Unable to create snapshot class for " + (ConfigurableUtilTest.TestConfiguration.class)), re.getMessage());
            Throwable throwable = re.getCause();
            throwable = throwable.getCause();
            Assert.assertTrue((throwable instanceof IllegalStateException));
            Assert.assertEquals("Attribute is required but not set testReqiredString", throwable.getMessage());
        }
        // Constructor
        new ConfigurableUtil();
    }

    public static class TestClass {
        public TestClass(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }

        private final String _name;
    }

    private interface TestConfiguration {
        @AD(deflt = "true", required = false)
        public boolean testBoolean();

        @AD(deflt = "1", required = false)
        public byte testByte();

        @AD(deflt = "test.class", required = false)
        public ConfigurableUtilTest.TestClass testClass();

        @AD(deflt = "1.0", required = false)
        public double testDouble();

        @AD(deflt = "TEST_VALUE", required = false)
        public ConfigurableUtilTest.TestEnum testEnum();

        @AD(deflt = "1.0", required = false)
        public float testFloat();

        @AD(deflt = "100", required = false)
        public int testInt();

        @AD(deflt = "100", required = false)
        public long testLong();

        @AD(required = false)
        public String testNullResult();

        @AD(required = true)
        public String testReqiredString();

        @AD(deflt = "1", required = false)
        public short testShort();

        @AD(deflt = "test_string", required = false)
        public String testString();

        @AD(deflt = "test_string_1|test_string_2", required = false)
        public String[] testStringArray();
    }

    private enum TestEnum {

        TEST_VALUE;}
}

