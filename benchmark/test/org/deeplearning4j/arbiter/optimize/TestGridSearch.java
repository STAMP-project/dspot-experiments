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
package org.deeplearning4j.arbiter.optimize;


import DataSetIteratorFactoryProvider.FACTORY_KEY;
import GridSearchCandidateGenerator.Mode;
import java.util.HashMap;
import java.util.Map;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.generator.GridSearchCandidateGenerator;
import org.junit.Assert;
import org.junit.Test;


public class TestGridSearch {
    @Test
    public void testIndexing() {
        int[] nValues = new int[]{ 2, 3 };
        int prod = 2 * 3;
        double[][] expVals = new double[][]{ new double[]{ 0.0, 0.0 }, new double[]{ 1.0, 0.0 }, new double[]{ 0.0, 0.5 }, new double[]{ 1.0, 0.5 }, new double[]{ 0.0, 1.0 }, new double[]{ 1.0, 1.0 } };
        for (int i = 0; i < prod; i++) {
            double[] out = GridSearchCandidateGenerator.indexToValues(nValues, i, prod);
            double[] exp = expVals[i];
            Assert.assertArrayEquals(exp, out, 1.0E-4);
        }
    }

    @Test
    public void testGeneration() throws Exception {
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, new HashMap());
        // Define configuration:
        CandidateGenerator candidateGenerator = new GridSearchCandidateGenerator(new BraninFunction.BraninSpace(), 4, Mode.Sequential, commands);
        // Check sequential:
        double[] expValuesFirst = new double[]{ -5, 0, 5, 10 };// Range: -5 to +10, with 4 values

        double[] expValuesSecond = new double[]{ 0, 5, 10, 15 };// Range: 0 to +15, with 4 values

        for (int i = 0; i < (4 * 4); i++) {
            BraninFunction.BraninConfig conf = ((BraninFunction.BraninConfig) (candidateGenerator.getCandidate().getValue()));
            double expF = expValuesFirst[(i % 4)];// Changes most rapidly

            double expS = expValuesSecond[(i / 4)];
            double actF = getX1();
            double actS = getX2();
            Assert.assertEquals(expF, actF, 1.0E-4);
            Assert.assertEquals(expS, actS, 1.0E-4);
        }
        // Check random order. specifically: check that all values are generated, in some order
        double[][] orderedOutput = new double[16][2];
        for (int i = 0; i < (expValuesFirst.length); i++) {
            for (int j = 0; j < (expValuesSecond.length); j++) {
                orderedOutput[((4 * j) + i)][0] = expValuesFirst[i];
                orderedOutput[((4 * j) + i)][1] = expValuesSecond[j];
            }
        }
        candidateGenerator = new GridSearchCandidateGenerator(new BraninFunction.BraninSpace(), 4, Mode.RandomOrder, commands);
        boolean[] seen = new boolean[16];
        int seenCount = 0;
        for (int i = 0; i < (4 * 4); i++) {
            Assert.assertTrue(candidateGenerator.hasMoreCandidates());
            BraninFunction.BraninConfig config = ((BraninFunction.BraninConfig) (candidateGenerator.getCandidate().getValue()));
            double x1 = getX1();
            double x2 = getX2();
            // Work out which of the values this is...
            boolean matched = false;
            for (int j = 0; j < 16; j++) {
                if (((Math.abs(((orderedOutput[j][0]) - x1))) < 1.0E-5) && ((Math.abs(((orderedOutput[j][1]) - x2))) < 1.0E-5)) {
                    matched = true;
                    if (seen[j])
                        Assert.fail("Same candidate generated multiple times");

                    seen[j] = true;
                    seenCount++;
                    break;
                }
            }
            Assert.assertTrue((((("Candidate " + x1) + ", ") + x2) + " not found; invalid?"), matched);
        }
        Assert.assertFalse(candidateGenerator.hasMoreCandidates());
        Assert.assertEquals(16, seenCount);
    }
}

