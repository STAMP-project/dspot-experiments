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


import com.google.common.collect.Maps;
import com.netflix.config.AbstractDynamicPropertyListener.EventType;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.configuration.AbstractConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class DynamicPropertyUpdaterTest {
    DynamicPropertyUpdater dynamicPropertyUpdater;

    /**
     * Test method for {@link com.charter.aesd.archaius.DynamicPropertyUpdater#updateProperties(com.netflix.config.WatchedUpdateResult, org.apache.commons.configuration.Configuration, boolean)}.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testUpdateProperties() throws InterruptedException {
        AbstractConfiguration.setDefaultListDelimiter(',');
        AbstractConfiguration config = new ConcurrentCompositeConfiguration();
        config.addConfigurationListener(new ExpandedConfigurationListenerAdapter(new DynamicPropertyUpdaterTest.MyListener()));
        DynamicPropertyUpdaterTest.MyListener.resetCount();
        config.setProperty("test", "host,host1,host2");
        config.setProperty("test12", "host12");
        Map<String, Object> added = Maps.newHashMap();
        added.put("test.host", "test,test1");
        Map<String, Object> changed = Maps.newHashMap();
        changed.put("test", "host,host1");
        changed.put("test.host", "");
        dynamicPropertyUpdater.updateProperties(WatchedUpdateResult.createIncremental(added, changed, null), config, false);
        Assert.assertEquals("", config.getProperty("test.host"));
        Assert.assertEquals(2, ((CopyOnWriteArrayList) (config.getProperty("test"))).size());
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test"))).contains("host"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test"))).contains("host1"));
        Assert.assertEquals(5, DynamicPropertyUpdaterTest.MyListener.count);
    }

    @Test
    public void testAddorChangeProperty() {
        AbstractConfiguration.setDefaultListDelimiter(',');
        AbstractConfiguration config = new ConcurrentCompositeConfiguration();
        config.addConfigurationListener(new ExpandedConfigurationListenerAdapter(new DynamicPropertyUpdaterTest.MyListener()));
        DynamicPropertyUpdaterTest.MyListener.resetCount();
        config.setProperty("test.host", "test,test1,test2");
        Assert.assertEquals(1, DynamicPropertyUpdaterTest.MyListener.count);
        dynamicPropertyUpdater.addOrChangeProperty("test.host", "test,test1,test2", config);
        Assert.assertEquals(3, ((CopyOnWriteArrayList) (config.getProperty("test.host"))).size());
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test1"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test2"));
        Assert.assertEquals(1, DynamicPropertyUpdaterTest.MyListener.count);
        dynamicPropertyUpdater.addOrChangeProperty("test.host", "test,test1,test2", config);
        Assert.assertEquals(3, ((CopyOnWriteArrayList) (config.getProperty("test.host"))).size());
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test1"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test2"));
        Assert.assertEquals(1, DynamicPropertyUpdaterTest.MyListener.count);
        dynamicPropertyUpdater.addOrChangeProperty("test.host", "test,test1", config);
        Assert.assertEquals(2, ((CopyOnWriteArrayList) (config.getProperty("test.host"))).size());
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test1"));
        Assert.assertEquals(2, DynamicPropertyUpdaterTest.MyListener.count);
        dynamicPropertyUpdater.addOrChangeProperty("test.host1", "test1,test12", config);
        Assert.assertEquals(2, ((CopyOnWriteArrayList) (config.getProperty("test.host1"))).size());
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host1"))).contains("test1"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host1"))).contains("test12"));
        Assert.assertEquals(3, DynamicPropertyUpdaterTest.MyListener.count);
        config.setProperty("test.host1", "test1.test12");
        dynamicPropertyUpdater.addOrChangeProperty("test.host1", "test1.test12", config);
        Assert.assertEquals("test1.test12", config.getProperty("test.host1"));
        Assert.assertEquals(4, DynamicPropertyUpdaterTest.MyListener.count);
    }

    @Test
    public void testAddorUpdatePropertyWithColonDelimiter() {
        AbstractConfiguration.setDefaultListDelimiter(':');
        AbstractConfiguration config = new ConcurrentCompositeConfiguration();
        config.addConfigurationListener(new ExpandedConfigurationListenerAdapter(new DynamicPropertyUpdaterTest.MyListener()));
        DynamicPropertyUpdaterTest.MyListener.resetCount();
        config.setProperty("test.host", "test:test1:test2");
        Assert.assertEquals(1, DynamicPropertyUpdaterTest.MyListener.count);
        dynamicPropertyUpdater.addOrChangeProperty("test.host", "test:test1:test2", config);
        Assert.assertEquals(3, ((CopyOnWriteArrayList) (config.getProperty("test.host"))).size());
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test1"));
        Assert.assertTrue(((CopyOnWriteArrayList) (config.getProperty("test.host"))).contains("test2"));
        Assert.assertEquals(1, DynamicPropertyUpdaterTest.MyListener.count);// the config is not set again. when the value is still not changed.

        config.setProperty("test.host1", "test1:test12");
        // changing the new object value , the config.setProperty should be called again.
        dynamicPropertyUpdater.addOrChangeProperty("test.host1", "test1.test12", config);
        Assert.assertEquals("test1.test12", config.getProperty("test.host1"));
        Assert.assertEquals(3, DynamicPropertyUpdaterTest.MyListener.count);
    }

    static class MyListener extends AbstractDynamicPropertyListener {
        static int count = 0;

        @Override
        public void handlePropertyEvent(String arg0, Object arg1, EventType arg2) {
            incrementCount();
        }

        private void incrementCount() {
            (DynamicPropertyUpdaterTest.MyListener.count)++;
        }

        protected static void resetCount() {
            DynamicPropertyUpdaterTest.MyListener.count = 0;
        }
    }
}

