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

import static org.ehcache.core.statistics.CacheOperationOutcomes.ConditionalRemoveOutcome.FAILURE;
import static org.ehcache.core.statistics.CacheOperationOutcomes.ConditionalRemoveOutcome.FAILURE_KEY_MISSING;
import static org.ehcache.core.statistics.CacheOperationOutcomes.ConditionalRemoveOutcome.FAILURE_KEY_PRESENT;
import static org.ehcache.core.statistics.CacheOperationOutcomes.ConditionalRemoveOutcome.SUCCESS;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicRemoveValueTest extends EhcacheBasicCrudBase {
    @Test
    public void testRemoveNullNull() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.remove(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testRemoveKeyNull() throws Exception {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.remove("key", null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testRemoveNullValue() throws Exception {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.remove(null, "value");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testRemoveValueNoStoreEntry() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(fakeStore);
        Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertFalse(ehcache.remove("key", "value"));
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().containsKey("key"), CoreMatchers.is(false));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE_KEY_MISSING));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object, Object)} for
     * <ul>
     *   <li>key with unequal value in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testRemoveValueUnequalStoreEntry() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "unequalValue"));
        this.store = Mockito.spy(fakeStore);
        Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertFalse(ehcache.remove("key", "value"));
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.is(CoreMatchers.equalTo("unequalValue")));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE_KEY_PRESENT));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object, Object)} for
     * <ul>
     *   <li>key with equal value in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testRemoveValueEqualStoreEntry() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "value"));
        this.store = Mockito.spy(fakeStore);
        Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertTrue(ehcache.remove("key", "value"));
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().containsKey("key"), CoreMatchers.is(false));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(SUCCESS));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>>{@code Store.remove} throws</li>
     * </ul>
     */
    @Test
    public void testRemoveValueNoStoreEntryStoreAccessException() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key", "value");
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).removeFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object, Object)} for
     * <ul>
     *   <li>key with unequal value present in {@code Store}</li>
     *   <li>>{@code Store.remove} throws</li>
     * </ul>
     */
    @Test
    public void testRemoveValueUnequalStoreEntryStoreAccessException() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "unequalValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key", "value");
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).removeFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#remove(Object, Object)} for
     * <ul>
     *   <li>key with equal value present in {@code Store}</li>
     *   <li>>{@code Store.remove} throws</li>
     * </ul>
     */
    @Test
    public void testRemoveValueEqualStoreEntryStoreAccessException() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "value"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.remove("key", "value");
        Mockito.verify(this.store).remove(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"));
        Mockito.verify(this.resilienceStrategy).removeFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("value"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

