/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.util.generators;


import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.ignite.ml.dataset.DatasetBuilder;
import org.apache.ignite.ml.dataset.UpstreamEntry;
import org.apache.ignite.ml.dataset.UpstreamTransformer;
import org.apache.ignite.ml.dataset.UpstreamTransformerBuilder;
import org.apache.ignite.ml.environment.LearningEnvironment;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.apache.ignite.ml.structures.LabeledVector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DataStreamGenerator}.
 */
public class DataStreamGeneratorTest {
    /**
     *
     */
    @Test
    public void testUnlabeled() {
        DataStreamGenerator generator = new DataStreamGenerator() {
            @Override
            public Stream<LabeledVector<Double>> labeled() {
                return Stream.generate(() -> new LabeledVector(VectorUtils.of(1.0, 2.0), 100.0));
            }
        };
        generator.unlabeled().limit(100).forEach(( v) -> {
            assertArrayEquals(new double[]{ 1.0, 2.0 }, v.asArray(), 1.0E-7);
        });
    }

    /**
     *
     */
    @Test
    public void testLabeled() {
        DataStreamGenerator generator = new DataStreamGenerator() {
            @Override
            public Stream<LabeledVector<Double>> labeled() {
                return Stream.generate(() -> new LabeledVector(VectorUtils.of(1.0, 2.0), 100.0));
            }
        };
        generator.labeled(( v) -> -100.0).limit(100).forEach(( v) -> {
            assertArrayEquals(new double[]{ 1.0, 2.0 }, v.features().asArray(), 1.0E-7);
            assertEquals((-100.0), v.label(), 1.0E-7);
        });
    }

    /**
     *
     */
    @Test
    public void testMapVectors() {
        DataStreamGenerator generator = new DataStreamGenerator() {
            @Override
            public Stream<LabeledVector<Double>> labeled() {
                return Stream.generate(() -> new LabeledVector(VectorUtils.of(1.0, 2.0), 100.0));
            }
        };
        generator.mapVectors(( v) -> VectorUtils.of(2.0, 1.0)).labeled().limit(100).forEach(( v) -> {
            assertArrayEquals(new double[]{ 2.0, 1.0 }, v.features().asArray(), 1.0E-7);
            assertEquals(100.0, v.label(), 1.0E-7);
        });
    }

    /**
     *
     */
    @Test
    public void testBlur() {
        DataStreamGenerator generator = new DataStreamGenerator() {
            @Override
            public Stream<LabeledVector<Double>> labeled() {
                return Stream.generate(() -> new LabeledVector(VectorUtils.of(1.0, 2.0), 100.0));
            }
        };
        generator.blur(() -> 1.0).labeled().limit(100).forEach(( v) -> {
            assertArrayEquals(new double[]{ 2.0, 3.0 }, v.features().asArray(), 1.0E-7);
            assertEquals(100.0, v.label(), 1.0E-7);
        });
    }

    /**
     *
     */
    @Test
    public void testAsMap() {
        DataStreamGenerator generator = new DataStreamGenerator() {
            @Override
            public Stream<LabeledVector<Double>> labeled() {
                return Stream.generate(() -> new LabeledVector(VectorUtils.of(1.0, 2.0), 100.0));
            }
        };
        int N = 100;
        Map<Vector, Double> dataset = generator.asMap(N);
        Assert.assertEquals(N, dataset.size());
        dataset.forEach(( vector, label) -> {
            assertArrayEquals(new double[]{ 1.0, 2.0 }, vector.asArray(), 1.0E-7);
            assertEquals(100.0, label, 1.0E-7);
        });
    }

    /**
     *
     */
    @Test
    public void testAsDatasetBuilder() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        DataStreamGenerator generator = new DataStreamGenerator() {
            @Override
            public Stream<LabeledVector<Double>> labeled() {
                return Stream.generate(() -> {
                    int value = counter.getAndIncrement();
                    return new LabeledVector(VectorUtils.of(value), (((double) (value)) % 2));
                });
            }
        };
        int N = 100;
        counter.set(0);
        DatasetBuilder<Vector, Double> b1 = generator.asDatasetBuilder(N, 2);
        counter.set(0);
        DatasetBuilder<Vector, Double> b2 = generator.asDatasetBuilder(N, ( v, l) -> l == 0, 2);
        counter.set(0);
        DatasetBuilder<Vector, Double> b3 = generator.asDatasetBuilder(N, ( v, l) -> l == 1, 2, new UpstreamTransformerBuilder() {
            @Override
            public UpstreamTransformer build(LearningEnvironment env) {
                return new DataStreamGeneratorTest.UpstreamTransformerForTest();
            }
        });
        checkDataset(N, b1, ( v) -> (((Double) (v.label())) == 0) || (((Double) (v.label())) == 1));
        checkDataset((N / 2), b2, ( v) -> ((Double) (v.label())) == 0);
        checkDataset((N / 2), b3, ( v) -> ((Double) (v.label())) < 0);
    }

    /**
     *
     */
    private static class UpstreamTransformerForTest implements UpstreamTransformer {
        @Override
        public Stream<UpstreamEntry> transform(Stream<UpstreamEntry> upstream) {
            return upstream.map(( entry) -> new UpstreamEntry<>(entry.getKey(), (-((double) (entry.getValue())))));
        }
    }
}

