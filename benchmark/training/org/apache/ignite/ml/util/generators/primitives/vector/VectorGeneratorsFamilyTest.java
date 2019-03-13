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
package org.apache.ignite.ml.util.generators.primitives.vector;


import VectorGeneratorsFamily.VectorWithDistributionId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.VectorUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link VectorGeneratorsFamily}.
 */
public class VectorGeneratorsFamilyTest {
    /**
     *
     */
    @Test
    public void testSelection() {
        VectorGeneratorsFamily family = new VectorGeneratorsFamily.Builder().add(() -> VectorUtils.of(1.0, 2.0), 0.5).add(() -> VectorUtils.of(1.0, 2.0), 0.25).add(() -> VectorUtils.of(1.0, 4.0), 0.25).build(0L);
        Map<Integer, Vector> counters = new HashMap<>();
        for (int i = 0; i < 3; i++)
            counters.put(i, VectorUtils.zeroes(2));

        int N = 50000;
        IntStream.range(0, N).forEach(( i) -> {
            VectorGeneratorsFamily.VectorWithDistributionId vector = family.getWithId();
            int id = vector.distributionId();
            counters.put(id, counters.get(id).plus(vector.vector()));
        });
        for (int i = 0; i < 3; i++)
            counters.put(i, counters.get(i).divide(N));

        Assert.assertArrayEquals(new double[]{ 0.5, 1.0 }, counters.get(0).asArray(), 0.01);
        Assert.assertArrayEquals(new double[]{ 0.25, 0.5 }, counters.get(1).asArray(), 0.01);
        Assert.assertArrayEquals(new double[]{ 0.25, 1.0 }, counters.get(2).asArray(), 0.01);
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParameters1() {
        new VectorGeneratorsFamily.Builder().build();
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParameters2() {
        new VectorGeneratorsFamily.Builder().add(() -> VectorUtils.of(1.0), (-1.0)).build();
    }

    /**
     *
     */
    @Test
    public void testMap() {
        VectorGeneratorsFamily family = new VectorGeneratorsFamily.Builder().add(() -> VectorUtils.of(1.0, 2.0)).map(( g) -> g.move(VectorUtils.of(1, (-1)))).build(0L);
        Assert.assertArrayEquals(new double[]{ 2.0, 1.0 }, family.get().asArray(), 1.0E-7);
    }

    /**
     *
     */
    @Test
    public void testGet() {
        VectorGeneratorsFamily family = new VectorGeneratorsFamily.Builder().add(() -> VectorUtils.of(0.0)).add(() -> VectorUtils.of(1.0)).add(() -> VectorUtils.of(2.0)).build(0L);
        Set<Double> validValues = DoubleStream.of(0.0, 1.0, 2.0).boxed().collect(Collectors.toSet());
        for (int i = 0; i < 100; i++) {
            Vector vector = family.get();
            Assert.assertTrue(validValues.contains(vector.get(0)));
        }
    }

    /**
     *
     */
    @Test
    public void testAsDataStream() {
        VectorGeneratorsFamily family = new VectorGeneratorsFamily.Builder().add(() -> VectorUtils.of(0.0)).add(() -> VectorUtils.of(1.0)).add(() -> VectorUtils.of(2.0)).build(0L);
        family.asDataStream().labeled().limit(100).forEach(( v) -> {
            assertEquals(v.features().get(0), v.label(), 1.0E-7);
        });
    }
}

