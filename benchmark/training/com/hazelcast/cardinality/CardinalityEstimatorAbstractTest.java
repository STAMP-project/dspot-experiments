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
package com.hazelcast.cardinality;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.hazelcast.test.HazelcastTestSupport;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runners.Parameterized;


public abstract class CardinalityEstimatorAbstractTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    private CardinalityEstimator estimator;

    @Parameterized.Parameter(0)
    public Config config;

    @Test
    public void estimate() {
        Assert.assertEquals(0, estimator.estimate());
    }

    @Test
    public void estimateAsync() throws Exception {
        Assert.assertEquals(0, estimator.estimateAsync().get().longValue());
    }

    @Test
    public void add() {
        estimator.add(1L);
        Assert.assertEquals(1L, estimator.estimate());
        estimator.add(1L);
        estimator.add(1L);
        Assert.assertEquals(1L, estimator.estimate());
        estimator.add(2L);
        estimator.add(3L);
        Assert.assertEquals(3L, estimator.estimate());
        estimator.add("Test");
        Assert.assertEquals(4L, estimator.estimate());
    }

    @Test
    public void addAsync() throws Exception {
        estimator.addAsync(1L).get();
        Assert.assertEquals(1L, estimator.estimateAsync().get().longValue());
        estimator.addAsync(1L).get();
        estimator.addAsync(1L).get();
        Assert.assertEquals(1L, estimator.estimateAsync().get().longValue());
        estimator.addAsync(2L).get();
        estimator.addAsync(3L);
        Assert.assertEquals(3L, estimator.estimateAsync().get().longValue());
        estimator.addAsync("Test").get();
        Assert.assertEquals(4L, estimator.estimateAsync().get().longValue());
    }

    @Test(expected = HazelcastSerializationException.class)
    public void addCustomObject() {
        Assume.assumeTrue(((config) == null));
        estimator.add(new CardinalityEstimatorAbstractTest.CustomObject(1, 2));
    }

    @Test
    public void addCustomObjectRegisteredAsync() throws Exception {
        Assume.assumeTrue(((config) != null));
        Assert.assertEquals(0L, estimator.estimate());
        estimator.add(new CardinalityEstimatorAbstractTest.CustomObject(1, 2));
        Assert.assertEquals(1L, estimator.estimate());
    }

    @Test
    public void addCustomObjectRegistered() {
        Assume.assumeTrue(((config) != null));
        Assert.assertEquals(0L, estimator.estimate());
        estimator.add(new CardinalityEstimatorAbstractTest.CustomObject(1, 2));
        Assert.assertEquals(1L, estimator.estimate());
    }

    private class CustomObject {
        private final int x;

        private final int y;

        private CustomObject(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class CustomObjectSerializer implements StreamSerializer<CardinalityEstimatorAbstractTest.CustomObject> {
        @Override
        public int getTypeId() {
            return 1;
        }

        @Override
        public void destroy() {
        }

        @Override
        public void write(ObjectDataOutput out, CardinalityEstimatorAbstractTest.CustomObject object) throws IOException {
            out.writeLong((((object.x) << (Bits.INT_SIZE_IN_BYTES)) | (object.y)));
        }

        @Override
        public CardinalityEstimatorAbstractTest.CustomObject read(ObjectDataInput in) throws IOException {
            // not needed
            throw new UnsupportedOperationException();
        }
    }
}

