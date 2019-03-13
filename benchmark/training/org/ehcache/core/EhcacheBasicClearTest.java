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
import org.ehcache.core.spi.store.Store;
import org.ehcache.spi.resilience.StoreAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicClearTest extends EhcacheBasicCrudBase {
    /**
     * Tests {@link Ehcache#clear()} over an empty cache.
     */
    @Test
    public void testClearEmpty() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(realStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.clear();
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(realStore.getEntryMap().isEmpty(), Matchers.is(true));
    }

    /**
     * Tests {@link Ehcache#clear()} over an empty cache where
     * {@link Store#clear() Store.clear} throws a
     * {@link StoreAccessException StoreAccessException}.
     */
    @Test
    public void testClearEmptyStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(realStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).clear();
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.clear();
        Mockito.verify(this.resilienceStrategy).clearFailure(ArgumentMatchers.any(StoreAccessException.class));
    }

    /**
     * Tests {@link Ehcache#clear()} over a non-empty cache.
     */
    @Test
    public void testClearNonEmpty() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        this.store = Mockito.spy(realStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(realStore.getEntryMap().isEmpty(), Matchers.is(false));
        ehcache.clear();
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(realStore.getEntryMap().isEmpty(), Matchers.is(true));
    }

    /**
     * Tests {@link Ehcache#clear()} over a non-empty cache where
     * {@link Store#clear() Store.clear} throws a
     * {@link StoreAccessException StoreAccessException}.
     */
    @Test
    public void testClearNonEmptyStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        this.store = Mockito.spy(realStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).clear();
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(realStore.getEntryMap().isEmpty(), Matchers.is(false));
        ehcache.clear();
        Mockito.verify(this.resilienceStrategy).clearFailure(ArgumentMatchers.any(StoreAccessException.class));
        // Not testing ResilienceStrategy implementation here
    }
}

