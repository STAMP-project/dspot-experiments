/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.sequence;


import org.junit.Assert;
import org.junit.Test;
import smile.stat.distribution.EmpiricalDistribution;


/**
 *
 *
 * @author Haifeng Li
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class HMMTest {
    double[] pi = new double[]{ 0.5, 0.5 };

    double[][] a = new double[][]{ new double[]{ 0.8, 0.2 }, new double[]{ 0.2, 0.8 } };

    double[][] b = new double[][]{ new double[]{ 0.6, 0.4 }, new double[]{ 0.4, 0.6 } };

    public HMMTest() {
    }

    /**
     * Test of numStates method, of class HMM.
     */
    @Test
    public void testNumStates() {
        System.out.println("numStates");
        HMM hmm = new HMM(pi, a, b);
        int expResult = 2;
        int result = hmm.numStates();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of numSymbols method, of class HMM.
     */
    @Test
    public void testNumSymbols() {
        System.out.println("numSymbols");
        HMM hmm = new HMM(pi, a, b);
        int expResult = 2;
        int result = hmm.numSymbols();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getInitialStateProbabilities method, of class HMM.
     */
    @Test
    public void testGetInitialStateProbabilities() {
        System.out.println("getInitialStateProbabilities");
        HMM hmm = new HMM(pi, a, b);
        double[] expResult = pi;
        double[] result = hmm.getInitialStateProbabilities();
        for (int i = 0; i < (expResult.length); i++) {
            Assert.assertEquals(expResult[i], result[i], 1.0E-7);
        }
    }

    /**
     * Test of getStateTransitionProbabilities method, of class HMM.
     */
    @Test
    public void testGetStateTransitionProbabilities() {
        System.out.println("getStateTransitionProbabilities");
        HMM hmm = new HMM(pi, a, b);
        double[][] expResult = a;
        double[][] result = hmm.getStateTransitionProbabilities();
        for (int i = 0; i < (expResult.length); i++) {
            for (int j = 0; j < (expResult[i].length); j++) {
                Assert.assertEquals(expResult[i][j], result[i][j], 1.0E-7);
            }
        }
    }

    /**
     * Test of getSymbolEmissionProbabilities method, of class HMM.
     */
    @Test
    public void testGetSymbolEmissionProbabilities() {
        System.out.println("getSymbolEmissionProbabilities");
        HMM hmm = new HMM(pi, a, b);
        double[][] expResult = b;
        double[][] result = hmm.getSymbolEmissionProbabilities();
        for (int i = 0; i < (expResult.length); i++) {
            for (int j = 0; j < (expResult[i].length); j++) {
                Assert.assertEquals(expResult[i][j], result[i][j], 1.0E-7);
            }
        }
    }

    /**
     * Test of p method, of class HMM.
     */
    @Test
    public void testP_intArr_intArr() {
        System.out.println("p");
        int[] o = new int[]{ 0, 0, 1, 1, 0, 1, 1, 0 };
        int[] s = new int[]{ 0, 0, 1, 1, 1, 1, 1, 0 };
        HMM hmm = new HMM(pi, a, b);
        double expResult = 7.33836E-5;
        double result = hmm.p(o, s);
        Assert.assertEquals(expResult, result, 1.0E-10);
    }

    /**
     * Test of logp method, of class HMM.
     */
    @Test
    public void testLogp_intArr_intArr() {
        System.out.println("logp");
        HMM hmm = new HMM(pi, a, b);
        int[] o = new int[]{ 0, 0, 1, 1, 0, 1, 1, 0 };
        int[] s = new int[]{ 0, 0, 1, 1, 1, 1, 1, 0 };
        double expResult = -9.51981;
        double result = hmm.logp(o, s);
        Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /**
     * Test of p method, of class HMM.
     */
    @Test
    public void testP_intArr() {
        System.out.println("p");
        HMM hmm = new HMM(pi, a, b);
        int[] o = new int[]{ 0, 0, 1, 1, 0, 1, 1, 0 };
        double expResult = 0.003663364;
        double result = hmm.p(o);
        Assert.assertEquals(expResult, result, 1.0E-9);
    }

    /**
     * Test of logp method, of class HMM.
     */
    @Test
    public void testLogp_intArr() {
        System.out.println("logp");
        HMM hmm = new HMM(pi, a, b);
        int[] o = new int[]{ 0, 0, 1, 1, 0, 1, 1, 0 };
        double expResult = -5.609373;
        double result = hmm.logp(o);
        Assert.assertEquals(expResult, result, 1.0E-6);
    }

    /**
     * Test of predict method, of class HMM.
     */
    @Test
    public void testPredict() {
        System.out.println("predict");
        HMM hmm = new HMM(pi, a, b);
        int[] o = new int[]{ 0, 0, 1, 1, 0, 1, 1, 0 };
        int[] s = new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 };
        int[] result = hmm.predict(o);
        Assert.assertEquals(o.length, result.length);
        for (int i = 0; i < (s.length); i++) {
            Assert.assertEquals(s[i], result[i]);
        }
    }

    /**
     * Test of learn method, of class HMM.
     */
    @Test
    public void testLearn() {
        System.out.println("learn");
        EmpiricalDistribution initial = new EmpiricalDistribution(pi);
        EmpiricalDistribution[] transition = new EmpiricalDistribution[a.length];
        for (int i = 0; i < (transition.length); i++) {
            transition[i] = new EmpiricalDistribution(a[i]);
        }
        EmpiricalDistribution[] emission = new EmpiricalDistribution[b.length];
        for (int i = 0; i < (emission.length); i++) {
            emission[i] = new EmpiricalDistribution(b[i]);
        }
        int[][] sequences = new int[5000][];
        int[][] labels = new int[5000][];
        for (int i = 0; i < (sequences.length); i++) {
            sequences[i] = new int[30 * ((randomInt(5)) + 1)];
            labels[i] = new int[sequences[i].length];
            int state = ((int) (initial.rand()));
            sequences[i][0] = ((int) (emission[state].rand()));
            labels[i][0] = state;
            for (int j = 1; j < (sequences[i].length); j++) {
                state = ((int) (transition[state].rand()));
                sequences[i][j] = ((int) (emission[state].rand()));
                labels[i][j] = state;
            }
        }
        HMM hmm = new HMM(sequences, labels);
        System.out.println(hmm);
        double[] pi2 = new double[]{ 0.55, 0.45 };
        double[][] a2 = new double[][]{ new double[]{ 0.7, 0.3 }, new double[]{ 0.15, 0.85 } };
        double[][] b2 = new double[][]{ new double[]{ 0.45, 0.55 }, new double[]{ 0.3, 0.7 } };
        HMM init = new HMM(pi2, a2, b2);
        HMM result = init.learn(sequences, 100);
        System.out.println(result);
    }

    /**
     * Test of p method, of class HMM.
     */
    @Test
    public void testP_intArr_intArr2() {
        System.out.println("p");
        String[] symbols = new String[]{ "0", "1" };
        HMM<String> hmm = new HMM(pi, a, b, symbols);
        String[] o = new String[]{ "0", "0", "1", "1", "0", "1", "1", "0" };
        int[] s = new int[]{ 0, 0, 1, 1, 1, 1, 1, 0 };
        double expResult = 7.33836E-5;
        double result = hmm.p(o, s);
        Assert.assertEquals(expResult, result, 1.0E-10);
    }

    /**
     * Test of logp method, of class HMM.
     */
    @Test
    public void testLogp_intArr_intArr2() {
        System.out.println("logp");
        String[] symbols = new String[]{ "0", "1" };
        HMM<String> hmm = new HMM(pi, a, b, symbols);
        String[] o = new String[]{ "0", "0", "1", "1", "0", "1", "1", "0" };
        int[] s = new int[]{ 0, 0, 1, 1, 1, 1, 1, 0 };
        double expResult = -9.51981;
        double result = hmm.logp(o, s);
        Assert.assertEquals(expResult, result, 1.0E-5);
    }

    /**
     * Test of p method, of class HMM.
     */
    @Test
    public void testP_intArr2() {
        System.out.println("p");
        String[] symbols = new String[]{ "0", "1" };
        HMM<String> hmm = new HMM(pi, a, b, symbols);
        String[] o = new String[]{ "0", "0", "1", "1", "0", "1", "1", "0" };
        double expResult = 0.003663364;
        double result = hmm.p(o);
        Assert.assertEquals(expResult, result, 1.0E-9);
    }

    /**
     * Test of logp method, of class HMM.
     */
    @Test
    public void testLogp_intArr2() {
        System.out.println("logp");
        String[] symbols = new String[]{ "0", "1" };
        HMM<String> hmm = new HMM(pi, a, b, symbols);
        String[] o = new String[]{ "0", "0", "1", "1", "0", "1", "1", "0" };
        double expResult = -5.609373;
        double result = hmm.logp(o);
        Assert.assertEquals(expResult, result, 1.0E-6);
    }

    /**
     * Test of predict method, of class HMM.
     */
    @Test
    public void testPredict2() {
        System.out.println("predict");
        String[] symbols = new String[]{ "0", "1" };
        HMM<String> hmm = new HMM(pi, a, b, symbols);
        String[] o = new String[]{ "0", "0", "1", "1", "0", "1", "1", "0" };
        int[] s = new int[]{ 0, 0, 0, 0, 0, 0, 0, 0 };
        int[] result = hmm.predict(o);
        Assert.assertEquals(o.length, result.length);
        for (int i = 0; i < (s.length); i++) {
            Assert.assertEquals(s[i], result[i]);
        }
    }

    /**
     * Test of predict method, of class HMM.
     */
    @Test
    public void testPredict3() {
        System.out.println("predict");
        String[] symbols = new String[]{ "H", "T", "P" };
        double[] pi2 = new double[]{ 0.4, 0.3, 0.3 };
        double[][] a2 = new double[][]{ new double[]{ 0.3, 0.4, 0.3 }, new double[]{ 0.3, 0.3, 0.4 }, new double[]{ 0.4, 0.2, 0.4 } };
        double[][] b2 = new double[][]{ new double[]{ 0.4, 0.3, 0.3 }, new double[]{ 0.5, 0.2, 0.3 }, new double[]{ 0.2, 0.3, 0.5 } };
        HMM<String> hmm = new HMM(pi2, a2, b2, symbols);
        String[] o = new String[]{ "H", "H", "P", "P", "P", "H", "H", "H", "P", "P", "P", "H", "T", "T", "T" };
        int[] s = new int[]{ 0, 1, 2, 2, 2, 0, 1, 1, 2, 2, 2, 0, 2, 2, 0 };
        int[] result = hmm.predict(o);
        Assert.assertEquals(o.length, result.length);
        for (int i = 0; i < (s.length); i++) {
            Assert.assertEquals(s[i], result[i]);
        }
    }

    /**
     * Test of learn method, of class HMM.
     */
    @Test
    public void testLearn2() {
        System.out.println("learn");
        EmpiricalDistribution initial = new EmpiricalDistribution(pi);
        EmpiricalDistribution[] transition = new EmpiricalDistribution[a.length];
        for (int i = 0; i < (transition.length); i++) {
            transition[i] = new EmpiricalDistribution(a[i]);
        }
        EmpiricalDistribution[] emission = new EmpiricalDistribution[b.length];
        for (int i = 0; i < (emission.length); i++) {
            emission[i] = new EmpiricalDistribution(b[i]);
        }
        String[] symbols = new String[]{ "0", "1" };
        String[][] sequences = new String[5000][];
        int[][] labels = new int[5000][];
        for (int i = 0; i < (sequences.length); i++) {
            sequences[i] = new String[30 * ((randomInt(5)) + 1)];
            labels[i] = new int[sequences[i].length];
            int state = ((int) (initial.rand()));
            sequences[i][0] = symbols[((int) (emission[state].rand()))];
            labels[i][0] = state;
            for (int j = 1; j < (sequences[i].length); j++) {
                state = ((int) (transition[state].rand()));
                sequences[i][j] = symbols[((int) (emission[state].rand()))];
                labels[i][j] = state;
            }
        }
        HMM<String> hmm = new HMM(sequences, labels);
        System.out.println(hmm);
        double[] pi2 = new double[]{ 0.55, 0.45 };
        double[][] a2 = new double[][]{ new double[]{ 0.7, 0.3 }, new double[]{ 0.15, 0.85 } };
        double[][] b2 = new double[][]{ new double[]{ 0.45, 0.55 }, new double[]{ 0.3, 0.7 } };
        HMM<String> init = new HMM(pi2, a2, b2, symbols);
        HMM<String> result = init.learn(sequences, 100);
        System.out.println(result);
    }
}

