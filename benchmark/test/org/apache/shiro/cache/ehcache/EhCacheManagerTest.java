/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.cache.ehcache;


import net.sf.ehcache.CacheManager;
import org.apache.shiro.cache.Cache;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO - Class JavaDoc
 *
 * @since May 11, 2010 12:41:38 PM
 */
public class EhCacheManagerTest {
    private EhCacheManager cacheManager;

    @Test
    public void testCacheManagerCreationDuringInit() {
        CacheManager ehCacheManager = cacheManager.getCacheManager();
        Assert.assertNull(ehCacheManager);
        cacheManager.init();
        // now assert that an internal CacheManager has been created:
        ehCacheManager = cacheManager.getCacheManager();
        Assert.assertNotNull(ehCacheManager);
    }

    @Test
    public void testLazyCacheManagerCreationWithoutCallingInit() {
        CacheManager ehCacheManager = cacheManager.getCacheManager();
        Assert.assertNull(ehCacheManager);
        // don't call init here - the ehcache CacheManager should be lazily created
        // because of the default Shiro ehcache.xml file in the classpath.  Just acquire a cache:
        Cache<String, String> cache = cacheManager.getCache("test");
        // now assert that an internal CacheManager has been created:
        ehCacheManager = cacheManager.getCacheManager();
        Assert.assertNotNull(ehCacheManager);
        Assert.assertNotNull(cache);
        cache.put("hello", "world");
        String value = cache.get("hello");
        Assert.assertNotNull(value);
        Assert.assertEquals(value, "world");
    }
}

