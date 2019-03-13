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
package org.ehcache.transactions.xa.internal;


import Map.Entry;
import Status.STATUS_COMMITTED;
import Status.STATUS_ROLLEDBACK;
import XAStore.Provider;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.ehcache.Cache;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.events.StoreEventDispatcher;
import org.ehcache.core.spi.ServiceLocator;
import org.ehcache.core.spi.service.DiskResourceService;
import org.ehcache.core.spi.store.Store;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.internal.sizeof.NoopSizeOfEngine;
import org.ehcache.impl.internal.store.heap.OnHeapStore;
import org.ehcache.impl.internal.store.offheap.MemorySizeParser;
import org.ehcache.impl.internal.store.offheap.OffHeapStore;
import org.ehcache.impl.internal.store.offheap.OffHeapStoreLifecycleHelper;
import org.ehcache.impl.internal.store.tiering.TieredStore;
import org.ehcache.internal.TestTimeSource;
import org.ehcache.spi.copy.Copier;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.spi.service.ServiceProvider;
import org.ehcache.transactions.xa.XACacheException;
import org.ehcache.transactions.xa.configuration.XAStoreConfiguration;
import org.ehcache.transactions.xa.internal.journal.Journal;
import org.ehcache.transactions.xa.txmgr.TransactionManagerWrapper;
import org.ehcache.transactions.xa.txmgr.provider.TransactionManagerProvider;
import org.ehcache.transactions.xa.utils.TestXid;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;

import static org.ehcache.core.spi.store.Store.PutStatus.NOOP;
import static org.ehcache.core.spi.store.Store.PutStatus.PUT;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_MISSING;
import static org.ehcache.core.spi.store.Store.RemoveStatus.KEY_PRESENT;
import static org.ehcache.core.spi.store.Store.RemoveStatus.REMOVED;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.HIT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_NOT_PRESENT;
import static org.ehcache.core.spi.store.Store.ReplaceStatus.MISS_PRESENT;


/**
 * Tests for {@link XAStore} and {@link org.ehcache.transactions.xa.internal.XAStore.Provider}.
 */
public class XAStoreTest {
    private static final Supplier<Boolean> SUPPLY_TRUE = () -> true;

    private static final Supplier<Boolean> SUPPLY_FALSE = () -> false;

    @Rule
    public TestName testName = new TestName();

    @SuppressWarnings("unchecked")
    private final Class<SoftLock<String>> valueClass = ((Class) (SoftLock.class));

    private final XAStoreTest.TestTransactionManager testTransactionManager = new XAStoreTest.TestTransactionManager();

    private TransactionManagerWrapper transactionManagerWrapper;

    private OnHeapStore<Long, SoftLock<String>> onHeapStore;

    private Journal<Long> journal;

    private TestTimeSource testTimeSource;

    private ClassLoader classLoader;

    private Serializer<Long> keySerializer;

    private Serializer<SoftLock<String>> valueSerializer;

    private StoreEventDispatcher<Long, SoftLock<String>> eventDispatcher;

    private final ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(1));

    private Copier<Long> keyCopier;

    private Copier<SoftLock<String>> valueCopier;

    @Test
    public void testXAStoreProviderFailsToRankWhenNoTMProviderConfigured() throws Exception {
        XAStore.Provider provider = new XAStore.Provider();
        provider.start(new ServiceProvider<Service>() {
            @Override
            public <U extends Service> U getService(Class<U> serviceType) {
                return null;
            }

            @Override
            public <U extends Service> Collection<U> getServicesOfType(Class<U> serviceType) {
                return Collections.emptySet();
            }
        });
        try {
            provider.wrapperStoreRank(Collections.singleton(Mockito.mock(XAStoreConfiguration.class)));
            Assert.fail("Expected exception");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("TransactionManagerProvider"));
        }
    }

    @Test
    public void testSimpleGetPutRemove() throws Exception {
        XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(false));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.put(1L, "1"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
        }
        testTransactionManager.rollback();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.put(1L, "1"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(true));
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(false));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.put(1L, "1"), Matchers.equalTo(PUT));
        }
        testTransactionManager.rollback();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "un"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(true));
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(false));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.put(1L, "un"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("un"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "un");
    }

    @Test
    public void testConflictingGetPutRemove() throws Exception {
        final XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        testTransactionManager.begin();
        {
            xaStore.put(1L, "one");
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "un"), Matchers.equalTo(PUT));
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.put(1L, "uno"), Matchers.equalTo(NOOP));
                testTransactionManager.commit();
                return null;
            });
            Assert.assertThat(xaStore.put(1L, "eins"), Matchers.equalTo(PUT));
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "one");
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "un"), Matchers.equalTo(PUT));
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.remove(1L), Matchers.is(false));
                testTransactionManager.commit();
                return null;
            });
            Assert.assertThat(xaStore.put(1L, "een"), Matchers.equalTo(PUT));
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "one");
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "un"), Matchers.equalTo(PUT));
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
                testTransactionManager.commit();
                return null;
            });
            Assert.assertThat(xaStore.put(1L, "yksi"), Matchers.equalTo(PUT));
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testIterate() throws Exception {
        XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "one");
            xaStore.put(2L, "two");
            xaStore.put(3L, "three");
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            xaStore.put(0L, "zero");
            xaStore.put(1L, "un");
            xaStore.put(2L, "two");
            xaStore.remove(3L);
            Map<Long, String> iterated = new HashMap<>();
            Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = xaStore.iterator();
            while (iterator.hasNext()) {
                Cache.Entry<Long, Store.ValueHolder<String>> next = iterator.next();
                iterated.put(next.getKey(), next.getValue().get());
            } 
            Assert.assertThat(iterated.size(), Matchers.is(3));
            Assert.assertThat(iterated.get(0L), Matchers.equalTo("zero"));
            Assert.assertThat(iterated.get(1L), Matchers.equalTo("un"));
            Assert.assertThat(iterated.get(2L), Matchers.equalTo("two"));
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            Map<Long, String> iterated = new HashMap<>();
            Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator = xaStore.iterator();
            while (iterator.hasNext()) {
                Cache.Entry<Long, Store.ValueHolder<String>> next = iterator.next();
                iterated.put(next.getKey(), next.getValue().get());
            } 
            Assert.assertThat(iterated.size(), Matchers.is(3));
            Assert.assertThat(iterated.get(0L), Matchers.equalTo("zero"));
            Assert.assertThat(iterated.get(1L), Matchers.equalTo("un"));
            Assert.assertThat(iterated.get(2L), Matchers.equalTo("two"));
        }
        testTransactionManager.commit();
        Store.Iterator<Cache.Entry<Long, Store.ValueHolder<String>>> iterator;
        testTransactionManager.begin();
        {
            iterator = xaStore.iterator();
            iterator.next();
        }
        testTransactionManager.commit();
        // cannot use iterator outside of tx context
        try {
            iterator.hasNext();
            Assert.fail();
        } catch (XACacheException e) {
            // expected
        }
        try {
            iterator.next();
            Assert.fail();
        } catch (XACacheException e) {
            // expected
        }
        // cannot use iterator outside of original tx context
        testTransactionManager.begin();
        {
            try {
                iterator.hasNext();
                Assert.fail();
            } catch (IllegalStateException e) {
                // expected
            }
            try {
                iterator.next();
                Assert.fail();
            } catch (IllegalStateException e) {
                // expected
            }
        }
        testTransactionManager.commit();
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        final XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.putIfAbsent(1L, "one", ( b) -> {
            }), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.putIfAbsent(1L, "un", ( b) -> {
            }).get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.putIfAbsent(1L, "un", ( b) -> {
            }).get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(true));
            Assert.assertThat(xaStore.putIfAbsent(1L, "uno", ( b) -> {
            }), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.putIfAbsent(1L, "un", ( b) -> {
                }), Matchers.is(Matchers.nullValue()));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testRemove2Args() throws Exception {
        final XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.remove(1L, "one"), Matchers.equalTo(KEY_MISSING));
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.remove(1L, "un"), Matchers.equalTo(KEY_PRESENT));
            Assert.assertThat(xaStore.remove(1L, "one"), Matchers.equalTo(REMOVED));
            Assert.assertThat(xaStore.remove(1L, "eins"), Matchers.equalTo(KEY_MISSING));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.remove(1L, "een"), Matchers.equalTo(KEY_PRESENT));
            Assert.assertThat(xaStore.remove(1L, "one"), Matchers.equalTo(REMOVED));
            Assert.assertThat(xaStore.remove(1L, "eins"), Matchers.equalTo(KEY_MISSING));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.remove(1L, "un"), Matchers.equalTo(KEY_MISSING));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.remove(1L, "un"), Matchers.equalTo(KEY_MISSING));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testReplace2Args() throws Exception {
        final XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.replace(1L, "one"), Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.replace(1L, "un").get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.replace(1L, "uno").get(), Matchers.equalTo("un"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.replace(1L, "een").get(), Matchers.equalTo("uno"));
            Assert.assertThat(xaStore.replace(1L, "eins").get(), Matchers.equalTo("een"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "eins");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.remove(1L), Matchers.is(true));
            Assert.assertThat(xaStore.replace(1L, "yksi"), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.replace(1L, "un"), Matchers.is(Matchers.nullValue()));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.is(PUT));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.replace(1L, "un"), Matchers.is(Matchers.nullValue()));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testReplace3Args() throws Exception {
        final XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        final AtomicReference<Throwable> exception = new AtomicReference<>();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.replace(1L, "one", "un"), Matchers.equalTo(MISS_NOT_PRESENT));
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.equalTo(PUT));
            Assert.assertThat(xaStore.replace(1L, "eins", "un"), Matchers.equalTo(MISS_PRESENT));
            Assert.assertThat(xaStore.replace(1L, "one", "un"), Matchers.equalTo(HIT));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("un"));
            Assert.assertThat(xaStore.replace(1L, "eins", "een"), Matchers.equalTo(MISS_PRESENT));
            Assert.assertThat(xaStore.replace(1L, "un", "uno"), Matchers.equalTo(HIT));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("uno"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.replace(1L, "one", "uno"), Matchers.equalTo(MISS_PRESENT));
            Assert.assertThat(xaStore.replace(1L, "uno", "un"), Matchers.equalTo(HIT));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("un"));
            Assert.assertThat(xaStore.remove(1L), Matchers.equalTo(true));
            Assert.assertThat(xaStore.replace(1L, "un", "eins"), Matchers.equalTo(MISS_NOT_PRESENT));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.replace(1L, "eins", "one"), Matchers.is(MISS_NOT_PRESENT));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.put(1L, "one"), Matchers.is(PUT));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            xaStore.put(1L, "eins");
            executeWhileIn2PC(exception, () -> {
                testTransactionManager.begin();
                Assert.assertThat(xaStore.replace(1L, "one", "un"), Matchers.is(MISS_NOT_PRESENT));
                testTransactionManager.commit();
                return null;
            });
        }
        testTransactionManager.commit();
        Assert.assertThat(exception.get(), Matchers.is(Matchers.nullValue()));
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testGetAndCompute() throws Exception {
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            });
            Assert.assertThat(computed1, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return "un";
            });
            Assert.assertThat(computed2.get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("un"));
            Store.ValueHolder<String> computed3 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("un"));
                return null;
            });
            Assert.assertThat(computed3.get(), Matchers.equalTo("un"));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            });
            Assert.assertThat(computed1, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return null;
            });
            Assert.assertThat(computed2.get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            });
            Assert.assertThat(computed1, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return null;
            });
            Assert.assertThat(computed2.get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            });
            Assert.assertThat(computed1, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return "un";
            });
            Assert.assertThat(computed2.get(), Matchers.equalTo("one"));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("un"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "un");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("un"));
                return "eins";
            });
            Assert.assertThat(computed.get(), Matchers.equalTo("un"));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("eins"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "eins");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("eins"));
                return null;
            });
            Assert.assertThat(computed.get(), Matchers.equalTo("eins"));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.rollback();
        assertMapping(xaStore, 1L, "eins");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("eins"));
                return null;
            });
            Assert.assertThat(computed1.get(), Matchers.equalTo("eins"));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
            Store.ValueHolder<String> computed2 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return null;
            });
            Assert.assertThat(computed2, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L), Matchers.is(Matchers.nullValue()));
            Store.ValueHolder<String> computed3 = xaStore.getAndCompute(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "uno";
            });
            Assert.assertThat(computed3, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(xaStore.get(1L).get(), Matchers.equalTo("uno"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            xaStore.remove(1L);
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(false));
            xaStore.put(1L, "uno");
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(true));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(true));
            xaStore.remove(1L);
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(false));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        OffHeapStoreLifecycleHelper.close(offHeapStore);
    }

    @Test
    public void testCompute() throws Exception {
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed1.get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return "un";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed2.get(), Matchers.equalTo("un"));
            Store.ValueHolder<String> computed3 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("un"));
                return null;
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed3, Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            }, XAStoreTest.SUPPLY_FALSE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed1.get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return null;
            }, XAStoreTest.SUPPLY_FALSE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed2, Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed1.get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return null;
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed2, Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "one";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed1.get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("one"));
                return "un";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed2.get(), Matchers.equalTo("un"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "un");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("un"));
                return "eins";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed.get(), Matchers.equalTo("eins"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "eins");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("eins"));
                return null;
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed, Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.rollback();
        assertMapping(xaStore, 1L, "eins");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, equalTo("eins"));
                return null;
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed1, Matchers.is(Matchers.nullValue()));
            Store.ValueHolder<String> computed2 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return null;
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed2, Matchers.is(Matchers.nullValue()));
            Store.ValueHolder<String> computed3 = xaStore.computeAndGet(1L, ( aLong, s) -> {
                assertThat(aLong, is(1L));
                assertThat(s, is(nullValue()));
                return "uno";
            }, XAStoreTest.SUPPLY_TRUE, XAStoreTest.SUPPLY_FALSE);
            Assert.assertThat(computed3.get(), Matchers.equalTo("uno"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            xaStore.remove(1L);
        }
        testTransactionManager.commit();
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(false));
            xaStore.put(1L, "uno");
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(true));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "uno");
        testTransactionManager.begin();
        {
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(true));
            xaStore.remove(1L);
            Assert.assertThat(xaStore.containsKey(1L), Matchers.is(false));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
        OffHeapStoreLifecycleHelper.close(offHeapStore);
    }

    @Test
    public void testComputeIfAbsent() throws Exception {
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeIfAbsent(1L, ( aLong) -> {
                assertThat(aLong, is(1L));
                return "one";
            });
            Assert.assertThat(computed1.get(), Matchers.equalTo("one"));
            Store.ValueHolder<String> computed2 = xaStore.computeIfAbsent(1L, ( aLong) -> {
                fail("should not be absent");
                throw new AssertionError();
            });
            Assert.assertThat(computed2.get(), Matchers.equalTo("one"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTransactionManager.begin();
        {
            Store.ValueHolder<String> computed1 = xaStore.computeIfAbsent(1L, ( aLong) -> {
                fail("should not be absent");
                throw new AssertionError();
            });
            Assert.assertThat(computed1.get(), Matchers.equalTo("one"));
            xaStore.remove(1L);
            Store.ValueHolder<String> computed2 = xaStore.computeIfAbsent(1L, ( aLong) -> {
                assertThat(aLong, is(1L));
                return "un";
            });
            Assert.assertThat(computed2.get(), Matchers.equalTo("un"));
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "un");
        OffHeapStoreLifecycleHelper.close(offHeapStore);
    }

    @Test
    public void testExpiry() throws Exception {
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "one");
        }
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, "one");
        testTimeSource.advanceTime(2000);
        assertMapping(xaStore, 1L, null);
        OffHeapStoreLifecycleHelper.close(offHeapStore);
    }

    @Test
    public void testExpiryCreateException() throws Exception {
        ExpiryPolicy<Object, Object> expiry = new ExpiryPolicy<Object, Object>() {
            @Override
            public Duration getExpiryForCreation(Object key, Object value) {
                throw new RuntimeException();
            }

            @Override
            public Duration getExpiryForAccess(Object key, Supplier<?> value) {
                throw new AssertionError();
            }

            @Override
            public Duration getExpiryForUpdate(Object key, Supplier<?> oldValue, Object newValue) {
                throw new AssertionError();
            }
        };
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        OnHeapStore<Long, SoftLock<String>> onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        xaStore.put(1L, "one");
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testExpiryAccessException() throws Exception {
        String uniqueXAResourceId = "testExpiryAccessException";
        ExpiryPolicy<Object, Object> expiry = new ExpiryPolicy<Object, Object>() {
            @Override
            public Duration getExpiryForCreation(Object key, Object value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForAccess(Object key, Supplier<?> value) {
                if ((testTimeSource.getTimeMillis()) > 0) {
                    throw new RuntimeException();
                }
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForUpdate(Object key, Supplier<?> oldValue, Object newValue) {
                return ExpiryPolicy.INFINITE;
            }
        };
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        OnHeapStore<Long, SoftLock<String>> onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        xaStore.put(1L, "one");
        testTransactionManager.commit();
        testTimeSource.advanceTime(1000);
        testTransactionManager.begin();
        Assert.assertThat(xaStore.get(1L).get(), Matchers.is("one"));
        testTransactionManager.commit();
        testTransactionManager.begin();
        Assert.assertThat(xaStore.get(1L), Matchers.nullValue());
        testTransactionManager.commit();
    }

    @Test
    public void testExpiryUpdateException() throws Exception {
        ExpiryPolicy<Object, Object> expiry = new ExpiryPolicy<Object, Object>() {
            @Override
            public Duration getExpiryForCreation(Object key, Object value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForAccess(Object key, Supplier<?> value) {
                return ExpiryPolicy.INFINITE;
            }

            @Override
            public Duration getExpiryForUpdate(Object key, Supplier<?> oldValue, Object newValue) {
                if ((testTimeSource.getTimeMillis()) > 0) {
                    throw new RuntimeException();
                }
                return ExpiryPolicy.INFINITE;
            }
        };
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        OnHeapStore<Long, SoftLock<String>> onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        xaStore.put(1L, "one");
        xaStore.get(1L);
        testTransactionManager.commit();
        testTimeSource.advanceTime(1000);
        testTransactionManager.begin();
        xaStore.put(1L, "two");
        testTransactionManager.commit();
        assertMapping(xaStore, 1L, null);
    }

    @Test
    public void testBulkCompute() throws Exception {
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(1));
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        OnHeapStore<Long, SoftLock<String>> onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        {
            Map<Long, Store.ValueHolder<String>> computedMap = xaStore.bulkCompute(asSet(1L, 2L, 3L), ( entries) -> {
                Map<Long, String> result = new HashMap<>();
                for (Entry<? extends Long, ? extends String> entry : entries) {
                    Long key = entry.getKey();
                    String value = entry.getValue();
                    assertThat(value, is(nullValue()));
                    result.put(key, ("stuff#" + key));
                }
                return result.entrySet();
            });
            Assert.assertThat(computedMap.size(), Matchers.is(3));
            Assert.assertThat(computedMap.get(1L).get(), Matchers.equalTo("stuff#1"));
            Assert.assertThat(computedMap.get(2L).get(), Matchers.equalTo("stuff#2"));
            Assert.assertThat(computedMap.get(3L).get(), Matchers.equalTo("stuff#3"));
            computedMap = xaStore.bulkCompute(asSet(0L, 1L, 3L), ( entries) -> {
                Map<Long, String> result = new HashMap<>();
                for (Entry<? extends Long, ? extends String> entry : entries) {
                    Long key = entry.getKey();
                    String value = entry.getValue();
                    switch (key.intValue()) {
                        case 0 :
                            assertThat(value, is(nullValue()));
                            break;
                        case 1 :
                        case 3 :
                            assertThat(value, equalTo(("stuff#" + key)));
                            break;
                    }
                    if (key != 3L) {
                        result.put(key, ("otherStuff#" + key));
                    } else {
                        result.put(key, null);
                    }
                }
                return result.entrySet();
            });
            Assert.assertThat(computedMap.size(), Matchers.is(3));
            Assert.assertThat(computedMap.get(0L).get(), Matchers.equalTo("otherStuff#0"));
            Assert.assertThat(computedMap.get(1L).get(), Matchers.equalTo("otherStuff#1"));
            Assert.assertThat(computedMap.get(3L), Matchers.is(Matchers.nullValue()));
        }
        testTransactionManager.commit();
        assertSize(xaStore, 3);
        assertMapping(xaStore, 0L, "otherStuff#0");
        assertMapping(xaStore, 1L, "otherStuff#1");
        assertMapping(xaStore, 2L, "stuff#2");
        OffHeapStoreLifecycleHelper.close(offHeapStore);
    }

    @Test
    public void testBulkComputeIfAbsent() throws Exception {
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(1));
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        OnHeapStore<Long, SoftLock<String>> onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        Store.Configuration<Long, SoftLock<String>> offHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, null, classLoader, expiry, ResourcePoolsBuilder.newResourcePoolsBuilder().offheap(10, MemoryUnit.MB).build(), 0, keySerializer, valueSerializer);
        OffHeapStore<Long, SoftLock<String>> offHeapStore = new OffHeapStore(offHeapConfig, testTimeSource, eventDispatcher, MemorySizeParser.parse("10M"));
        OffHeapStoreLifecycleHelper.init(offHeapStore);
        TieredStore<Long, SoftLock<String>> tieredStore = new TieredStore(onHeapStore, offHeapStore);
        XAStore<Long, String> xaStore = getXAStore(tieredStore);
        testTransactionManager.begin();
        {
            Map<Long, Store.ValueHolder<String>> computedMap = xaStore.bulkComputeIfAbsent(asSet(1L, 2L, 3L), ( keys) -> {
                Map<Long, String> result = new HashMap<>();
                for (Long key : keys) {
                    result.put(key, ("stuff#" + key));
                }
                return result.entrySet();
            });
            Assert.assertThat(computedMap.size(), Matchers.is(3));
            Assert.assertThat(computedMap.get(1L).get(), Matchers.equalTo("stuff#1"));
            Assert.assertThat(computedMap.get(2L).get(), Matchers.equalTo("stuff#2"));
            Assert.assertThat(computedMap.get(3L).get(), Matchers.equalTo("stuff#3"));
            computedMap = xaStore.bulkComputeIfAbsent(asSet(0L, 1L, 3L), ( keys) -> {
                Map<Long, String> result = new HashMap<>();
                for (Long key : keys) {
                    switch (key.intValue()) {
                        case 0 :
                            result.put(key, ("otherStuff#" + key));
                            break;
                        case 1 :
                        case 3 :
                            fail((("key " + key) + " should not be absent"));
                            break;
                    }
                }
                return result.entrySet();
            });
            Assert.assertThat(computedMap.size(), Matchers.is(3));
            Assert.assertThat(computedMap.get(0L).get(), Matchers.equalTo("otherStuff#0"));
            Assert.assertThat(computedMap.get(1L).get(), Matchers.equalTo("stuff#1"));
            Assert.assertThat(computedMap.get(3L).get(), Matchers.equalTo("stuff#3"));
        }
        testTransactionManager.commit();
        assertSize(xaStore, 4);
        assertMapping(xaStore, 0L, "otherStuff#0");
        assertMapping(xaStore, 1L, "stuff#1");
        assertMapping(xaStore, 2L, "stuff#2");
        assertMapping(xaStore, 3L, "stuff#3");
        OffHeapStoreLifecycleHelper.close(offHeapStore);
    }

    @Test
    public void testCustomEvictionAdvisor() throws Exception {
        final AtomicBoolean invoked = new AtomicBoolean();
        EvictionAdvisor<Long, SoftLock<String>> evictionAdvisor = ( key, value) -> {
            invoked.set(true);
            return false;
        };
        Store.Configuration<Long, SoftLock<String>> onHeapConfig = new org.ehcache.core.store.StoreConfigurationImpl(Long.class, valueClass, evictionAdvisor, classLoader, ExpiryPolicyBuilder.noExpiration(), ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).build(), 0, keySerializer, valueSerializer);
        OnHeapStore<Long, SoftLock<String>> onHeapStore = new OnHeapStore(onHeapConfig, testTimeSource, keyCopier, valueCopier, new NoopSizeOfEngine(), eventDispatcher);
        final XAStore<Long, String> xaStore = getXAStore(onHeapStore);
        testTransactionManager.begin();
        {
            xaStore.put(1L, "1");
        }
        testTransactionManager.rollback();
        Assert.assertThat(invoked.get(), Matchers.is(false));
        testTransactionManager.begin();
        {
            xaStore.put(1L, "1");
        }
        testTransactionManager.commit();
        Assert.assertThat(invoked.get(), Matchers.is(true));
    }

    @Test
    public void testRank() throws Exception {
        XAStore.Provider provider = new XAStore.Provider();
        XAStoreConfiguration configuration = new XAStoreConfiguration("testXAResourceId");
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(provider).with(Store.Provider.class).with(Mockito.mock(DiskResourceService.class)).with(Mockito.mock(TransactionManagerProvider.class)).build();
        serviceLocator.startAllServices();
        Set<ServiceConfiguration<?>> xaStoreConfigs = Collections.singleton(configuration);
        Assert.assertThat(provider.wrapperStoreRank(xaStoreConfigs), Matchers.is(1));
        Set<ServiceConfiguration<?>> emptyConfigs = Collections.emptySet();
        Assert.assertThat(provider.wrapperStoreRank(emptyConfigs), Matchers.is(0));
    }

    static class TestTransactionManager implements TransactionManager {
        volatile XAStoreTest.TestTransaction currentTransaction;

        final AtomicLong gtridGenerator = new AtomicLong();

        public XAStoreTest.TestTransaction getCurrentTransaction() {
            return currentTransaction;
        }

        @Override
        public void begin() {
            currentTransaction = new XAStoreTest.TestTransaction(gtridGenerator.incrementAndGet());
        }

        @Override
        public void commit() throws IllegalStateException, SecurityException, HeuristicMixedException, HeuristicRollbackException, RollbackException, SystemException {
            currentTransaction.commit();
            currentTransaction = null;
        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public Transaction getTransaction() {
            return currentTransaction;
        }

        @Override
        public void resume(Transaction tobj) {
        }

        @Override
        public void rollback() throws IllegalStateException, SecurityException, SystemException {
            currentTransaction.rollback();
            currentTransaction = null;
        }

        @Override
        public void setRollbackOnly() {
        }

        @Override
        public void setTransactionTimeout(int seconds) {
        }

        @Override
        public Transaction suspend() {
            return null;
        }
    }

    static class TestTransaction implements Transaction {
        final long gtrid;

        final Map<XAResource, TestXid> xids = new IdentityHashMap<>();

        final AtomicLong bqualGenerator = new AtomicLong();

        final List<Synchronization> synchronizations = new CopyOnWriteArrayList<>();

        final List<XAStoreTest.TwoPcListener> twoPcListeners = new CopyOnWriteArrayList<>();

        public TestTransaction(long gtrid) {
            this.gtrid = gtrid;
        }

        @Override
        public void commit() throws IllegalStateException, SecurityException, HeuristicMixedException, HeuristicRollbackException, RollbackException, SystemException {
            try {
                Set<Map.Entry<XAResource, TestXid>> entries = xids.entrySet();
                // delist
                for (Map.Entry<XAResource, TestXid> entry : entries) {
                    try {
                        entry.getKey().end(entry.getValue(), XAResource.TMSUCCESS);
                    } catch (XAException e) {
                        throw ((SystemException) (new SystemException(XAException.XAER_RMERR).initCause(e)));
                    }
                }
                fireBeforeCompletion();
                Set<XAResource> preparedResources = new HashSet<>();
                // prepare
                for (Map.Entry<XAResource, TestXid> entry : entries) {
                    try {
                        int prepareStatus = entry.getKey().prepare(entry.getValue());
                        if (prepareStatus != (XAResource.XA_RDONLY)) {
                            preparedResources.add(entry.getKey());
                        }
                    } catch (XAException e) {
                        throw ((SystemException) (new SystemException(XAException.XAER_RMERR).initCause(e)));
                    }
                }
                fireInMiddleOf2PC();
                // commit
                for (Map.Entry<XAResource, TestXid> entry : entries) {
                    try {
                        if (preparedResources.contains(entry.getKey())) {
                            entry.getKey().commit(entry.getValue(), false);
                        }
                    } catch (XAException e) {
                        throw ((SystemException) (new SystemException(XAException.XAER_RMERR).initCause(e)));
                    }
                }
            } finally {
                fireAfterCompletion(STATUS_COMMITTED);
            }
        }

        @Override
        public boolean delistResource(XAResource xaRes, int flag) {
            return true;
        }

        @Override
        public boolean enlistResource(XAResource xaRes) throws IllegalStateException, SystemException {
            TestXid testXid = xids.get(xaRes);
            if (testXid == null) {
                testXid = new TestXid(gtrid, bqualGenerator.incrementAndGet());
                xids.put(xaRes, testXid);
            }
            try {
                xaRes.start(testXid, XAResource.TMNOFLAGS);
            } catch (XAException e) {
                throw ((SystemException) (new SystemException(XAException.XAER_RMERR).initCause(e)));
            }
            return true;
        }

        @Override
        public int getStatus() {
            return 0;
        }

        public void registerTwoPcListener(XAStoreTest.TwoPcListener listener) {
            twoPcListeners.add(listener);
        }

        @Override
        public void registerSynchronization(Synchronization sync) {
            synchronizations.add(sync);
        }

        @Override
        public void rollback() throws IllegalStateException, SystemException {
            try {
                Set<Map.Entry<XAResource, TestXid>> entries = xids.entrySet();
                // delist
                for (Map.Entry<XAResource, TestXid> entry : entries) {
                    try {
                        entry.getKey().end(entry.getValue(), XAResource.TMSUCCESS);
                    } catch (XAException e) {
                        throw ((SystemException) (new SystemException(XAException.XAER_RMERR).initCause(e)));
                    }
                }
                // rollback
                for (Map.Entry<XAResource, TestXid> entry : entries) {
                    try {
                        entry.getKey().rollback(entry.getValue());
                    } catch (XAException e) {
                        throw ((SystemException) (new SystemException(XAException.XAER_RMERR).initCause(e)));
                    }
                }
            } finally {
                fireAfterCompletion(STATUS_ROLLEDBACK);
            }
        }

        private void fireBeforeCompletion() {
            for (Synchronization synchronization : synchronizations) {
                synchronization.beforeCompletion();
            }
        }

        private void fireAfterCompletion(int status) {
            for (Synchronization synchronization : synchronizations) {
                synchronization.afterCompletion(status);
            }
        }

        private void fireInMiddleOf2PC() {
            for (XAStoreTest.TwoPcListener twoPcListener : twoPcListeners) {
                twoPcListener.inMiddleOf2PC();
            }
        }

        @Override
        public void setRollbackOnly() {
        }
    }

    interface TwoPcListener {
        void inMiddleOf2PC();
    }
}

