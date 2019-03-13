/**
 * Copyright 2014 Netflix, Inc.
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
package com.netflix.config;


import AbstractConfiguration.EVENT_ADD_PROPERTY;
import AbstractConfiguration.EVENT_CLEAR;
import AbstractConfiguration.EVENT_CLEAR_PROPERTY;
import AbstractConfiguration.EVENT_SET_PROPERTY;
import ConcurrentMapConfiguration.DISABLE_DELIMITER_PARSING;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentMapConfigurationTest {
    @Test
    public void testSetGet() {
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        conf.addProperty("key1", "xyz");
        Assert.assertEquals("xyz", conf.getProperty("key1"));
        conf.setProperty("key1", "newProp");
        Assert.assertEquals("newProp", conf.getProperty("key1"));
        conf.setProperty("listProperty", "0,1,2,3");
        Assert.assertTrue(((conf.getProperty("listProperty")) instanceof List));
        List<String> props = ((List<String>) (conf.getProperty("listProperty")));
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, Integer.parseInt(props.get(i)));
        }
        conf.addProperty("listProperty", "4");
        conf.addProperty("listProperty", Arrays.asList(new String[]{ "5", "6" }));
        props = ((List<String>) (conf.getProperty("listProperty")));
        for (int i = 0; i < 7; i++) {
            Assert.assertEquals(i, Integer.parseInt(props.get(i)));
        }
        Date date = new Date();
        conf.setProperty("listProperty", date);
        Assert.assertEquals(date, conf.getProperty("listProperty"));
    }

    @Test
    public void testDelimiterParsingDisabled() {
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        conf.setDelimiterParsingDisabled(true);
        conf.setProperty("listProperty", "0,1,2,3");
        Assert.assertEquals("0,1,2,3", conf.getProperty("listProperty"));
        conf.addProperty("listProperty2", "0,1,2,3");
        Assert.assertEquals("0,1,2,3", conf.getProperty("listProperty2"));
        conf.setDelimiterParsingDisabled(false);
        Assert.assertEquals("0,1,2,3", conf.getProperty("listProperty"));
        conf.setProperty("anotherList", "0,1,2,3");
        List<String> props = ((List<String>) (conf.getProperty("anotherList")));
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, Integer.parseInt(props.get(i)));
        }
    }

    @Test
    public void testConcurrency() {
        final ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        ExecutorService exectuor = Executors.newFixedThreadPool(20);
        final CountDownLatch doneSignal = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            final Integer index = i;
            exectuor.submit(new Runnable() {
                public void run() {
                    conf.addProperty("key", index);
                    conf.addProperty("key", "stringValue");
                    doneSignal.countDown();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
        }
        List prop = ((List) (conf.getProperty("key")));
        Assert.assertEquals(2000, prop.size());
    }

    @Test
    public void testListeners() {
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        final AtomicReference<ConfigurationEvent> eventRef = new AtomicReference<ConfigurationEvent>();
        conf.addConfigurationListener(new ConfigurationListener() {
            @Override
            public void configurationChanged(ConfigurationEvent arg0) {
                eventRef.set(arg0);
            }
        });
        conf.addProperty("key", "1");
        Assert.assertEquals(1, conf.getInt("key"));
        ConfigurationEvent event = eventRef.get();
        Assert.assertEquals("key", event.getPropertyName());
        Assert.assertEquals("1", event.getPropertyValue());
        Assert.assertTrue((conf == (event.getSource())));
        Assert.assertEquals(EVENT_ADD_PROPERTY, event.getType());
        conf.setProperty("key", "2");
        event = eventRef.get();
        Assert.assertEquals("key", event.getPropertyName());
        Assert.assertEquals("2", event.getPropertyValue());
        Assert.assertTrue((conf == (event.getSource())));
        Assert.assertEquals(EVENT_SET_PROPERTY, event.getType());
        conf.clearProperty("key");
        event = eventRef.get();
        Assert.assertEquals("key", event.getPropertyName());
        Assert.assertNull(event.getPropertyValue());
        Assert.assertTrue((conf == (event.getSource())));
        Assert.assertEquals(EVENT_CLEAR_PROPERTY, event.getType());
        conf.clear();
        Assert.assertFalse(conf.getKeys().hasNext());
        event = eventRef.get();
        Assert.assertTrue((conf == (event.getSource())));
        Assert.assertEquals(EVENT_CLEAR, event.getType());
    }

    @Test
    public void testInterpolate() {
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        conf.setProperty("key1", "value1");
        conf.setProperty("key2", "${key1}");
        Assert.assertEquals("${key1}", conf.getProperty("key2"));
        Assert.assertEquals("value1", conf.getString("key2"));
    }

    @Test
    public void testSubset() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("a", "x.y.a");
        properties.put("b", "x.y.b");
        properties.put("c", "x.y.c");
        ConcurrentMapConfiguration config = new ConcurrentMapConfiguration();
        for (String prop : properties.values()) {
            config.addProperty(prop, prop);
        }
        Configuration subconfig = config.subset("x.y");
        Set<String> subsetKeys = new HashSet<String>();
        for (Iterator<String> i = subconfig.getKeys(); i.hasNext();) {
            String key = i.next();
            subsetKeys.add(key);
            Assert.assertEquals(properties.get(key), subconfig.getString(key));
        }
        Assert.assertEquals(properties.keySet(), subsetKeys);
    }

    static class MyListener implements ConfigurationListener {
        AtomicLong counter = new AtomicLong();

        @Override
        public void configurationChanged(ConfigurationEvent arg0) {
            counter.incrementAndGet();
        }

        public void resetCounter() {
            counter.set(0);
        }

        public long getCount() {
            return counter.get();
        }
    }

    @Test
    public void testPerformance() {
        ConcurrentMapConfigurationTest.MyListener listener = new ConcurrentMapConfigurationTest.MyListener();
        BaseConfiguration baseConfig = new BaseConfiguration();
        baseConfig.addConfigurationListener(listener);
        HierarchicalConfiguration hConfig = new HierarchicalConfiguration();
        hConfig.addConfigurationListener(listener);
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        conf.addConfigurationListener(listener);
        testConfigurationSet(baseConfig);
        testConfigurationSet(hConfig);
        testConfigurationSet(conf);
        testConfigurationAdd(baseConfig);
        testConfigurationAdd(hConfig);
        testConfigurationAdd(conf);
        testConfigurationGet(baseConfig);
        testConfigurationGet(hConfig);
        testConfigurationGet(conf);
    }

    @Test
    public void testNullValue() {
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        try {
            conf.setProperty("xyz", null);
        } catch (NullPointerException e) {
            Assert.assertNotNull(e);
        }
        try {
            conf.addProperty("xyz", null);
        } catch (NullPointerException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testDelimiterParsingSystemProperty() {
        // Default behavior of enabling Delimiter parsing
        ConcurrentMapConfiguration conf = new ConcurrentMapConfiguration();
        conf.setProperty("anotherList", "0,1,2,3");
        List<String> props = ((List<String>) (conf.getProperty("anotherList")));
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, Integer.parseInt(props.get(i)));
        }
        // Set the system property to turn off delimiter parsing
        System.setProperty(DISABLE_DELIMITER_PARSING, "true");
        ConcurrentMapConfiguration conf_delimiter_parsing_off = new ConcurrentMapConfiguration();
        conf_delimiter_parsing_off.setProperty("listProperty", "0,1,2,3");
        Assert.assertEquals("0,1,2,3", conf_delimiter_parsing_off.getProperty("listProperty"));
        conf_delimiter_parsing_off.addProperty("listProperty2", "0,1,2,3");
        Assert.assertEquals("0,1,2,3", conf_delimiter_parsing_off.getProperty("listProperty2"));
        conf_delimiter_parsing_off.setDelimiterParsingDisabled(false);
        conf_delimiter_parsing_off.setProperty("listProperty3", "0,1,2,3");
        List<String> newProps = ((List<String>) (conf_delimiter_parsing_off.getProperty("listProperty3")));
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, Integer.parseInt(newProps.get(i)));
        }
        // Restore the system property to turn off delimiter parsing
        System.setProperty(DISABLE_DELIMITER_PARSING, "false");
    }
}

