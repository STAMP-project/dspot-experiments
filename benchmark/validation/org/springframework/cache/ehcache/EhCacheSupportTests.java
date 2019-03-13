/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cache.ehcache;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @since 27.09.2004
 */
public class EhCacheSupportTests {
    @Test
    public void testBlankCacheManager() {
        EhCacheManagerFactoryBean cacheManagerFb = new EhCacheManagerFactoryBean();
        cacheManagerFb.setCacheManagerName("myCacheManager");
        Assert.assertEquals(CacheManager.class, cacheManagerFb.getObjectType());
        Assert.assertTrue("Singleton property", cacheManagerFb.isSingleton());
        cacheManagerFb.afterPropertiesSet();
        try {
            CacheManager cm = cacheManagerFb.getObject();
            Assert.assertTrue("Loaded CacheManager with no caches", ((cm.getCacheNames().length) == 0));
            Cache myCache1 = cm.getCache("myCache1");
            Assert.assertTrue("No myCache1 defined", (myCache1 == null));
        } finally {
            cacheManagerFb.destroy();
        }
    }

    @Test
    public void testCacheManagerConflict() {
        EhCacheManagerFactoryBean cacheManagerFb = new EhCacheManagerFactoryBean();
        cacheManagerFb.setCacheManagerName("myCacheManager");
        Assert.assertEquals(CacheManager.class, cacheManagerFb.getObjectType());
        Assert.assertTrue("Singleton property", cacheManagerFb.isSingleton());
        cacheManagerFb.afterPropertiesSet();
        try {
            CacheManager cm = cacheManagerFb.getObject();
            Assert.assertTrue("Loaded CacheManager with no caches", ((cm.getCacheNames().length) == 0));
            Cache myCache1 = cm.getCache("myCache1");
            Assert.assertTrue("No myCache1 defined", (myCache1 == null));
            EhCacheManagerFactoryBean cacheManagerFb2 = new EhCacheManagerFactoryBean();
            cacheManagerFb2.setCacheManagerName("myCacheManager");
            cacheManagerFb2.afterPropertiesSet();
            Assert.fail("Should have thrown CacheException because of naming conflict");
        } catch (CacheException ex) {
            // expected
        } finally {
            cacheManagerFb.destroy();
        }
    }

    @Test
    public void testAcceptExistingCacheManager() {
        EhCacheManagerFactoryBean cacheManagerFb = new EhCacheManagerFactoryBean();
        cacheManagerFb.setCacheManagerName("myCacheManager");
        Assert.assertEquals(CacheManager.class, cacheManagerFb.getObjectType());
        Assert.assertTrue("Singleton property", cacheManagerFb.isSingleton());
        cacheManagerFb.afterPropertiesSet();
        try {
            CacheManager cm = cacheManagerFb.getObject();
            Assert.assertTrue("Loaded CacheManager with no caches", ((cm.getCacheNames().length) == 0));
            Cache myCache1 = cm.getCache("myCache1");
            Assert.assertTrue("No myCache1 defined", (myCache1 == null));
            EhCacheManagerFactoryBean cacheManagerFb2 = new EhCacheManagerFactoryBean();
            cacheManagerFb2.setCacheManagerName("myCacheManager");
            cacheManagerFb2.setAcceptExisting(true);
            cacheManagerFb2.afterPropertiesSet();
            CacheManager cm2 = cacheManagerFb2.getObject();
            Assert.assertSame(cm, cm2);
            cacheManagerFb2.destroy();
        } finally {
            cacheManagerFb.destroy();
        }
    }

    @Test
    public void testEhCacheFactoryBeanWithDefaultCacheManager() {
        doTestEhCacheFactoryBean(false);
    }

    @Test
    public void testEhCacheFactoryBeanWithExplicitCacheManager() {
        doTestEhCacheFactoryBean(true);
    }

    @Test
    public void testEhCacheFactoryBeanWithBlockingCache() {
        EhCacheManagerFactoryBean cacheManagerFb = new EhCacheManagerFactoryBean();
        cacheManagerFb.afterPropertiesSet();
        try {
            CacheManager cm = cacheManagerFb.getObject();
            EhCacheFactoryBean cacheFb = new EhCacheFactoryBean();
            cacheFb.setCacheManager(cm);
            cacheFb.setCacheName("myCache1");
            cacheFb.setBlocking(true);
            Assert.assertEquals(cacheFb.getObjectType(), BlockingCache.class);
            cacheFb.afterPropertiesSet();
            Ehcache myCache1 = cm.getEhcache("myCache1");
            Assert.assertTrue((myCache1 instanceof BlockingCache));
        } finally {
            cacheManagerFb.destroy();
        }
    }

    @Test
    public void testEhCacheFactoryBeanWithSelfPopulatingCache() {
        EhCacheManagerFactoryBean cacheManagerFb = new EhCacheManagerFactoryBean();
        cacheManagerFb.afterPropertiesSet();
        try {
            CacheManager cm = cacheManagerFb.getObject();
            EhCacheFactoryBean cacheFb = new EhCacheFactoryBean();
            cacheFb.setCacheManager(cm);
            cacheFb.setCacheName("myCache1");
            cacheFb.setCacheEntryFactory(( key) -> key);
            Assert.assertEquals(cacheFb.getObjectType(), SelfPopulatingCache.class);
            cacheFb.afterPropertiesSet();
            Ehcache myCache1 = cm.getEhcache("myCache1");
            Assert.assertTrue((myCache1 instanceof SelfPopulatingCache));
            Assert.assertEquals("myKey1", myCache1.get("myKey1").getObjectValue());
        } finally {
            cacheManagerFb.destroy();
        }
    }

    @Test
    public void testEhCacheFactoryBeanWithUpdatingSelfPopulatingCache() {
        EhCacheManagerFactoryBean cacheManagerFb = new EhCacheManagerFactoryBean();
        cacheManagerFb.afterPropertiesSet();
        try {
            CacheManager cm = cacheManagerFb.getObject();
            EhCacheFactoryBean cacheFb = new EhCacheFactoryBean();
            cacheFb.setCacheManager(cm);
            cacheFb.setCacheName("myCache1");
            cacheFb.setCacheEntryFactory(new UpdatingCacheEntryFactory() {
                @Override
                public Object createEntry(Object key) {
                    return key;
                }

                @Override
                public void updateEntryValue(Object key, Object value) {
                }
            });
            Assert.assertEquals(cacheFb.getObjectType(), UpdatingSelfPopulatingCache.class);
            cacheFb.afterPropertiesSet();
            Ehcache myCache1 = cm.getEhcache("myCache1");
            Assert.assertTrue((myCache1 instanceof UpdatingSelfPopulatingCache));
            Assert.assertEquals("myKey1", myCache1.get("myKey1").getObjectValue());
        } finally {
            cacheManagerFb.destroy();
        }
    }
}

