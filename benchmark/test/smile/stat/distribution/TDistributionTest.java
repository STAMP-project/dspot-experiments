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
public class TDistributionTest {
    public TDistributionTest() {
    }

    /**
     * Test of npara method, of class TDistribution.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals(1, instance.npara());
    }

    /**
     * Test of mean method, of class TDistribution.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals(0.0, instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class TDistribution.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals((10 / 9.0), instance.var(), 1.0E-7);
    }

    /**
     * Test of sd method, of class TDistribution.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals(Math.sqrt((10 / 9.0)), instance.sd(), 1.0E-7);
    }

    /**
     * Test of entropy method, of class TDistribution.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        TDistribution instance = new TDistribution(20);
        Assert.assertEquals(1.46954202, instance.entropy(), 1.0E-7);
    }

    /**
     * Test of p method, of class TDistribution.
     */
    @Test
    public void testP() {
        System.out.println("p");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals(2.660085E-9, instance.p((-10.0)), 1.0E-16);
        Assert.assertEquals(0.05808722, instance.p((-2.0)), 1.0E-7);
        Assert.assertEquals(0.2360456, instance.p((-1.0)), 1.0E-7);
        Assert.assertEquals(0.3939886, instance.p(0.0), 1.0E-7);
        Assert.assertEquals(0.2360456, instance.p(1.0), 1.0E-7);
        Assert.assertEquals(0.05808722, instance.p(2.0), 1.0E-7);
        Assert.assertEquals(2.660085E-9, instance.p(10.0), 1.0E-16);
    }

    /**
     * Test of logP method, of class TDistribution.
     */
    @Test
    public void testLogP() {
        System.out.println("logP");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals(Math.log(2.660085E-9), instance.logp((-10.0)), 1.0E-5);
        Assert.assertEquals(Math.log(0.05808722), instance.logp((-2.0)), 1.0E-5);
        Assert.assertEquals(Math.log(0.2360456), instance.logp((-1.0)), 1.0E-5);
        Assert.assertEquals(Math.log(0.3939886), instance.logp(0.0), 1.0E-5);
        Assert.assertEquals(Math.log(0.2360456), instance.logp(1.0), 1.0E-5);
        Assert.assertEquals(Math.log(0.05808722), instance.logp(2.0), 1.0E-5);
        Assert.assertEquals(Math.log(2.660085E-9), instance.logp(10.0), 1.0E-5);
    }

    /**
     * Test of cdf method, of class TDistribution.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals(1.581891E-9, instance.cdf((-10.0)), 1.0E-15);
        Assert.assertEquals(0.02963277, instance.cdf((-2.0)), 1.0E-7);
        Assert.assertEquals(0.1646283, instance.cdf((-1.0)), 1.0E-7);
        Assert.assertEquals(0.5, instance.cdf(0.0), 1.0E-7);
        Assert.assertEquals(0.8353717, instance.cdf(1.0), 1.0E-7);
        Assert.assertEquals(0.9703672, instance.cdf(2.0), 1.0E-7);
        Assert.assertEquals(1.0, instance.cdf(10.0), 1.0E-7);
    }

    /**
     * Test of quantile method, of class TDistribution.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        TDistribution instance = new TDistribution(20);
        instance.rand();
        Assert.assertEquals((-3.551808), instance.quantile(0.001), 1.0E-6);
        Assert.assertEquals((-2.527977), instance.quantile(0.01), 1.0E-6);
        Assert.assertEquals((-1.325341), instance.quantile(0.1), 1.0E-6);
        Assert.assertEquals((-0.8599644), instance.quantile(0.2), 1.0E-6);
        Assert.assertEquals(0.0, instance.quantile(0.5), 1.0E-6);
        Assert.assertEquals(1.325341, instance.quantile(0.9), 1.0E-6);
        Assert.assertEquals(2.527977, instance.quantile(0.99), 1.0E-6);
        Assert.assertEquals(3.551808, instance.quantile(0.999), 1.0E-6);
    }
}

