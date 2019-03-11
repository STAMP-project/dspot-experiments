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
import org.ehcache.core.statistics.CacheOperationOutcomes;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.core.statistics.CacheOperationOutcomes.GetAllOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.GetAllOutcome.SUCCESS;


/**
 * Provides testing of basic GET_ALL operations on an {@code Ehcache}.
 *
 * @author Clifford W. Johnson
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class EhcacheBasicGetAllTest extends EhcacheBasicCrudBase {
    @Test
    public void testGetAllNull() throws Exception {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.getAll(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testGetAllNullKey() throws Exception {
        final Set<String> keys = new LinkedHashSet<>();
        for (final String key : EhcacheBasicBulkUtil.KEY_SET_A) {
            keys.add(key);
            if ("keyA2".equals(key)) {
                keys.add(null);// Add a null element

            }
        }
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.getAll(keys);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>empty request key set</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllEmptyRequestNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Map<String, String> actual = ehcache.getAll(Collections.<String>emptySet());
        Assert.assertThat(actual, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(actual.isEmpty(), Matchers.is(true));
        Mockito.verify(this.store, Mockito.never()).bulkComputeIfAbsent(ArgumentMatchers.eq(Collections.<String>emptySet()), EhcacheBasicGetAllTest.getAnyIterableFunction());
        Mockito.verify(this.resilienceStrategy, Mockito.never()).getAllFailure(ArgumentMatchers.eq(Collections.<String>emptySet()), ArgumentMatchers.any(StoreAccessException.class));
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>non-empty request key set</li>
     *    <li>no {@link Store} entries match</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllStoreNoMatchNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_B));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Map<String, String> actual = ehcache.getAll(EhcacheBasicBulkUtil.KEY_SET_A);
        Assert.assertThat(actual, Matchers.equalTo(EhcacheBasicBulkUtil.getNullEntryMap(EhcacheBasicBulkUtil.KEY_SET_A)));
        Mockito.verify(this.store).bulkComputeIfAbsent(ArgumentMatchers.eq(EhcacheBasicBulkUtil.KEY_SET_A), EhcacheBasicGetAllTest.getAnyIterableFunction());
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_B)));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        EhcacheBasicGetAllTest.validateBulkCounters(ehcache, 0, EhcacheBasicBulkUtil.KEY_SET_A.size());
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>non-empty request key set</li>
     *    <li>all {@link Store} entries match</li>
     *    <li>{@link Store#bulkComputeIfAbsent} throws before accessing loader</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllStoreAllMatchStoreAccessExceptionBeforeNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).bulkComputeIfAbsent(EhcacheBasicGetAllTest.getAnyStringSet(), EhcacheBasicGetAllTest.getAnyIterableFunction());
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> fetchKeys = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        ehcache.getAll(fetchKeys);
        Mockito.verify(this.store).bulkComputeIfAbsent(ArgumentMatchers.eq(fetchKeys), EhcacheBasicGetAllTest.getAnyIterableFunction());
        // ResilienceStrategy invoked: no assertion for Store content
        Mockito.verify(this.resilienceStrategy).getAllFailure(ArgumentMatchers.eq(fetchKeys), ArgumentMatchers.any(StoreAccessException.class));
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        EhcacheBasicGetAllTest.validateBulkCounters(ehcache, 0, 0);
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>non-empty request key set</li>
     *    <li>all {@link Store} entries match</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllStoreAllMatchNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> fetchKeys = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B);
        final Map<String, String> actual = ehcache.getAll(fetchKeys);
        Assert.assertThat(actual, Matchers.equalTo(EhcacheBasicBulkUtil.getEntryMap(fetchKeys)));
        Mockito.verify(this.store).bulkComputeIfAbsent(ArgumentMatchers.eq(fetchKeys), EhcacheBasicGetAllTest.getAnyIterableFunction());
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B)));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        EhcacheBasicGetAllTest.validateBulkCounters(ehcache, fetchKeys.size(), 0);
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>non-empty request key set</li>
     *    <li>no {@link Store} entries match</li>
     *    <li>{@link Store#bulkComputeIfAbsent} throws before accessing loader</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllStoreNoMatchStoreAccessExceptionBeforeNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_B));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).bulkComputeIfAbsent(EhcacheBasicGetAllTest.getAnyStringSet(), EhcacheBasicGetAllTest.getAnyIterableFunction());
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.getAll(EhcacheBasicBulkUtil.KEY_SET_A);
        Mockito.verify(this.store).bulkComputeIfAbsent(ArgumentMatchers.eq(EhcacheBasicBulkUtil.KEY_SET_A), EhcacheBasicGetAllTest.getAnyIterableFunction());
        // ResilienceStrategy invoked: no assertion for Store content
        Mockito.verify(this.resilienceStrategy).getAllFailure(ArgumentMatchers.eq(EhcacheBasicBulkUtil.KEY_SET_A), ArgumentMatchers.any(StoreAccessException.class));
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        EhcacheBasicGetAllTest.validateBulkCounters(ehcache, 0, 0);
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>non-empty request key set</li>
     *    <li>some {@link Store} entries match</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllStoreSomeMatchNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> fetchKeys = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C);
        final Map<String, String> actual = ehcache.getAll(fetchKeys);
        Assert.assertThat(actual, Matchers.equalTo(EhcacheBasicBulkUtil.union(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A), EhcacheBasicBulkUtil.getNullEntryMap(EhcacheBasicBulkUtil.KEY_SET_C))));
        Mockito.verify(this.store).bulkComputeIfAbsent(ArgumentMatchers.eq(fetchKeys), EhcacheBasicGetAllTest.getAnyIterableFunction());
        Assert.assertThat(fakeStore.getEntryMap(), Matchers.equalTo(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B)));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
        EhcacheBasicGetAllTest.validateBulkCounters(ehcache, EhcacheBasicBulkUtil.KEY_SET_A.size(), EhcacheBasicBulkUtil.KEY_SET_C.size());
    }

    /**
     * Tests {@link Ehcache#getAll(Set)} for
     * <ul>
     *    <li>non-empty request key set</li>
     *    <li>some {@link Store} entries match</li>
     *    <li>{@link Store#bulkComputeIfAbsent} throws before accessing loader</li>
     *    <li>no {@code CacheLoaderWriter}</li>
     * </ul>
     */
    @Test
    public void testGetAllStoreSomeMatchStoreAccessExceptionBeforeNoLoader() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(EhcacheBasicBulkUtil.getEntryMap(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_B));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).bulkComputeIfAbsent(EhcacheBasicGetAllTest.getAnyStringSet(), EhcacheBasicGetAllTest.getAnyIterableFunction());
        final Ehcache<String, String> ehcache = this.getEhcache();
        final Set<String> fetchKeys = EhcacheBasicBulkUtil.fanIn(EhcacheBasicBulkUtil.KEY_SET_A, EhcacheBasicBulkUtil.KEY_SET_C);
        ehcache.getAll(fetchKeys);
        Mockito.verify(this.store).bulkComputeIfAbsent(ArgumentMatchers.eq(fetchKeys), EhcacheBasicGetAllTest.getAnyIterableFunction());
        // ResilienceStrategy invoked: no assertion for Store content
        Mockito.verify(this.resilienceStrategy).getAllFailure(ArgumentMatchers.eq(fetchKeys), ArgumentMatchers.any(StoreAccessException.class));
        validateStatsNoneof(ehcache);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
        EhcacheBasicGetAllTest.validateBulkCounters(ehcache, 0, 0);
    }
}

