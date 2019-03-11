/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.xml;


import com.pany.ehcache.copier.Description;
import com.pany.ehcache.copier.Employee;
import com.pany.ehcache.copier.Person;
import com.pany.ehcache.integration.TestCacheEventListener;
import com.pany.ehcache.integration.TestCacheLoaderWriter;
import com.pany.ehcache.integration.TestSecondCacheEventListener;
import com.pany.ehcache.integration.ThreadRememberingLoaderWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.event.EventType;
import org.ehcache.impl.config.event.DefaultCacheEventDispatcherConfiguration;
import org.ehcache.impl.config.loaderwriter.DefaultCacheLoaderWriterConfiguration;
import org.ehcache.spi.service.ServiceConfiguration;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;


/**
 *
 *
 * @author Alex Snaps
 */
public class IntegrationConfigurationTest {
    @Test
    public void testSerializers() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/default-serializer.xml"));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        Cache<Long, Double> bar = cacheManager.getCache("bar", Long.class, Double.class);
        bar.put(1L, 1.0);
        Assert.assertThat(bar.get(1L), CoreMatchers.equalTo(1.0));
        Cache<String, String> baz = cacheManager.getCache("baz", String.class, String.class);
        baz.put("1", "one");
        Assert.assertThat(baz.get("1"), CoreMatchers.equalTo("one"));
        Cache<String, Object> bam = cacheManager.createCache("bam", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Object.class, ResourcePoolsBuilder.heap(10)).build());
        bam.put("1", "one");
        Assert.assertThat(bam.get("1"), CoreMatchers.equalTo(((Object) ("one"))));
        cacheManager.close();
    }

    @Test
    public void testCopiers() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/cache-copiers.xml"));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        Cache<Description, Person> bar = cacheManager.getCache("bar", Description.class, Person.class);
        Description desc = new Description(1234, "foo");
        Person person = new Person("Bar", 24);
        bar.put(desc, person);
        Assert.assertEquals(person, bar.get(desc));
        Assert.assertNotSame(person, bar.get(desc));
        Cache<Long, Person> baz = cacheManager.getCache("baz", Long.class, Person.class);
        baz.put(1L, person);
        Assert.assertEquals(person, baz.get(1L));
        Assert.assertNotSame(person, baz.get(1L));
        Employee empl = new Employee(1234, "foo", 23);
        Cache<Long, Employee> bak = cacheManager.getCache("bak", Long.class, Employee.class);
        bak.put(1L, empl);
        Assert.assertSame(empl, bak.get(1L));
        cacheManager.close();
    }

    @Test
    public void testLoaderWriter() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SAXException {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/cache-integration.xml"));
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("bar"), CoreMatchers.is(true));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        final Cache<Number, String> cache = cacheManager.getCache("bar", Number.class, String.class);
        Assert.assertThat(cache, CoreMatchers.notNullValue());
        Assert.assertThat(cache.get(1), CoreMatchers.notNullValue());
        final Number key = new Long(42);
        cache.put(key, "Bye y'all!");
        Assert.assertThat(TestCacheLoaderWriter.lastWrittenKey, CoreMatchers.is(key));
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("template1"), CoreMatchers.is(true));
        final Cache<Number, String> templateCache = cacheManager.getCache("template1", Number.class, String.class);
        Assert.assertThat(templateCache, CoreMatchers.notNullValue());
        Assert.assertThat(templateCache.get(1), CoreMatchers.notNullValue());
        final Number key1 = new Long(100);
        templateCache.put(key1, "Bye y'all!");
        Assert.assertThat(TestCacheLoaderWriter.lastWrittenKey, CoreMatchers.is(key1));
    }

    @Test
    public void testWriteBehind() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InterruptedException, SAXException {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/writebehind-cache.xml"));
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("bar"), CoreMatchers.is(true));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        final Cache<Number, String> cache = cacheManager.getCache("bar", Number.class, String.class);
        Assert.assertThat(cache, CoreMatchers.notNullValue());
        Assert.assertThat(cache.get(1), CoreMatchers.notNullValue());
        final Number key = 42L;
        TestCacheLoaderWriter.latch = new CountDownLatch(1);
        cache.put(key, "Bye y'all!");
        TestCacheLoaderWriter.latch.await(2, TimeUnit.SECONDS);
        Assert.assertThat(TestCacheLoaderWriter.lastWrittenKey, CoreMatchers.is(key));
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("template1"), CoreMatchers.is(true));
        final Cache<Number, String> templateCache = cacheManager.getCache("template1", Number.class, String.class);
        Assert.assertThat(templateCache, CoreMatchers.notNullValue());
        Assert.assertThat(templateCache.get(1), CoreMatchers.notNullValue());
        final Number key1 = 100L;
        TestCacheLoaderWriter.latch = new CountDownLatch(2);
        templateCache.put(42L, "Howdy!");
        templateCache.put(key1, "Bye y'all!");
        TestCacheLoaderWriter.latch.await(2, TimeUnit.SECONDS);
        Assert.assertThat(TestCacheLoaderWriter.lastWrittenKey, CoreMatchers.is(key1));
    }

    @Test
    public void testCacheEventListener() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/ehcache-cacheEventListener.xml"));
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("bar"), CoreMatchers.is(true));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        final Cache<Number, String> cache = cacheManager.getCache("bar", Number.class, String.class);
        cache.put(10, "dog");
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.CREATED));
        cache.put(10, "cat");
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.UPDATED));
        cache.remove(10);
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.REMOVED));
        cache.put(10, "dog");
        IntegrationConfigurationTest.resetValues();
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("template1"), CoreMatchers.is(true));
        final Cache<Number, String> templateCache = cacheManager.getCache("template1", Number.class, String.class);
        templateCache.put(10, "cat");
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT, CoreMatchers.nullValue());
        templateCache.put(10, "dog");
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.UPDATED));
    }

    @Test
    public void testCacheEventListenerThreadPoolName() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/ehcache-cacheEventListener.xml"));
        CacheConfiguration<?, ?> template1 = configuration.getCacheConfigurations().get("template1");
        DefaultCacheEventDispatcherConfiguration eventDispatcherConfig = null;
        for (ServiceConfiguration<?> serviceConfiguration : template1.getServiceConfigurations()) {
            if (serviceConfiguration instanceof DefaultCacheEventDispatcherConfiguration) {
                eventDispatcherConfig = ((DefaultCacheEventDispatcherConfiguration) (serviceConfiguration));
            }
        }
        Assert.assertThat(eventDispatcherConfig.getThreadPoolAlias(), CoreMatchers.is("listeners-pool"));
    }

    @Test
    public void testCacheEventListenerWithMultipleListener() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/ehcache-multipleCacheEventListener.xml"));
        Assert.assertThat(configuration.getCacheConfigurations().containsKey("bar"), CoreMatchers.is(true));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        final Cache<Number, String> cache = cacheManager.getCache("bar", Number.class, String.class);
        IntegrationConfigurationTest.resetValues();
        cache.put(10, "dog");
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.CREATED));
        Assert.assertThat(TestSecondCacheEventListener.SECOND_LISTENER_FIRED_EVENT, CoreMatchers.is(CoreMatchers.nullValue()));
        IntegrationConfigurationTest.resetValues();
        cache.put(10, "cat");
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.UPDATED));
        Assert.assertThat(TestSecondCacheEventListener.SECOND_LISTENER_FIRED_EVENT.getType(), CoreMatchers.is(EventType.UPDATED));
        IntegrationConfigurationTest.resetValues();
        cache.remove(10);
        Assert.assertThat(TestCacheEventListener.FIRED_EVENT.getType(), CoreMatchers.is(EventType.REMOVED));
        Assert.assertThat(TestSecondCacheEventListener.SECOND_LISTENER_FIRED_EVENT.getType(), CoreMatchers.is(EventType.REMOVED));
    }

    @Test
    public void testThreadPools() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/thread-pools.xml"));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        try {
            Cache<String, String> cache = cacheManager.createCache("testThreadPools", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCacheLoaderWriterConfiguration(ThreadRememberingLoaderWriter.class)).add(WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration().useThreadPool("small")).build());
            cache.put("foo", "bar");
            ThreadRememberingLoaderWriter.USED.acquireUninterruptibly();
            Assert.assertThat(ThreadRememberingLoaderWriter.LAST_SEEN_THREAD.getName(), StringContains.containsString("[small]"));
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void testThreadPoolsUsingDefaultPool() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/thread-pools.xml"));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        try {
            Cache<String, String> cache = cacheManager.createCache("testThreadPools", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)).add(new DefaultCacheLoaderWriterConfiguration(ThreadRememberingLoaderWriter.class)).add(WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration()).build());
            cache.put("foo", "bar");
            ThreadRememberingLoaderWriter.USED.acquireUninterruptibly();
            Assert.assertThat(ThreadRememberingLoaderWriter.LAST_SEEN_THREAD.getName(), StringContains.containsString("[big]"));
        } finally {
            cacheManager.close();
        }
    }

    @Test
    public void testCacheWithSizeOfEngine() throws Exception {
        Configuration configuration = new XmlConfiguration(this.getClass().getResource("/configs/sizeof-engine.xml"));
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManager(configuration);
        cacheManager.init();
        try {
            Cache<String, String> usesDefaultSizeOfEngine = cacheManager.getCache("usesDefaultSizeOfEngine", String.class, String.class);
            usesDefaultSizeOfEngine.put("foo", "bar");
            Cache<String, String> usesConfiguredInCache = cacheManager.getCache("usesConfiguredInCache", String.class, String.class);
            usesConfiguredInCache.put("foo", "bar");
            Assert.assertThat(usesDefaultSizeOfEngine.get("foo"), CoreMatchers.is("bar"));
            Assert.assertThat(usesConfiguredInCache.get("foo"), CoreMatchers.is("bar"));
            ResourcePools pools = ResourcePoolsBuilder.newResourcePoolsBuilder().heap(20L, EntryUnit.ENTRIES).build();
            try {
                usesDefaultSizeOfEngine.getRuntimeConfiguration().updateResourcePools(pools);
                Assert.fail();
            } catch (Exception ex) {
                Assert.assertThat(ex, CoreMatchers.instanceOf(IllegalArgumentException.class));
                Assert.assertThat(ex.getMessage(), CoreMatchers.equalTo("ResourcePool for heap with ResourceUnit 'entries' can not replace 'kB'"));
            }
            try {
                usesConfiguredInCache.getRuntimeConfiguration().updateResourcePools(pools);
                Assert.fail();
            } catch (Exception ex) {
                Assert.assertThat(ex, CoreMatchers.instanceOf(IllegalArgumentException.class));
                Assert.assertThat(ex.getMessage(), CoreMatchers.equalTo("ResourcePool for heap with ResourceUnit 'entries' can not replace 'kB'"));
            }
        } finally {
            cacheManager.close();
        }
    }
}

