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
import static org.ehcache.core.statistics.CacheOperationOutcomes.ReplaceOutcome.MISS_PRESENT;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicReplaceValueTest extends EhcacheBasicCrudBase {
    @Test
    public void testReplaceValueNullNullNull() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace(null, null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceKeyNullNull() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace("key", null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceKeyValueNull() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace("key", "oldValue", null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceKeyNullValue() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace("key", null, "newValue");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceNullValueNull() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace(null, "oldValue", null);
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceNullValueValue() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace(null, "oldValue", "newValue");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testReplaceNullNullValue() {
        Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.replace(null, null, "newValue");
            Assert.fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testReplaceValueNoStoreEntry() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        this.store = Mockito.spy(fakeStore);
        Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertFalse(ehcache.replace("key", "oldValue", "newValue"));
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().containsKey("key"), CoreMatchers.is(false));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(MISS_NOT_PRESENT));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object, Object)} for
     * <ul>
     *   <li>key with unequal value in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testReplaceValueUnequalStoreEntry() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "unequalValue"));
        this.store = Mockito.spy(fakeStore);
        Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertFalse(ehcache.replace("key", "oldValue", "newValue"));
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.is(CoreMatchers.equalTo("unequalValue")));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(MISS_PRESENT));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object, Object)} for
     * <ul>
     *   <li>key with equal value in {@code Store}</li>
     * </ul>
     */
    @Test
    public void testReplaceValueEqualStoreEntry() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertTrue(ehcache.replace("key", "oldValue", "newValue"));
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
        Assert.assertThat(fakeStore.getEntryMap().get("key"), CoreMatchers.is(CoreMatchers.equalTo("newValue")));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(HIT));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object, Object)} for
     * <ul>
     *   <li>key not present in {@code Store}</li>
     *   <li>>{@code Store.replace} throws</li>
     * </ul>
     */
    @Test
    public void testReplaceValueNoStoreEntryStoreAccessException() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.emptyMap());
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.replace("key", "oldValue", "newValue");
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Mockito.verify(this.resilienceStrategy).replaceFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object, Object)} for
     * <ul>
     *   <li>key with unequal value present in {@code Store}</li>
     *   <li>>{@code Store.replace} throws</li>
     * </ul>
     */
    @Test
    public void testReplaceValueUnequalStoreEntryStoreAccessException() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "unequalValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.replace("key", "oldValue", "newValue");
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Mockito.verify(this.resilienceStrategy).replaceFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }

    /**
     * Tests the effect of a {@link Ehcache#replace(Object, Object, Object)} for
     * <ul>
     *   <li>key with equal value present in {@code Store}</li>
     *   <li>>{@code Store.replace} throws</li>
     * </ul>
     */
    @Test
    public void testReplaceValueEqualStoreEntryStoreAccessException() throws Exception {
        EhcacheBasicCrudBase.FakeStore fakeStore = new EhcacheBasicCrudBase.FakeStore(Collections.singletonMap("key", "oldValue"));
        this.store = Mockito.spy(fakeStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.replace("key", "oldValue", "newValue");
        Mockito.verify(this.store).replace(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"));
        Mockito.verify(this.resilienceStrategy).replaceFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.eq("oldValue"), ArgumentMatchers.eq("newValue"), ArgumentMatchers.any(StoreAccessException.class));
        EhcacheBasicCrudBase.validateStats(ehcache, EnumSet.of(FAILURE));
    }
}

