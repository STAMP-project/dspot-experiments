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
package org.ehcache.impl.internal.store.heap;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import org.ehcache.config.Eviction;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.events.NullStoreEventDispatcher;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.events.StoreEvent;
import org.ehcache.core.spi.store.events.StoreEventListener;
import org.ehcache.core.spi.store.heap.SizeOfEngine;
import org.ehcache.core.spi.time.SystemTimeSource;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.core.store.StoreConfigurationImpl;
import org.ehcache.event.EventType;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.internal.events.TestStoreEventDispatcher;
import org.ehcache.impl.internal.sizeof.NoopSizeOfEngine;
import org.ehcache.impl.internal.store.heap.holders.OnHeapValueHolder;
import org.ehcache.internal.TestTimeSource;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class OnHeapStoreEvictionTest {
    /**
     * eviction tests : asserting the evict method is called *
     */
    @Test
    public void testComputeCalledEnforceCapacity() throws Exception {
        OnHeapStoreEvictionTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put("key", "value");
        store.getAndCompute("key", ( mappedKey, mappedValue) -> "value2");
        MatcherAssert.assertThat(store.enforceCapacityWasCalled(), Matchers.is(true));
    }

    @Test
    public void testComputeIfAbsentCalledEnforceCapacity() throws Exception {
        OnHeapStoreEvictionTest.OnHeapStoreForTests<String, String> store = newStore();
        store.computeIfAbsent("key", ( mappedKey) -> "value2");
        MatcherAssert.assertThat(store.enforceCapacityWasCalled(), Matchers.is(true));
    }

    @Test
    public void testFaultsDoNotGetToEvictionAdvisor() throws StoreAccessException {
        final Semaphore semaphore = new Semaphore(0);
        final OnHeapStoreEvictionTest.OnHeapStoreForTests<String, String> store = newStore(SystemTimeSource.INSTANCE, Eviction.noAdvice());
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            executor.submit(() -> store.getOrComputeIfAbsent("prime", ( key) -> {
                semaphore.acquireUninterruptibly();
                return new OnHeapValueHolder<String>(0, 0, false) {
                    @Override
                    public String get() {
                        return key;
                    }
                };
            }));
            while (!(semaphore.hasQueuedThreads()));
            store.put("boom", "boom");
        } finally {
            semaphore.release(1);
            executor.shutdown();
        }
    }

    @Test
    public void testEvictionCandidateLimits() throws Exception {
        TestTimeSource timeSource = new TestTimeSource();
        StoreConfigurationImpl<String, String> configuration = new StoreConfigurationImpl(String.class, String.class, Eviction.noAdvice(), getClass().getClassLoader(), ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.heap(1).build(), 1, null, null);
        TestStoreEventDispatcher<String, String> eventDispatcher = new TestStoreEventDispatcher<>();
        final String firstKey = "daFirst";
        eventDispatcher.addEventListener(( event) -> {
            if (event.getType().equals(EventType.EVICTED)) {
                MatcherAssert.assertThat(event.getKey(), Matchers.is(firstKey));
            }
        });
        OnHeapStore<String, String> store = new OnHeapStore(configuration, timeSource, new IdentityCopier(), new IdentityCopier(), new NoopSizeOfEngine(), eventDispatcher);
        timeSource.advanceTime(10000L);
        store.put(firstKey, "daValue");
        timeSource.advanceTime(10000L);
        store.put("other", "otherValue");
    }

    public static class OnHeapStoreForTests<K, V> extends OnHeapStore<K, V> {
        public OnHeapStoreForTests(final Store.Configuration<K, V> config, final TimeSource timeSource) {
            super(config, timeSource, IdentityCopier.identityCopier(), IdentityCopier.identityCopier(), new NoopSizeOfEngine(), NullStoreEventDispatcher.nullStoreEventDispatcher());
        }

        public OnHeapStoreForTests(final Store.Configuration<K, V> config, final TimeSource timeSource, final SizeOfEngine engine) {
            super(config, timeSource, IdentityCopier.identityCopier(), IdentityCopier.identityCopier(), engine, NullStoreEventDispatcher.nullStoreEventDispatcher());
        }

        private boolean enforceCapacityWasCalled = false;

        @Override
        protected void enforceCapacity() {
            enforceCapacityWasCalled = true;
            super.enforceCapacity();
        }

        boolean enforceCapacityWasCalled() {
            return enforceCapacityWasCalled;
        }
    }
}

