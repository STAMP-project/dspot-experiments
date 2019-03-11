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
package org.ehcache.impl.internal.store.offheap;


import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.core.spi.store.AbstractValueHolder;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.events.StoreEvent;
import org.ehcache.core.spi.store.events.StoreEventListener;
import org.ehcache.core.spi.store.tiering.CachingTier;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.core.statistics.LowerCachingTierOperationsOutcome;
import org.ehcache.core.statistics.StoreOperationOutcomes;
import org.ehcache.event.EventType;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.internal.util.StatisticsTestUtils;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.core.statistics.LowerCachingTierOperationsOutcome.GetAndRemoveOutcome.HIT_REMOVED;
import static org.ehcache.core.statistics.LowerCachingTierOperationsOutcome.GetAndRemoveOutcome.MISS;
import static org.ehcache.core.statistics.LowerCachingTierOperationsOutcome.InstallMappingOutcome.PUT;
import static org.ehcache.core.statistics.LowerCachingTierOperationsOutcome.InvalidateOutcome.REMOVED;
import static org.ehcache.core.statistics.StoreOperationOutcomes.ExpirationOutcome.SUCCESS;
import static org.ehcache.impl.internal.util.Matchers.valueHeld;


/**
 *
 *
 * @author cdennis
 */
public abstract class AbstractOffHeapStoreTest {
    private AbstractOffHeapStoreTest.TestTimeSource timeSource = new AbstractOffHeapStoreTest.TestTimeSource();

    private AbstractOffHeapStore<String, String> offHeapStore;

    @Test
    public void testGetAndRemoveNoValue() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        Assert.assertThat(offHeapStore.getAndRemove("1"), CoreMatchers.is(CoreMatchers.nullValue()));
        StatisticsTestUtils.validateStats(offHeapStore, EnumSet.of(MISS));
    }

    @Test
    public void testGetAndRemoveValue() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        offHeapStore.put("1", "one");
        Assert.assertThat(offHeapStore.getAndRemove("1").get(), CoreMatchers.equalTo("one"));
        StatisticsTestUtils.validateStats(offHeapStore, EnumSet.of(HIT_REMOVED));
        Assert.assertThat(offHeapStore.get("1"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testGetAndRemoveExpiredElementReturnsNull() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L)));
        Assert.assertThat(offHeapStore.getAndRemove("1"), CoreMatchers.is(CoreMatchers.nullValue()));
        offHeapStore.put("1", "one");
        final AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        offHeapStore.setInvalidationListener(( key, valueHolder) -> {
            valueHolder.get();
            invalidated.set(valueHolder);
        });
        timeSource.advanceTime(20);
        Assert.assertThat(offHeapStore.getAndRemove("1"), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(invalidated.get().get(), CoreMatchers.equalTo("one"));
        Assert.assertThat(invalidated.get().isExpired(timeSource.getTimeMillis()), CoreMatchers.is(true));
        Assert.assertThat(getExpirationStatistic(offHeapStore).count(SUCCESS), CoreMatchers.is(1L));
    }

    @Test
    public void testInstallMapping() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L)));
        Assert.assertThat(offHeapStore.installMapping("1", ( key) -> new AbstractOffHeapStoreTest.SimpleValueHolder<>("one", timeSource.getTimeMillis(), 15)).get(), CoreMatchers.equalTo("one"));
        StatisticsTestUtils.validateStats(offHeapStore, EnumSet.of(PUT));
        timeSource.advanceTime(20);
        try {
            offHeapStore.installMapping("1", ( key) -> new AbstractOffHeapStoreTest.SimpleValueHolder<>("un", timeSource.getTimeMillis(), 15));
            Assert.fail("expected AssertionError");
        } catch (AssertionError ae) {
            // expected
        }
    }

    @Test
    public void testInvalidateKeyAbsent() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L)));
        final AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        offHeapStore.setInvalidationListener(( key, valueHolder) -> invalidated.set(valueHolder));
        offHeapStore.invalidate("1");
        Assert.assertThat(invalidated.get(), CoreMatchers.is(CoreMatchers.nullValue()));
        StatisticsTestUtils.validateStats(offHeapStore, EnumSet.of(LowerCachingTierOperationsOutcome.InvalidateOutcome.MISS));
    }

    @Test
    public void testInvalidateKeyPresent() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L)));
        offHeapStore.put("1", "one");
        final AtomicReference<Store.ValueHolder<String>> invalidated = new AtomicReference<>();
        offHeapStore.setInvalidationListener(( key, valueHolder) -> {
            valueHolder.get();
            invalidated.set(valueHolder);
        });
        offHeapStore.invalidate("1");
        Assert.assertThat(invalidated.get().get(), CoreMatchers.equalTo("one"));
        StatisticsTestUtils.validateStats(offHeapStore, EnumSet.of(REMOVED));
        Assert.assertThat(offHeapStore.get("1"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testClear() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L)));
        offHeapStore.put("1", "one");
        offHeapStore.put("2", "two");
        offHeapStore.put("3", "three");
        offHeapStore.clear();
        Assert.assertThat(offHeapStore.get("1"), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(offHeapStore.get("2"), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(offHeapStore.get("3"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testWriteBackOfValueHolder() throws StoreAccessException {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L)));
        offHeapStore.put("key1", "value1");
        timeSource.advanceTime(10);
        OffHeapValueHolder<String> valueHolder = ((OffHeapValueHolder<String>) (offHeapStore.get("key1")));
        Assert.assertThat(valueHolder.lastAccessTime(), CoreMatchers.is(10L));
        timeSource.advanceTime(10);
        Assert.assertThat(offHeapStore.get("key1"), CoreMatchers.notNullValue());
        timeSource.advanceTime(16);
        Assert.assertThat(offHeapStore.get("key1"), CoreMatchers.nullValue());
    }

    @Test
    public void testEvictionAdvisor() throws StoreAccessException {
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L));
        EvictionAdvisor<String, byte[]> evictionAdvisor = ( key, value) -> true;
        performEvictionTest(timeSource, expiry, evictionAdvisor);
    }

    @Test
    public void testBrokenEvictionAdvisor() throws StoreAccessException {
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L));
        EvictionAdvisor<String, byte[]> evictionAdvisor = ( key, value) -> {
            throw new UnsupportedOperationException("Broken advisor!");
        };
        performEvictionTest(timeSource, expiry, evictionAdvisor);
    }

    @Test
    public void testFlushUpdatesAccessStats() throws StoreAccessException {
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(15L));
        offHeapStore = createAndInitStore(timeSource, expiry);
        try {
            final String key = "foo";
            final String value = "bar";
            offHeapStore.put(key, value);
            final Store.ValueHolder<String> firstValueHolder = offHeapStore.getAndFault(key);
            offHeapStore.put(key, value);
            final Store.ValueHolder<String> secondValueHolder = offHeapStore.getAndFault(key);
            timeSource.advanceTime(10);
            ((AbstractValueHolder) (firstValueHolder)).accessed(timeSource.getTimeMillis(), expiry.getExpiryForAccess(key, () -> value));
            timeSource.advanceTime(10);
            ((AbstractValueHolder) (secondValueHolder)).accessed(timeSource.getTimeMillis(), expiry.getExpiryForAccess(key, () -> value));
            Assert.assertThat(offHeapStore.flush(key, new AbstractOffHeapStoreTest.DelegatingValueHolder<>(firstValueHolder)), CoreMatchers.is(false));
            Assert.assertThat(offHeapStore.flush(key, new AbstractOffHeapStoreTest.DelegatingValueHolder<>(secondValueHolder)), CoreMatchers.is(true));
            timeSource.advanceTime(10);// this should NOT affect

            Assert.assertThat(offHeapStore.getAndFault(key).lastAccessTime(), CoreMatchers.is(((secondValueHolder.creationTime()) + 20)));
        } finally {
            destroyStore(offHeapStore);
        }
    }

    @Test
    public void testExpiryEventFiredOnExpiredCachedEntry() throws StoreAccessException {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(10L)));
        final List<String> expiredKeys = new ArrayList<>();
        offHeapStore.getStoreEventSource().addEventListener(( event) -> {
            if ((event.getType()) == (EventType.EXPIRED)) {
                expiredKeys.add(event.getKey());
            }
        });
        offHeapStore.put("key1", "value1");
        offHeapStore.put("key2", "value2");
        offHeapStore.get("key1");// Bring the entry to the caching tier

        timeSource.advanceTime(11);// Expire the elements

        offHeapStore.get("key1");
        offHeapStore.get("key2");
        Assert.assertThat(expiredKeys, Matchers.containsInAnyOrder("key1", "key2"));
        Assert.assertThat(getExpirationStatistic(offHeapStore).count(SUCCESS), CoreMatchers.is(2L));
    }

    @Test
    public void testGetWithExpiryOnAccess() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).build());
        offHeapStore.put("key", "value");
        final AtomicReference<String> expired = new AtomicReference<>();
        offHeapStore.getStoreEventSource().addEventListener(( event) -> {
            if ((event.getType()) == (EventType.EXPIRED)) {
                expired.set(event.getKey());
            }
        });
        Assert.assertThat(offHeapStore.get("key"), valueHeld("value"));
        Assert.assertThat(expired.get(), CoreMatchers.is("key"));
    }

    @Test
    public void testExpiryCreateException() throws Exception {
        offHeapStore = createAndInitStore(timeSource, new ExpiryPolicy<String, String>() {
            @Override
            public Duration getExpiryForCreation(String key, String value) {
                throw new RuntimeException();
            }

            @Override
            public Duration getExpiryForAccess(String key, Supplier<? extends String> value) {
                throw new AssertionError();
            }

            @Override
            public Duration getExpiryForUpdate(String key, Supplier<? extends String> oldValue, String newValue) {
                throw new AssertionError();
            }
        });
        offHeapStore.put("key", "value");
        Assert.assertNull(offHeapStore.get("key"));
    }

    @Test
    public void testExpiryAccessException() throws Exception {
        offHeapStore = createAndInitStore(timeSource, new ExpiryPolicy<String, String>() {
            @Override
            public Duration getExpiryForCreation(String key, String value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForAccess(String key, Supplier<? extends String> value) {
                throw new RuntimeException();
            }

            @Override
            public Duration getExpiryForUpdate(String key, Supplier<? extends String> oldValue, String newValue) {
                return null;
            }
        });
        offHeapStore.put("key", "value");
        Assert.assertThat(offHeapStore.get("key"), valueHeld("value"));
        Assert.assertNull(offHeapStore.get("key"));
    }

    @Test
    public void testExpiryUpdateException() throws Exception {
        offHeapStore = createAndInitStore(timeSource, new ExpiryPolicy<String, String>() {
            @Override
            public Duration getExpiryForCreation(String key, String value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForAccess(String key, Supplier<? extends String> value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForUpdate(String key, Supplier<? extends String> oldValue, String newValue) {
                if ((timeSource.getTimeMillis()) > 0) {
                    throw new RuntimeException();
                }
                return ExpiryPolicy.INFINITE;
            }
        });
        offHeapStore.put("key", "value");
        Assert.assertThat(offHeapStore.get("key").get(), CoreMatchers.is("value"));
        timeSource.advanceTime(1000);
        offHeapStore.put("key", "newValue");
        Assert.assertNull(offHeapStore.get("key"));
    }

    @Test
    public void testGetAndFaultOnExpiredEntry() throws StoreAccessException {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(10L)));
        try {
            offHeapStore.put("key", "value");
            timeSource.advanceTime(20L);
            Store.ValueHolder<String> valueHolder = offHeapStore.getAndFault("key");
            Assert.assertThat(valueHolder, CoreMatchers.nullValue());
            Assert.assertThat(getExpirationStatistic(offHeapStore).count(SUCCESS), CoreMatchers.is(1L));
        } finally {
            destroyStore(offHeapStore);
        }
    }

    @Test
    public void testComputeExpiresOnAccess() throws StoreAccessException {
        timeSource.advanceTime(1000L);
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).update(Duration.ZERO).build());
        offHeapStore.put("key", "value");
        Store.ValueHolder<String> result = offHeapStore.computeAndGet("key", ( s, s2) -> s2, () -> false, () -> false);
        Assert.assertThat(result, valueHeld("value"));
    }

    @Test
    public void testComputeExpiresOnUpdate() throws StoreAccessException {
        timeSource.advanceTime(1000L);
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).update(Duration.ZERO).build());
        offHeapStore.put("key", "value");
        Store.ValueHolder<String> result = offHeapStore.computeAndGet("key", ( s, s2) -> "newValue", () -> false, () -> false);
        Assert.assertThat(result, valueHeld("newValue"));
    }

    @Test
    public void testComputeOnExpiredEntry() throws StoreAccessException {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(10L)));
        offHeapStore.put("key", "value");
        timeSource.advanceTime(20L);
        offHeapStore.getAndCompute("key", ( mappedKey, mappedValue) -> {
            assertThat(mappedKey, is("key"));
            assertThat(mappedValue, Matchers.nullValue());
            return "value2";
        });
        Assert.assertThat(getExpirationStatistic(offHeapStore).count(SUCCESS), CoreMatchers.is(1L));
    }

    @Test
    public void testComputeIfAbsentOnExpiredEntry() throws StoreAccessException {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMillis(10L)));
        offHeapStore.put("key", "value");
        timeSource.advanceTime(20L);
        offHeapStore.computeIfAbsent("key", ( mappedKey) -> {
            Assert.assertThat(mappedKey, CoreMatchers.is("key"));
            return "value2";
        });
        Assert.assertThat(getExpirationStatistic(offHeapStore).count(SUCCESS), CoreMatchers.is(1L));
    }

    @Test
    public void testIteratorDoesNotSkipOrExpiresEntries() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10L)));
        offHeapStore.put("key1", "value1");
        offHeapStore.put("key2", "value2");
        timeSource.advanceTime(11L);
        offHeapStore.put("key3", "value3");
        offHeapStore.put("key4", "value4");
        final List<String> expiredKeys = new ArrayList<>();
        offHeapStore.getStoreEventSource().addEventListener(( event) -> {
            if ((event.getType()) == (EventType.EXPIRED)) {
                expiredKeys.add(event.getKey());
            }
        });
        List<String> iteratedKeys = new ArrayList<>();
        Store.Iterator<Cache.Entry<String, Store.ValueHolder<String>>> iterator = offHeapStore.iterator();
        while (iterator.hasNext()) {
            iteratedKeys.add(iterator.next().getKey());
        } 
        Assert.assertThat(iteratedKeys, Matchers.containsInAnyOrder("key1", "key2", "key3", "key4"));
        Assert.assertThat(expiredKeys.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testIteratorWithSingleExpiredEntry() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10L)));
        offHeapStore.put("key1", "value1");
        timeSource.advanceTime(11L);
        Store.Iterator<Cache.Entry<String, Store.ValueHolder<String>>> iterator = offHeapStore.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.next().getKey(), CoreMatchers.equalTo("key1"));
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorWithSingleNonExpiredEntry() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10L)));
        offHeapStore.put("key1", "value1");
        timeSource.advanceTime(5L);
        Store.Iterator<Cache.Entry<String, Store.ValueHolder<String>>> iterator = offHeapStore.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.next().getKey(), CoreMatchers.is("key1"));
    }

    @Test
    public void testIteratorOnEmptyStore() throws Exception {
        offHeapStore = createAndInitStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10L)));
        Store.Iterator<Cache.Entry<String, Store.ValueHolder<String>>> iterator = offHeapStore.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    private static class TestTimeSource implements TimeSource {
        private long time = 0;

        @Override
        public long getTimeMillis() {
            return time;
        }

        public void advanceTime(long step) {
            time += step;
        }
    }

    public static class DelegatingValueHolder<T> implements Store.ValueHolder<T> {
        private final Store.ValueHolder<T> valueHolder;

        public DelegatingValueHolder(final Store.ValueHolder<T> valueHolder) {
            this.valueHolder = valueHolder;
        }

        @Override
        public T get() {
            return valueHolder.get();
        }

        @Override
        public long creationTime() {
            return valueHolder.creationTime();
        }

        @Override
        public long expirationTime() {
            return valueHolder.expirationTime();
        }

        @Override
        public boolean isExpired(long expirationTime) {
            return valueHolder.isExpired(expirationTime);
        }

        @Override
        public long lastAccessTime() {
            return valueHolder.lastAccessTime();
        }

        @Override
        public long getId() {
            return valueHolder.getId();
        }
    }

    static class SimpleValueHolder<T> extends AbstractValueHolder<T> {
        private final T value;

        public SimpleValueHolder(T v, long creationTime, long expirationTime) {
            super((-1), creationTime, expirationTime);
            this.value = v;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public long creationTime() {
            return 0;
        }

        @Override
        public long expirationTime() {
            return 0;
        }

        @Override
        public boolean isExpired(long expirationTime) {
            return false;
        }

        @Override
        public long lastAccessTime() {
            return 0;
        }

        @Override
        public long getId() {
            return 0;
        }
    }
}

