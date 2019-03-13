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
package org.ehcache.core;


import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.statistics.BulkOps;
import org.ehcache.core.statistics.CacheOperationOutcomes;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.ehcache.core.statistics.CacheOperationOutcomes.PutAllOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.PutAllOutcome.SUCCESS;


/**
 * Provides testing of basic PUT_ALL operations on an {@code Ehcache}.
 *
 * @author Clifford W. Johnson
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class EhcacheBasicPutAllTest extends EhcacheBasicCrudBase {
    /**
     * A Mockito {@code ArgumentCaptor} for the {@code Set} argument to the
     * {@link Store#bulkCompute(Set, Function, java.util.function.Supplier)
     *    Store.bulkCompute(Set, Function, NullaryFunction} method.
     */
    @Captor
    private ArgumentCaptor<Set<String>> bulkComputeSetCaptor;

    @Test
    public void testPutAllNull() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.putAll(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
    }

    @Test
    public void testPutAllNullKey() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Map<String, String> entries = new LinkedHashMap<>();
        for (final Map.Entry<String, String> entry : EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A).entrySet()) {
            final String key = entry.getKey();
            entries.put(key, entry.getValue());
            if ("keyA2".equals(key)) {
                entries.put(null, "nullKey");
            }
        }
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.putAll(entries);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
    }

    @Test
    public void testPutAllNullValue() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Map<String, String> entries = new LinkedHashMap<>();
        for (final Map.Entry<String, String> entry : EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A).entrySet()) {
            final String key = entry.getKey();
            entries.put(key, entry.getValue());
            if ("keyA2".equals(key)) {
                entries.put("keyA2a", null);
            }
        }
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.putAll(entries);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
    }

    /**
     * Tests {@link Ehcache#putAll(Map)} for
     * <ul>
     *    <li>empty request map</li>
     *    <li>populated {@code Store} (keys not relevant)</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testPutAllEmptyRequestNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.putAll(Collections.<String, String>emptyMap());
        Mockito.verify(this.store, Mockito.never()).bulkCompute(ArgumentMatchers.eq(Collections.<String>emptySet()), EhcacheBasicPutAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
        Mockito.verify(this.resilienceStrategy, Mockito.never()).putAllFailure(ArgumentMatchers.eq(Collections.<String, String>emptyMap()), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.PutOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.PUT_ALL).intValue(), Matchers.is(0));
    }

    /**
     * Tests {@link Ehcache#putAll(Map)} for
     * <ul>
     *    <li>non-empty request map</li>
     *    <li>populated {@code Store} - some keys overlap request</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testPutAllStoreSomeOverlapNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Map<String, String> contentUpdates = EhcacheBasicBulkUtil.getAltEntryMap("new_", EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C));
        ehcache.putAll(contentUpdates);
        Mockito.verify(this.store, Mockito.atLeast(1)).bulkCompute(this.bulkComputeSetCaptor.capture(), EhcacheBasicPutAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(this.getBulkComputeArgs(), Matchers.equalTo(contentUpdates.keySet()));
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(EhcacheBasicBulkUtil.union(originalStoreContent, contentUpdates)));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.PutOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.PUT_ALL).intValue(), Matchers.is(contentUpdates.size()));
    }

    /**
     * Tests {@link Ehcache#putAll(Map)} for
     * <ul>
     *    <li>non-empty request map</li>
     *    <li>populated {@code Store} - some keys overlap request</li>
     *    <li>{@link Store#bulkCompute} throws before accessing writer</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testPutAllStoreSomeOverlapStoreAccessExceptionBeforeNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).bulkCompute(EhcacheBasicPutAllTest.getAnyStringSet(), EhcacheBasicPutAllTest.getAnyEntryIterableFunction());
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Map<String, String> contentUpdates = EhcacheBasicBulkUtil.getAltEntryMap("new_", EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C));
        ehcache.putAll(contentUpdates);
        final InOrder ordered = Mockito.inOrder(this.store, this.resilienceStrategy);
        ordered.verify(this.store, Mockito.atLeast(1)).bulkCompute(this.bulkComputeSetCaptor.capture(), EhcacheBasicPutAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(this.getBulkComputeArgs(), Matchers.everyItem(Matchers.isIn(contentUpdates.keySet())));
        // ResilienceStrategy invoked; no assertions about Store content
        ordered.verify(this.resilienceStrategy).putAllFailure(ArgumentMatchers.eq(contentUpdates), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.PutOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.PUT_ALL).intValue(), Matchers.is(0));
    }

    /**
     * Tests {@link Ehcache#putAll(Map)} for
     * <ul>
     *    <li>non-empty request map</li>
     *    <li>populated {@code Store} - some keys overlap request</li>
     *    <li>{@link Store#bulkCompute} throws after accessing writer</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testPutAllStoreSomeOverlapStoreAccessExceptionAfterNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent, Collections.singleton("keyA3"));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Map<String, String> contentUpdates = EhcacheBasicBulkUtil.getAltEntryMap("new_", EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C));
        ehcache.putAll(contentUpdates);
        final InOrder ordered = Mockito.inOrder(this.store, this.resilienceStrategy);
        ordered.verify(this.store, Mockito.atLeast(1)).bulkCompute(this.bulkComputeSetCaptor.capture(), EhcacheBasicPutAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(this.getBulkComputeArgs(), Matchers.everyItem(Matchers.isIn(contentUpdates.keySet())));
        // ResilienceStrategy invoked; no assertions about Store content
        ordered.verify(this.resilienceStrategy).putAllFailure(ArgumentMatchers.eq(contentUpdates), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.PutOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.PUT_ALL).intValue(), Matchers.is(0));
    }

    /**
     * Indicates whether or not {@link #dumpResults} should emit output.
     */
    private static final boolean debugResults;

    static {
        debugResults = Boolean.parseBoolean(System.getProperty(((EhcacheBasicPutAllTest.class.getName()) + ".debug"), "false"));
    }

    @Rule
    public TestName name = new TestName();
}

