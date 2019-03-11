/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.client.cache;


import Cache.NamedKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.apache.druid.guice.GuiceInjectors;
import org.apache.druid.guice.JsonConfigProvider;
import org.apache.druid.guice.ManageLifecycle;
import org.apache.druid.initialization.Initialization;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.java.util.emitter.core.Emitter;
import org.apache.druid.java.util.emitter.core.Event;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.java.util.metrics.AbstractMonitor;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class MemcachedCacheTest {
    private static final Logger log = new Logger(MemcachedCacheTest.class);

    private static final byte[] HI = StringUtils.toUtf8("hiiiiiiiiiiiiiiiiiii");

    private static final byte[] HO = StringUtils.toUtf8("hooooooooooooooooooo");

    protected static final AbstractMonitor NOOP_MONITOR = new AbstractMonitor() {
        @Override
        public boolean doMonitor(ServiceEmitter emitter) {
            return false;
        }
    };

    private MemcachedCache cache;

    private final MemcachedCacheConfig memcachedCacheConfig = new MemcachedCacheConfig() {
        @Override
        public String getMemcachedPrefix() {
            return "druid-memcached-test";
        }

        @Override
        public int getTimeout() {
            return 10;
        }

        @Override
        public int getExpiration() {
            return 3600;
        }

        @Override
        public String getHosts() {
            return "localhost:9999";
        }
    };

    @Test
    public void testBasicInjection() throws Exception {
        final MemcachedCacheConfig config = new MemcachedCacheConfig() {
            @Override
            public String getHosts() {
                return "127.0.0.1:22";
            }
        };
        Injector injector = Initialization.makeInjectorWithModules(GuiceInjectors.makeStartupInjector(), ImmutableList.of(new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test/memcached");
                binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
                binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
                binder.bind(MemcachedCacheConfig.class).toInstance(config);
                binder.bind(Cache.class).toProvider(MemcachedProviderWithConfig.class).in(ManageLifecycle.class);
            }
        }));
        Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
        lifecycle.start();
        try {
            Cache cache = injector.getInstance(Cache.class);
            Assert.assertEquals(MemcachedCache.class, cache.getClass());
        } finally {
            lifecycle.stop();
        }
    }

    @Test
    public void testSimpleInjection() {
        final String uuid = UUID.randomUUID().toString();
        System.setProperty((uuid + ".type"), "memcached");
        System.setProperty((uuid + ".hosts"), "localhost");
        final Injector injector = Initialization.makeInjectorWithModules(GuiceInjectors.makeStartupInjector(), ImmutableList.<Module>of(new Module() {
            @Override
            public void configure(Binder binder) {
                binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test/memcached");
                binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
                binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
                binder.bind(Cache.class).toProvider(CacheProvider.class);
                JsonConfigProvider.bind(binder, uuid, CacheProvider.class);
            }
        }));
        final CacheProvider memcachedCacheProvider = injector.getInstance(CacheProvider.class);
        Assert.assertNotNull(memcachedCacheProvider);
        Assert.assertEquals(MemcachedCacheProvider.class, memcachedCacheProvider.getClass());
    }

    @Test
    public void testMonitor() throws Exception {
        final MemcachedCache cache = MemcachedCache.create(memcachedCacheConfig);
        final Emitter emitter = EasyMock.createNiceMock(Emitter.class);
        final Collection<Event> events = new ArrayList<>();
        final ServiceEmitter serviceEmitter = new ServiceEmitter("service", "host", emitter) {
            @Override
            public void emit(Event event) {
                events.add(event);
            }
        };
        while (events.isEmpty()) {
            Thread.sleep(memcachedCacheConfig.getTimeout());
            cache.doMonitor(serviceEmitter);
        } 
        Assert.assertFalse(events.isEmpty());
        ObjectMapper mapper = new DefaultObjectMapper();
        for (Event event : events) {
            MemcachedCacheTest.log.debug("Found event `%s`", mapper.writeValueAsString(event.toMap()));
        }
    }

    @Test
    public void testSanity() {
        Assert.assertNull(cache.get(new Cache.NamedKey("a", MemcachedCacheTest.HI)));
        put(cache, "a", MemcachedCacheTest.HI, 1);
        Assert.assertEquals(1, get(cache, "a", MemcachedCacheTest.HI));
        Assert.assertNull(cache.get(new Cache.NamedKey("the", MemcachedCacheTest.HI)));
        put(cache, "the", MemcachedCacheTest.HI, 2);
        Assert.assertEquals(1, get(cache, "a", MemcachedCacheTest.HI));
        Assert.assertEquals(2, get(cache, "the", MemcachedCacheTest.HI));
        put(cache, "the", MemcachedCacheTest.HO, 10);
        Assert.assertEquals(1, get(cache, "a", MemcachedCacheTest.HI));
        Assert.assertNull(cache.get(new Cache.NamedKey("a", MemcachedCacheTest.HO)));
        Assert.assertEquals(2, get(cache, "the", MemcachedCacheTest.HI));
        Assert.assertEquals(10, get(cache, "the", MemcachedCacheTest.HO));
        cache.close("the");
        Assert.assertEquals(1, get(cache, "a", MemcachedCacheTest.HI));
        Assert.assertNull(cache.get(new Cache.NamedKey("a", MemcachedCacheTest.HO)));
        cache.close("a");
    }

    @Test
    public void testGetBulk() {
        Assert.assertNull(cache.get(new Cache.NamedKey("the", MemcachedCacheTest.HI)));
        put(cache, "the", MemcachedCacheTest.HI, 2);
        put(cache, "the", MemcachedCacheTest.HO, 10);
        Cache.NamedKey key1 = new Cache.NamedKey("the", MemcachedCacheTest.HI);
        Cache.NamedKey key2 = new Cache.NamedKey("the", MemcachedCacheTest.HO);
        Map<Cache.NamedKey, byte[]> result = cache.getBulk(Lists.newArrayList(key1, key2));
        Assert.assertEquals(2, Ints.fromByteArray(result.get(key1)));
        Assert.assertEquals(10, Ints.fromByteArray(result.get(key2)));
    }
}

