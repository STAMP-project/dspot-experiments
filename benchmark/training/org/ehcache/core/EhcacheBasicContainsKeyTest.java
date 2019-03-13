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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Abhilash
 */
public class EhcacheBasicContainsKeyTest extends EhcacheBasicCrudBase {
    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} with a {@code null} key.
     */
    @Test
    public void testContainsKeyNull() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(realStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        try {
            ehcache.containsKey(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
    }

    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} over an empty cache.
     */
    @Test
    public void testContainsKeyEmpty() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(realStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertFalse(ehcache.containsKey("key"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
    }

    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} over an empty cache
     * where {@link Store#containsKey(Object) Store.containsKey} throws a
     * {@link StoreAccessException StoreAccessException}.
     */
    @Test
    public void testContainsKeyEmptyStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(Collections.<String, String>emptyMap());
        this.store = Mockito.spy(realStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).containsKey("key");
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.containsKey("key");
        Mockito.verify(this.resilienceStrategy).containsKeyFailure(ArgumentMatchers.eq("key"), ArgumentMatchers.any(StoreAccessException.class));
    }

    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} over a cache holding
     * the target key.
     */
    @Test
    public void testContainsKeyContains() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        this.store = Mockito.spy(realStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertTrue(ehcache.containsKey("keyA"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
    }

    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} over a cache holding
     * the target key where {@link Store#containsKey(Object) Store.containsKey}
     * throws a {@link StoreAccessException StoreAccessException}.
     */
    @Test
    public void testContainsKeyContainsStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        this.store = Mockito.spy(realStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).containsKey("keyA");
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.containsKey("keyA");
        Mockito.verify(this.resilienceStrategy).containsKeyFailure(ArgumentMatchers.eq("keyA"), ArgumentMatchers.any(StoreAccessException.class));
    }

    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} over a non-empty cache
     * not holding the target key.
     */
    @Test
    public void testContainsKeyMissing() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        this.store = Mockito.spy(realStore);
        final Ehcache<String, String> ehcache = this.getEhcache();
        Assert.assertFalse(ehcache.containsKey("missingKey"));
        Mockito.verifyZeroInteractions(this.resilienceStrategy);
    }

    /**
     * Tests {@link Ehcache#containsKey(Object) Ehcache.containsKey} over a non-empty cache
     * not holding the target key where {@link Store#containsKey(Object) Store.containsKey}
     * throws a {@link StoreAccessException StoreAccessException}.
     */
    @Test
    public void testContainsKeyMissingStoreAccessException() throws Exception {
        final EhcacheBasicCrudBase.FakeStore realStore = new EhcacheBasicCrudBase.FakeStore(this.getTestStoreEntries());
        this.store = Mockito.spy(realStore);
        Mockito.doThrow(new StoreAccessException("")).when(this.store).containsKey("missingKey");
        final Ehcache<String, String> ehcache = this.getEhcache();
        ehcache.containsKey("missingKey");
        Mockito.verify(this.resilienceStrategy).containsKeyFailure(ArgumentMatchers.eq("missingKey"), ArgumentMatchers.any(StoreAccessException.class));
    }
}

