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
package com.hazelcast.cache.jsr;


import Cache.Entry;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListenerException;
import org.jsr107.tck.event.CacheEntryListenerClient;
import org.jsr107.tck.event.CacheEntryListenerServer;
import org.jsr107.tck.event.CacheListenerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


// Base class for member and client side CacheListenerTest
public abstract class AbstractCacheListenerTest extends CacheListenerTest {
    @Rule
    public TestName testName = new TestName();

    // this field is private in the TCK test; when running our overridden test we use this field
    // otherwise the cacheEntryListenerServer is started by superclass
    private CacheEntryListenerServer cacheEntryListenerServer;

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    @Test
    public void testFilteredListener() {
        // remove standard listener.
        cacheEntryListenerServer.removeCacheEventListener(this.listener);
        cache.deregisterCacheEntryListener(this.listenerConfiguration);
        CacheEntryListenerClient<Long, String> clientListener = new CacheEntryListenerClient<Long, String>(cacheEntryListenerServer.getInetAddress(), cacheEntryListenerServer.getPort());
        MyCacheEntryListener<Long, String> filteredListener = new MyCacheEntryListener<Long, String>(oldValueRequired);
        CacheEntryListenerConfiguration<Long, String> listenerConfiguration = new javax.cache.configuration.MutableCacheEntryListenerConfiguration<Long, String>(FactoryBuilder.factoryOf(clientListener), FactoryBuilder.factoryOf(new AbstractCacheListenerTest.MyCacheEntryEventFilter()), oldValueRequired, true);
        cache.registerCacheEntryListener(listenerConfiguration);
        cacheEntryListenerServer.addCacheEventListener(filteredListener);
        Assert.assertEquals(0, filteredListener.getCreated());
        Assert.assertEquals(0, filteredListener.getUpdated());
        Assert.assertEquals(0, filteredListener.getRemoved());
        cache.put(1L, "Sooty");
        Assert.assertEquals(1, filteredListener.getCreated());
        Assert.assertEquals(0, filteredListener.getUpdated());
        Assert.assertEquals(0, filteredListener.getRemoved());
        Map<Long, String> entries = new HashMap<Long, String>();
        entries.put(2L, "Lucky");
        entries.put(3L, "Bryn");
        cache.putAll(entries);
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(0, filteredListener.getUpdated());
        Assert.assertEquals(0, filteredListener.getRemoved());
        cache.put(1L, "Zyn");
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(0, filteredListener.getUpdated());
        Assert.assertEquals(0, filteredListener.getRemoved());
        cache.remove(2L);
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(0, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        cache.replace(1L, "Fred");
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(1, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        cache.replace(3L, "Bryn", "Sooty");
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(2, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        cache.get(1L);
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(2, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        // containsKey is not a read for filteredListener purposes.
        cache.containsKey(1L);
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(2, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        // iterating should cause read events on non-expired entries
        for (Entry<Long, String> entry : cache) {
            String value = entry.getValue();
            logger.info(value);
        }
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(2, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        cache.getAndPut(1L, "Pistachio");
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(3, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        Set<Long> keys = new HashSet<Long>();
        keys.add(1L);
        cache.getAll(keys);
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(3, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        cache.getAndReplace(1L, "Prince");
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(4, filteredListener.getUpdated());
        Assert.assertEquals(1, filteredListener.getRemoved());
        cache.getAndRemove(1L);
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(4, filteredListener.getUpdated());
        Assert.assertEquals(2, filteredListener.getRemoved());
        Assert.assertEquals(2, filteredListener.getCreated());
        Assert.assertEquals(4, filteredListener.getUpdated());
        Assert.assertEquals(2, filteredListener.getRemoved());
    }

    private static class MyCacheEntryEventFilter implements Serializable , CacheEntryEventFilter<Long, String> {
        @Override
        public boolean evaluate(CacheEntryEvent<? extends Long, ? extends String> event) throws CacheEntryListenerException {
            return ((((((event.getValue()) == null) || (event.getValue().contains("a"))) || (event.getValue().contains("e"))) || (event.getValue().contains("i"))) || (event.getValue().contains("o"))) || (event.getValue().contains("u"));
        }
    }
}

