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
import java.util.LinkedHashSet;
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

import static org.ehcache.core.statistics.CacheOperationOutcomes.RemoveAllOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.RemoveAllOutcome.SUCCESS;


/**
 * Provides testing of basic REMOVE_ALL operations on an {@code Ehcache}.
 *
 * @author Clifford W. Johnson
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class EhcacheBasicRemoveAllTest extends EhcacheBasicCrudBase {
    /**
     * A Mockito {@code ArgumentCaptor} for the {@code Set} argument to the
     * {@link Store#bulkCompute(Set, Function, java.util.function.Supplier)
     *    Store.bulkCompute(Set, Function, NullaryFunction} method.
     */
    @Captor
    protected ArgumentCaptor<Set<String>> bulkComputeSetCaptor;

    @Test
    public void testRemoveAllNull() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.removeAll(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
    }

    @Test
    public void testRemoveAllNullKey() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Set<String> keys = new LinkedHashSet<>();
        for (final String key : EhcacheBasicBulkUtil.KEY_SET_A) {
            keys.add(key);
            if ("keyA2".equals(key)) {
                keys.add(null);
            }
        }
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.removeAll(keys);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
    }

    /**
     * Tests {@link Ehcache#removeAll(Set)} for
     * <ul>
     *    <li>empty request set</li>
     *    <li>populated {@code Store} (keys not relevant)</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testRemoveAllEmptyRequestNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.removeAll(Collections.<String>emptySet());
        Mockito.verify(this.store, Mockito.never()).bulkCompute(ArgumentMatchers.eq(Collections.<String>emptySet()), EhcacheBasicRemoveAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(originalStoreContent));
        Mockito.verify(this.resilienceStrategy, Mockito.never()).removeAllFailure(ArgumentMatchers.eq(Collections.<String>emptySet()), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.RemoveOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.REMOVE_ALL).intValue(), Matchers.is(0));
    }

    /**
     * Tests {@link Ehcache#removeAll(Set)} for
     * <ul>
     *    <li>non-empty request set</li>
     *    <li>populated {@code Store} - some keys overlap request</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testRemoveAllStoreSomeOverlapNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> contentUpdates = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C);
        ehcache.removeAll(contentUpdates);
        Mockito.verify(this.store, Mockito.atLeast(1)).bulkCompute(this.bulkComputeSetCaptor.capture(), EhcacheBasicRemoveAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(this.getBulkComputeArgs(), Matchers.equalTo(contentUpdates));
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(EhcacheBasicBulkUtil.copyWithout(originalStoreContent, contentUpdates)));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.RemoveOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.REMOVE_ALL).intValue(), Matchers.is(EhcacheBasicBulkUtil.KEY_SET_A.size()));
    }

    /**
     * Tests {@link Ehcache#removeAll(Set)} for
     * <ul>
     *    <li>non-empty request set</li>
     *    <li>populated {@code Store} - some keys overlap request</li>
     *    <li>{@link Store#bulkCompute} throws before accessing writer</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testRemoveAllStoreSomeOverlapStoreAccessExceptionBeforeNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent);
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).bulkCompute(EhcacheBasicRemoveAllTest.getAnyStringSet(), EhcacheBasicRemoveAllTest.getAnyEntryIterableFunction());
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> contentUpdates = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C);
        ehcache.removeAll(contentUpdates);
        final InOrder ordered = Mockito.inOrder(this.store, this.resilienceStrategy);
        ordered.verify(this.store, Mockito.atLeast(1)).bulkCompute(this.bulkComputeSetCaptor.capture(), EhcacheBasicRemoveAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(this.getBulkComputeArgs(), Matchers.everyItem(Matchers.isIn(contentUpdates)));
        // ResilienceStrategy invoked; no assertions about Store content
        ordered.verify(this.resilienceStrategy).removeAllFailure(ArgumentMatchers.eq(contentUpdates), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.RemoveOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.REMOVE_ALL).intValue(), Matchers.is(0));
    }

    /**
     * Tests {@link Ehcache#removeAll(Set)} for
     * <ul>
     *    <li>non-empty request set</li>
     *    <li>populated {@code Store} - some keys overlap request</li>
     *    <li>{@link Store#bulkCompute} throws after accessing writer</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testRemoveAllStoreSomeOverlapStoreAccessExceptionAfterNoWriter() throws Exception {
        final Map<String, String> originalStoreContent = EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(originalStoreContent, Collections.singleton("keyA3"));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> contentUpdates = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C);
        ehcache.removeAll(contentUpdates);
        final InOrder ordered = Mockito.inOrder(this.store, this.resilienceStrategy);
        ordered.verify(this.store, Mockito.atLeast(1)).bulkCompute(this.bulkComputeSetCaptor.capture(), EhcacheBasicRemoveAllTest.getAnyEntryIterableFunction());
        Assert.assertThat(this.getBulkComputeArgs(), Matchers.everyItem(Matchers.isIn(contentUpdates)));
        // ResilienceStrategy invoked; no assertions about Store content
        ordered.verify(this.resilienceStrategy).removeAllFailure(ArgumentMatchers.eq(contentUpdates), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.noneOf(CacheOperationOutcomes.RemoveOutcome.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        Assert.assertThat(ehcache.getBulkMethodEntries().get(BulkOps.REMOVE_ALL).intValue(), Matchers.is(0));
    }

    /**
     * Indicates whether or not {@link #dumpResults} should emit output.
     */
    private static final boolean debugResults;

    static {
        debugResults = Boolean.parseBoolean(System.getProperty(((EhcacheBasicRemoveAllTest.class.getName()) + ".debug"), "false"));
    }

    @Rule
    public TestName name = new TestName();
}

