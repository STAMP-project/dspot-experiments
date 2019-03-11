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
import org.ehcache.core.spi.store.Store;
import org.ehcache.core.statistics.CacheOperationOutcomes;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.core.statistics.CacheOperationOutcomes.PutOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.PutOutcome.PUT;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicPutTest extends EhcacheBasicCrudBase {
    @Test
    public void testPutNullNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.put(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testPutKeyNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.put("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testPutNullValue() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.put(null, "value");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#put(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testPutNoStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.put("key", "value");
        Mockito.verify(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.equalTo("value"));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(PUT));
    }

    /**
     * Tests the effect of a {@link Ehcache#put(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>{@code Store.put} throws</li>
     * </ul>
     */
    @Test
    public void testPutNoStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.put("key", "value");
        Mockito.verify(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).putFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#put(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testPutHasStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.put("key", "value");
        Mockito.verify(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.equalTo("value"));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(PUT));
    }

    /**
     * Tests the effect of a {@link Ehcache#put(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     *   <li>{@code Store.put} throws</li>
     * </ul>
     */
    @Test
    public void testPutHasStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.put("key", "value");
        Mockito.verify(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).putFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#put(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     *   <li>{@code Store.put} throws a {@code RuntimeException}</li>
     * </ul>
     */
    @Test
    public void testPutThrowsExceptionShouldKeepTheValueInPlace() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new RuntimeException("failed")).when(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.put("key", "value");
            Assert.fail();
        } catch (RuntimeException e) {
            // expected
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("failed"));
        }
        // Key and old value should still be in place
        Assert.assertThat(ehcache.get("key"), CoreMatchers.equalTo("oldValue"));
        Mockito.verify(this.store).put(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyNoMoreInteractions(this.resilienceStrategy);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

