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
package org.apache.mahout.math.jet.random;


import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.MahoutTestCase;
import org.junit.Test;


public final class ExponentialTest extends MahoutTestCase {
    @Test
    public void consistency() {
        Exponential dist = new Exponential(1, RandomUtils.getRandom());
        // deciles computed using R
        double[] breaks = new double[]{ 0.1053605, 0.2231436, 0.3566749, 0.5108256, 0.6931472, 0.9162907, 1.2039728, 1.6094379, 2.3025851 };
        for (double lambda : new double[]{ 0.01, 0.1, 1, 2, 5, 100 }) {
            dist.setState(lambda);
            DistributionChecks.checkDistribution(dist, breaks, 0, (1 / lambda), 10000);
        }
    }

    @Test
    public void testCdf() {
        Exponential dist = new Exponential(5.0, RandomUtils.getRandom());
        for (int i = 0; i < 1000; i++) {
            double x = i / 50.0;
            assertEquals((1 - (Math.exp(((-x) * 5.0)))), dist.cdf(x), 1.0E-9);
        }
    }

    @Test
    public void testPdf() {
        ExponentialTest.checkPdf(new Exponential(13.0, null), 13.0);
    }

    @Test
    public void testSetState() {
        Exponential dist = new Exponential(13.0, null);
        for (double lambda = 0.1; lambda < 1000; lambda *= 1.3) {
            dist.setState(lambda);
            ExponentialTest.checkPdf(dist, lambda);
        }
    }

    @Test
    public void testNextDouble() throws Exception {
        double[] x = new double[]{ -0.01, 0.1053605, 0.2231436, 0.3566749, 0.5108256, 0.6931472, 0.9162907, 1.2039728, 1.6094379, 2.3025851 };
        Exponential dist = new Exponential(1, RandomUtils.getRandom());
        for (double lambda : new double[]{ 13.0, 0.02, 1.6 }) {
            dist.setState(lambda);
            ExponentialTest.checkEmpiricalDistribution(dist, 10000, lambda);
            DistributionChecks.checkDistribution(dist, x, 0, (1 / lambda), 10000);
        }
    }

    @Test
    public void testToString() {
        assertEquals("org.apache.mahout.math.jet.random.Exponential(3.1000)", new Exponential(3.1, null).toString());
        assertEquals("org.apache.mahout.math.jet.random.Exponential(3.1000)", new Exponential(3.1, null).toString());
    }
}

