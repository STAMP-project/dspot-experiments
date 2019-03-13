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
package org.apache.ignite.ml.dataset.feature;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import org.apache.ignite.ml.math.functions.IgniteFunction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ObjectHistogramTest {
    /**
     * Data first partition.
     */
    private double[] dataFirstPart = new double[]{ 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1, 2.0, 2.0, 3.0, 4.0, 5.0 };

    /**
     * Data second partition.
     */
    private double[] dataSecondPart = new double[]{ 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0, 1.0, 0.0, 1.0, 0.0, 5.0, 6.0 };

    /**
     *
     */
    private ObjectHistogram<Double> hist1;

    /**
     *
     */
    private ObjectHistogram<Double> hist2;

    /**
     *
     */
    @Test
    public void testBuckets() {
        testBuckets(hist1, new int[]{ 0, 1, 2, 3, 4, 5 }, new int[]{ 4, 3, 2, 1, 1, 1 });
        testBuckets(hist2, new int[]{ 0, 1, 5, 6 }, new int[]{ 6, 5, 1, 1 });
    }

    /**
     *
     */
    @Test
    public void testAdd() {
        double val = 100.0;
        hist1.addElement(val);
        Optional<Double> cntr = hist1.getValue(computeBucket(val));
        Assert.assertTrue(cntr.isPresent());
        Assert.assertEquals(1, cntr.get().intValue());
    }

    /**
     *
     */
    @Test
    public void testAddHist() {
        ObjectHistogram<Double> res = hist1.plus(hist2);
        testBuckets(res, new int[]{ 0, 1, 2, 3, 4, 5, 6 }, new int[]{ 10, 8, 2, 1, 1, 2, 1 });
    }

    /**
     *
     */
    @Test
    public void testDistributionFunction() {
        TreeMap<Integer, Double> distribution = hist1.computeDistributionFunction();
        int[] buckets = new int[distribution.size()];
        double[] sums = new double[distribution.size()];
        int ptr = 0;
        for (int bucket : distribution.keySet()) {
            sums[ptr] = distribution.get(bucket);
            buckets[(ptr++)] = bucket;
        }
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5 }, buckets);
        Assert.assertArrayEquals(new double[]{ 4.0, 7.0, 9.0, 10.0, 11.0, 12.0 }, sums, 0.01);
    }

    /**
     *
     */
    @Test
    public void testOfSum() {
        IgniteFunction<Double, Integer> bucketMap = ( x) -> ((int) ((Math.ceil((x * 100))) % 100));
        IgniteFunction<Double, Double> cntrMap = ( x) -> Math.pow(x, 2);
        ObjectHistogram<Double> forAllHistogram = new ObjectHistogramTest.TestHist2();
        Random rnd = new Random();
        List<ObjectHistogram<Double>> partitions = new ArrayList<>();
        int cntOfPartitions = rnd.nextInt(100);
        int sizeOfDataset = rnd.nextInt(10000);
        for (int i = 0; i < cntOfPartitions; i++)
            partitions.add(new ObjectHistogramTest.TestHist2());

        for (int i = 0; i < sizeOfDataset; i++) {
            double objVal = rnd.nextDouble();
            forAllHistogram.addElement(objVal);
            partitions.get(rnd.nextInt(partitions.size())).addElement(objVal);
        }
        Optional<ObjectHistogram<Double>> leftSum = partitions.stream().reduce(ObjectHistogram::plus);
        Optional<ObjectHistogram<Double>> rightSum = partitions.stream().reduce(( x, y) -> y.plus(x));
        Assert.assertTrue(leftSum.isPresent());
        Assert.assertTrue(rightSum.isPresent());
        Assert.assertTrue(forAllHistogram.isEqualTo(leftSum.get()));
        Assert.assertTrue(forAllHistogram.isEqualTo(rightSum.get()));
        Assert.assertTrue(leftSum.get().isEqualTo(rightSum.get()));
    }

    /**
     *
     */
    private static class TestHist1 extends ObjectHistogram<Double> {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = 2397005559193012602L;

        /**
         * {@inheritDoc }
         */
        @Override
        public Integer mapToBucket(Double obj) {
            return ((int) (Math.rint(obj)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Double mapToCounter(Double obj) {
            return 1.0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ObjectHistogram<Double> newInstance() {
            return new ObjectHistogramTest.TestHist1();
        }
    }

    /**
     *
     */
    private static class TestHist2 extends ObjectHistogram<Double> {
        /**
         * Serial version uid.
         */
        private static final long serialVersionUID = -2080037140817825107L;

        /**
         * {@inheritDoc }
         */
        @Override
        public Integer mapToBucket(Double x) {
            return ((int) ((Math.ceil((x * 100))) % 100));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Double mapToCounter(Double x) {
            return Math.pow(x, 2);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ObjectHistogram<Double> newInstance() {
            return new ObjectHistogramTest.TestHist2();
        }
    }
}

