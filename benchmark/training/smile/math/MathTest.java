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
package smile.math;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class MathTest {
    public MathTest() {
    }

    @Test
    public void testIsZero() {
        System.out.println("isZero");
        Assert.assertEquals(true, isZero(0.0));
        Assert.assertEquals(true, isZero(Double.MIN_VALUE));
        Assert.assertEquals(true, isZero(Double.MIN_NORMAL));
        Assert.assertEquals(false, Math.isZero(EPSILON));
    }

    /**
     * Test of isPower2 method, of class Math.
     */
    @Test
    public void testIsPower2() {
        System.out.println("isPower2");
        Assert.assertEquals(false, isPower2((-1)));
        Assert.assertEquals(false, isPower2(0));
        Assert.assertEquals(true, isPower2(1));
        Assert.assertEquals(true, isPower2(2));
        Assert.assertEquals(false, isPower2(3));
        Assert.assertEquals(true, isPower2(4));
        Assert.assertEquals(true, isPower2(8));
        Assert.assertEquals(true, isPower2(16));
        Assert.assertEquals(true, isPower2(32));
        Assert.assertEquals(true, isPower2(64));
        Assert.assertEquals(true, isPower2(128));
        Assert.assertEquals(true, isPower2(256));
        Assert.assertEquals(true, isPower2(512));
        Assert.assertEquals(true, isPower2(1024));
        Assert.assertEquals(true, isPower2(65536));
        Assert.assertEquals(true, isPower2(131072));
    }

    /**
     * Test of log2 method, of class Math.
     */
    @Test
    public void testLog2() {
        System.out.println("log2");
        Assert.assertEquals(0, log2(1), 1.0E-6);
        Assert.assertEquals(1, log2(2), 1.0E-6);
        Assert.assertEquals(1.584963, log2(3), 1.0E-6);
        Assert.assertEquals(2, log2(4), 1.0E-6);
    }

    /**
     * Test of sqr method, of class Math.
     */
    @Test
    public void testSqr() {
        System.out.println("sqr");
        Assert.assertEquals(0, sqr(0), 1.0E-10);
        Assert.assertEquals(1, sqr(1), 1.0E-10);
        Assert.assertEquals(4, sqr(2), 1.0E-10);
        Assert.assertEquals(9, sqr(3), 1.0E-10);
    }

    /**
     * Test of factorial method, of class Math.
     */
    @Test
    public void testFactorial() {
        System.out.println("factorial");
        Assert.assertEquals(1.0, factorial(0), 1.0E-7);
        Assert.assertEquals(1.0, factorial(1), 1.0E-7);
        Assert.assertEquals(2.0, factorial(2), 1.0E-7);
        Assert.assertEquals(6.0, factorial(3), 1.0E-7);
        Assert.assertEquals(24.0, factorial(4), 1.0E-7);
    }

    /**
     * Test of logFactorial method, of class Math.
     */
    @Test
    public void testLogFactorial() {
        System.out.println("logFactorial");
        Assert.assertEquals(0.0, logFactorial(0), 1.0E-7);
        Assert.assertEquals(0.0, logFactorial(1), 1.0E-7);
        Assert.assertEquals(Math.log(2.0), logFactorial(2), 1.0E-7);
        Assert.assertEquals(Math.log(6.0), logFactorial(3), 1.0E-7);
        Assert.assertEquals(Math.log(24.0), logFactorial(4), 1.0E-7);
    }

    /**
     * Test of choose method, of class Math.
     */
    @Test
    public void testChoose() {
        System.out.println("choose");
        Assert.assertEquals(1.0, choose(10, 0), 1.0E-7);
        Assert.assertEquals(10.0, choose(10, 1), 1.0E-7);
        Assert.assertEquals(45.0, choose(10, 2), 1.0E-7);
        Assert.assertEquals(120.0, choose(10, 3), 1.0E-7);
        Assert.assertEquals(210.0, choose(10, 4), 1.0E-7);
    }

    /**
     * Test of logChoose method, of class Math.
     */
    @Test
    public void testLogChoose() {
        System.out.println("logChoose");
        Assert.assertEquals(0.0, logChoose(10, 0), 1.0E-6);
        Assert.assertEquals(2.302585, logChoose(10, 1), 1.0E-6);
        Assert.assertEquals(3.806662, logChoose(10, 2), 1.0E-6);
        Assert.assertEquals(4.787492, logChoose(10, 3), 1.0E-6);
        Assert.assertEquals(5.347108, logChoose(10, 4), 1.0E-6);
    }

    /**
     * Test of random method, of class Math.
     */
    @Test
    public void testRandom() {
        System.out.println("random");
        double[] prob = new double[]{ 0.473646292, 0.206116725, 0.009308497, 0.227844687, 0.083083799 };
        int[] sample = Math.random(prob, 300);
        double[][] hist = Histogram.histogram(sample, 5);
        double[] p = new double[5];
        for (int i = 0; i < 5; i++) {
            p[i] = (hist[2][i]) / 300.0;
        }
        Assert.assertTrue(((KullbackLeiblerDivergence(prob, p)) < 0.05));
    }

    /**
     * Test of random method, of class Math.
     */
    @Test
    public void testRandom2() {
        System.out.println("random");
        double[] prob = new double[]{ 0.473646292, 0.206116725, 0.009308497, 0.227844687, 0.083083799 };
        int[] sample = new int[300];
        for (int i = 0; i < 300; i++) {
            sample[i] = Math.random(prob);
        }
        double[][] hist = Histogram.histogram(sample, 5);
        double[] p = new double[5];
        for (int i = 0; i < 5; i++) {
            p[i] = (hist[2][i]) / 300.0;
        }
        Assert.assertTrue(((KullbackLeiblerDivergence(prob, p)) < 0.05));
    }

    /**
     * Test of min method, of class Math.
     */
    @Test
    public void testMin_3args() {
        System.out.println("min");
        int a = -1;
        int b = 0;
        int c = 1;
        int expResult = -1;
        int result = Math.min(a, b, c);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of max method, of class Math.
     */
    @Test
    public void testMax_3args() {
        System.out.println("max");
        int a = -1;
        int b = 0;
        int c = 1;
        int expResult = 1;
        int result = Math.max(a, b, c);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of min method, of class Math.
     */
    @Test
    public void testMin_doubleArr() {
        System.out.println("min");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        Assert.assertEquals((-2.196822), Math.min(x), 1.0E-7);
    }

    /**
     * Test of max method, of class Math.
     */
    @Test
    public void testMax_doubleArr() {
        System.out.println("max");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        Assert.assertEquals(1.0567679, Math.max(x), 1.0E-7);
    }

    /**
     * Test of min method, of class Math.
     */
    @Test
    public void testMin_doubleArrArr() {
        System.out.println("min");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        Assert.assertEquals((-0.89044952), Math.min(A), 1.0E-7);
    }

    /**
     * Test of max method, of class Math.
     */
    @Test
    public void testMax_doubleArrArr() {
        System.out.println("max");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        Assert.assertEquals(0.722018, Math.max(A), 1.0E-7);
    }

    /**
     * Test of transpose method, of class Math.
     */
    @Test
    public void testTranspose() {
        System.out.println("transpose");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[][] B = new double[][]{ new double[]{ 0.722018, -0.2648886, -0.6391588 }, new double[]{ 0.07121225, -0.8904495, 0.4494758 }, new double[]{ 0.6881997, 0.3700456, 0.6240573 } };
        Assert.assertTrue(Math.equals(transpose(A), B, 1.0E-7));
    }

    /**
     * Test of rowMin method, of class Math.
     */
    @Test
    public void testRowMin() {
        System.out.println("rowMin");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ 0.07121225, -0.89044952, -0.6391588 };
        double[] result = rowMin(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of rowMax method, of class Math.
     */
    @Test
    public void testRowMax() {
        System.out.println("rowMax");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ 0.722018, 0.3700456, 0.6240573 };
        double[] result = rowMax(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of rowSum method, of class Math.
     */
    @Test
    public void testRowSums() {
        System.out.println("rowSums");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ 1.48143, -0.7852925, 0.4343743 };
        double[] result = rowSums(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of rowMean method, of class Math.
     */
    @Test
    public void testRowMeans() {
        System.out.println("rowMeans");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ 0.49381, -0.2617642, 0.1447914 };
        double[] result = rowMeans(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of colMin method, of class Math.
     */
    @Test
    public void testColMin() {
        System.out.println("colMin");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ -0.6391588, -0.89044952, 0.3700456 };
        double[] result = colMin(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of colMax method, of class Math.
     */
    @Test
    public void testColMax() {
        System.out.println("colMax");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ 0.722018, 0.44947578, 0.6881997 };
        double[] result = colMax(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of colSum method, of class Math.
     */
    @Test
    public void testColSums() {
        System.out.println("colSums");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ -0.1820294, -0.3697615, 1.6823026 };
        double[] result = colSums(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of colMean method, of class Math.
     */
    @Test
    public void testColMeans() {
        System.out.println("colMeans");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[] r = new double[]{ -0.06067647, -0.12325383, 0.56076753 };
        double[] result = colMeans(A);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(result[i], r[i], 1.0E-7);
        }
    }

    /**
     * Test of sum method, of class Math.
     */
    @Test
    public void testSum_doubleArr() {
        System.out.println("sum");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        Assert.assertEquals(45, sum(data), 1.0E-6);
    }

    /**
     * Test of mean method, of class Math.
     */
    @Test
    public void testMean_doubleArr() {
        System.out.println("mean");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        Assert.assertEquals(5, mean(data), 1.0E-6);
    }

    /**
     * Test of var method, of class Math.
     */
    @Test
    public void testVar_doubleArr() {
        System.out.println("var");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        Assert.assertEquals(7.5, var(data), 1.0E-6);
    }

    /**
     * Test of sd method, of class Math.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        Assert.assertEquals(2.73861, sd(data), 1.0E-5);
    }

    /**
     * Test of colSd method, of class Math.
     */
    @Test
    public void testColSd() {
        System.out.println("colSd");
        double[][] data = new double[][]{ new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 }, new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 }, new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 } };
        data = Math.transpose(data);
        Assert.assertEquals(2.73861, colSds(data)[0], 1.0E-5);
        Assert.assertEquals(2.73861, colSds(data)[1], 1.0E-5);
        Assert.assertEquals(2.73861, colSds(data)[2], 1.0E-5);
    }

    /**
     * Test of mad method, of class Math.
     */
    @Test
    public void testMad() {
        System.out.println("mad");
        double[] data = new double[]{ 1, 1, 2, 2, 4, 6, 9 };
        Assert.assertEquals(1.0, mad(data), 1.0E-5);
    }

    /**
     * Test of distance method, of class Math.
     */
    @Test
    public void testDistance_doubleArr_doubleArr() {
        System.out.println("distance");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(2.422302, distance(x, y), 1.0E-6);
    }

    /**
     * Test of squaredDistance method, of class Math.
     */
    @Test
    public void testSquaredDistance_doubleArr_doubleArr() {
        System.out.println("squaredDistance");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(5.867547, squaredDistance(x, y), 1.0E-6);
    }

    /**
     * Test of dot method, of class Math.
     */
    @Test
    public void testDot_doubleArr_doubleArr() {
        System.out.println("dot");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(3.350726, dot(x, y), 1.0E-6);
    }

    /**
     * Test of cov method, of class Math.
     */
    @Test
    public void testCov_doubleArr_doubleArr() {
        System.out.println("cov");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(0.5894983, cov(x, y), 1.0E-7);
    }

    /**
     * Test of cor method, of class Math.
     */
    @Test
    public void testCor_doubleArr_doubleArr() {
        System.out.println("cor");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(0.4686847, cor(x, y), 1.0E-7);
    }

    /**
     * Test of spearman method, of class Math.
     */
    @Test
    public void testSpearman_doubleArr_doubleArr() {
        System.out.println("spearman");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(0.3, spearman(x, y), 1.0E-7);
    }

    /**
     * Test of kendall method, of class Math.
     */
    @Test
    public void testKendall_doubleArr_doubleArr() {
        System.out.println("kendall");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(0.2, kendall(x, y), 1.0E-7);
    }

    /**
     * Test of norm1 method, of class Math.
     */
    @Test
    public void testNorm1_doubleArr() {
        System.out.println("norm1");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        Assert.assertEquals(4.638106, norm1(x), 1.0E-6);
    }

    /**
     * Test of norm2 method, of class Math.
     */
    @Test
    public void testNorm2_doubleArr() {
        System.out.println("norm2");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        Assert.assertEquals(2.647086, norm2(x), 1.0E-6);
    }

    /**
     * Test of normInf method, of class Math.
     */
    @Test
    public void testNormInf_doubleArr() {
        System.out.println("normInf");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        Assert.assertEquals(2.196822, normInf(x), 1.0E-6);
    }

    /**
     * Test of norm method, of class Math.
     */
    @Test
    public void testNorm_doubleArr() {
        System.out.println("norm");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        Assert.assertEquals(2.647086, norm(x), 1.0E-6);
    }

    /**
     * Test of standardize method, of class StatUtils.
     */
    @Test
    public void testStandardize() {
        System.out.println("standardize");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        standardize(data);
        Assert.assertEquals(0, mean(data), 1.0E-7);
        Assert.assertEquals(1, sd(data), 1.0E-7);
    }

    /**
     * Test of unitize method, of class Math.
     */
    @Test
    public void testUnitize() {
        System.out.println("unitize");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        unitize(data);
        Assert.assertEquals(1, norm(data), 1.0E-7);
    }

    /**
     * Test of unitize1 method, of class Math.
     */
    @Test
    public void testUnitize1() {
        System.out.println("unitize1");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        unitize1(data);
        Assert.assertEquals(1, norm1(data), 1.0E-7);
    }

    /**
     * Test of unitize2 method, of class Math.
     */
    @Test
    public void testUnitize2() {
        System.out.println("unitize2");
        double[] data = new double[]{ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        unitize2(data);
        Assert.assertEquals(1, norm2(data), 1.0E-7);
    }

    /**
     * Test of GoodTuring method, of class Math.
     */
    @Test
    public void testGoodTuring() {
        System.out.println("GoodTuring");
        int[] r = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12 };
        int[] Nr = new int[]{ 120, 40, 24, 13, 15, 5, 11, 2, 2, 1, 3 };
        double p0 = 0.2047782;
        double[] p = new double[]{ 9.267E-4, 0.0024393, 0.0040945, 0.0058063, 0.0075464, 0.0093026, 0.0110689, 0.0128418, 0.0146194, 0.0164005, 0.0199696 };
        double[] result = new double[r.length];
        Assert.assertEquals(p0, GoodTuring(r, Nr, result), 1.0E-7);
        for (int i = 0; i < (r.length); i++) {
            Assert.assertEquals(p[i], result[i], 1.0E-7);
        }
    }

    /**
     * Test of clone method, of class Math.
     */
    @Test
    public void testClone() {
        System.out.println("clone");
        double[][] A = new double[][]{ new double[]{ 0.722018, 0.07121225, 0.6881997 }, new double[]{ -0.2648886, -0.89044952, 0.3700456 }, new double[]{ -0.6391588, 0.44947578, 0.6240573 } };
        double[][] B = Math.clone(A);
        Assert.assertTrue(Math.equals(A, B));
        Assert.assertTrue((A != B));
        for (int i = 0; i < (A.length); i++) {
            Assert.assertTrue(((A[i]) != (B[i])));
        }
    }

    /**
     * Test of plusEquals method, of class Math.
     */
    @Test
    public void testAdd_doubleArr_doubleArr() {
        System.out.println("add");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        double[] z = new double[]{ -3.9749544, -1.6219752, 0.909441, 0.110676, -0.0071785 };
        plus(x, y);
        Assert.assertTrue(Math.equals(x, z));
    }

    /**
     * Test of minusEquals method, of class Math.
     */
    @Test
    public void testMinus_doubleArr_doubleArr() {
        System.out.println("minus");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        double[] z = new double[]{ -0.4186894, -0.2900074, -0.9957886, 2.0028598, 0.7778815 };
        minus(x, y);
        Assert.assertTrue(Math.equals(x, z));
    }

    /**
     * Test of root method, of class Math.
     */
    @Test
    public void testRoot_4args() {
        System.out.println("root");
        Function func = new Function() {
            @Override
            public double f(double x) {
                return ((((x * x) * x) + (x * x)) - (5 * x)) + 3;
            }
        };
        double result = Math.root(func, (-4), (-2), 1.0E-7);
        Assert.assertEquals((-3), result, 1.0E-7);
    }

    /**
     * Test of root method, of class Math.
     */
    @Test
    public void testRoot_5args() {
        System.out.println("root");
        Function func = new DifferentiableFunction() {
            @Override
            public double f(double x) {
                return ((((x * x) * x) + (x * x)) - (5 * x)) + 3;
            }

            @Override
            public double df(double x) {
                return (((3 * x) * x) + (2 * x)) - 5;
            }
        };
        double result = Math.root(func, (-4), (-2), 1.0E-7);
        Assert.assertEquals((-3), result, 1.0E-7);
    }

    /**
     * Test of min method, of class Math.
     */
    @Test
    public void testMin_5args() {
        System.out.println("L-BFGS");
        DifferentiableMultivariateFunction func = new DifferentiableMultivariateFunction() {
            @Override
            public double f(double[] x) {
                double f = 0.0;
                for (int j = 1; j <= (x.length); j += 2) {
                    double t1 = 1.0 - (x[(j - 1)]);
                    double t2 = 10.0 * ((x[j]) - ((x[(j - 1)]) * (x[(j - 1)])));
                    f = (f + (t1 * t1)) + (t2 * t2);
                }
                return f;
            }

            @Override
            public double f(double[] x, double[] g) {
                double f = 0.0;
                for (int j = 1; j <= (x.length); j += 2) {
                    double t1 = 1.0 - (x[(j - 1)]);
                    double t2 = 10.0 * ((x[j]) - ((x[(j - 1)]) * (x[(j - 1)])));
                    g[((j + 1) - 1)] = 20.0 * t2;
                    g[(j - 1)] = (-2.0) * (((x[(j - 1)]) * (g[((j + 1) - 1)])) + t1);
                    f = (f + (t1 * t1)) + (t2 * t2);
                }
                return f;
            }
        };
        double[] x = new double[100];
        for (int j = 1; j <= (x.length); j += 2) {
            x[(j - 1)] = -1.2;
            x[((j + 1) - 1)] = 1.0;
        }
        double result = Math.min(func, 5, x, 1.0E-4);
        Assert.assertEquals(3.2760183604E-14, result, 1.0E-15);
    }

    /**
     * Test of min method, of class Math.
     */
    @Test
    public void testMin_4args() {
        System.out.println("BFGS");
        DifferentiableMultivariateFunction func = new DifferentiableMultivariateFunction() {
            @Override
            public double f(double[] x) {
                double f = 0.0;
                for (int j = 1; j <= (x.length); j += 2) {
                    double t1 = 1.0 - (x[(j - 1)]);
                    double t2 = 10.0 * ((x[j]) - ((x[(j - 1)]) * (x[(j - 1)])));
                    f = (f + (t1 * t1)) + (t2 * t2);
                }
                return f;
            }

            @Override
            public double f(double[] x, double[] g) {
                double f = 0.0;
                for (int j = 1; j <= (x.length); j += 2) {
                    double t1 = 1.0 - (x[(j - 1)]);
                    double t2 = 10.0 * ((x[j]) - ((x[(j - 1)]) * (x[(j - 1)])));
                    g[((j + 1) - 1)] = 20.0 * t2;
                    g[(j - 1)] = (-2.0) * (((x[(j - 1)]) * (g[((j + 1) - 1)])) + t1);
                    f = (f + (t1 * t1)) + (t2 * t2);
                }
                return f;
            }
        };
        double[] x = new double[100];
        for (int j = 1; j <= (x.length); j += 2) {
            x[(j - 1)] = -1.2;
            x[((j + 1) - 1)] = 1.0;
        }
        double result = Math.min(func, x, 1.0E-4);
        Assert.assertEquals(2.95793E-10, result, 1.0E-15);
    }
}

