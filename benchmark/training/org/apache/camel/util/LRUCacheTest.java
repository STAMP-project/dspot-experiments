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
package org.apache.camel.util;


import org.apache.camel.Service;
import org.apache.camel.support.LRUCache;
import org.junit.Assert;
import org.junit.Test;


public class LRUCacheTest extends Assert {
    private LRUCache<String, Service> cache;

    @Test
    public void testLRUCache() {
        LRUCacheTest.MyService service1 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service2 = new LRUCacheTest.MyService();
        cache.put("A", service1);
        cache.put("B", service2);
        Assert.assertEquals(2, cache.size());
        Assert.assertSame(service1, cache.get("A"));
        Assert.assertSame(service2, cache.get("B"));
    }

    @Test
    public void testLRUCacheEviction() throws Exception {
        LRUCacheTest.MyService service1 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service2 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service3 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service4 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service5 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service6 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service7 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service8 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service9 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service10 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service11 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service12 = new LRUCacheTest.MyService();
        cache.put("A", service1);
        Assert.assertNull(service1.getStopped());
        cache.put("B", service2);
        Assert.assertNull(service2.getStopped());
        cache.put("C", service3);
        Assert.assertNull(service3.getStopped());
        cache.put("D", service4);
        Assert.assertNull(service4.getStopped());
        cache.put("E", service5);
        Assert.assertNull(service5.getStopped());
        cache.put("F", service6);
        Assert.assertNull(service6.getStopped());
        cache.put("G", service7);
        Assert.assertNull(service7.getStopped());
        cache.put("H", service8);
        Assert.assertNull(service8.getStopped());
        cache.put("I", service9);
        Assert.assertNull(service9.getStopped());
        cache.put("J", service10);
        Assert.assertNull(service10.getStopped());
        // we are now full
        Assert.assertEquals(10, cache.size());
        cache.put("K", service11);
        Assert.assertNull(service11.getStopped());
        // the eviction is async so force cleanup
        cache.cleanUp();
        cache.put("L", service12);
        // the eviction is async so force cleanup
        cache.cleanUp();
        Assert.assertEquals(10, cache.size());
    }

    @Test
    public void testLRUCacheHitsAndMisses() {
        LRUCacheTest.MyService service1 = new LRUCacheTest.MyService();
        LRUCacheTest.MyService service2 = new LRUCacheTest.MyService();
        cache.put("A", service1);
        cache.put("B", service2);
        Assert.assertEquals(0, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get("A");
        Assert.assertEquals(1, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get("A");
        Assert.assertEquals(2, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get("B");
        Assert.assertEquals(3, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get("C");
        Assert.assertEquals(3, cache.getHits());
        Assert.assertEquals(1, cache.getMisses());
        cache.get("D");
        Assert.assertEquals(3, cache.getHits());
        Assert.assertEquals(2, cache.getMisses());
        cache.resetStatistics();
        Assert.assertEquals(0, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get("B");
        Assert.assertEquals(1, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.clear();
        Assert.assertEquals(0, cache.getHits());
        Assert.assertEquals(0, cache.getMisses());
        cache.get("B");
        Assert.assertEquals(0, cache.getHits());
        Assert.assertEquals(1, cache.getMisses());
    }

    private static final class MyService implements Service {
        private Boolean stopped;

        public void start() throws Exception {
        }

        public void stop() throws Exception {
            stopped = true;
        }

        public Boolean getStopped() {
            return stopped;
        }
    }
}

