/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import Constants.DUBBO_PROPERTIES_KEY;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.dubbo.common.threadpool.ThreadPool;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConfigUtilsTest {
    @Test
    public void testIsNotEmpty() throws Exception {
        MatcherAssert.assertThat(ConfigUtils.isNotEmpty("abc"), Matchers.is(true));
    }

    @Test
    public void testIsEmpty() throws Exception {
        MatcherAssert.assertThat(ConfigUtils.isEmpty(null), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty(""), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("false"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("FALSE"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("0"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("null"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("NULL"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("n/a"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isEmpty("N/A"), Matchers.is(true));
    }

    @Test
    public void testIsDefault() throws Exception {
        MatcherAssert.assertThat(ConfigUtils.isDefault("true"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isDefault("TRUE"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isDefault("default"), Matchers.is(true));
        MatcherAssert.assertThat(ConfigUtils.isDefault("DEFAULT"), Matchers.is(true));
    }

    @Test
    public void testMergeValues() {
        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "aaa,bbb,default.custom", Arrays.asList("fixed", "default.limited", "cached"));
        Assertions.assertEquals(Arrays.asList("fixed", "cached", "aaa", "bbb", "default.custom"), merged);
    }

    @Test
    public void testMergeValuesAddDefault() {
        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "aaa,bbb,default,zzz", Arrays.asList("fixed", "default.limited", "cached"));
        Assertions.assertEquals(Arrays.asList("aaa", "bbb", "fixed", "cached", "zzz"), merged);
    }

    @Test
    public void testMergeValuesDeleteDefault() {
        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "-default", Arrays.asList("fixed", "default.limited", "cached"));
        Assertions.assertEquals(Arrays.asList(), merged);
    }

    @Test
    public void testMergeValuesDeleteDefault_2() {
        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "-default,aaa", Arrays.asList("fixed", "default.limited", "cached"));
        Assertions.assertEquals(Arrays.asList("aaa"), merged);
    }

    /**
     * The user configures -default, which will delete all the default parameters
     */
    @Test
    public void testMergeValuesDelete() {
        List<String> merged = ConfigUtils.mergeValues(ThreadPool.class, "-fixed,aaa", Arrays.asList("fixed", "default.limited", "cached"));
        Assertions.assertEquals(Arrays.asList("cached", "aaa"), merged);
    }

    @Test
    public void testReplaceProperty() throws Exception {
        String s = ConfigUtils.replaceProperty("1${a.b.c}2${a.b.c}3", Collections.singletonMap("a.b.c", "ABC"));
        Assertions.assertEquals(s, "1ABC2ABC3");
        s = ConfigUtils.replaceProperty("1${a.b.c}2${a.b.c}3", Collections.<String, String>emptyMap());
        Assertions.assertEquals(s, "123");
    }

    @Test
    public void testGetProperties1() throws Exception {
        try {
            System.setProperty(DUBBO_PROPERTIES_KEY, "properties.load");
            Properties p = ConfigUtils.getProperties();
            MatcherAssert.assertThat(((String) (p.get("a"))), Matchers.equalTo("12"));
            MatcherAssert.assertThat(((String) (p.get("b"))), Matchers.equalTo("34"));
            MatcherAssert.assertThat(((String) (p.get("c"))), Matchers.equalTo("56"));
        } finally {
            System.clearProperty(DUBBO_PROPERTIES_KEY);
        }
    }

    @Test
    public void testGetProperties2() throws Exception {
        System.clearProperty(DUBBO_PROPERTIES_KEY);
        Properties p = ConfigUtils.getProperties();
        MatcherAssert.assertThat(((String) (p.get("dubbo"))), Matchers.equalTo("properties"));
    }

    @Test
    public void testAddProperties() throws Exception {
        Properties p = new Properties();
        p.put("key1", "value1");
        ConfigUtils.addProperties(p);
        MatcherAssert.assertThat(((String) (ConfigUtils.getProperties().get("key1"))), Matchers.equalTo("value1"));
    }

    @Test
    public void testLoadPropertiesNoFile() throws Exception {
        Properties p = ConfigUtils.loadProperties("notExisted", true);
        Properties expected = new Properties();
        Assertions.assertEquals(expected, p);
        p = ConfigUtils.loadProperties("notExisted", false);
        Assertions.assertEquals(expected, p);
    }

    @Test
    public void testGetProperty() throws Exception {
        MatcherAssert.assertThat(ConfigUtils.getProperty("dubbo"), Matchers.equalTo("properties"));
    }

    @Test
    public void testGetPropertyDefaultValue() throws Exception {
        MatcherAssert.assertThat(ConfigUtils.getProperty("not-exist", "default"), Matchers.equalTo("default"));
    }

    @Test
    public void testGetPropertyFromSystem() throws Exception {
        try {
            System.setProperty("dubbo", "system");
            MatcherAssert.assertThat(ConfigUtils.getProperty("dubbo"), Matchers.equalTo("system"));
        } finally {
            System.clearProperty("dubbo");
        }
    }

    @Test
    public void testGetSystemProperty() throws Exception {
        try {
            System.setProperty("dubbo", "system-only");
            MatcherAssert.assertThat(ConfigUtils.getSystemProperty("dubbo"), Matchers.equalTo("system-only"));
        } finally {
            System.clearProperty("dubbo");
        }
    }

    @Test
    public void testLoadProperties() throws Exception {
        Properties p = ConfigUtils.loadProperties("dubbo.properties");
        MatcherAssert.assertThat(((String) (p.get("dubbo"))), Matchers.equalTo("properties"));
    }

    @Test
    public void testLoadPropertiesOneFile() throws Exception {
        Properties p = ConfigUtils.loadProperties("properties.load", false);
        Properties expected = new Properties();
        expected.put("a", "12");
        expected.put("b", "34");
        expected.put("c", "56");
        Assertions.assertEquals(expected, p);
    }

    @Test
    public void testLoadPropertiesOneFileAllowMulti() throws Exception {
        Properties p = ConfigUtils.loadProperties("properties.load", true);
        Properties expected = new Properties();
        expected.put("a", "12");
        expected.put("b", "34");
        expected.put("c", "56");
        Assertions.assertEquals(expected, p);
    }

    @Test
    public void testLoadPropertiesOneFileNotRootPath() throws Exception {
        Properties p = ConfigUtils.loadProperties("META-INF/dubbo/internal/org.apache.dubbo.common.threadpool.ThreadPool", false);
        Properties expected = new Properties();
        expected.put("fixed", "org.apache.dubbo.common.threadpool.support.fixed.FixedThreadPool");
        expected.put("cached", "org.apache.dubbo.common.threadpool.support.cached.CachedThreadPool");
        expected.put("limited", "org.apache.dubbo.common.threadpool.support.limited.LimitedThreadPool");
        expected.put("eager", "org.apache.dubbo.common.threadpool.support.eager.EagerThreadPool");
        Assertions.assertEquals(expected, p);
    }

    @Test
    public void testLoadPropertiesMultiFileNotRootPath() throws Exception {
        Properties p = ConfigUtils.loadProperties("META-INF/dubbo/internal/org.apache.dubbo.common.status.StatusChecker", true);
        Properties expected = new Properties();
        expected.put("memory", "org.apache.dubbo.common.status.support.MemoryStatusChecker");
        expected.put("load", "org.apache.dubbo.common.status.support.LoadStatusChecker");
        expected.put("aa", "12");
        Assertions.assertEquals(expected, p);
    }

    @Test
    public void testGetPid() throws Exception {
        MatcherAssert.assertThat(ConfigUtils.getPid(), Matchers.greaterThan(0));
    }
}

