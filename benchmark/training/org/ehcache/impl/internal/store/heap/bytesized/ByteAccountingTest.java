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
package org.ehcache.impl.internal.store.heap.bytesized;


import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.core.events.StoreEventDispatcher;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.events.StoreEvent;
import org.ehcache.core.spi.store.events.StoreEventListener;
import org.ehcache.core.spi.store.heap.SizeOfEngine;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.event.EventType;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.internal.sizeof.DefaultSizeOfEngine;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.ehcache.internal.TestTimeSource;
import org.ehcache.internal.store.StoreCreationEventListenerTest;
import org.ehcache.sizeof.SizeOf;
import org.ehcache.sizeof.SizeOfFilterSource;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;


/**
 *
 *
 * @author Abhilash
 */
public class ByteAccountingTest {
    private static final SizeOfEngine SIZE_OF_ENGINE = new DefaultSizeOfEngine(Long.MAX_VALUE, Long.MAX_VALUE);

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private static final long SIZE_OF_KEY_VALUE_PAIR = ByteAccountingTest.getSize(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);

    private static final SizeOfFilterSource FILTERSOURCE = new SizeOfFilterSource(true);

    private static final SizeOf SIZEOF = SizeOf.newInstance(ByteAccountingTest.FILTERSOURCE.getFilters());

    @Test
    public void testPut() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
    }

    @Test
    public void testPutUpdate() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        String otherValue = "otherValue";
        store.put(ByteAccountingTest.KEY, otherValue);
        long delta = (ByteAccountingTest.SIZEOF.deepSizeOf(otherValue)) - (ByteAccountingTest.SIZEOF.deepSizeOf(ByteAccountingTest.VALUE));
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(((ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR) + delta)));
    }

    @Test
    public void testPutExpiryOnCreate() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().create(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testPutExpiryOnUpdate() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().update(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.put(ByteAccountingTest.KEY, "otherValue");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testRemove() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        long beforeRemove = store.getCurrentUsageInBytes();
        store.remove("Another Key");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(beforeRemove));
        store.remove(ByteAccountingTest.KEY);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testRemoveExpired() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ttlCreation600ms());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        timeSource.advanceTime(1000L);
        store.remove(ByteAccountingTest.KEY);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testRemoveTwoArg() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        long beforeRemove = store.getCurrentUsageInBytes();
        store.remove(ByteAccountingTest.KEY, "Another value");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(beforeRemove));
        store.remove(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testRemoveTwoArgExpired() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ttlCreation600ms());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        timeSource.advanceTime(1000L);
        store.remove(ByteAccountingTest.KEY, "whatever value, it is expired anyway");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testRemoveTwoArgExpiresOnAccess() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.remove(ByteAccountingTest.KEY, "whatever value, it expires on access");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testReplace() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        long beforeReplace = store.getCurrentUsageInBytes();
        store.replace("Another Key", "Another Value");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(beforeReplace));
        String toReplace = "Replaced Value";
        store.replace(ByteAccountingTest.KEY, toReplace);
        long delta = (ByteAccountingTest.SIZEOF.deepSizeOf(toReplace)) - (ByteAccountingTest.SIZEOF.deepSizeOf(ByteAccountingTest.VALUE));
        long afterReplace = store.getCurrentUsageInBytes();
        MatcherAssert.assertThat((afterReplace - beforeReplace), Matchers.is(delta));
        // when delta is negative
        store.replace(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat((afterReplace - (store.getCurrentUsageInBytes())), Matchers.is(delta));
    }

    @Test
    public void testReplaceTwoArgExpired() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ttlCreation600ms());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        timeSource.advanceTime(1000L);
        store.replace(ByteAccountingTest.KEY, "whatever value, it is expired anyway");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testReplaceTwoArgExpiresOnUpdate() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().update(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.replace(ByteAccountingTest.KEY, "whatever value, it expires on update");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testReplaceThreeArg() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        long beforeReplace = store.getCurrentUsageInBytes();
        store.replace(ByteAccountingTest.KEY, "Another Value", ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(beforeReplace));
        String toReplace = "Replaced Value";
        store.replace(ByteAccountingTest.KEY, ByteAccountingTest.VALUE, toReplace);
        long delta = (ByteAccountingTest.SIZEOF.deepSizeOf(toReplace)) - (ByteAccountingTest.SIZEOF.deepSizeOf(ByteAccountingTest.VALUE));
        long afterReplace = store.getCurrentUsageInBytes();
        MatcherAssert.assertThat((afterReplace - beforeReplace), Matchers.is(delta));
        // when delta is negative
        store.replace(ByteAccountingTest.KEY, toReplace, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat((afterReplace - (store.getCurrentUsageInBytes())), Matchers.is(delta));
    }

    @Test
    public void testReplaceThreeArgExpired() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ttlCreation600ms());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        timeSource.advanceTime(1000L);
        store.replace(ByteAccountingTest.KEY, ByteAccountingTest.VALUE, "whatever value, it is expired anyway");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testReplaceThreeArgExpiresOnUpdate() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().update(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.replace(ByteAccountingTest.KEY, ByteAccountingTest.VALUE, "whatever value, it expires on update");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testPutIfAbsent() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.putIfAbsent(ByteAccountingTest.KEY, ByteAccountingTest.VALUE, ( b) -> {
        });
        long current = store.getCurrentUsageInBytes();
        MatcherAssert.assertThat(current, Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        store.putIfAbsent(ByteAccountingTest.KEY, "New Value to Put", ( b) -> {
        });
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(current));
    }

    @Test
    public void testPutIfAbsentOverExpired() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ttlCreation600ms());
        store.put(ByteAccountingTest.KEY, "an expired value");
        timeSource.advanceTime(1000L);
        store.putIfAbsent(ByteAccountingTest.KEY, ByteAccountingTest.VALUE, ( b) -> {
        });
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
    }

    @Test
    public void testPutIfAbsentExpiresOnAccess() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(1000L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.putIfAbsent(ByteAccountingTest.KEY, "another value ... whatever", ( b) -> {
        });
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testInvalidate() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<Object, Object> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.invalidate(ByteAccountingTest.KEY);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testSilentInvalidate() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<Object, Object> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.silentInvalidate(ByteAccountingTest.KEY, ( objectValueHolder) -> {
            // Nothing to do
            return null;
        });
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testComputeRemove() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        store.getAndCompute("another", ( a, b) -> null);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        store.getAndCompute(ByteAccountingTest.KEY, ( a, b) -> null);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testCompute() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.getAndCompute(ByteAccountingTest.KEY, ( a, b) -> VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        final String replace = "Replace the original value";
        long delta = (ByteAccountingTest.SIZEOF.deepSizeOf(replace)) - (ByteAccountingTest.SIZEOF.deepSizeOf(ByteAccountingTest.VALUE));
        store.getAndCompute(ByteAccountingTest.KEY, ( a, b) -> replace);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(((ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR) + delta)));
    }

    @Test
    public void testComputeExpiryOnAccess() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(100L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.computeAndGet(ByteAccountingTest.KEY, ( s, s2) -> s2, () -> false, () -> false);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testGetAndComputeExpiryOnUpdate() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(100L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().update(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.getAndCompute(ByteAccountingTest.KEY, ( s, s2) -> s2);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testComputeIfAbsent() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore();
        store.computeIfAbsent(ByteAccountingTest.KEY, ( a) -> ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        store.computeIfAbsent(ByteAccountingTest.KEY, ( a) -> "Should not be replaced");
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
    }

    @Test
    public void testComputeIfAbsentExpireOnCreate() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(100L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().create(Duration.ZERO).build());
        store.computeIfAbsent(ByteAccountingTest.KEY, ( s) -> ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testComputeIfAbsentExpiryOnAccess() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource(100L);
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).build());
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        store.computeIfAbsent(ByteAccountingTest.KEY, ( s) -> {
            Assert.fail("should not be called");
            return s;
        });
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testExpiry() throws StoreAccessException {
        TestTimeSource timeSource = new TestTimeSource();
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        timeSource.advanceTime(1);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        MatcherAssert.assertThat(store.get(ByteAccountingTest.KEY), Matchers.nullValue());
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(0L));
    }

    @Test
    public void testEviction() throws StoreAccessException {
        ByteAccountingTest.OnHeapStoreForTests<String, String> store = newStore(1);
        @SuppressWarnings("unchecked")
        StoreEventListener<String, String> listener = Mockito.mock(StoreEventListener.class);
        store.getStoreEventSource().addEventListener(listener);
        store.put(ByteAccountingTest.KEY, ByteAccountingTest.VALUE);
        MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        String key1 = "key1";
        char[] chars = new char[250];
        Arrays.fill(chars, ((char) (65535)));
        String value1 = new String(chars);
        long requiredSize = ByteAccountingTest.getSize(key1, value1);
        store.put(key1, value1);
        Matcher<StoreEvent<String, String>> matcher = StoreCreationEventListenerTest.eventType(EventType.EVICTED);
        Mockito.verify(listener, Mockito.times(1)).onEvent(MockitoHamcrest.argThat(matcher));
        if ((store.get(key1)) != null) {
            MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(requiredSize));
        } else {
            MatcherAssert.assertThat(store.getCurrentUsageInBytes(), Matchers.is(ByteAccountingTest.SIZE_OF_KEY_VALUE_PAIR));
        }
    }

    static class OnHeapStoreForTests<K, V> extends OnHeapStore<K, V> {
        @SuppressWarnings("unchecked")
        OnHeapStoreForTests(final Store.Configuration<K, V> config, final TimeSource timeSource, final SizeOfEngine engine, StoreEventDispatcher<K, V> eventDispatcher) {
            super(config, timeSource, IdentityCopier.identityCopier(), IdentityCopier.identityCopier(), engine, eventDispatcher);
        }

        long getCurrentUsageInBytes() {
            return super.byteSized();
        }
    }
}

