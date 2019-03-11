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
public class LogNormalDistributionTest {
    public LogNormalDistributionTest() {
    }

    /**
     * Test of constructor, of class LogNormalDistribution.
     */
    @Test
    public void testLogNormalDistribution() {
        System.out.println("LogNormalDistribution");
        LogNormalDistribution instance = new LogNormalDistribution(3, 2.1);
        double[] data = new double[1000];
        for (int i = 0; i < (data.length); i++)
            data[i] = instance.rand();

        LogNormalDistribution est = new LogNormalDistribution(data);
        Assert.assertEquals(0.0, (((est.getMu()) - 3.0) / 3.0), 0.1);
        Assert.assertEquals(0.0, (((est.getSigma()) - 2.1) / 2.1), 0.1);
    }

    /**
     * Test of npara method, of class LogNormalDistribution.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(2, instance.npara());
    }

    /**
     * Test of mean method, of class LogNormalDistribution.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(4.481689, instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class LogNormalDistribution.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(34.51261, instance.var(), 1.0E-5);
    }

    /**
     * Test of sd method, of class LogNormalDistribution.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(5.874744, instance.sd(), 1.0E-6);
    }

    /**
     * Test of entropy method, of class LogNormalDistribution.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(2.418939, instance.entropy(), 1.0E-6);
    }

    /**
     * Test of p method, of class LogNormalDistribution.
     */
    @Test
    public void testP() {
        System.out.println("p");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(6.006101E-6, instance.p(0.01), 1.0E-12);
        Assert.assertEquals(0.01707931, instance.p(0.1), 1.0E-7);
        Assert.assertEquals(0.2419707, instance.p(1.0), 1.0E-7);
        Assert.assertEquals(0.1902978, instance.p(2.0), 1.0E-7);
        Assert.assertEquals(0.06626564, instance.p(5.0), 1.0E-7);
        Assert.assertEquals(0.01707931, instance.p(10.0), 1.0E-7);
    }

    /**
     * Test of logP method, of class LogNormalDistribution.
     */
    @Test
    public void testLogP() {
        System.out.println("logP");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(Math.log(6.006101E-6), instance.logp(0.01), 1.0E-5);
        Assert.assertEquals(Math.log(0.01707931), instance.logp(0.1), 1.0E-5);
        Assert.assertEquals(Math.log(0.2419707), instance.logp(1.0), 1.0E-5);
        Assert.assertEquals(Math.log(0.1902978), instance.logp(2.0), 1.0E-5);
        Assert.assertEquals(Math.log(0.06626564), instance.logp(5.0), 1.0E-5);
        Assert.assertEquals(Math.log(0.01707931), instance.logp(10.0), 1.0E-5);
    }

    /**
     * Test of cdf method, of class LogNormalDistribution.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(1.040252E-8, instance.cdf(0.01), 1.0E-12);
        Assert.assertEquals(4.789901E-4, instance.cdf(0.1), 1.0E-7);
        Assert.assertEquals(0.1586553, instance.cdf(1.0), 1.0E-7);
        Assert.assertEquals(0.3794777, instance.cdf(2.0), 1.0E-7);
        Assert.assertEquals(0.7288829, instance.cdf(5.0), 1.0E-7);
        Assert.assertEquals(0.9036418, instance.cdf(10.0), 1.0E-7);
    }

    /**
     * Test of quantile method, of class LogNormalDistribution.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        LogNormalDistribution instance = new LogNormalDistribution(1.0, 1.0);
        instance.rand();
        Assert.assertEquals(0.2654449, instance.quantile(0.01), 1.0E-7);
        Assert.assertEquals(0.754612, instance.quantile(0.1), 1.0E-6);
        Assert.assertEquals(1.17161, instance.quantile(0.2), 1.0E-6);
        Assert.assertEquals(1.608978, instance.quantile(0.3), 1.0E-6);
        Assert.assertEquals(9.791861, instance.quantile(0.9), 1.0E-6);
        Assert.assertEquals(27.83649, instance.quantile(0.99), 1.0E-5);
    }
}

