/**
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.cache;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;


/**
 * Unit tests for Caffeine.
 */
@GwtCompatible(emulated = true)
@SuppressWarnings({ "CanonicalDuration", "ThreadPriorityCheck" })
public class CacheBuilderTest extends TestCase {
    public void testNewBuilder() {
        CacheLoader<Object, Integer> loader = TestingCacheLoaders.constantLoader(1);
        LoadingCache<String, Integer> cache = CaffeinatedGuava.build(Caffeine.newBuilder().removalListener(TestingRemovalListeners.countingRemovalListener()), loader);
        TestCase.assertEquals(Integer.valueOf(1), cache.getUnchecked("one"));
        TestCase.assertEquals(1, cache.size());
    }

    public void testInitialCapacity_negative() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        try {
            builder.initialCapacity((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testInitialCapacity_setTwice() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().initialCapacity(16);
        try {
            // even to the same value is not allowed
            builder.initialCapacity(16);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testInitialCapacity_large() {
        Caffeine.newBuilder().initialCapacity(Integer.MAX_VALUE);
        // that the builder didn't blow up is enough;
        // don't actually create this monster!
    }

    public void testMaximumSize_negative() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        try {
            builder.maximumSize((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testMaximumSize_setTwice() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(16);
        try {
            // even to the same value is not allowed
            builder.maximumSize(16);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testTimeToLive_negative() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        try {
            builder.expireAfterWrite((-1), TimeUnit.SECONDS);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testTimeToLive_small() {
        CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.NANOSECONDS), TestingCacheLoaders.identityLoader());
        // well, it didn't blow up.
    }

    public void testTimeToLive_setTwice() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().expireAfterWrite(3600, TimeUnit.SECONDS);
        try {
            // even to the same value is not allowed
            builder.expireAfterWrite(3600, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testTimeToIdle_negative() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        try {
            builder.expireAfterAccess((-1), TimeUnit.SECONDS);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testTimeToIdle_small() {
        CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.NANOSECONDS), TestingCacheLoaders.identityLoader());
        // well, it didn't blow up.
    }

    public void testTimeToIdle_setTwice() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder().expireAfterAccess(3600, TimeUnit.SECONDS);
        try {
            // even to the same value is not allowed
            builder.expireAfterAccess(3600, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testTimeToIdleAndToLive() {
        CaffeinatedGuava.build(Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.NANOSECONDS).expireAfterAccess(1, TimeUnit.NANOSECONDS), TestingCacheLoaders.identityLoader());
        // well, it didn't blow up.
    }

    public void testTicker_setTwice() {
        Ticker testTicker = Ticker.systemTicker();
        Caffeine<Object, Object> builder = Caffeine.newBuilder().ticker(testTicker);
        try {
            // even to the same instance is not allowed
            builder.ticker(testTicker);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testRemovalListener_setTwice() {
        RemovalListener<Object, Object> testListener = TestingRemovalListeners.nullRemovalListener();
        Caffeine<Object, Object> builder = Caffeine.newBuilder().removalListener(testListener);
        try {
            // even to the same instance is not allowed
            builder = builder.removalListener(testListener);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testValuesIsNotASet() {
        assertThat(Caffeine.newBuilder().build().asMap().values()).isNotInstanceOf(Set.class);
    }

    @GwtIncompatible("CountDownLatch")
    static final class DelayingIdentityLoader<T> extends CacheLoader<T, T> {
        private final AtomicBoolean shouldWait;

        private final CountDownLatch delayLatch;

        DelayingIdentityLoader(AtomicBoolean shouldWait, CountDownLatch delayLatch) {
            this.shouldWait = shouldWait;
            this.delayLatch = delayLatch;
        }

        @Override
        public T load(T key) {
            if (shouldWait.get()) {
                TestCase.assertTrue(Uninterruptibles.awaitUninterruptibly(delayLatch, 300, TimeUnit.SECONDS));
            }
            return key;
        }
    }
}

