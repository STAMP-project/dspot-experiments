/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.properties;


import Diagnostics.METRICS_LEVEL;
import GroupProperty.APPLICATION_VALIDATION_TOKEN;
import GroupProperty.IO_INPUT_THREAD_COUNT;
import GroupProperty.IO_THREAD_COUNT;
import GroupProperty.LOCK_MAX_LEASE_TIME_SECONDS;
import GroupProperty.PARTITION_TABLE_SEND_INTERVAL;
import ProbeLevel.DEBUG;
import ProbeLevel.MANDATORY;
import com.hazelcast.config.Config;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class HazelcastPropertiesTest {
    private final Config config = new Config();

    private final HazelcastProperties defaultProperties = new HazelcastProperties(config);

    @Test
    public void testNullProperties() {
        HazelcastProperties properties = new HazelcastProperties(((Properties) (null)));
        Assert.assertTrue(properties.keySet().isEmpty());
    }

    @Test
    public void testKeySet_whenPropertiesAvailable() {
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        props.setProperty("key2", "value2");
        HazelcastProperties properties = new HazelcastProperties(props);
        Assert.assertEquals(props.keySet(), properties.keySet());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testKeySet_isImmutable() {
        HazelcastProperties properties = new HazelcastProperties(config);
        properties.keySet().remove("foo");
    }

    @Test(expected = NullPointerException.class)
    public void testGet_whenKeyNull() {
        HazelcastProperties properties = new HazelcastProperties(config);
        properties.get(null);
    }

    @Test
    public void testGet_whenKeyNotExisting() {
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        props.setProperty("key2", "value2");
        HazelcastProperties properties = new HazelcastProperties(props);
        Assert.assertNull(properties.get("nonExistingKey"));
    }

    @Test
    public void testGet_whenKeyExisting() {
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        props.setProperty("key2", "value2");
        HazelcastProperties properties = new HazelcastProperties(props);
        Assert.assertEquals("value1", properties.get("key1"));
    }

    @Test
    public void setProperty_ensureHighestPriorityOfConfig() {
        config.setProperty(GroupProperty.ENTERPRISE_LICENSE_KEY.getName(), "configValue");
        GroupProperty.ENTERPRISE_LICENSE_KEY.setSystemProperty("systemValue");
        HazelcastProperties properties = new HazelcastProperties(config);
        String value = properties.getString(GroupProperty.ENTERPRISE_LICENSE_KEY);
        System.clearProperty(GroupProperty.ENTERPRISE_LICENSE_KEY.getName());
        Assert.assertEquals("configValue", value);
    }

    @Test
    public void setProperty_ensureUsageOfSystemProperty() {
        GroupProperty.ENTERPRISE_LICENSE_KEY.setSystemProperty("systemValue");
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        String value = hazelcastProperties.getString(GroupProperty.ENTERPRISE_LICENSE_KEY);
        System.clearProperty(GroupProperty.ENTERPRISE_LICENSE_KEY.getName());
        Assert.assertEquals("systemValue", value);
    }

    @Test
    public void setProperty_ensureUsageOfDefaultValue() {
        String value = defaultProperties.getString(GroupProperty.ENTERPRISE_LICENSE_KEY);
        Assert.assertNull(value);
    }

    @Test
    public void setProperty_inheritDefaultValueOfParentProperty() {
        String inputIOThreadCount = defaultProperties.getString(IO_INPUT_THREAD_COUNT);
        Assert.assertEquals(IO_THREAD_COUNT.getDefaultValue(), inputIOThreadCount);
    }

    @Test
    public void setProperty_inheritActualValueOfParentProperty() {
        config.setProperty(IO_THREAD_COUNT.getName(), "1");
        HazelcastProperties properties = new HazelcastProperties(config);
        String inputIOThreadCount = properties.getString(IO_INPUT_THREAD_COUNT);
        Assert.assertEquals("1", inputIOThreadCount);
        Assert.assertNotEquals(IO_THREAD_COUNT.getDefaultValue(), inputIOThreadCount);
    }

    @Test
    public void getSystemProperty() {
        APPLICATION_VALIDATION_TOKEN.setSystemProperty("token");
        Assert.assertEquals("token", APPLICATION_VALIDATION_TOKEN.getSystemProperty());
        System.clearProperty(APPLICATION_VALIDATION_TOKEN.getName());
    }

    @Test
    public void getBoolean() {
        HazelcastProperty property = new HazelcastProperty("foo", "true");
        boolean isHumanReadable = defaultProperties.getBoolean(property);
        Assert.assertTrue(isHumanReadable);
    }

    @Test
    public void getInteger() {
        int ioThreadCount = defaultProperties.getInteger(IO_THREAD_COUNT);
        Assert.assertEquals(3, ioThreadCount);
    }

    @Test
    public void getLong() {
        long lockMaxLeaseTimeSeconds = defaultProperties.getLong(LOCK_MAX_LEASE_TIME_SECONDS);
        Assert.assertEquals(Long.MAX_VALUE, lockMaxLeaseTimeSeconds);
    }

    @Test
    public void getFloat() {
        HazelcastProperty property = new HazelcastProperty("foo", "10");
        float maxFileSize = defaultProperties.getFloat(property);
        Assert.assertEquals(10, maxFileSize, 1.0E-4);
    }

    @Test
    public void getPositiveMillisOrDefault() {
        String name = PARTITION_TABLE_SEND_INTERVAL.getName();
        config.setProperty(name, "-300");
        HazelcastProperty property = new HazelcastProperty(name, "20", TimeUnit.MILLISECONDS);
        long millis = defaultProperties.getPositiveMillisOrDefault(property);
        Assert.assertEquals(20, millis);
    }

    @Test
    public void getPositiveMillisOrDefaultWithManualDefault() {
        String name = PARTITION_TABLE_SEND_INTERVAL.getName();
        config.setProperty(name, "-300");
        HazelcastProperties properties = new HazelcastProperties(config);
        HazelcastProperty property = new HazelcastProperty(name, "20", TimeUnit.MILLISECONDS);
        long millis = properties.getPositiveMillisOrDefault(property, 50);
        Assert.assertEquals(50, millis);
    }

    @Test
    public void getTimeUnit() {
        config.setProperty(PARTITION_TABLE_SEND_INTERVAL.getName(), "300");
        HazelcastProperties properties = new HazelcastProperties(config);
        Assert.assertEquals(300, properties.getSeconds(PARTITION_TABLE_SEND_INTERVAL));
    }

    @Test
    public void getTimeUnit_default() {
        long expectedSeconds = 15;
        long intervalNanos = defaultProperties.getNanos(PARTITION_TABLE_SEND_INTERVAL);
        long intervalMillis = defaultProperties.getMillis(PARTITION_TABLE_SEND_INTERVAL);
        long intervalSeconds = defaultProperties.getSeconds(PARTITION_TABLE_SEND_INTERVAL);
        Assert.assertEquals(TimeUnit.SECONDS.toNanos(expectedSeconds), intervalNanos);
        Assert.assertEquals(TimeUnit.SECONDS.toMillis(expectedSeconds), intervalMillis);
        Assert.assertEquals(expectedSeconds, intervalSeconds);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTimeUnit_noTimeUnitProperty() {
        defaultProperties.getMillis(APPLICATION_VALIDATION_TOKEN);
    }

    @Test
    public void getEnum() {
        config.setProperty(METRICS_LEVEL.getName(), DEBUG.toString());
        HazelcastProperties properties = new HazelcastProperties(config);
        ProbeLevel level = properties.getEnum(METRICS_LEVEL, ProbeLevel.class);
        Assert.assertEquals(DEBUG, level);
    }

    @Test
    public void getEnum_default() {
        ProbeLevel level = defaultProperties.getEnum(METRICS_LEVEL, ProbeLevel.class);
        Assert.assertEquals(MANDATORY, level);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEnum_nonExistingEnum() {
        config.setProperty(METRICS_LEVEL.getName(), "notExist");
        HazelcastProperties properties = new HazelcastProperties(config);
        properties.getEnum(METRICS_LEVEL, ProbeLevel.class);
    }

    @Test
    public void getEnum_ignoredName() {
        config.setProperty(METRICS_LEVEL.getName(), "dEbUg");
        HazelcastProperties properties = new HazelcastProperties(config);
        ProbeLevel level = properties.getEnum(METRICS_LEVEL, ProbeLevel.class);
        Assert.assertEquals(DEBUG, level);
    }

    @Test
    public void getString_whenDeprecatedNameUsed() {
        Properties props = new Properties();
        props.setProperty("oldname", "10");
        HazelcastProperties properties = new HazelcastProperties(props);
        HazelcastProperty property = new HazelcastProperty("newname").setDeprecatedName("oldname");
        String value = properties.getString(property);
        Assert.assertEquals("10", value);
    }
}

