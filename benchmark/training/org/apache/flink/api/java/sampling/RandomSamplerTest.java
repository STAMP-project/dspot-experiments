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
package org.apache.flink.api.java.sampling;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.flink.testutils.junit.RetryOnFailure;
import org.apache.flink.testutils.junit.RetryRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * This test suite try to verify whether all the random samplers work as we expected, which mainly focus on:
 * <ul>
 * <li>Does sampled result fit into input parameters? we check parameters like sample fraction, sample size,
 * w/o replacement, and so on.</li>
 * <li>Does sampled result randomly selected? we verify this by measure how much does it distributed on source data.
 * Run Kolmogorov-Smirnov (KS) test between the random samplers and default reference samplers which is distributed
 * well-proportioned on source data. If random sampler select elements randomly from source, it would distributed
 * well-proportioned on source data as well. The KS test will fail to strongly reject the null hypothesis that
 * the distributions of sampling gaps are the same.
 * </li>
 * </ul>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Kolmogorov%E2%80%93Smirnov_test">Kolmogorov Smirnov test</a>
 */
public class RandomSamplerTest {
    private static final int SOURCE_SIZE = 10000;

    private static final int DEFAULT_PARTITION_NUMBER = 10;

    private static final KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();

    private static final List<Double> source = new ArrayList<Double>(RandomSamplerTest.SOURCE_SIZE);

    @Rule
    public final RetryRule retryRule = new RetryRule();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private final List<Double>[] sourcePartitions = new List[RandomSamplerTest.DEFAULT_PARTITION_NUMBER];

    @Test(expected = IllegalArgumentException.class)
    public void testBernoulliSamplerWithUnexpectedFraction1() {
        verifySamplerFraction((-1), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBernoulliSamplerWithUnexpectedFraction2() {
        verifySamplerFraction(2, false);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testBernoulliSamplerFraction() {
        verifySamplerFraction(0.01, false);
        verifySamplerFraction(0.05, false);
        verifySamplerFraction(0.1, false);
        verifySamplerFraction(0.3, false);
        verifySamplerFraction(0.5, false);
        verifySamplerFraction(0.854, false);
        verifySamplerFraction(0.99, false);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testBernoulliSamplerDuplicateElements() {
        verifyRandomSamplerDuplicateElements(new BernoulliSampler<Double>(0.01));
        verifyRandomSamplerDuplicateElements(new BernoulliSampler<Double>(0.1));
        verifyRandomSamplerDuplicateElements(new BernoulliSampler<Double>(0.5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPoissonSamplerWithUnexpectedFraction1() {
        verifySamplerFraction((-1), true);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testPoissonSamplerFraction() {
        verifySamplerFraction(0.01, true);
        verifySamplerFraction(0.05, true);
        verifySamplerFraction(0.1, true);
        verifySamplerFraction(0.5, true);
        verifySamplerFraction(0.854, true);
        verifySamplerFraction(0.99, true);
        verifySamplerFraction(1.5, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReservoirSamplerUnexpectedSize1() {
        verifySamplerFixedSampleSize((-1), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReservoirSamplerUnexpectedSize2() {
        verifySamplerFixedSampleSize((-1), false);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testBernoulliSamplerDistribution() {
        verifyBernoulliSampler(0.01);
        verifyBernoulliSampler(0.05);
        verifyBernoulliSampler(0.1);
        verifyBernoulliSampler(0.5);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testPoissonSamplerDistribution() {
        verifyPoissonSampler(0.01);
        verifyPoissonSampler(0.05);
        verifyPoissonSampler(0.1);
        verifyPoissonSampler(0.5);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerSampledSize() {
        verifySamplerFixedSampleSize(1, true);
        verifySamplerFixedSampleSize(10, true);
        verifySamplerFixedSampleSize(100, true);
        verifySamplerFixedSampleSize(1234, true);
        verifySamplerFixedSampleSize(9999, true);
        verifySamplerFixedSampleSize(20000, true);
        verifySamplerFixedSampleSize(1, false);
        verifySamplerFixedSampleSize(10, false);
        verifySamplerFixedSampleSize(100, false);
        verifySamplerFixedSampleSize(1234, false);
        verifySamplerFixedSampleSize(9999, false);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerSampledSize2() {
        RandomSampler<Double> sampler = new ReservoirSamplerWithoutReplacement<Double>(20000);
        Iterator<Double> sampled = sampler.sample(RandomSamplerTest.source.iterator());
        Assert.assertTrue("ReservoirSamplerWithoutReplacement sampled output size should not beyond the source size.", ((getSize(sampled)) == (RandomSamplerTest.SOURCE_SIZE)));
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerDuplicateElements() {
        verifyRandomSamplerDuplicateElements(new ReservoirSamplerWithoutReplacement<Double>(100));
        verifyRandomSamplerDuplicateElements(new ReservoirSamplerWithoutReplacement<Double>(1000));
        verifyRandomSamplerDuplicateElements(new ReservoirSamplerWithoutReplacement<Double>(5000));
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerWithoutReplacement() {
        verifyReservoirSamplerWithoutReplacement(100, false);
        verifyReservoirSamplerWithoutReplacement(500, false);
        verifyReservoirSamplerWithoutReplacement(1000, false);
        verifyReservoirSamplerWithoutReplacement(5000, false);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerWithReplacement() {
        verifyReservoirSamplerWithReplacement(100, false);
        verifyReservoirSamplerWithReplacement(500, false);
        verifyReservoirSamplerWithReplacement(1000, false);
        verifyReservoirSamplerWithReplacement(5000, false);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerWithMultiSourcePartitions1() {
        initSourcePartition();
        verifyReservoirSamplerWithoutReplacement(100, true);
        verifyReservoirSamplerWithoutReplacement(500, true);
        verifyReservoirSamplerWithoutReplacement(1000, true);
        verifyReservoirSamplerWithoutReplacement(5000, true);
    }

    @Test
    @RetryOnFailure(times = 3)
    public void testReservoirSamplerWithMultiSourcePartitions2() {
        initSourcePartition();
        verifyReservoirSamplerWithReplacement(100, true);
        verifyReservoirSamplerWithReplacement(500, true);
        verifyReservoirSamplerWithReplacement(1000, true);
        verifyReservoirSamplerWithReplacement(5000, true);
    }
}

