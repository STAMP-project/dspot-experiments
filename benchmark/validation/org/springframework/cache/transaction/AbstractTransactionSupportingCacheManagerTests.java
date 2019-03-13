/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cache.transaction;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.cache.CacheManager;


/**
 * Shared tests for {@link CacheManager} that inherit from
 * {@link AbstractTransactionSupportingCacheManager}.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractTransactionSupportingCacheManagerTests<T extends CacheManager> {
    public static final String CACHE_NAME = "testCacheManager";

    @Rule
    public final TestName name = new TestName();

    @Test
    public void getOnExistingCache() {
        Assert.assertThat(getCache(AbstractTransactionSupportingCacheManagerTests.CACHE_NAME), is(AbstractTransactionSupportingCacheManagerTests.instanceOf(getCacheType())));
    }

    @Test
    public void getOnNewCache() {
        T cacheManager = getCacheManager(false);
        String cacheName = name.getMethodName();
        addNativeCache(cacheName);
        Assert.assertFalse(getCacheNames().contains(cacheName));
        try {
            Assert.assertThat(getCache(cacheName), is(AbstractTransactionSupportingCacheManagerTests.instanceOf(getCacheType())));
            Assert.assertTrue(getCacheNames().contains(cacheName));
        } finally {
            removeNativeCache(cacheName);
        }
    }

    @Test
    public void getOnUnknownCache() {
        T cacheManager = getCacheManager(false);
        String cacheName = name.getMethodName();
        Assert.assertFalse(getCacheNames().contains(cacheName));
        Assert.assertThat(getCache(cacheName), AbstractTransactionSupportingCacheManagerTests.nullValue());
    }

    @Test
    public void getTransactionalOnExistingCache() {
        Assert.assertThat(getCache(AbstractTransactionSupportingCacheManagerTests.CACHE_NAME), is(AbstractTransactionSupportingCacheManagerTests.instanceOf(TransactionAwareCacheDecorator.class)));
    }

    @Test
    public void getTransactionalOnNewCache() {
        String cacheName = name.getMethodName();
        T cacheManager = getCacheManager(true);
        Assert.assertFalse(getCacheNames().contains(cacheName));
        addNativeCache(cacheName);
        try {
            Assert.assertThat(getCache(cacheName), is(AbstractTransactionSupportingCacheManagerTests.instanceOf(TransactionAwareCacheDecorator.class)));
            Assert.assertTrue(getCacheNames().contains(cacheName));
        } finally {
            removeNativeCache(cacheName);
        }
    }
}

