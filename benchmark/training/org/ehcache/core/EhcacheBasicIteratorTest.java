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
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.ehcache.Cache;
import org.ehcache.core.spi.store.Store;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.core.spi.store.Store.RemoveStatus.REMOVED;


/**
 * Provides testing of basic ITERATOR operations on an {@code Ehcache}.
 * <p>
 * The {@link Ehcache#iterator()} tests require the use of a {@link Store Store}
 * implementation which returns an {@link java.util.Iterator} from the
 * {@link Store#iterator() Store.iterator} method that does <b>not</b>
 * throw a {@link java.util.ConcurrentModificationException ConcurrentModificationException}.
 *
 * @author Clifford W. Johnson
 */
public class EhcacheBasicIteratorTest extends EhcacheBasicCrudBase {
    /**
     * Tests {@link Ehcache#iterator()} on an empty cache.
     */
    @Test
    public void testIteratorEmptyStoreGet() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        final InternalCache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.iterator(), Matchers.is(Matchers.notNullValue()));
    }

    /**
     * Tests {@link java.util.Iterator#hasNext()} from {@link Ehcache#iterator()} on an empty cache.
     */
    @Test
    public void testIteratorEmptyStoreHasNext() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
    }

    /**
     * Tests {@link java.util.Iterator#next()} from {@link Ehcache#iterator()} on an empty cache.
     */
    @Test
    public void testIteratorEmptyStoreNext() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        try {
            iterator.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * Tests {@link java.util.Iterator#remove()} from {@link Ehcache#iterator()} on an empty cache.
     */
    @Test
    public void testIteratorEmptyStoreRemoveBeforeNext() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        try {
            iterator.remove();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    /**
     * Tests {@link Ehcache#iterator()} on a non-empty cache.
     */
    @Test
    public void testIteratorNonEmptyStoreGet() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        final InternalCache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.iterator(), Matchers.is(Matchers.notNullValue()));
    }

    /**
     * Tests {@link java.util.Iterator#hasNext()} from {@link Ehcache#iterator()} on a non-empty cache.
     */
    @Test
    public void testIteratorNonEmptyStoreHasNext() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        Assert.assertThat(iterator.hasNext(), Matchers.is(true));
    }

    /**
     * Tests {@link java.util.Iterator#next()} from {@link Ehcache#iterator()} on a non-empty cache.
     */
    @Test
    public void testIteratorNonEmptyStoreNext() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        Assert.assertThat(iterator.next(), Matchers.is(Matchers.notNullValue()));
    }

    /**
     * Tests fetching all entries via an {@link Ehcache#iterator()} on a non-empty cache.
     */
    @Test
    public void testIteratorNonEmptyAll() throws Exception {
        final Map<String, String> testStoreEntries = this.getTestStoreEntries();
        this.store = new EhcacheBasicCrudBase.FakeStore(testStoreEntries);
        final InternalCache<String, String> ehcache = this.getEhcache();
        for (Cache.Entry<String, String> cacheEntry : ehcache) {
            final String cacheEntryKey = cacheEntry.getKey();
            Assert.assertThat(testStoreEntries, Matchers.hasEntry(Matchers.equalTo(cacheEntryKey), Matchers.equalTo(cacheEntry.getValue())));
            testStoreEntries.remove(cacheEntryKey);
        }
        Assert.assertThat("Iterator did not return all values", testStoreEntries.isEmpty(), Matchers.is(true));
    }

    /**
     * Tests {@link java.util.Iterator#hasNext()} <b>after</b> exhausting the {@code Iterator} returned
     * from {@link Ehcache#iterator()} on a non-empty cache.
     */
    @Test
    public void testIteratorNonEmptyHasNextAfterLast() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        } 
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
    }

    /**
     * Tests {@link java.util.Iterator#next()} <b>after</b> exhausting the {@code Iterator} returned
     * from {@link Ehcache#iterator()} on a non-empty cache.
     */
    @Test
    public void testIteratorNonEmptyNextAfterLast() throws Exception {
        this.store = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        } 
        try {
            iterator.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * Tests the {@link java.util.Iterator} returned when the {@link Store Store}
     * throws a {@link StoreAccessException StoreAccessException} from
     * {@code Store.iterator}.
     */
    @Test
    public void testIteratorStoreAccessException() throws Exception {
        @SuppressWarnings("unchecked")
        Store.ValueHolder<String> valueHolder = Mockito.mock(Store.ValueHolder.class);
        Mockito.doReturn("bar").when(valueHolder).get();
        @SuppressWarnings("unchecked")
        Cache.Entry<String, Store.ValueHolder<String>> storeEntry = Mockito.mock(Cache.Entry.class);
        Mockito.doReturn(valueHolder).when(storeEntry).getValue();
        Mockito.doReturn("foo").when(storeEntry).getKey();
        @SuppressWarnings("unchecked")
        Store.Iterator<Cache.Entry<String, Store.ValueHolder<String>>> storeIterator = Mockito.mock(Store.Iterator.class);
        Mockito.doReturn(true).when(storeIterator).hasNext();
        Mockito.doReturn(storeEntry).when(storeIterator).next();
        Mockito.doReturn(storeIterator).when(this.store).iterator();
        Mockito.doReturn(valueHolder).when(this.store).get(ArgumentMatchers.eq("foo"));
        final InternalCache<String, String> ehcache = this.getEhcache();
        final Iterator<Cache.Entry<String, String>> iterator = ehcache.iterator();
        Assert.assertThat(iterator, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(iterator.hasNext(), Matchers.is(true));
        StoreAccessException exception = new StoreAccessException("");
        Mockito.doThrow(exception).when(storeIterator).next();
        Cache.Entry<String, String> entry = iterator.next();
        Assert.assertThat(entry.getKey(), Matchers.is("foo"));
        Assert.assertThat(entry.getValue(), Matchers.is("bar"));
        Mockito.doReturn(REMOVED).when(this.store).remove(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        iterator.next();
        Mockito.verify(resilienceStrategy).iteratorFailure(exception);
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        try {
            iterator.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Expected
        }
        try {
            iterator.remove();
        } catch (Exception e) {
            Assert.fail();
        }
        try {
            iterator.remove();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
    }
}

