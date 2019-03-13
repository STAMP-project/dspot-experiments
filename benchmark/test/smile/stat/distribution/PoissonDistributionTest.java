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
public class PoissonDistributionTest {
    public PoissonDistributionTest() {
    }

    /**
     * Test of constructor, of class PoissonDistribution.
     */
    @Test
    public void testPoissonDistribution() {
        System.out.println("PoissonDistribution");
        PoissonDistribution instance = new PoissonDistribution(5.5);
        int[] data = new int[1000];
        for (int i = 0; i < (data.length); i++)
            data[i] = ((int) (instance.rand()));

        PoissonDistribution est = new PoissonDistribution(data);
        Assert.assertEquals(5.5, est.getLambda(), 0.15);
    }

    /**
     * Test of npara method, of class Poisson.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(1, instance.npara());
    }

    /**
     * Test of mean method, of class Poisson.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(3.5, instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class Poisson.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(3.5, instance.var(), 1.0E-7);
    }

    /**
     * Test of sd method, of class Poisson.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(Math.sqrt(3.5), instance.sd(), 1.0E-7);
    }

    /**
     * Test of entropy method, of class Poisson.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(2.016878, instance.entropy(), 1.0E-6);
    }

    /**
     * Test of pdf method, of class Poisson.
     */
    @Test
    public void testPdf() {
        System.out.println("pdf");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(0.0, instance.p((-1)), 1.0E-7);
        Assert.assertEquals(0.03019738, instance.p(0), 1.0E-7);
        Assert.assertEquals(0.1056908, instance.p(1), 1.0E-7);
        Assert.assertEquals(0.184959, instance.p(2), 1.0E-7);
        Assert.assertEquals(0.2157855, instance.p(3), 1.0E-7);
        Assert.assertEquals(0.1888123, instance.p(4), 1.0E-7);
        Assert.assertEquals(0.00229555, instance.p(10), 1.0E-7);
        Assert.assertEquals(9.445079E-10, instance.p(20), 1.0E-10);
        Assert.assertEquals(8.256008E-106, instance.p(100), 1.0E-110);
    }

    /**
     * Test of logPdf method, of class Poisson.
     */
    @Test
    public void testLogPdf() {
        System.out.println("logPdf");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertTrue(Double.isInfinite(instance.logp((-1))));
        Assert.assertEquals((-3.5), instance.logp(0), 1.0E-7);
        Assert.assertEquals((-2.247237), instance.logp(1), 1.0E-6);
        Assert.assertEquals((-1.687621), instance.logp(2), 1.0E-6);
        Assert.assertEquals((-1.533471), instance.logp(3), 1.0E-6);
        Assert.assertEquals((-1.667002), instance.logp(4), 1.0E-6);
        Assert.assertEquals((-6.076783), instance.logp(10), 1.0E-6);
        Assert.assertEquals((-20.78036), instance.logp(20), 1.0E-5);
        Assert.assertEquals((-241.9631), instance.logp(100), 1.0E-4);
    }

    /**
     * Test of cdf method, of class Poisson.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(0, instance.cdf((-1)), 1.0E-7);
        Assert.assertEquals(0.03019738, instance.cdf(0), 1.0E-7);
        Assert.assertEquals(0.1358882, instance.cdf(1), 1.0E-7);
        Assert.assertEquals(0.3208472, instance.cdf(2), 1.0E-7);
        Assert.assertEquals(0.5366327, instance.cdf(3), 1.0E-7);
        Assert.assertEquals(0.725445, instance.cdf(4), 1.0E-6);
        Assert.assertEquals(0.9989806, instance.cdf(10), 1.0E-6);
        Assert.assertEquals(0.999999, instance.cdf(15), 1.0E-6);
        Assert.assertEquals(1.0, instance.cdf(20), 1.0E-6);
    }

    /**
     * Test of quantile method, of class Poisson.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        PoissonDistribution instance = new PoissonDistribution(3.5);
        instance.rand();
        Assert.assertEquals(0, instance.quantile(0.01), 1.0E-7);
        Assert.assertEquals(1, instance.quantile(0.1), 1.0E-7);
        Assert.assertEquals(2, instance.quantile(0.2), 1.0E-7);
        Assert.assertEquals(2, instance.quantile(0.3), 1.0E-7);
        Assert.assertEquals(3, instance.quantile(0.4), 1.0E-6);
        Assert.assertEquals(3, instance.quantile(0.5), 1.0E-6);
        Assert.assertEquals(6, instance.quantile(0.9), 1.0E-6);
        Assert.assertEquals(8, instance.quantile(0.99), 1.0E-6);
    }
}

