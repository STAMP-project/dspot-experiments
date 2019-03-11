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
package org.ehcache.docs;


import java.io.File;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.docs.plugs.ListenerObject;
import org.ehcache.docs.plugs.OddKeysEvictionAdvisor;
import org.ehcache.docs.plugs.SampleLoaderWriter;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.copy.ReadWriteCopier;
import org.ehcache.impl.serialization.JavaSerializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Samples to get started with Ehcache 3
 *
 * If you add new examples, you should use tags to have them included in the README.adoc
 * You need to edit the README.adoc too to add  your new content.
 * The callouts are also used in docs/user/index.adoc
 */
@SuppressWarnings("unused")
public class GettingStarted {
    @Rule
    public final TemporaryFolder diskPath = new TemporaryFolder();

    @Test
    public void cachemanagerExample() {
        // tag::cachemanagerExample[]
        CacheManager cacheManager = // <2>
        // <1>
        CacheManagerBuilder.newCacheManagerBuilder().withCache("preConfigured", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))).build();// <3>

        cacheManager.init();// <4>

        Cache<Long, String> preConfigured = cacheManager.getCache("preConfigured", Long.class, String.class);// <5>

        Cache<Long, String> myCache = // <6>
        cacheManager.createCache("myCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)));
        myCache.put(1L, "da one!");// <7>

        String value = myCache.get(1L);// <8>

        cacheManager.removeCache("preConfigured");// <9>

        cacheManager.close();// <10>

        // end::cachemanagerExample[]
    }

    @Test
    public void threeTiersCacheManager() throws Exception {
        // tag::threeTiersCacheManager[]
        PersistentCacheManager persistentCacheManager = // <1>
        CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(new File(getStoragePath(), "myData"))).withCache("threeTieredCache", // <4>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, // <3>
        // <2>
        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(1, MemoryUnit.MB).disk(20, MemoryUnit.MB, true))).build(true);
        Cache<Long, String> threeTieredCache = persistentCacheManager.getCache("threeTieredCache", Long.class, String.class);
        threeTieredCache.put(1L, "stillAvailableAfterRestart");// <5>

        persistentCacheManager.close();
        // end::threeTiersCacheManager[]
    }

    @Test
    public void testCacheEventListener() {
        // tag::cacheEventListener[]
        CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = // <1>
        CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(new ListenerObject(), EventType.CREATED, EventType.UPDATED).unordered().asynchronous();// <2>

        final CacheManager manager = // <3>
        CacheManagerBuilder.newCacheManagerBuilder().withCache("foo", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)).add(cacheEventListenerConfiguration)).build(true);
        final Cache<String, String> cache = manager.getCache("foo", String.class, String.class);
        cache.put("Hello", "World");// <4>

        cache.put("Hello", "Everyone");// <5>

        cache.remove("Hello");// <6>

        // end::cacheEventListener[]
        manager.close();
    }

    @Test
    public void writeThroughCache() {
        // tag::writeThroughCache[]
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        Cache<Long, String> writeThroughCache = cacheManager.createCache("writeThroughCache", // <1>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).withLoaderWriter(new SampleLoaderWriter<>(Collections.singletonMap(41L, "zero"))).build());
        Assert.assertThat(writeThroughCache.get(41L), Matchers.is("zero"));// <2>

        writeThroughCache.put(42L, "one");// <3>

        Assert.assertThat(writeThroughCache.get(42L), Matchers.equalTo("one"));
        cacheManager.close();
        // end::writeThroughCache[]
    }

    @Test
    public void writeBehindCache() {
        // tag::writeBehindCache[]
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        Cache<Long, String> writeBehindCache = cacheManager.createCache("writeBehindCache", // <6>
        // <1>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).withLoaderWriter(new SampleLoaderWriter<>(Collections.singletonMap(41L, "zero"))).add(// <5>
        // <4>
        // <3>
        // <2>
        WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 3).queueSize(3).concurrencyLevel(1).enableCoalescing()).build());
        Assert.assertThat(writeBehindCache.get(41L), Matchers.is("zero"));
        writeBehindCache.put(42L, "one");
        writeBehindCache.put(43L, "two");
        writeBehindCache.put(42L, "This goes for the record");
        Assert.assertThat(writeBehindCache.get(42L), Matchers.equalTo("This goes for the record"));
        cacheManager.close();
        // end::writeBehindCache[]
    }

    @Test
    public void registerListenerAtRuntime() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("cache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10L))).build(true);
        Cache<Long, String> cache = cacheManager.getCache("cache", Long.class, String.class);
        // tag::registerListenerAtRuntime[]
        ListenerObject listener = new ListenerObject();// <1>

        cache.getRuntimeConfiguration().registerCacheEventListener(listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.REMOVED));// <2>

        cache.put(1L, "one");
        cache.put(2L, "two");
        cache.remove(1L);
        cache.remove(2L);
        cache.getRuntimeConfiguration().deregisterCacheEventListener(listener);// <3>

        cache.put(1L, "one again");
        cache.remove(1L);
        // end::registerListenerAtRuntime[]
        cacheManager.close();
    }

    @Test
    public void configuringEventProcessing() {
        CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(ListenerObject.class, EventType.EVICTED).ordered().synchronous();
        // tag::configuringEventProcessingQueues[]
        CacheConfiguration<Long, String> cacheConfiguration = // <1>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(5L)).withDispatcherConcurrency(10).withEventListenersThreadPool("listeners-pool").build();
        // end::configuringEventProcessingQueues[]
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("cache", cacheConfiguration).build(true);
        cacheManager.close();
    }

    @Test
    public void cacheEvictionAdvisor() throws Exception {
        // tag::cacheEvictionAdvisor[]
        CacheConfiguration<Long, String> cacheConfiguration = // <2>
        // <1>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(2L)).withEvictionAdvisor(new OddKeysEvictionAdvisor<>()).build();
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("cache", cacheConfiguration).build(true);
        Cache<Long, String> cache = cacheManager.getCache("cache", Long.class, String.class);
        // Work with the cache
        cache.put(42L, "The Answer!");
        cache.put(41L, "The wrong Answer!");
        cache.put(39L, "The other wrong Answer!");
        cacheManager.close();
        // end::cacheEvictionAdvisor[]
    }

    @Test
    public void expiry() throws Exception {
        // tag::expiry[]
        CacheConfiguration<Long, String> cacheConfiguration = // <2>
        // <1>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(100)).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20))).build();
        // end::expiry[]
    }

    @Test
    public void customExpiry() throws Exception {
        // tag::customExpiry[]
        CacheConfiguration<Long, String> cacheConfiguration = // <1>
        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(100)).withExpiry(new GettingStarted.CustomExpiry()).build();
        // end::customExpiry[]
    }

    private static class Description {
        int id;

        String alias;

        Description(GettingStarted.Description other) {
            this.id = other.id;
            this.alias = other.alias;
        }

        Description(int id, String alias) {
            this.id = id;
            this.alias = alias;
        }

        @Override
        public boolean equals(final Object other) {
            if ((this) == other)
                return true;

            if ((other == null) || ((this.getClass()) != (other.getClass())))
                return false;

            GettingStarted.Description that = ((GettingStarted.Description) (other));
            if ((id) != (that.id))
                return false;

            if ((alias) == null ? (alias) != null : !(alias.equals(that.alias)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = (31 * result) + (id);
            result = (31 * result) + ((alias) == null ? 0 : alias.hashCode());
            return result;
        }
    }

    private static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;

        int age;

        Person(GettingStarted.Person other) {
            this.name = other.name;
            this.age = other.age;
        }

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(final Object other) {
            if ((this) == other)
                return true;

            if ((other == null) || ((this.getClass()) != (other.getClass())))
                return false;

            GettingStarted.Person that = ((GettingStarted.Person) (other));
            if ((age) != (that.age))
                return false;

            if ((name) == null ? (that.name) != null : !(name.equals(that.name)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = (31 * result) + (age);
            result = (31 * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }
    }

    public static class DescriptionCopier extends ReadWriteCopier<GettingStarted.Description> {
        @Override
        public GettingStarted.Description copy(final GettingStarted.Description obj) {
            return new GettingStarted.Description(obj);
        }
    }

    public static class PersonCopier extends ReadWriteCopier<GettingStarted.Person> {
        @Override
        public GettingStarted.Person copy(final GettingStarted.Person obj) {
            return new GettingStarted.Person(obj);
        }
    }

    static class PersonSerializer extends JavaSerializer<GettingStarted.Person> {
        public PersonSerializer() {
            super(ClassLoader.getSystemClassLoader());
        }
    }

    public static class CustomExpiry implements ExpiryPolicy<Long, String> {
        @Override
        public Duration getExpiryForCreation(Long key, String value) {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public Duration getExpiryForAccess(Long key, Supplier<? extends String> value) {
            throw new UnsupportedOperationException("TODO Implement me!");
        }

        @Override
        public Duration getExpiryForUpdate(Long key, Supplier<? extends String> oldValue, String newValue) {
            throw new UnsupportedOperationException("TODO Implement me!");
        }
    }
}

