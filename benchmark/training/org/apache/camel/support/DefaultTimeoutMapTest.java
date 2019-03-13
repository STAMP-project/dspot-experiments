/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.support;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultTimeoutMapTest extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTimeoutMapTest.class);

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

    @Test
    public void testDefaultTimeoutMap() throws Exception {
        DefaultTimeoutMap<?, ?> map = new DefaultTimeoutMap(executor);
        map.start();
        Assert.assertTrue(((map.currentTime()) > 0));
        Assert.assertEquals(0, map.size());
        map.stop();
    }

    @Test
    public void testDefaultTimeoutMapPurge() throws Exception {
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap(executor, 100);
        map.start();
        Assert.assertTrue(((map.currentTime()) > 0));
        Assert.assertEquals(0, map.size());
        map.put("A", 123, 50);
        Assert.assertEquals(1, map.size());
        Thread.sleep(250);
        if ((map.size()) > 0) {
            DefaultTimeoutMapTest.LOG.warn("Waiting extra due slow CI box");
            Thread.sleep(1000);
        }
        Assert.assertEquals(0, map.size());
        map.stop();
    }

    @Test
    public void testDefaultTimeoutMapForcePurge() throws Exception {
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap(executor, 100);
        map.start();
        Assert.assertTrue(((map.currentTime()) > 0));
        Assert.assertEquals(0, map.size());
        map.put("A", 123, 50);
        Assert.assertEquals(1, map.size());
        Thread.sleep(250);
        // will purge and remove old entries
        map.purge();
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void testDefaultTimeoutMapGetRemove() throws Exception {
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap(executor, 100);
        map.start();
        Assert.assertTrue(((map.currentTime()) > 0));
        Assert.assertEquals(0, map.size());
        map.put("A", 123, 50);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(123, ((int) (map.get("A"))));
        Object old = map.remove("A");
        Assert.assertEquals(123, old);
        Assert.assertEquals(null, map.get("A"));
        Assert.assertEquals(0, map.size());
        map.stop();
    }

    @Test
    public void testDefaultTimeoutMapGetKeys() throws Exception {
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap(executor, 100);
        map.start();
        Assert.assertTrue(((map.currentTime()) > 0));
        Assert.assertEquals(0, map.size());
        map.put("A", 123, 50);
        map.put("B", 456, 50);
        Assert.assertEquals(2, map.size());
        Object[] keys = map.getKeys();
        Assert.assertNotNull(keys);
        Assert.assertEquals(2, keys.length);
    }

    @Test
    public void testExecutor() throws Exception {
        ScheduledExecutorService e = Executors.newScheduledThreadPool(2);
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap(e, 50);
        map.start();
        Assert.assertEquals(50, map.getPurgePollTime());
        map.put("A", 123, 100);
        Assert.assertEquals(1, map.size());
        Thread.sleep(250);
        if ((map.size()) > 0) {
            DefaultTimeoutMapTest.LOG.warn("Waiting extra due slow CI box");
            Thread.sleep(1000);
        }
        // should have been timed out now
        Assert.assertEquals(0, map.size());
        Assert.assertSame(e, map.getExecutor());
        map.stop();
    }

    @Test
    public void testExpiredInCorrectOrder() throws Exception {
        final List<String> keys = new ArrayList<>();
        final List<Integer> values = new ArrayList<>();
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap<String, Integer>(executor, 100) {
            @Override
            public boolean onEviction(String key, Integer value) {
                keys.add(key);
                values.add(value);
                return true;
            }
        };
        map.start();
        Assert.assertEquals(0, map.size());
        map.put("A", 1, 50);
        map.put("B", 2, 30);
        map.put("C", 3, 40);
        map.put("D", 4, 20);
        map.put("E", 5, 40);
        // is not expired
        map.put("F", 6, 800);
        Thread.sleep(250);
        // force purge
        map.purge();
        Assert.assertEquals("D", keys.get(0));
        Assert.assertEquals(4, values.get(0).intValue());
        Assert.assertEquals("B", keys.get(1));
        Assert.assertEquals(2, values.get(1).intValue());
        Assert.assertEquals("C", keys.get(2));
        Assert.assertEquals(3, values.get(2).intValue());
        Assert.assertEquals("E", keys.get(3));
        Assert.assertEquals(5, values.get(3).intValue());
        Assert.assertEquals("A", keys.get(4));
        Assert.assertEquals(1, values.get(4).intValue());
        Assert.assertEquals(1, map.size());
        map.stop();
    }

    @Test
    public void testExpiredNotEvicted() throws Exception {
        final List<String> keys = new ArrayList<>();
        final List<Integer> values = new ArrayList<>();
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap<String, Integer>(executor, 100) {
            @Override
            public boolean onEviction(String key, Integer value) {
                // do not evict special key
                if ("gold".equals(key)) {
                    return false;
                }
                keys.add(key);
                values.add(value);
                return true;
            }
        };
        map.start();
        Assert.assertEquals(0, map.size());
        map.put("A", 1, 90);
        map.put("B", 2, 100);
        map.put("gold", 9, 110);
        map.put("C", 3, 120);
        Thread.sleep(250);
        // force purge
        map.purge();
        Assert.assertEquals("A", keys.get(0));
        Assert.assertEquals(1, values.get(0).intValue());
        Assert.assertEquals("B", keys.get(1));
        Assert.assertEquals(2, values.get(1).intValue());
        Assert.assertEquals("C", keys.get(2));
        Assert.assertEquals(3, values.get(2).intValue());
        // and keep the gold in the map
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(Integer.valueOf(9), map.get("gold"));
        map.stop();
    }

    @Test
    public void testDefaultTimeoutMapStopStart() throws Exception {
        DefaultTimeoutMap<String, Integer> map = new DefaultTimeoutMap(executor, 100);
        map.start();
        map.put("A", 1, 500);
        Assert.assertEquals(1, map.size());
        map.stop();
        Assert.assertEquals(0, map.size());
        map.put("A", 1, 50);
        // should not timeout as the scheduler doesn't run
        Thread.sleep(250);
        Assert.assertEquals(1, map.size());
        // start
        map.start();
        // start and wait for scheduler to purge
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(0, map.size()));
        map.stop();
    }
}

