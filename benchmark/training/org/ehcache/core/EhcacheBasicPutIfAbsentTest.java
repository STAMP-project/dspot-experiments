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
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.ehcache.core.statistics.CacheOperationOutcomes.PutIfAbsentOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.PutIfAbsentOutcome.HIT;
import static org.ehcache.core.statistics.CacheOperationOutcomes.PutIfAbsentOutcome.PUT;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicPutIfAbsentTest extends EhcacheBasicCrudBase {
    @Test
    public void testPutIfAbsentNullNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.putIfAbsent(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testPutIfAbsentKeyNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.putIfAbsent("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testPutIfAbsentNullValue() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.putIfAbsent(null, "value");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#putIfAbsent(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testPutIfAbsentNoStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.putIfAbsent("key", "value"), Matchers.is(Matchers.nullValue()));
        Mockito.verify(this.store).putIfAbsent(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), Matchers.equalTo("value"));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(PUT));
    }

    /**
     * Tests the effect of a {@link Ehcache#putIfAbsent(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testPutIfAbsentHasStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.putIfAbsent("key", "value"), Matchers.is(Matchers.equalTo("oldValue")));
        Mockito.verify(this.store).putIfAbsent(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), Matchers.equalTo("oldValue"));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(HIT));
    }

    /**
     * Tests the effect of a {@link Ehcache#putIfAbsent(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>{@code Store.putIfAbsent} throws</li>
     * </ul>
     */
    @Test
    public void testPutIfAbsentNoStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).putIfAbsent(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any());
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.putIfAbsent("key", "value");
        Mockito.verify(this.store).putIfAbsent(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any());
        Mockito.verify(this.resilienceStrategy).putIfAbsentFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#putIfAbsent(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     *   <li>{@code Store.putIfAbsent} throws</li>
     * </ul>
     */
    @Test
    public void testPutIfAbsentHasStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).putIfAbsent(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any());
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.putIfAbsent("key", "value");
        Mockito.verify(this.store).putIfAbsent(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any());
        Mockito.verify(this.resilienceStrategy).putIfAbsentFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

