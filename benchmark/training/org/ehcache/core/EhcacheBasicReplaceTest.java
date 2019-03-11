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

import static org.ehcache.core.statistics.CacheOperationOutcomes.ReplaceOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.ReplaceOutcome.HIT;
import static org.ehcache.core.statistics.CacheOperationOutcomes.ReplaceOutcome.MISS_NOT_PRESENT;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicReplaceTest extends EhcacheBasicCrudBase {
    @Test
    public void testReplaceNullNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceKeyNull() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceNullValue() {
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace(null, "value");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testReplaceNoStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertNull(ehcache.replace("key", "value"));
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().containsKey("key"), CoreMatchers.is(false));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(MISS_NOT_PRESENT));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>{@code Store.replace} throws</li>
     * </ul>
     */
    @Test
    public void testReplaceNoStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.replace("key", "value");
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).replaceFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testReplaceHasStoreEntry() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertThat(ehcache.replace("key", "value"), CoreMatchers.is(CoreMatchers.equalTo("oldValue")));
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.is(CoreMatchers.equalTo("value")));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(HIT));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object)} for
     * <ul>
     *   <li>key present in {@code Store}</li>
     *   <li>{@code Store.replace} throws</li>
     * </ul>
     */
    @Test
    public void testReplaceHasStoreEntryStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.replace("key", "value");
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).replaceFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

