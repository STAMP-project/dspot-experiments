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


import com.netflix.config.validation.ValidationException;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DynamicFileConfigurationTest {
    boolean propertyChanged = false;

    static DynamicPropertyFactory propertyFactory = null;

    private DynamicLongProperty longProp = null;

    private static DynamicProperty dynProp = null;

    static File configFile = null;

    static class Listener implements ConfigurationListener {
        volatile ConfigurationEvent lastEventBeforeUpdate;

        volatile ConfigurationEvent lastEventAfterUpdate;

        AtomicInteger counter = new AtomicInteger();

        @Override
        public void configurationChanged(ConfigurationEvent event) {
            System.out.println(((((((("Event received: " + (event.getType())) + ",") + (event.getPropertyName())) + ",") + (event.isBeforeUpdate())) + ",") + (event.getPropertyValue())));
            counter.incrementAndGet();
            if (event.isBeforeUpdate()) {
                lastEventBeforeUpdate = event;
            } else {
                lastEventAfterUpdate = event;
            }
        }

        public void clear() {
            lastEventBeforeUpdate = null;
            lastEventAfterUpdate = null;
        }

        public ConfigurationEvent getLastEvent(boolean beforeUpdate) {
            if (beforeUpdate) {
                return lastEventBeforeUpdate;
            } else {
                return lastEventAfterUpdate;
            }
        }

        public int getCount() {
            return counter.get();
        }
    }

    static DynamicFileConfigurationTest.Listener listener = new DynamicFileConfigurationTest.Listener();

    @Test
    public void testDefaultConfigFile() throws Exception {
        longProp = DynamicFileConfigurationTest.propertyFactory.getLongProperty("dprops1", Long.MAX_VALUE, new Runnable() {
            public void run() {
                propertyChanged = true;
            }
        });
        DynamicIntProperty validatedProp = new DynamicIntProperty("abc", 0) {
            @Override
            public void validate(String newValue) {
                if ((Integer.parseInt(newValue)) < 0) {
                    throw new ValidationException("Cannot be negative");
                }
            }
        };
        Assert.assertEquals(0, validatedProp.get());
        Assert.assertFalse(propertyChanged);
        DynamicDoubleProperty doubleProp = DynamicFileConfigurationTest.propertyFactory.getDoubleProperty("dprops2", 0.0);
        Assert.assertEquals(123456789, longProp.get());
        Assert.assertEquals(123456789, DynamicFileConfigurationTest.dynProp.getInteger().intValue());
        Assert.assertEquals(79.98, doubleProp.get(), 1.0E-5);
        Assert.assertEquals(Double.valueOf(79.98), doubleProp.getValue());
        Assert.assertEquals(Long.valueOf(123456789L), longProp.getValue());
        DynamicFileConfigurationTest.modifyConfigFile();
        ConfigurationManager.getConfigInstance().addConfigurationListener(DynamicFileConfigurationTest.listener);
        Thread.sleep(1000);
        Assert.assertEquals(Long.MIN_VALUE, longProp.get());
        Assert.assertEquals(0, validatedProp.get());
        Assert.assertTrue(propertyChanged);
        Assert.assertEquals(Double.MAX_VALUE, doubleProp.get(), 0.01);
        Assert.assertFalse(ConfigurationManager.isConfigurationInstalled());
        Thread.sleep(3000);
        // Only 4 events expected, two each for dprops1 and dprops2
        Assert.assertEquals(4, DynamicFileConfigurationTest.listener.getCount());
    }

    @Test
    public void testSwitchingToConfiguration() throws Exception {
        longProp = DynamicFileConfigurationTest.propertyFactory.getLongProperty("dprops1", Long.MAX_VALUE, new Runnable() {
            public void run() {
                propertyChanged = true;
            }
        });
        AbstractConfiguration newConfig = new ConcurrentMapConfiguration();
        DynamicStringProperty prop = DynamicFileConfigurationTest.propertyFactory.getStringProperty("abc", "default");
        newConfig.setProperty("abc", "nondefault");
        newConfig.setProperty("dprops1", "0");
        DynamicPropertyFactory.initWithConfigurationSource(newConfig);
        Thread.sleep(2000);
        Assert.assertEquals("nondefault", prop.get());
        Assert.assertEquals(0, longProp.get());
        Assert.assertTrue((newConfig == (ConfigurationManager.getConfigInstance())));
        Assert.assertTrue(ConfigurationManager.isConfigurationInstalled());
    }
}

