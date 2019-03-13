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
package org.apache.mahout.classifier.sequencelearning.hmm;


import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.Matrix;
import org.junit.Test;


public class HMMAlgorithmsTest extends HMMTestBase {
    /**
     * Test the forward algorithm by comparing the alpha values with the values
     * obtained from HMM R model. We test the test observation sequence "O1" "O0"
     * "O2" "O2" "O0" "O0" "O1" by comparing the generated alpha values to the
     * R-generated "reference".
     */
    @Test
    public void testForwardAlgorithm() {
        // intialize the expected alpha values
        double[][] alphaExpectedA = new double[][]{ new double[]{ 0.02, 0.0392, 0.002438, 3.5456E-4, 0.0011554672, 7.158497E-4, 4.614927E-5 }, new double[]{ 0.01, 0.0054, 0.001824, 6.9486E-4, 7.586904E-4, 2.514137E-4, 1.721505E-5 }, new double[]{ 0.32, 0.0262, 0.002542, 3.8026E-4, 1.360234E-4, 3.002345E-5, 9.659608E-5 }, new double[]{ 0.03, 0.0, 0.013428, 0.00951084, 0.0, 0.0, 2.428986E-5 } };
        // fetch the alpha matrix using the forward algorithm
        Matrix alpha = HmmAlgorithms.forwardAlgorithm(getModel(), getSequence(), false);
        // first do some basic checking
        assertNotNull(alpha);
        assertEquals(4, alpha.numCols());
        assertEquals(7, alpha.numRows());
        // now compare the resulting matrices
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 7; ++j) {
                assertEquals(alphaExpectedA[i][j], alpha.get(j, i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testLogScaledForwardAlgorithm() {
        // intialize the expected alpha values
        double[][] alphaExpectedA = new double[][]{ new double[]{ 0.02, 0.0392, 0.002438, 3.5456E-4, 0.0011554672, 7.158497E-4, 4.614927E-5 }, new double[]{ 0.01, 0.0054, 0.001824, 6.9486E-4, 7.586904E-4, 2.514137E-4, 1.721505E-5 }, new double[]{ 0.32, 0.0262, 0.002542, 3.8026E-4, 1.360234E-4, 3.002345E-5, 9.659608E-5 }, new double[]{ 0.03, 0.0, 0.013428, 0.00951084, 0.0, 0.0, 2.428986E-5 } };
        // fetch the alpha matrix using the forward algorithm
        Matrix alpha = HmmAlgorithms.forwardAlgorithm(getModel(), getSequence(), true);
        // first do some basic checking
        assertNotNull(alpha);
        assertEquals(4, alpha.numCols());
        assertEquals(7, alpha.numRows());
        // now compare the resulting matrices
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 7; ++j) {
                assertEquals(Math.log(alphaExpectedA[i][j]), alpha.get(j, i), MahoutTestCase.EPSILON);
            }
        }
    }

    /**
     * Test the backward algorithm by comparing the beta values with the values
     * obtained from HMM R model. We test the following observation sequence "O1"
     * "O0" "O2" "O2" "O0" "O0" "O1" by comparing the generated beta values to the
     * R-generated "reference".
     */
    @Test
    public void testBackwardAlgorithm() {
        // intialize the expected beta values
        double[][] betaExpectedA = new double[][]{ new double[]{ 0.0015730559, 0.003543656, 0.00738264, 0.040692, 0.0848, 0.17, 1 }, new double[]{ 0.0017191865, 0.002386795, 0.00923652, 0.052232, 0.1018, 0.17, 1 }, new double[]{ 3.825772E-4, 0.001238558, 0.00259464, 0.012096, 0.0664, 0.66, 1 }, new double[]{ 4.390858E-4, 0.007076994, 0.01063512, 0.013556, 0.0304, 0.17, 1 } };
        // fetch the beta matrix using the backward algorithm
        Matrix beta = HmmAlgorithms.backwardAlgorithm(getModel(), getSequence(), false);
        // first do some basic checking
        assertNotNull(beta);
        assertEquals(4, beta.numCols());
        assertEquals(7, beta.numRows());
        // now compare the resulting matrices
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 7; ++j) {
                assertEquals(betaExpectedA[i][j], beta.get(j, i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testLogScaledBackwardAlgorithm() {
        // intialize the expected beta values
        double[][] betaExpectedA = new double[][]{ new double[]{ 0.0015730559, 0.003543656, 0.00738264, 0.040692, 0.0848, 0.17, 1 }, new double[]{ 0.0017191865, 0.002386795, 0.00923652, 0.052232, 0.1018, 0.17, 1 }, new double[]{ 3.825772E-4, 0.001238558, 0.00259464, 0.012096, 0.0664, 0.66, 1 }, new double[]{ 4.390858E-4, 0.007076994, 0.01063512, 0.013556, 0.0304, 0.17, 1 } };
        // fetch the beta matrix using the backward algorithm
        Matrix beta = HmmAlgorithms.backwardAlgorithm(getModel(), getSequence(), true);
        // first do some basic checking
        assertNotNull(beta);
        assertEquals(4, beta.numCols());
        assertEquals(7, beta.numRows());
        // now compare the resulting matrices
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 7; ++j) {
                assertEquals(Math.log(betaExpectedA[i][j]), beta.get(j, i), MahoutTestCase.EPSILON);
            }
        }
    }

    @Test
    public void testViterbiAlgorithm() {
        // initialize the expected hidden sequence
        int[] expected = new int[]{ 2, 0, 3, 3, 0, 0, 2 };
        // fetch the viterbi generated sequence
        int[] computed = HmmAlgorithms.viterbiAlgorithm(getModel(), getSequence(), false);
        // first make sure we return the correct size
        assertNotNull(computed);
        assertEquals(computed.length, getSequence().length);
        // now check the contents
        for (int i = 0; i < (getSequence().length); ++i) {
            assertEquals(expected[i], computed[i]);
        }
    }

    @Test
    public void testLogScaledViterbiAlgorithm() {
        // initialize the expected hidden sequence
        int[] expected = new int[]{ 2, 0, 3, 3, 0, 0, 2 };
        // fetch the viterbi generated sequence
        int[] computed = HmmAlgorithms.viterbiAlgorithm(getModel(), getSequence(), true);
        // first make sure we return the correct size
        assertNotNull(computed);
        assertEquals(computed.length, getSequence().length);
        // now check the contents
        for (int i = 0; i < (getSequence().length); ++i) {
            assertEquals(expected[i], computed[i]);
        }
    }
}

