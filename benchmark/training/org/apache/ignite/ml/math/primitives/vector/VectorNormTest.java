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
package org.apache.ignite.ml.math.primitives.vector;


import Vector.Element;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class VectorNormTest {
    /**
     *
     */
    @Test
    public void normalizeTest() {
        normalizeTest(2, ( val, len) -> val / len, Vector::normalize);
    }

    /**
     *
     */
    @Test
    public void normalizePowerTest() {
        for (double pow : new double[]{ 0, 0.5, 1, 2, 2.5, Double.POSITIVE_INFINITY })
            normalizeTest(pow, ( val, norm) -> val / norm, ( v) -> v.normalize(pow));

    }

    /**
     *
     */
    @Test
    public void logNormalizeTest() {
        normalizeTest(2, ( val, len) -> (Math.log1p(val)) / (len * (Math.log(2))), Vector::logNormalize);
    }

    /**
     *
     */
    @Test
    public void logNormalizePowerTest() {
        for (double pow : new double[]{ 1.1, 2, 2.5 })
            normalizeTest(pow, ( val, norm) -> (Math.log1p(val)) / (norm * (Math.log(pow))), ( v) -> v.logNormalize(pow));

    }

    /**
     *
     */
    @Test
    public void kNormTest() {
        for (double pow : new double[]{ 0, 0.5, 1, 2, 2.5, Double.POSITIVE_INFINITY })
            toDoubleTest(pow, ( ref) -> new VectorNormTest.Norm(ref, pow).calculate(), ( v) -> v.kNorm(pow));

    }

    /**
     *
     */
    @Test
    public void getLengthSquaredTest() {
        toDoubleTest(2.0, ( ref) -> new org.apache.ignite.ml.math.primitives.vector.Norm(ref, 2).sumPowers(), Vector::getLengthSquared);
    }

    /**
     *
     */
    @Test
    public void getDistanceSquaredTest() {
        consumeSampleVectors(( v, desc) -> {
            new VectorImplementationsTest.ElementsChecker(v, desc);// IMPL NOTE this initialises vector

            final int size = v.size();
            final Vector vOnHeap = new DenseVector(size);
            invertValues(v, vOnHeap);
            for (int idx = 0; idx < size; idx++) {
                final double exp = v.get(idx);
                final int idxMirror = (size - 1) - idx;
                Assert.assertTrue(((("On heap vector difference at " + desc) + ", idx ") + idx), ((exp - (vOnHeap.get(idxMirror))) == 0));
            }
            final double exp = vOnHeap.minus(v).getLengthSquared();// IMPL NOTE this won't mutate vOnHeap

            final VectorImplementationsTest.Metric metric = new VectorImplementationsTest.Metric(exp, v.getDistanceSquared(vOnHeap));
            Assert.assertTrue(((("On heap vector not close enough at " + desc) + ", ") + metric), metric.closeEnough());
        });
    }

    /**
     *
     */
    @Test
    public void dotTest() {
        consumeSampleVectors(( v, desc) -> {
            new VectorImplementationsTest.ElementsChecker(v, desc);// IMPL NOTE this initialises vector

            final int size = v.size();
            final Vector v1 = new DenseVector(size);
            invertValues(v, v1);
            final double actual = v.dot(v1);
            double exp = 0;
            for (Vector.Element e : v.all())
                exp += (e.get()) * (v1.get(e.index()));

            final VectorImplementationsTest.Metric metric = new VectorImplementationsTest.Metric(exp, actual);
            Assert.assertTrue(((("Dot product not close enough at " + desc) + ", ") + metric), metric.closeEnough());
        });
    }

    /**
     *
     */
    private static class Norm {
        /**
         *
         */
        private final double[] arr;

        /**
         *
         */
        private final Double pow;

        /**
         *
         */
        Norm(double[] arr, double pow) {
            this.arr = arr;
            this.pow = pow;
        }

        /**
         *
         */
        double calculate() {
            if (pow.equals(0.0))
                return countNonZeroes();
            // IMPL NOTE this is beautiful if you think of it

            if (pow.equals(Double.POSITIVE_INFINITY))
                return maxAbs();

            return Math.pow(sumPowers(), (1 / (pow)));
        }

        /**
         *
         */
        double sumPowers() {
            if (pow.equals(0.0))
                return countNonZeroes();

            double norm = 0;
            for (double val : arr)
                norm += ((pow) == 1) ? Math.abs(val) : Math.pow(val, pow);

            return norm;
        }

        /**
         *
         */
        private int countNonZeroes() {
            int cnt = 0;
            final Double zero = 0.0;
            for (double val : arr)
                if (!(zero.equals(val)))
                    cnt++;


            return cnt;
        }

        /**
         *
         */
        private double maxAbs() {
            double res = 0;
            for (double val : arr) {
                final double abs = Math.abs(val);
                if (abs > res)
                    res = abs;

            }
            return res;
        }
    }
}

