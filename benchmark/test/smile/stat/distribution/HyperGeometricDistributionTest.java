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
package smile.stat.distribution;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class HyperGeometricDistributionTest {
    public HyperGeometricDistributionTest() {
    }

    /**
     * Test of npara method, of class HyperGeometricDistribution.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(3, instance.npara());
    }

    /**
     * Test of mean method, of class HyperGeometricDistribution.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(21, instance.mean(), 1.0E-7);
        instance = new HyperGeometricDistribution(100, 30, 80);
        instance.rand();
        Assert.assertEquals(24, instance.mean(), 1.0E-7);
        instance = new HyperGeometricDistribution(100, 30, 60);
        instance.rand();
        Assert.assertEquals(18, instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class HyperGeometricDistribution.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(4.454545, instance.var(), 1.0E-6);
        instance = new HyperGeometricDistribution(100, 30, 80);
        instance.rand();
        Assert.assertEquals(3.393939, instance.var(), 1.0E-6);
        instance = new HyperGeometricDistribution(100, 30, 60);
        instance.rand();
        Assert.assertEquals(5.090909, instance.var(), 1.0E-6);
    }

    /**
     * Test of sd method, of class HyperGeometricDistribution.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(2.110579, instance.sd(), 1.0E-6);
        instance = new HyperGeometricDistribution(100, 30, 80);
        instance.rand();
        Assert.assertEquals(1.842265, instance.sd(), 1.0E-6);
        instance = new HyperGeometricDistribution(100, 30, 60);
        instance.rand();
        Assert.assertEquals(2.256304, instance.sd(), 1.0E-6);
    }

    /**
     * Test of p method, of class HyperGeometricDistribution.
     */
    @Test
    public void testP() {
        System.out.println("p");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(0.0, instance.p((-1)), 1.0E-6);
        Assert.assertEquals(3.404564E-26, instance.p(0), 1.0E-30);
        Assert.assertEquals(7.149584E-23, instance.p(1), 1.0E-27);
        Assert.assertEquals(3.576579E-20, instance.p(2), 1.0E-25);
        Assert.assertEquals(0.165592, instance.p(20), 1.0E-7);
        Assert.assertEquals(0.1877461, instance.p(21), 1.0E-7);
        Assert.assertEquals(4.1413E-4, instance.p(28), 1.0E-8);
        Assert.assertEquals(4.136376E-5, instance.p(29), 1.0E-10);
        Assert.assertEquals(1.884349E-6, instance.p(30), 1.0E-12);
        Assert.assertEquals(0.0, instance.p(31), 1.0E-6);
    }

    /**
     * Test of logP method, of class HyperGeometricDistribution.
     */
    @Test
    public void testLogP() {
        System.out.println("logP");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(Math.log(3.404564E-26), instance.logp(0), 1.0E-5);
        Assert.assertEquals(Math.log(7.149584E-23), instance.logp(1), 1.0E-5);
        Assert.assertEquals(Math.log(3.576579E-20), instance.logp(2), 1.0E-5);
        Assert.assertEquals(Math.log(0.165592), instance.logp(20), 1.0E-5);
        Assert.assertEquals(Math.log(0.1877461), instance.logp(21), 1.0E-5);
        Assert.assertEquals(Math.log(4.1413E-4), instance.logp(28), 1.0E-5);
        Assert.assertEquals(Math.log(4.136376E-5), instance.logp(29), 1.0E-5);
        Assert.assertEquals(Math.log(1.884349E-6), instance.logp(30), 1.0E-5);
    }

    /**
     * Test of cdf method, of class HyperGeometricDistribution.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(3.404564E-26, instance.cdf(0), 1.0E-30);
        Assert.assertEquals(7.152988E-23, instance.cdf(1), 1.0E-27);
        Assert.assertEquals(3.583732E-20, instance.cdf(2), 1.0E-25);
        Assert.assertEquals(0.4013632, instance.cdf(20), 1.0E-7);
        Assert.assertEquals(0.5891093, instance.cdf(21), 1.0E-7);
        Assert.assertEquals(0.9999568, instance.cdf(28), 1.0E-7);
        Assert.assertEquals(0.9999981, instance.cdf(29), 1.0E-7);
        Assert.assertEquals(1.0, instance.cdf(30), 1.0E-7);
        Assert.assertEquals(1.0, instance.cdf(31), 1.0E-7);
    }

    /**
     * Test of quantile method, of class HyperGeometricDistribution.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        HyperGeometricDistribution instance = new HyperGeometricDistribution(100, 30, 70);
        instance.rand();
        Assert.assertEquals(0, instance.quantile(0), 1.0E-30);
        Assert.assertEquals(14, instance.quantile(0.001), 1.0E-27);
        Assert.assertEquals(16, instance.quantile(0.01), 1.0E-25);
        Assert.assertEquals(18, instance.quantile(0.1), 1.0E-25);
        Assert.assertEquals(19, instance.quantile(0.2), 1.0E-7);
        Assert.assertEquals(20, instance.quantile(0.3), 1.0E-7);
        Assert.assertEquals(24, instance.quantile(0.9), 1.0E-8);
        Assert.assertEquals(26, instance.quantile(0.99), 1.0E-10);
        Assert.assertEquals(27, instance.quantile(0.999), 1.0E-12);
        Assert.assertEquals(30, instance.quantile(1), 1.0E-6);
    }
}

