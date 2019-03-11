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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.util.Map;
import java.util.UUID;
import org.apache.druid.guice.GuiceInjectors;
import org.apache.druid.guice.JsonConfigProvider;
import org.apache.druid.initialization.Initialization;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.lifecycle.Lifecycle;
import org.junit.Assert;
import org.junit.Test;


public class RedisCacheTest {
    private static final byte[] HI = StringUtils.toUtf8("hiiiiiiiiiiiiiiiiiii");

    private static final byte[] HO = StringUtils.toUtf8("hooooooooooooooooooo");

    private RedisCache cache;

    private final RedisCacheConfig cacheConfig = new RedisCacheConfig() {
        @Override
        public int getTimeout() {
            return 10;
        }

        @Override
        public long getExpiration() {
            return 3600000;
        }
    };

    @Test
    public void testBasicInjection() throws Exception {
        final RedisCacheConfig config = new RedisCacheConfig();
        Injector injector = Initialization.makeInjectorWithModules(GuiceInjectors.makeStartupInjector(), ImmutableList.of(( binder) -> {
            binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test/redis");
            binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
            binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
            binder.bind(.class).toInstance(config);
            binder.bind(.class).toProvider(.class).in(.class);
        }));
        Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
        lifecycle.start();
        try {
            Cache cache = injector.getInstance(Cache.class);
            Assert.assertEquals(RedisCache.class, cache.getClass());
        } finally {
            lifecycle.stop();
        }
    }

    @Test
    public void testSimpleInjection() {
        final String uuid = UUID.randomUUID().toString();
        System.setProperty((uuid + ".type"), "redis");
        final Injector injector = Initialization.makeInjectorWithModules(GuiceInjectors.makeStartupInjector(), ImmutableList.of(( binder) -> {
            binder.bindConstant().annotatedWith(Names.named("serviceName")).to("druid/test/redis");
            binder.bindConstant().annotatedWith(Names.named("servicePort")).to(0);
            binder.bindConstant().annotatedWith(Names.named("tlsServicePort")).to((-1));
            binder.bind(.class).toProvider(.class);
            JsonConfigProvider.bind(binder, uuid, .class);
        }));
        final CacheProvider cacheProvider = injector.getInstance(CacheProvider.class);
        Assert.assertNotNull(cacheProvider);
        Assert.assertEquals(RedisCacheProvider.class, cacheProvider.getClass());
    }

    @Test
    public void testSanity() {
        Assert.assertNull(cache.get(new Cache.NamedKey("a", RedisCacheTest.HI)));
        put(cache, "a", RedisCacheTest.HI, 0);
        Assert.assertEquals(0, get(cache, "a", RedisCacheTest.HI));
        Assert.assertNull(cache.get(new Cache.NamedKey("the", RedisCacheTest.HI)));
        put(cache, "the", RedisCacheTest.HI, 1);
        Assert.assertEquals(0, get(cache, "a", RedisCacheTest.HI));
        Assert.assertEquals(1, get(cache, "the", RedisCacheTest.HI));
        put(cache, "the", RedisCacheTest.HO, 10);
        Assert.assertEquals(0, get(cache, "a", RedisCacheTest.HI));
        Assert.assertNull(cache.get(new Cache.NamedKey("a", RedisCacheTest.HO)));
        Assert.assertEquals(1, get(cache, "the", RedisCacheTest.HI));
        Assert.assertEquals(10, get(cache, "the", RedisCacheTest.HO));
        cache.close("the");
        Assert.assertEquals(0, get(cache, "a", RedisCacheTest.HI));
        Assert.assertNull(cache.get(new Cache.NamedKey("a", RedisCacheTest.HO)));
    }

    @Test
    public void testGetBulk() {
        Assert.assertNull(cache.get(new Cache.NamedKey("the", RedisCacheTest.HI)));
        put(cache, "the", RedisCacheTest.HI, 1);
        put(cache, "the", RedisCacheTest.HO, 10);
        Cache.NamedKey key1 = new Cache.NamedKey("the", RedisCacheTest.HI);
        Cache.NamedKey key2 = new Cache.NamedKey("the", RedisCacheTest.HO);
        Cache.NamedKey key3 = new Cache.NamedKey("a", RedisCacheTest.HI);
        Map<Cache.NamedKey, byte[]> result = cache.getBulk(Lists.newArrayList(key1, key2, key3));
        Assert.assertEquals(1, Ints.fromByteArray(result.get(key1)));
        Assert.assertEquals(10, Ints.fromByteArray(result.get(key2)));
        Assert.assertEquals(null, result.get(key3));
    }
}

