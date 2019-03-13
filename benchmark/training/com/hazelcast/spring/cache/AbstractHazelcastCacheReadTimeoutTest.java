/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.spring.cache;


import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


/**
 * Tests for {@link HazelcastCache} for timeout.
 *
 * @author Gokhan Oner
 */
public abstract class AbstractHazelcastCacheReadTimeoutTest extends HazelcastTestSupport {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IDummyTimeoutBean dummyTimeoutBean;

    private Cache delay150;

    private Cache delay50;

    private Cache delayNo;

    private Cache delay100;

    @Test
    public void testCache_TimeoutConfig() {
        Assert.assertEquals(150, getReadTimeout());
        Assert.assertEquals(50, getReadTimeout());
        Assert.assertEquals(0, getReadTimeout());
        Assert.assertEquals(100, getReadTimeout());
    }

    @Test(expected = OperationTimeoutException.class)
    public void testCache_delay150() {
        delay150.get(createRandomKey());
    }

    @Test
    public void testCache_delay50() {
        String key = createRandomKey();
        long start = System.nanoTime();
        try {
            delay50.get(key);
        } catch (OperationTimeoutException e) {
            // the exception can be thrown when the call is really slower than 50ms
            // it not that uncommon due non-determinism of JVM
            long deltaMs = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
            Assert.assertTrue((deltaMs >= 50));
            return;
        }
        long time = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
        Assert.assertTrue((time >= 2L));
    }

    @Test
    public void testCache_delayNo() {
        String key = createRandomKey();
        long start = System.nanoTime();
        delayNo.get(key);
        long time = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
        Assert.assertTrue((time >= 300L));
    }

    @Test(expected = OperationTimeoutException.class)
    public void testBean_delay150() {
        dummyTimeoutBean.getDelay150(createRandomKey());
    }

    @Test
    public void testBean_delay50() {
        String key = createRandomKey();
        long start = System.nanoTime();
        try {
            dummyTimeoutBean.getDelay50(key);
        } catch (OperationTimeoutException e) {
            // the exception can be thrown when the call is really slower than 50ms
            // it not that uncommon due non-determinism of JVM
            long deltaMs = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
            Assert.assertTrue((deltaMs >= 50));
            return;
        }
        long time = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
        Assert.assertTrue((time >= 2L));
    }

    @Test
    public void testBean_delayNo() {
        String key = createRandomKey();
        long start = System.nanoTime();
        dummyTimeoutBean.getDelayNo(key);
        long time = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
        Assert.assertTrue((time >= 300L));
    }

    public static class DelayIMapGetInterceptor implements MapInterceptor {
        private final int delay;

        public DelayIMapGetInterceptor(int delay) {
            this.delay = delay;
        }

        @Override
        public Object interceptGet(Object value) {
            sleepMillis(delay);
            return null;
        }

        @Override
        public void afterGet(Object value) {
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            return null;
        }

        @Override
        public void afterPut(Object value) {
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            return null;
        }

        @Override
        public void afterRemove(Object value) {
        }
    }

    public static class DummyTimeoutBean implements IDummyTimeoutBean {
        @Override
        public Object getDelay150(String key) {
            return null;
        }

        @Override
        public Object getDelay50(String key) {
            return null;
        }

        @Override
        public Object getDelayNo(String key) {
            return null;
        }

        @Override
        public String getDelay100(String key) {
            return null;
        }
    }
}

