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


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class PollingSourceTest {
    static class DummyPollingSource implements PolledConfigurationSource {
        volatile boolean incremental;

        volatile Map<String, Object> full;

        volatile Map<String, Object> added;

        volatile Map<String, Object> deleted;

        volatile Map<String, Object> changed;

        public DummyPollingSource(boolean incremental) {
            this.incremental = incremental;
        }

        public synchronized void setIncremental(boolean value) {
            this.incremental = value;
        }

        public synchronized void setContent(String content, Map<String, Object> map) {
            String[] pairs = content.split(",");
            if (pairs != null) {
                for (String pair : pairs) {
                    String[] nameValue = pair.trim().split("=");
                    if ((nameValue.length) == 2) {
                        map.put(nameValue[0], nameValue[1]);
                    }
                }
            }
        }

        public synchronized void setFull(String content) {
            full = new ConcurrentHashMap<String, Object>();
            setContent(content, full);
        }

        public synchronized void setAdded(String content) {
            added = new ConcurrentHashMap<String, Object>();
            setContent(content, added);
        }

        public synchronized void setDeleted(String content) {
            deleted = new ConcurrentHashMap<String, Object>();
            setContent(content, deleted);
        }

        public synchronized void setChanged(String content) {
            changed = new ConcurrentHashMap<String, Object>();
            setContent(content, changed);
        }

        @Override
        public synchronized PollResult poll(boolean initial, Object checkPoint) throws Exception {
            if (incremental) {
                return PollResult.createIncremental(added, changed, deleted, null);
            } else {
                return PollResult.createFull(full);
            }
        }
    }

    @Test
    public void testDeletingPollingSource() throws Exception {
        BaseConfiguration config = new BaseConfiguration();
        config.addProperty("prop1", "original");
        PollingSourceTest.DummyPollingSource source = new PollingSourceTest.DummyPollingSource(false);
        source.setFull("prop1=changed");
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, false);
        ConfigurationWithPollingSource pollingConfig = new ConfigurationWithPollingSource(config, source, scheduler);
        Thread.sleep(200);
        Assert.assertEquals("changed", pollingConfig.getProperty("prop1"));
        source.setFull("");
        Thread.sleep(250);
        Assert.assertFalse(pollingConfig.containsKey("prop1"));
        source.setFull("prop1=changedagain,prop2=new");
        Thread.sleep(200);
        Assert.assertEquals("changedagain", pollingConfig.getProperty("prop1"));
        Assert.assertEquals("new", pollingConfig.getProperty("prop2"));
        source.setFull("prop3=new");
        Thread.sleep(200);
        Assert.assertFalse(pollingConfig.containsKey("prop1"));
        Assert.assertFalse(pollingConfig.containsKey("prop2"));
        Assert.assertEquals("new", pollingConfig.getProperty("prop3"));
    }

    @Test
    public void testNoneDeletingPollingSource() throws Exception {
        BaseConfiguration config = new BaseConfiguration();
        config.addProperty("prop1", "original");
        PollingSourceTest.DummyPollingSource source = new PollingSourceTest.DummyPollingSource(false);
        source.setFull("");
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, true);
        ConfigurationWithPollingSource pollingConfig = new ConfigurationWithPollingSource(config, source, scheduler);
        Thread.sleep(200);
        Assert.assertEquals("original", pollingConfig.getProperty("prop1"));
        source.setFull("prop1=changed");
        Thread.sleep(200);
        Assert.assertEquals("changed", pollingConfig.getProperty("prop1"));
        source.setFull("prop1=changedagain,prop2=new");
        Thread.sleep(200);
        Assert.assertEquals("changedagain", pollingConfig.getProperty("prop1"));
        Assert.assertEquals("new", pollingConfig.getProperty("prop2"));
        source.setFull("prop3=new");
        Thread.sleep(200);
        Assert.assertEquals("changedagain", pollingConfig.getProperty("prop1"));
        Assert.assertEquals("new", pollingConfig.getProperty("prop2"));
        Assert.assertEquals("new", pollingConfig.getProperty("prop3"));
    }

    @Test
    public void testIncrementalPollingSource() throws Exception {
        BaseConfiguration config = new BaseConfiguration();
        DynamicPropertyFactory.initWithConfigurationSource(config);
        DynamicStringProperty prop1 = new DynamicStringProperty("prop1", null);
        DynamicStringProperty prop2 = new DynamicStringProperty("prop2", null);
        config.addProperty("prop1", "original");
        PollingSourceTest.DummyPollingSource source = new PollingSourceTest.DummyPollingSource(true);
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, true);
        scheduler.setIgnoreDeletesFromSource(false);
        // ConfigurationWithPollingSource pollingConfig = new ConfigurationWithPollingSource(config, source,scheduler);
        scheduler.startPolling(source, config);
        Assert.assertEquals("original", config.getProperty("prop1"));
        Assert.assertEquals("original", prop1.get());
        source.setAdded("prop2=new");
        Thread.sleep(200);
        Assert.assertEquals("original", config.getProperty("prop1"));
        Assert.assertEquals("new", config.getProperty("prop2"));
        Assert.assertEquals("new", prop2.get());
        source.setDeleted("prop1=DoesNotMatter");
        source.setChanged("prop2=changed");
        source.setAdded("");
        Thread.sleep(200);
        Assert.assertFalse(config.containsKey("prop1"));
        Assert.assertNull(prop1.get());
        Assert.assertEquals("changed", config.getProperty("prop2"));
        Assert.assertEquals("changed", prop2.get());
    }
}

