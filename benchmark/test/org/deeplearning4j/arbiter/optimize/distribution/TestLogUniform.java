/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.arbiter.optimize.distribution;


import org.apache.commons.math3.distribution.RealDistribution;
import org.junit.Assert;
import org.junit.Test;


public class TestLogUniform {
    @Test
    public void testSimple() {
        double min = 0.5;
        double max = 3;
        double logMin = Math.log(min);
        double logMax = Math.log(max);
        RealDistribution rd = new LogUniformDistribution(min, max);
        for (double d = 0.1; d <= 3.5; d += 0.1) {
            double density = rd.density(d);
            double cumulative = rd.cumulativeProbability(d);
            double dExp;
            double cumExp;
            if (d < min) {
                dExp = 0;
                cumExp = 0;
            } else
                if (d > max) {
                    dExp = 0;
                    cumExp = 1;
                } else {
                    dExp = 1.0 / (d * (logMax - logMin));
                    cumExp = ((Math.log(d)) - logMin) / (logMax - logMin);
                }

            Assert.assertTrue((dExp >= 0));
            Assert.assertTrue((cumExp >= 0));
            Assert.assertTrue((cumExp <= 1.0));
            Assert.assertEquals(dExp, density, 1.0E-5);
            Assert.assertEquals(cumExp, cumulative, 1.0E-5);
        }
        rd.reseedRandomGenerator(12345);
        for (int i = 0; i < 100; i++) {
            double d = rd.sample();
            Assert.assertTrue((d >= min));
            Assert.assertTrue((d <= max));
        }
    }
}

