/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.clustering;


import com.google.common.collect.Lists;
import java.util.Collection;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class TestGaussianAccumulators extends MahoutTestCase {
    private static final Logger log = LoggerFactory.getLogger(TestGaussianAccumulators.class);

    private Collection<VectorWritable> sampleData = Lists.newArrayList();

    private int sampleN;

    private Vector sampleMean;

    private Vector sampleStd;

    @Test
    public void testAccumulatorNoSamples() {
        GaussianAccumulator accumulator0 = new RunningSumsGaussianAccumulator();
        GaussianAccumulator accumulator1 = new OnlineGaussianAccumulator();
        accumulator0.compute();
        accumulator1.compute();
        assertEquals("N", accumulator0.getN(), accumulator1.getN(), MahoutTestCase.EPSILON);
        assertEquals("Means", accumulator0.getMean(), accumulator1.getMean());
        assertEquals("Avg Stds", accumulator0.getAverageStd(), accumulator1.getAverageStd(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testAccumulatorOneSample() {
        GaussianAccumulator accumulator0 = new RunningSumsGaussianAccumulator();
        GaussianAccumulator accumulator1 = new OnlineGaussianAccumulator();
        Vector sample = new DenseVector(2);
        accumulator0.observe(sample, 1.0);
        accumulator1.observe(sample, 1.0);
        accumulator0.compute();
        accumulator1.compute();
        assertEquals("N", accumulator0.getN(), accumulator1.getN(), MahoutTestCase.EPSILON);
        assertEquals("Means", accumulator0.getMean(), accumulator1.getMean());
        assertEquals("Avg Stds", accumulator0.getAverageStd(), accumulator1.getAverageStd(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testOLAccumulatorResults() {
        GaussianAccumulator accumulator = new OnlineGaussianAccumulator();
        for (VectorWritable vw : sampleData) {
            accumulator.observe(vw.get(), 1.0);
        }
        accumulator.compute();
        TestGaussianAccumulators.log.info("OL Observed {} samples m=[{}, {}] sd=[{}, {}]", accumulator.getN(), accumulator.getMean().get(0), accumulator.getMean().get(1), accumulator.getStd().get(0), accumulator.getStd().get(1));
        assertEquals("OL N", sampleN, accumulator.getN(), MahoutTestCase.EPSILON);
        assertEquals("OL Mean", sampleMean.zSum(), accumulator.getMean().zSum(), MahoutTestCase.EPSILON);
        assertEquals("OL Std", sampleStd.zSum(), accumulator.getStd().zSum(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testRSAccumulatorResults() {
        GaussianAccumulator accumulator = new RunningSumsGaussianAccumulator();
        for (VectorWritable vw : sampleData) {
            accumulator.observe(vw.get(), 1.0);
        }
        accumulator.compute();
        TestGaussianAccumulators.log.info("RS Observed {} samples m=[{}, {}] sd=[{}, {}]", ((int) (accumulator.getN())), accumulator.getMean().get(0), accumulator.getMean().get(1), accumulator.getStd().get(0), accumulator.getStd().get(1));
        assertEquals("OL N", sampleN, accumulator.getN(), MahoutTestCase.EPSILON);
        assertEquals("OL Mean", sampleMean.zSum(), accumulator.getMean().zSum(), MahoutTestCase.EPSILON);
        assertEquals("OL Std", sampleStd.zSum(), accumulator.getStd().zSum(), 1.0E-4);
    }

    @Test
    public void testAccumulatorWeightedResults() {
        GaussianAccumulator accumulator0 = new RunningSumsGaussianAccumulator();
        GaussianAccumulator accumulator1 = new OnlineGaussianAccumulator();
        for (VectorWritable vw : sampleData) {
            accumulator0.observe(vw.get(), 0.5);
            accumulator1.observe(vw.get(), 0.5);
        }
        accumulator0.compute();
        accumulator1.compute();
        assertEquals("N", accumulator0.getN(), accumulator1.getN(), MahoutTestCase.EPSILON);
        assertEquals("Means", accumulator0.getMean().zSum(), accumulator1.getMean().zSum(), MahoutTestCase.EPSILON);
        assertEquals("Stds", accumulator0.getStd().zSum(), accumulator1.getStd().zSum(), 0.001);
        assertEquals("Variance", accumulator0.getVariance().zSum(), accumulator1.getVariance().zSum(), 0.01);
    }

    @Test
    public void testAccumulatorWeightedResults2() {
        GaussianAccumulator accumulator0 = new RunningSumsGaussianAccumulator();
        GaussianAccumulator accumulator1 = new OnlineGaussianAccumulator();
        for (VectorWritable vw : sampleData) {
            accumulator0.observe(vw.get(), 1.5);
            accumulator1.observe(vw.get(), 1.5);
        }
        accumulator0.compute();
        accumulator1.compute();
        assertEquals("N", accumulator0.getN(), accumulator1.getN(), MahoutTestCase.EPSILON);
        assertEquals("Means", accumulator0.getMean().zSum(), accumulator1.getMean().zSum(), MahoutTestCase.EPSILON);
        assertEquals("Stds", accumulator0.getStd().zSum(), accumulator1.getStd().zSum(), 0.001);
        assertEquals("Variance", accumulator0.getVariance().zSum(), accumulator1.getVariance().zSum(), 0.01);
    }
}

