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


import com.hazelcast.map.MapInterceptor;
import com.hazelcast.spring.CustomSpringJUnit4ClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;


/**
 * Tests for {@link HazelcastCache} for timeout.
 *
 * @author Gokhan Oner
 */
@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "no-readtimeout-config.xml" })
@Category(QuickTest.class)
public class HazelcastCacheNoReadTimeoutTest extends HazelcastTestSupport {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IDummyTimeoutBean dummyTimeoutBean;

    private Cache delayNo;

    @Test
    public void testCache_TimeoutConfig() {
        Assert.assertEquals(0, getReadTimeout());
    }

    @Test
    public void testBean_delayNo() {
        String key = createRandomKey();
        long start = System.nanoTime();
        dummyTimeoutBean.getDelayNo(key);
        long time = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
        Assert.assertTrue((time >= 250L));
    }

    private static class DelayIMapGetInterceptor implements MapInterceptor {
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

