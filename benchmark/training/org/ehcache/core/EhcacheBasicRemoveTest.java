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

import static org.ehcache.core.statistics.CacheOperationOutcomes.RemoveOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.RemoveOutcome.NOOP;
import static org.ehcache.core.statistics.CacheOperationOutcomes.RemoveOutcome.SUCCESS;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicRemoveTest extends EhcacheBasicCrudBase {
    @Test
    public void testRemoveNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.remove(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testRemoveNoStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key");
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().containsKey("key"), CoreMatchers.is(false));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(NOOP));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>{@code Store.remove} throws</li>
     * </ul>
     */
    @Test
    public void testRemoveNoStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).remove(ArgumentMatchers.eq("key"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key");
        Mockito.verify(this.resilienceStrategy).removeFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testRemoveHasStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key");
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().containsKey("key"), CoreMatchers.is(false));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     *   <li>{@code Store.remove} throws</li>
     * </ul>
     */
    @Test
    public void testRemoveHasStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).remove(ArgumentMatchers.eq("key"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key");
        Mockito.verify(this.resilienceStrategy).removeFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

