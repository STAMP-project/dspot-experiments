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


import java.time.Duration;
import java.util.EnumSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import org.ehcache.Cache;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.core.events.StoreEventDispatcher;
import org.ehcache.core.events.StoreEventSink;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.spi.store.tiering.CachingTier;
import org.ehcache.core.spi.time.TimeSource;
import org.ehcache.core.statistics.CachingTierOperationOutcomes;
import org.ehcache.core.statistics.StoreOperationOutcomes;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.copy.IdentityCopier;
import org.ehcache.impl.internal.store.heap.holders.CopiedOnHeapValueHolder;
import org.ehcache.impl.internal.util.StatisticsTestUtils;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.WAITING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_MISSING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_PRESENT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_NOT_PRESENT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_PRESENT;
import static org.ehcache.core.statistics.CachingTierOperationOutcomes.InvalidateOutcome.REMOVED;
import static org.ehcache.core.statistics.StoreOperationOutcomes.ComputeIfAbsentOutcome.NOOP;
import static org.ehcache.core.statistics.StoreOperationOutcomes.EvictionOutcome.SUCCESS;
import static org.ehcache.core.statistics.StoreOperationOutcomes.GetOutcome.HIT;
import static org.ehcache.core.statistics.StoreOperationOutcomes.GetOutcome.MISS;
import static org.ehcache.core.statistics.StoreOperationOutcomes.PutOutcome.PUT;
import static org.ehcache.core.statistics.StoreOperationOutcomes.ReplaceOutcome.REPLACED;
import static org.ehcache.impl.internal.util.Matchers.holding;
import static org.ehcache.impl.internal.util.Matchers.valueHeld;


public abstract class BaseOnHeapStoreTest {
    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException();

    protected StoreEventDispatcher<Object, Object> eventDispatcher;

    protected StoreEventSink<Object, Object> eventSink;

    @Rule
    public TestRule watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            if (e.getMessage().startsWith("test timed out after")) {
                System.err.println(buildThreadDump());
            }
        }

        private String buildThreadDump() {
            StringBuilder dump = new StringBuilder();
            dump.append("***** Test timeout - printing thread dump ****");
            Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
            for (Map.Entry<Thread, StackTraceElement[]> e : stackTraces.entrySet()) {
                Thread thread = e.getKey();
                dump.append(String.format("\"%s\" %s prio=%d tid=%d %s\njava.lang.Thread.State: %s", thread.getName(), (thread.isDaemon() ? "daemon" : ""), thread.getPriority(), thread.getId(), (WAITING.equals(thread.getState()) ? "in Object.wait()" : thread.getState().name().toLowerCase()), (WAITING.equals(thread.getState()) ? "WAITING (on object monitor)" : thread.getState())));
                for (StackTraceElement stackTraceElement : e.getValue()) {
                    dump.append("\n        at ");
                    dump.append(stackTraceElement);
                }
                dump.append("\n");
            }
            return dump.toString();
        }
    };

    @Test
    public void testEvictEmptyStoreDoesNothing() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        MatcherAssert.assertThat(store.evict(eventSink), Matchers.is(false));
        Mockito.verify(eventSink, Mockito.never()).evicted(ArgumentMatchers.anyString(), anyValueSupplier());
    }

    @Test
    public void testEvictWithNoEvictionAdvisorDoesEvict() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        for (int i = 0; i < 100; i++) {
            store.put(Integer.toString(i), Integer.toString(i));
        }
        MatcherAssert.assertThat(store.evict(eventSink), Matchers.is(true));
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(99));
        Mockito.verify(eventSink, Mockito.times(1)).evicted(ArgumentMatchers.anyString(), anyValueSupplier());
        StatisticsTestUtils.validateStats(store, EnumSet.of(SUCCESS));
    }

    @Test
    public void testEvictWithFullyAdvisedAgainstEvictionDoesEvict() throws Exception {
        OnHeapStore<String, String> store = newStore(( key, value) -> true);
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        for (int i = 0; i < 100; i++) {
            store.put(Integer.toString(i), Integer.toString(i));
        }
        MatcherAssert.assertThat(store.evict(eventSink), Matchers.is(true));
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(99));
        Mockito.verify(eventSink, Mockito.times(1)).evicted(ArgumentMatchers.anyString(), anyValueSupplier());
        StatisticsTestUtils.validateStats(store, EnumSet.of(SUCCESS));
    }

    @Test
    public void testEvictWithBrokenEvictionAdvisorDoesEvict() throws Exception {
        OnHeapStore<String, String> store = newStore(( key, value) -> {
            throw new UnsupportedOperationException("Broken advisor!");
        });
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        for (int i = 0; i < 100; i++) {
            store.put(Integer.toString(i), Integer.toString(i));
        }
        MatcherAssert.assertThat(store.evict(eventSink), Matchers.is(true));
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(99));
        Mockito.verify(eventSink, Mockito.times(1)).evicted(ArgumentMatchers.anyString(), anyValueSupplier());
        StatisticsTestUtils.validateStats(store, EnumSet.of(SUCCESS));
    }

    @Test
    public void testGet() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
        StatisticsTestUtils.validateStats(store, EnumSet.of(HIT));
    }

    @Test
    public void testGetNoPut() throws Exception {
        OnHeapStore<String, String> store = newStore();
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(MISS));
    }

    @Test
    public void testGetExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
        timeSource.advanceTime(1);
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        BaseOnHeapStoreTest.checkExpiryEvent(eventSink, "key", "value");
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
        StatisticsTestUtils.validateStats(store, EnumSet.of(HIT, MISS));
    }

    @Test
    public void testGetNoExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(2)));
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        store.put("key", "value");
        timeSource.advanceTime(1);
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
        Mockito.verify(eventSink, Mockito.never()).expired(ArgumentMatchers.anyString(), anyValueSupplier());
        StatisticsTestUtils.validateStats(store, EnumSet.of(HIT));
    }

    @Test
    public void testAccessTime() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        store.put("key", "value");
        long first = store.get("key").lastAccessTime();
        MatcherAssert.assertThat(first, Matchers.equalTo(timeSource.getTimeMillis()));
        final long advance = 5;
        timeSource.advanceTime(advance);
        long next = store.get("key").lastAccessTime();
        MatcherAssert.assertThat(next, Matchers.equalTo((first + advance)));
    }

    @Test
    public void testContainsKey() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        MatcherAssert.assertThat(store.containsKey("key"), Matchers.is(true));
    }

    @Test
    public void testNotContainsKey() throws Exception {
        OnHeapStore<String, String> store = newStore();
        MatcherAssert.assertThat(store.containsKey("key"), Matchers.is(false));
    }

    @Test
    public void testContainsKeyExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        timeSource.advanceTime(1);
        MatcherAssert.assertThat(store.containsKey("key"), Matchers.is(false));
        BaseOnHeapStoreTest.checkExpiryEvent(eventSink, "key", "value");
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testPut() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        Mockito.verify(eventSink).created(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(PUT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testPutOverwrite() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        store.put("key", "value2");
        Mockito.verify(eventSink).updated(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")), ArgumentMatchers.eq("value2"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
    }

    @Test
    public void testCreateTime() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        MatcherAssert.assertThat(store.containsKey("key"), Matchers.is(false));
        store.put("key", "value");
        Store.ValueHolder<String> valueHolder = store.get("key");
        MatcherAssert.assertThat(timeSource.getTimeMillis(), Matchers.equalTo(valueHolder.creationTime()));
    }

    @Test
    public void testInvalidate() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        store.invalidate("key");
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(REMOVED));
    }

    @Test
    public void testPutIfAbsentNoValue() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        Store.ValueHolder<String> prev = store.putIfAbsent("key", "value", ( b) -> {
        });
        MatcherAssert.assertThat(prev, Matchers.nullValue());
        Mockito.verify(eventSink).created(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.PutIfAbsentOutcome.PUT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testPutIfAbsentValuePresent() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        Store.ValueHolder<String> prev = store.putIfAbsent("key", "value2", ( b) -> {
        });
        MatcherAssert.assertThat(prev.get(), Matchers.equalTo("value"));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.PutIfAbsentOutcome.HIT));
    }

    @Test
    public void testPutIfAbsentUpdatesAccessTime() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        store.putIfAbsent("key", "value", ( b) -> {
        });
        long first = store.get("key").lastAccessTime();
        timeSource.advanceTime(1);
        long next = store.putIfAbsent("key", "value2", ( b) -> {
        }).lastAccessTime();
        MatcherAssert.assertThat((next - first), Matchers.equalTo(1L));
    }

    @Test
    public void testPutIfAbsentExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        timeSource.advanceTime(1);
        Store.ValueHolder<String> prev = store.putIfAbsent("key", "value2", ( b) -> {
        });
        MatcherAssert.assertThat(prev, Matchers.nullValue());
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
        BaseOnHeapStoreTest.checkExpiryEvent(getStoreEventSink(), "key", "value");
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testRemove() throws StoreAccessException {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        store.remove("key");
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        Mockito.verify(eventSink).removed(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
    }

    @Test
    public void testRemoveTwoArgMatch() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        Store.RemoveStatus removed = store.remove("key", "value");
        MatcherAssert.assertThat(removed, Matchers.equalTo(Store.RemoveStatus.REMOVED));
        Mockito.verify(eventSink).removed(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalRemoveOutcome.REMOVED));
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
    }

    @Test
    public void testRemoveTwoArgNoMatch() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        Store.RemoveStatus removed = store.remove("key", "not value");
        MatcherAssert.assertThat(removed, Matchers.equalTo(KEY_PRESENT));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalRemoveOutcome.MISS));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testRemoveTwoArgExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
        timeSource.advanceTime(1);
        Store.RemoveStatus removed = store.remove("key", "value");
        MatcherAssert.assertThat(removed, Matchers.equalTo(KEY_MISSING));
        BaseOnHeapStoreTest.checkExpiryEvent(eventSink, "key", "value");
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testReplaceTwoArgPresent() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        Store.ValueHolder<String> existing = store.replace("key", "value2");
        MatcherAssert.assertThat(existing.get(), Matchers.equalTo("value"));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
        Mockito.verify(eventSink).updated(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")), ArgumentMatchers.eq("value2"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(REPLACED));
    }

    @Test
    public void testReplaceTwoArgAbsent() throws Exception {
        OnHeapStore<String, String> store = newStore();
        Store.ValueHolder<String> existing = store.replace("key", "value");
        MatcherAssert.assertThat(existing, Matchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ReplaceOutcome.MISS));
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
    }

    @Test
    public void testReplaceTwoArgExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        timeSource.advanceTime(1);
        Store.ValueHolder<String> existing = store.replace("key", "value2");
        MatcherAssert.assertThat(existing, Matchers.nullValue());
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        BaseOnHeapStoreTest.checkExpiryEvent(eventSink, "key", "value");
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testReplaceThreeArgMatch() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        Store.ReplaceStatus replaced = store.replace("key", "value", "value2");
        MatcherAssert.assertThat(replaced, Matchers.equalTo(Store.ReplaceStatus.HIT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
        Mockito.verify(eventSink).updated(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")), ArgumentMatchers.eq("value2"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalReplaceOutcome.REPLACED));
    }

    @Test
    public void testReplaceThreeArgNoMatch() throws Exception {
        OnHeapStore<String, String> store = newStore();
        Store.ReplaceStatus replaced = store.replace("key", "value", "value2");
        MatcherAssert.assertThat(replaced, Matchers.equalTo(MISS_NOT_PRESENT));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ConditionalReplaceOutcome.MISS));
        store.put("key", "value");
        replaced = store.replace("key", "not value", "value2");
        MatcherAssert.assertThat(replaced, Matchers.equalTo(MISS_PRESENT));
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ConditionalReplaceOutcome.MISS, 2L);
    }

    @Test
    public void testReplaceThreeArgExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        timeSource.advanceTime(1);
        Store.ReplaceStatus replaced = store.replace("key", "value", "value2");
        MatcherAssert.assertThat(replaced, Matchers.equalTo(MISS_NOT_PRESENT));
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        BaseOnHeapStoreTest.checkExpiryEvent(eventSink, "key", "value");
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testIterator() throws Exception {
        OnHeapStore<String, String> store = newStore();
        Store.Iterator<Cache.Entry<String, Store.ValueHolder<String>>> iter = store.iterator();
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(false));
        try {
            iter.next();
            Assert.fail("NoSuchElementException expected");
        } catch (NoSuchElementException nse) {
            // expected
        }
        store.put("key1", "value1");
        iter = store.iterator();
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        BaseOnHeapStoreTest.assertEntry(iter.next(), "key1", "value1");
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(false));
        store.put("key2", "value2");
        Map<String, String> observed = BaseOnHeapStoreTest.observe(store.iterator());
        MatcherAssert.assertThat(2, Matchers.equalTo(observed.size()));
        MatcherAssert.assertThat(observed.get("key1"), Matchers.equalTo("value1"));
        MatcherAssert.assertThat(observed.get("key2"), Matchers.equalTo("value2"));
    }

    @Test
    public void testIteratorExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key1", "value1");
        store.put("key2", "value2");
        store.put("key3", "value3");
        timeSource.advanceTime(1);
        Map<String, String> observed = BaseOnHeapStoreTest.observe(store.iterator());
        MatcherAssert.assertThat(0, Matchers.equalTo(observed.size()));
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ExpirationOutcome.SUCCESS, 3L);
    }

    @Test
    public void testIteratorDoesNotUpdateAccessTime() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        store.put("key1", "value1");
        store.put("key2", "value2");
        timeSource.advanceTime(5);
        Map<String, Long> times = BaseOnHeapStoreTest.observeAccessTimes(store.iterator());
        MatcherAssert.assertThat(2, Matchers.equalTo(times.size()));
        MatcherAssert.assertThat(times.get("key1"), Matchers.equalTo(0L));
        MatcherAssert.assertThat(times.get("key2"), Matchers.equalTo(0L));
    }

    @Test
    public void testComputeReplaceTrue() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        Store.ValueHolder<String> installedHolder = store.get("key");
        long createTime = installedHolder.creationTime();
        long accessTime = installedHolder.lastAccessTime();
        timeSource.advanceTime(1);
        Store.ValueHolder<String> newValue = store.computeAndGet("key", ( mappedKey, mappedValue) -> mappedValue, () -> true, () -> false);
        MatcherAssert.assertThat(newValue.get(), Matchers.equalTo("value"));
        MatcherAssert.assertThat((createTime + 1), Matchers.equalTo(newValue.creationTime()));
        MatcherAssert.assertThat((accessTime + 1), Matchers.equalTo(newValue.lastAccessTime()));
        Mockito.verify(eventSink).updated(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")), ArgumentMatchers.eq("value"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ComputeOutcome.PUT));
    }

    @Test
    public void testComputeReplaceFalse() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.noExpiration());
        store.put("key", "value");
        Store.ValueHolder<String> installedHolder = store.get("key");
        long createTime = installedHolder.creationTime();
        long accessTime = installedHolder.lastAccessTime();
        timeSource.advanceTime(1);
        Store.ValueHolder<String> newValue = store.computeAndGet("key", ( mappedKey, mappedValue) -> mappedValue, () -> false, () -> false);
        MatcherAssert.assertThat(newValue.get(), Matchers.equalTo("value"));
        MatcherAssert.assertThat(createTime, Matchers.equalTo(newValue.creationTime()));
        MatcherAssert.assertThat((accessTime + 1), Matchers.equalTo(newValue.lastAccessTime()));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ComputeOutcome.HIT));
    }

    @Test
    public void testGetAndCompute() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        Store.ValueHolder<String> oldValue = store.getAndCompute("key", ( mappedKey, mappedValue) -> {
            assertThat(mappedKey, equalTo("key"));
            assertThat(mappedValue, nullValue());
            return "value";
        });
        MatcherAssert.assertThat(oldValue, Matchers.nullValue());
        Mockito.verify(eventSink).created(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ComputeOutcome.PUT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testGetAndComputeNull() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        Store.ValueHolder<String> oldValue = store.getAndCompute("key", ( mappedKey, mappedValue) -> null);
        MatcherAssert.assertThat(oldValue, Matchers.nullValue());
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ComputeOutcome.MISS, 1L);
        store.put("key", "value");
        oldValue = store.getAndCompute("key", ( mappedKey, mappedValue) -> null);
        MatcherAssert.assertThat(oldValue.get(), Matchers.equalTo("value"));
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        Mockito.verify(eventSink).removed(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStat(store, StoreOperationOutcomes.ComputeOutcome.REMOVED, 1L);
    }

    @Test
    public void testComputeException() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        try {
            store.getAndCompute("key", ( mappedKey, mappedValue) -> {
                throw RUNTIME_EXCEPTION;
            });
            Assert.fail("RuntimeException expected");
        } catch (StoreAccessException cae) {
            MatcherAssert.assertThat(cae.getCause(), Matchers.is(((Throwable) (BaseOnHeapStoreTest.RUNTIME_EXCEPTION))));
        }
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testGetAndComputeExistingValue() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        store.put("key", "value");
        Store.ValueHolder<String> oldValue = store.getAndCompute("key", ( mappedKey, mappedValue) -> {
            assertThat(mappedKey, equalTo("key"));
            assertThat(mappedValue, equalTo("value"));
            return "value2";
        });
        MatcherAssert.assertThat(oldValue.get(), Matchers.equalTo("value"));
        Mockito.verify(eventSink).updated(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding("value")), ArgumentMatchers.eq("value2"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ComputeOutcome.PUT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
    }

    @Test
    public void testGetAndComputeExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        timeSource.advanceTime(1);
        Store.ValueHolder<String> oldValue = store.getAndCompute("key", ( mappedKey, mappedValue) -> {
            assertThat(mappedKey, equalTo("key"));
            assertThat(mappedValue, nullValue());
            return "value2";
        });
        MatcherAssert.assertThat(oldValue, Matchers.nullValue());
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
        BaseOnHeapStoreTest.checkExpiryEvent(eventSink, "key", "value");
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testComputeWhenExpireOnCreate() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        timeSource.advanceTime(1000L);
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().create(Duration.ZERO).build());
        Store.ValueHolder<String> result = store.computeAndGet("key", ( key, value) -> "value", () -> false, () -> false);
        MatcherAssert.assertThat(result, Matchers.nullValue());
    }

    @Test
    public void testComputeWhenExpireOnUpdate() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        timeSource.advanceTime(1000L);
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().update(Duration.ZERO).build());
        store.put("key", "value");
        Store.ValueHolder<String> result = store.computeAndGet("key", ( key, value) -> "newValue", () -> false, () -> false);
        MatcherAssert.assertThat(result, valueHeld("newValue"));
    }

    @Test
    public void testComputeWhenExpireOnAccess() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        timeSource.advanceTime(1000L);
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.expiry().access(Duration.ZERO).build());
        store.put("key", "value");
        Store.ValueHolder<String> result = store.computeAndGet("key", ( key, value) -> value, () -> false, () -> false);
        MatcherAssert.assertThat(result, valueHeld("value"));
    }

    @Test
    public void testComputeIfAbsent() throws Exception {
        OnHeapStore<String, String> store = newStore();
        StoreEventSink<String, String> eventSink = getStoreEventSink();
        StoreEventDispatcher<String, String> eventDispatcher = getStoreEventDispatcher();
        Store.ValueHolder<String> newValue = store.computeIfAbsent("key", ( mappedKey) -> {
            MatcherAssert.assertThat(mappedKey, Matchers.equalTo("key"));
            return "value";
        });
        MatcherAssert.assertThat(newValue.get(), Matchers.equalTo("value"));
        Mockito.verify(eventSink).created(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ComputeIfAbsentOutcome.PUT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testComputeIfAbsentExisting() throws Exception {
        OnHeapStore<String, String> store = newStore();
        store.put("key", "value");
        Store.ValueHolder<String> newValue = store.computeIfAbsent("key", ( mappedKey) -> {
            Assert.fail("Should not be called");
            return null;
        });
        MatcherAssert.assertThat(newValue.get(), Matchers.equalTo("value"));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ComputeIfAbsentOutcome.HIT));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value"));
    }

    @Test
    public void testComputeIfAbsentReturnNull() throws Exception {
        OnHeapStore<String, String> store = newStore();
        Store.ValueHolder<String> newValue = store.computeIfAbsent("key", ( mappedKey) -> null);
        MatcherAssert.assertThat(newValue, Matchers.nullValue());
        StatisticsTestUtils.validateStats(store, EnumSet.of(NOOP));
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
    }

    @Test
    public void testComputeIfAbsentException() throws Exception {
        OnHeapStore<String, String> store = newStore();
        try {
            store.computeIfAbsent("key", ( mappedKey) -> {
                throw BaseOnHeapStoreTest.RUNTIME_EXCEPTION;
            });
            Assert.fail("Expected exception");
        } catch (StoreAccessException cae) {
            MatcherAssert.assertThat(cae.getCause(), Matchers.is(((Throwable) (BaseOnHeapStoreTest.RUNTIME_EXCEPTION))));
        }
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComputeIfAbsentExpired() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        store.put("key", "value");
        timeSource.advanceTime(1);
        Store.ValueHolder<String> newValue = store.computeIfAbsent("key", ( mappedKey) -> {
            MatcherAssert.assertThat(mappedKey, Matchers.equalTo("key"));
            return "value2";
        });
        MatcherAssert.assertThat(newValue.get(), Matchers.equalTo("value2"));
        MatcherAssert.assertThat(store.get("key").get(), Matchers.equalTo("value2"));
        final String value = "value";
        Mockito.verify(eventSink).expired(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(holding(value)));
        Mockito.verify(eventSink).created(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value2"));
        verifyListenerReleaseEventsInOrder(eventDispatcher);
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testExpiryCreateException() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, new ExpiryPolicy<String, String>() {
            @Override
            public Duration getExpiryForCreation(String key, String value) {
                throw BaseOnHeapStoreTest.RUNTIME_EXCEPTION;
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
        store.put("key", "value");
        MatcherAssert.assertThat(store.containsKey("key"), Matchers.equalTo(false));
    }

    @Test
    public void testExpiryAccessExceptionReturnsValueAndExpiresIt() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        timeSource.advanceTime(5);
        OnHeapStore<String, String> store = newStore(timeSource, new ExpiryPolicy<String, String>() {
            @Override
            public Duration getExpiryForCreation(String key, String value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForAccess(String key, Supplier<? extends String> value) {
                throw BaseOnHeapStoreTest.RUNTIME_EXCEPTION;
            }

            @Override
            public Duration getExpiryForUpdate(String key, Supplier<? extends String> oldValue, String newValue) {
                return null;
            }
        });
        store.put("key", "value");
        MatcherAssert.assertThat(store.get("key"), valueHeld("value"));
        Assert.assertNull(store.get("key"));
    }

    @Test
    public void testExpiryUpdateException() throws Exception {
        final BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, new ExpiryPolicy<String, String>() {
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
        store.put("key", "value");
        store.get("key");
        timeSource.advanceTime(1000);
        store.put("key", "newValue");
        Assert.assertNull(store.get("key"));
    }

    @Test
    public void testGetOrComputeIfAbsentExpiresOnHit() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        @SuppressWarnings("unchecked")
        CachingTier.InvalidationListener<String, String> invalidationListener = Mockito.mock(CachingTier.InvalidationListener.class);
        store.setInvalidationListener(invalidationListener);
        store.put("key", "value");
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(1));
        timeSource.advanceTime(1);
        Store.ValueHolder<String> newValue = store.getOrComputeIfAbsent("key", ( s) -> null);
        MatcherAssert.assertThat(newValue, Matchers.nullValue());
        MatcherAssert.assertThat(store.get("key"), Matchers.nullValue());
        Mockito.verify(invalidationListener).onInvalidation(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(valueHeld("value")));
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(0));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testGetOfComputeIfAbsentExpiresWithLoaderWriter() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource timeSource = new BaseOnHeapStoreTest.TestTimeSource();
        OnHeapStore<String, String> store = newStore(timeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(1)));
        @SuppressWarnings("unchecked")
        CachingTier.InvalidationListener<String, String> invalidationListener = Mockito.mock(CachingTier.InvalidationListener.class);
        store.setInvalidationListener(invalidationListener);
        // Add an entry
        store.put("key", "value");
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(1));
        // Advance after expiration time
        timeSource.advanceTime(1);
        @SuppressWarnings("unchecked")
        final Store.ValueHolder<String> vh = Mockito.mock(Store.ValueHolder.class);
        Mockito.when(vh.get()).thenReturn("newvalue");
        Mockito.when(vh.expirationTime()).thenReturn(2L);
        Store.ValueHolder<String> newValue = store.getOrComputeIfAbsent("key", ( s) -> vh);
        MatcherAssert.assertThat(newValue, valueHeld("newvalue"));
        MatcherAssert.assertThat(store.get("key"), valueHeld("newvalue"));
        Mockito.verify(invalidationListener).onInvalidation(ArgumentMatchers.eq("key"), MockitoHamcrest.argThat(valueHeld("value")));
        MatcherAssert.assertThat(BaseOnHeapStoreTest.storeSize(store), Matchers.is(1));
        StatisticsTestUtils.validateStats(store, EnumSet.of(StoreOperationOutcomes.ExpirationOutcome.SUCCESS));
    }

    @Test
    public void testGetOrComputeIfAbsentRemovesFault() throws StoreAccessException {
        final OnHeapStore<String, String> store = newStore();
        final CountDownLatch testCompletionLatch = new CountDownLatch(1);
        final CountDownLatch threadFaultCompletionLatch = new CountDownLatch(1);
        final CountDownLatch mainFaultCreationLatch = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            try {
                try {
                    mainFaultCreationLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                store.invalidate("1");
                store.getOrComputeIfAbsent("1", ( s) -> {
                    threadFaultCompletionLatch.countDown();
                    try {
                        testCompletionLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
            } catch (StoreAccessException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        store.getOrComputeIfAbsent("1", ( s) -> {
            try {
                mainFaultCreationLatch.countDown();
                threadFaultCompletionLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
        testCompletionLatch.countDown();
    }

    @Test
    public void testGetOrComputeIfAbsentInvalidatesFault() throws InterruptedException, StoreAccessException {
        final OnHeapStore<String, String> store = newStore();
        final CountDownLatch testCompletionLatch = new CountDownLatch(1);
        final CountDownLatch threadFaultCompletionLatch = new CountDownLatch(1);
        CachingTier.InvalidationListener<String, String> invalidationListener = ( key, valueHolder) -> {
            try {
                valueHolder.getId();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Test tried to invalidate Fault");
            }
        };
        store.setInvalidationListener(invalidationListener);
        updateStoreCapacity(store, 1);
        Thread thread = new Thread(() -> {
            try {
                store.getOrComputeIfAbsent("1", ( s) -> {
                    threadFaultCompletionLatch.countDown();
                    try {
                        testCompletionLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
            } catch (StoreAccessException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        threadFaultCompletionLatch.await();
        store.getOrComputeIfAbsent("10", ( s) -> null);
        testCompletionLatch.countDown();
    }

    @Test
    public void testGetOrComputeIfAbsentContention() throws InterruptedException {
        final OnHeapStore<String, String> store = newStore();
        int threads = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threads);
        Runnable runnable = () -> {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                Assert.fail(("Got an exception waiting to start thread " + e));
            }
            try {
                Store.ValueHolder<String> result = store.getOrComputeIfAbsent("42", ( key) -> new CopiedOnHeapValueHolder<>("theAnswer!", System.currentTimeMillis(), (-1), false, new IdentityCopier<>()));
                MatcherAssert.assertThat(result.get(), Matchers.is("theAnswer!"));
                endLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(("Got an exception " + e));
            }
        };
        for (int i = 0; i < threads; i++) {
            new Thread(runnable).start();
        }
        startLatch.countDown();
        boolean result = endLatch.await(2, TimeUnit.SECONDS);
        if (!result) {
            Assert.fail("Wait expired before completion, logs should have exceptions");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConcurrentFaultingAndInvalidate() throws Exception {
        final OnHeapStore<String, String> store = newStore();
        CachingTier.InvalidationListener<String, String> invalidationListener = Mockito.mock(CachingTier.InvalidationListener.class);
        store.setInvalidationListener(invalidationListener);
        final AtomicReference<AssertionError> failedInThread = new AtomicReference<>();
        final CountDownLatch getLatch = new CountDownLatch(1);
        final CountDownLatch invalidateLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                store.getOrComputeIfAbsent("42", ( aString) -> {
                    invalidateLatch.countDown();
                    try {
                        boolean await = getLatch.await(5, TimeUnit.SECONDS);
                        if (!await) {
                            failedInThread.set(new AssertionError("latch timed out"));
                        }
                    } catch (InterruptedException e) {
                        failedInThread.set(new AssertionError(("Interrupted exception: " + (e.getMessage()))));
                    }
                    return new CopiedOnHeapValueHolder<>("TheAnswer!", System.currentTimeMillis(), false, new IdentityCopier<>());
                });
            } catch (StoreAccessException caex) {
                failedInThread.set(new AssertionError(("StoreAccessException: " + (caex.getMessage()))));
            }
        }).start();
        boolean await = invalidateLatch.await(5, TimeUnit.SECONDS);
        if (!await) {
            Assert.fail("latch timed out");
        }
        store.invalidate("42");
        getLatch.countDown();
        Mockito.verify(invalidationListener, Mockito.never()).onInvalidation(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Store.ValueHolder.class));
        if ((failedInThread.get()) != null) {
            throw failedInThread.get();
        }
    }

    @Test
    public void testConcurrentSilentFaultingAndInvalidate() throws Exception {
        final OnHeapStore<String, String> store = newStore();
        final AtomicReference<AssertionError> failedInThread = new AtomicReference<>();
        final CountDownLatch getLatch = new CountDownLatch(1);
        final CountDownLatch invalidateLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                store.getOrComputeIfAbsent("42", ( aString) -> {
                    invalidateLatch.countDown();
                    try {
                        boolean await = getLatch.await(5, TimeUnit.SECONDS);
                        if (!await) {
                            failedInThread.set(new AssertionError("latch timed out"));
                        }
                    } catch (InterruptedException e) {
                        failedInThread.set(new AssertionError(("Interrupted exception: " + (e.getMessage()))));
                    }
                    return new CopiedOnHeapValueHolder<>("TheAnswer!", System.currentTimeMillis(), false, new IdentityCopier<>());
                });
            } catch (StoreAccessException caex) {
                failedInThread.set(new AssertionError(("StoreAccessException: " + (caex.getMessage()))));
            }
        }).start();
        boolean await = invalidateLatch.await(5, TimeUnit.SECONDS);
        if (!await) {
            Assert.fail("latch timed out");
        }
        store.silentInvalidate("42", ( stringValueHolder) -> {
            if (stringValueHolder != null) {
                MatcherAssert.assertThat("Expected a null parameter otherwise we leak a Fault", stringValueHolder, Matchers.nullValue());
            }
            return null;
        });
        getLatch.countDown();
        if ((failedInThread.get()) != null) {
            throw failedInThread.get();
        }
    }

    @Test(timeout = 2000L)
    public void testEvictionDoneUnderEvictedKeyLockScope() throws Exception {
        final OnHeapStore<String, String> store = newStore();
        updateStoreCapacity(store, 2);
        // Fill in store at capacity
        store.put("keyA", "valueA");
        store.put("keyB", "valueB");
        final Exchanger<String> keyExchanger = new Exchanger<>();
        final AtomicReference<String> reference = new AtomicReference<>();
        final CountDownLatch faultingLatch = new CountDownLatch(1);
        // prepare concurrent faulting
        final Thread thread = new Thread(() -> {
            String key;
            try {
                key = keyExchanger.exchange("ready to roll!");
                store.put(key, "updateValue");
            } catch (InterruptedException | StoreAccessException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        store.setInvalidationListener(( key, valueHolder) -> {
            // Only want to exchange on the first invalidation!
            if (reference.compareAndSet(null, key)) {
                try {
                    keyExchanger.exchange(key);
                    long now = System.nanoTime();
                    while (!(thread.getState().equals(BLOCKED))) {
                        Thread.yield();
                    } 
                    MatcherAssert.assertThat(thread.getState(), Matchers.is(BLOCKED));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // trigger eviction
        store.put("keyC", "valueC");
    }

    @Test(timeout = 2000L)
    public void testIteratorExpiryHappensUnderExpiredKeyLockScope() throws Exception {
        BaseOnHeapStoreTest.TestTimeSource testTimeSource = new BaseOnHeapStoreTest.TestTimeSource();
        final OnHeapStore<String, String> store = newStore(testTimeSource, ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(10)));
        store.put("key", "value");
        final CountDownLatch expiryLatch = new CountDownLatch(1);
        final Thread thread = new Thread(() -> {
            try {
                expiryLatch.await();
                store.put("key", "newValue");
            } catch (InterruptedException | StoreAccessException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        store.setInvalidationListener(( key, valueHolder) -> {
            expiryLatch.countDown();
            while (!(thread.getState().equals(BLOCKED))) {
                Thread.yield();
            } 
            MatcherAssert.assertThat(thread.getState(), Matchers.is(BLOCKED));
        });
        testTimeSource.advanceTime(20);
        store.iterator();
    }

    private static class TestTimeSource implements TimeSource {
        private long time = 0;

        @Override
        public long getTimeMillis() {
            return time;
        }

        private void advanceTime(long delta) {
            this.time += delta;
        }
    }
}

