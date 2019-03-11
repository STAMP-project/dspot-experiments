/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.service.stream.limiter;


import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.config.DynamicDistributedLogConfiguration;
import com.twitter.distributedlog.exceptions.OverCapacityException;
import com.twitter.distributedlog.limiter.GuavaRateLimiter;
import com.twitter.distributedlog.limiter.RateLimiter;
import com.twitter.distributedlog.limiter.RequestLimiter;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.bookkeeper.feature.SettableFeature;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestServiceRequestLimiter {
    static final Logger LOG = LoggerFactory.getLogger(TestServiceRequestLimiter.class);

    class MockRequest {
        int size;

        MockRequest() {
            this(1);
        }

        MockRequest(int size) {
            this.size = size;
        }

        int getSize() {
            return size;
        }
    }

    class MockRequestLimiter implements RequestLimiter<TestServiceRequestLimiter.MockRequest> {
        public void apply(TestServiceRequestLimiter.MockRequest request) {
        }
    }

    static class CounterLimiter implements RateLimiter {
        final int limit;

        int count;

        public CounterLimiter(int limit) {
            this.limit = limit;
            this.count = 0;
        }

        @Override
        public boolean acquire(int permits) {
            if ((++(count)) > (limit)) {
                return false;
            }
            return true;
        }
    }

    class MockHardRequestLimiter implements RequestLimiter<TestServiceRequestLimiter.MockRequest> {
        RequestLimiter<TestServiceRequestLimiter.MockRequest> limiter;

        int limitHitCount;

        MockHardRequestLimiter(int limit) {
            this(GuavaRateLimiter.of(limit));
        }

        MockHardRequestLimiter(RateLimiter limiter) {
            this.limiter = new com.twitter.distributedlog.limiter.ComposableRequestLimiter<TestServiceRequestLimiter.MockRequest>(limiter, new com.twitter.distributedlog.limiter.ComposableRequestLimiter.OverlimitFunction<TestServiceRequestLimiter.MockRequest>() {
                public void apply(TestServiceRequestLimiter.MockRequest request) throws OverCapacityException {
                    (limitHitCount)++;
                    throw new OverCapacityException("Limit exceeded");
                }
            }, new com.twitter.distributedlog.limiter.ComposableRequestLimiter.CostFunction<TestServiceRequestLimiter.MockRequest>() {
                public int apply(TestServiceRequestLimiter.MockRequest request) {
                    return request.getSize();
                }
            }, NullStatsLogger.INSTANCE);
        }

        @Override
        public void apply(TestServiceRequestLimiter.MockRequest op) throws OverCapacityException {
            limiter.apply(op);
        }

        public int getLimitHitCount() {
            return limitHitCount;
        }
    }

    class MockSoftRequestLimiter implements RequestLimiter<TestServiceRequestLimiter.MockRequest> {
        RequestLimiter<TestServiceRequestLimiter.MockRequest> limiter;

        int limitHitCount;

        MockSoftRequestLimiter(int limit) {
            this(GuavaRateLimiter.of(limit));
        }

        MockSoftRequestLimiter(RateLimiter limiter) {
            this.limiter = new com.twitter.distributedlog.limiter.ComposableRequestLimiter<TestServiceRequestLimiter.MockRequest>(limiter, new com.twitter.distributedlog.limiter.ComposableRequestLimiter.OverlimitFunction<TestServiceRequestLimiter.MockRequest>() {
                public void apply(TestServiceRequestLimiter.MockRequest request) throws OverCapacityException {
                    (limitHitCount)++;
                }
            }, new com.twitter.distributedlog.limiter.ComposableRequestLimiter.CostFunction<TestServiceRequestLimiter.MockRequest>() {
                public int apply(TestServiceRequestLimiter.MockRequest request) {
                    return request.getSize();
                }
            }, NullStatsLogger.INSTANCE);
        }

        @Override
        public void apply(TestServiceRequestLimiter.MockRequest op) throws OverCapacityException {
            limiter.apply(op);
        }

        public int getLimitHitCount() {
            return limitHitCount;
        }
    }

    @Test(timeout = 60000)
    public void testDynamicLimiter() throws Exception {
        final AtomicInteger id = new AtomicInteger(0);
        final DynamicDistributedLogConfiguration dynConf = new DynamicDistributedLogConfiguration(new com.twitter.distributedlog.config.ConcurrentConstConfiguration(new DistributedLogConfiguration()));
        DynamicRequestLimiter<TestServiceRequestLimiter.MockRequest> limiter = new DynamicRequestLimiter<TestServiceRequestLimiter.MockRequest>(dynConf, NullStatsLogger.INSTANCE, new SettableFeature("", 0)) {
            @Override
            public RequestLimiter<TestServiceRequestLimiter.MockRequest> build() {
                id.getAndIncrement();
                return new TestServiceRequestLimiter.MockRequestLimiter();
            }
        };
        limiter.initialize();
        Assert.assertEquals(1, id.get());
        dynConf.setProperty("test1", 1);
        Assert.assertEquals(2, id.get());
        dynConf.setProperty("test2", 2);
        Assert.assertEquals(3, id.get());
    }

    @Test(timeout = 60000)
    public void testDynamicLimiterWithDisabledFeature() throws Exception {
        final DynamicDistributedLogConfiguration dynConf = new DynamicDistributedLogConfiguration(new com.twitter.distributedlog.config.ConcurrentConstConfiguration(new DistributedLogConfiguration()));
        final TestServiceRequestLimiter.MockSoftRequestLimiter rateLimiter = new TestServiceRequestLimiter.MockSoftRequestLimiter(0);
        final SettableFeature disabledFeature = new SettableFeature("", 0);
        DynamicRequestLimiter<TestServiceRequestLimiter.MockRequest> limiter = new DynamicRequestLimiter<TestServiceRequestLimiter.MockRequest>(dynConf, NullStatsLogger.INSTANCE, disabledFeature) {
            @Override
            public RequestLimiter<TestServiceRequestLimiter.MockRequest> build() {
                return rateLimiter;
            }
        };
        limiter.initialize();
        Assert.assertEquals(0, rateLimiter.getLimitHitCount());
        // Not disabled, rate limiter was invoked
        limiter.apply(new TestServiceRequestLimiter.MockRequest(Integer.MAX_VALUE));
        Assert.assertEquals(1, rateLimiter.getLimitHitCount());
        // Disabled, rate limiter not invoked
        disabledFeature.set(1);
        limiter.apply(new TestServiceRequestLimiter.MockRequest(Integer.MAX_VALUE));
        Assert.assertEquals(1, rateLimiter.getLimitHitCount());
    }

    @Test(timeout = 60000)
    public void testDynamicLimiterWithException() throws Exception {
        final AtomicInteger id = new AtomicInteger(0);
        final DynamicDistributedLogConfiguration dynConf = new DynamicDistributedLogConfiguration(new com.twitter.distributedlog.config.ConcurrentConstConfiguration(new DistributedLogConfiguration()));
        DynamicRequestLimiter<TestServiceRequestLimiter.MockRequest> limiter = new DynamicRequestLimiter<TestServiceRequestLimiter.MockRequest>(dynConf, NullStatsLogger.INSTANCE, new SettableFeature("", 0)) {
            @Override
            public RequestLimiter<TestServiceRequestLimiter.MockRequest> build() {
                if ((id.incrementAndGet()) >= 2) {
                    throw new RuntimeException("exception in dynamic limiter build()");
                }
                return new TestServiceRequestLimiter.MockRequestLimiter();
            }
        };
        limiter.initialize();
        Assert.assertEquals(1, id.get());
        try {
            dynConf.setProperty("test1", 1);
            Assert.fail("should have thrown on config failure");
        } catch (RuntimeException ex) {
        }
        Assert.assertEquals(2, id.get());
    }

    @Test(timeout = 60000)
    public void testServiceRequestLimiter() throws Exception {
        TestServiceRequestLimiter.MockHardRequestLimiter limiter = new TestServiceRequestLimiter.MockHardRequestLimiter(new TestServiceRequestLimiter.CounterLimiter(1));
        limiter.apply(new TestServiceRequestLimiter.MockRequest());
        try {
            limiter.apply(new TestServiceRequestLimiter.MockRequest());
        } catch (OverCapacityException ex) {
        }
        Assert.assertEquals(1, limiter.getLimitHitCount());
    }

    @Test(timeout = 60000)
    public void testServiceRequestLimiterWithDefaultRate() throws Exception {
        TestServiceRequestLimiter.MockHardRequestLimiter limiter = new TestServiceRequestLimiter.MockHardRequestLimiter((-1));
        limiter.apply(new TestServiceRequestLimiter.MockRequest(Integer.MAX_VALUE));
        limiter.apply(new TestServiceRequestLimiter.MockRequest(Integer.MAX_VALUE));
        Assert.assertEquals(0, limiter.getLimitHitCount());
    }

    @Test(timeout = 60000)
    public void testServiceRequestLimiterWithZeroRate() throws Exception {
        TestServiceRequestLimiter.MockHardRequestLimiter limiter = new TestServiceRequestLimiter.MockHardRequestLimiter(0);
        try {
            limiter.apply(new TestServiceRequestLimiter.MockRequest(1));
            Assert.fail("should have failed with overcap");
        } catch (OverCapacityException ex) {
        }
        Assert.assertEquals(1, limiter.getLimitHitCount());
    }

    @Test(timeout = 60000)
    public void testChainedServiceRequestLimiter() throws Exception {
        TestServiceRequestLimiter.MockSoftRequestLimiter softLimiter = new TestServiceRequestLimiter.MockSoftRequestLimiter(new TestServiceRequestLimiter.CounterLimiter(1));
        TestServiceRequestLimiter.MockHardRequestLimiter hardLimiter = new TestServiceRequestLimiter.MockHardRequestLimiter(new TestServiceRequestLimiter.CounterLimiter(3));
        RequestLimiter<TestServiceRequestLimiter.MockRequest> limiter = new com.twitter.distributedlog.limiter.ChainedRequestLimiter.Builder<TestServiceRequestLimiter.MockRequest>().addLimiter(softLimiter).addLimiter(hardLimiter).build();
        Assert.assertEquals(0, softLimiter.getLimitHitCount());
        Assert.assertEquals(0, hardLimiter.getLimitHitCount());
        limiter.apply(new TestServiceRequestLimiter.MockRequest());
        Assert.assertEquals(0, softLimiter.getLimitHitCount());
        Assert.assertEquals(0, hardLimiter.getLimitHitCount());
        limiter.apply(new TestServiceRequestLimiter.MockRequest());
        Assert.assertEquals(1, softLimiter.getLimitHitCount());
        Assert.assertEquals(0, hardLimiter.getLimitHitCount());
        limiter.apply(new TestServiceRequestLimiter.MockRequest());
        Assert.assertEquals(2, softLimiter.getLimitHitCount());
        Assert.assertEquals(0, hardLimiter.getLimitHitCount());
        try {
            limiter.apply(new TestServiceRequestLimiter.MockRequest());
        } catch (OverCapacityException ex) {
        }
        Assert.assertEquals(3, softLimiter.getLimitHitCount());
        Assert.assertEquals(1, hardLimiter.getLimitHitCount());
    }
}

