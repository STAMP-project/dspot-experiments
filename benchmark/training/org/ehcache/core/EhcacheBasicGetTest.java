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

import static org.ehcache.core.statistics.CacheOperationOutcomes.GetOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.GetOutcome.HIT;
import static org.ehcache.core.statistics.CacheOperationOutcomes.GetOutcome.MISS;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicGetTest extends EhcacheBasicCrudBase {
    @Test
    public void testGetNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.get(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#get(Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testGetNoStoreEntry() throws Exception {
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.get("key"), CoreMatchers.is(CoreMatchers.nullValue()));
        Mockito.verify(this.store).get(ArgumentMatchers.eq("key"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(MISS));
    }

    /**
     * Tests the effect of a {@link Ehcache#get(Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>{@code Store.get} throws</li>
     * </ul>
     */
    @Test
    public void testGetNoStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).get(ArgumentMatchers.eq("key"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.get("key");
        Mockito.verify(this.store).get(ArgumentMatchers.eq("key"));
        Mockito.verify(this.resilienceStrategy).getFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#get(Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testGetHasStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "value"));
        this.store = Mockito.spy(fakeStore);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.equalTo("value"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.get("key"), CoreMatchers.equalTo("value"));
        Mockito.verify(this.store).get(ArgumentMatchers.eq("key"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.equalTo("value"));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(HIT));
    }

    /**
     * Tests the effect of a {@link Ehcache#get(Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     *   <li>{@code Store.get} throws</li>
     * </ul>
     */
    @Test
    public void testGetHasStoreEntryStoreAccessExceptionNoCacheLoaderWriter() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "value"));
        this.store = Mockito.spy(fakeStore);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.equalTo("value"));
        Mockito.doThrow(new StoreAccessException("")).when(this.store).get(ArgumentMatchers.eq("key"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.get("key");
        Mockito.verify(this.store).get(ArgumentMatchers.eq("key"));
        Mockito.verify(this.resilienceStrategy).getFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

