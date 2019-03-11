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
public class BinomialDistributionTest {
    public BinomialDistributionTest() {
    }

    /**
     * Test of npara method, of class BinomialDistribution.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(2, instance.npara());
    }

    /**
     * Test of mean method, of class BinomialDistribution.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(30.0, instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class BinomialDistribution.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(21.0, instance.var(), 1.0E-7);
    }

    /**
     * Test of sd method, of class BinomialDistribution.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(Math.sqrt(21.0), instance.sd(), 1.0E-7);
    }

    /**
     * Test of entropy method, of class BinomialDistribution.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(2.9412, instance.entropy(), 1.0E-4);
    }

    /**
     * Test of p method, of class BinomialDistribution.
     */
    @Test
    public void testP() {
        System.out.println("p");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(3.234477E-16, instance.p(0), 1.0E-20);
        Assert.assertEquals(1.386204E-14, instance.p(1), 1.0E-18);
        Assert.assertEquals(1.170418E-6, instance.p(10), 1.0E-10);
        Assert.assertEquals(0.007575645, instance.p(20), 1.0E-7);
        Assert.assertEquals(0.08678386, instance.p(30), 1.0E-7);
        Assert.assertEquals(5.153775E-53, instance.p(100), 1.0E-58);
    }

    /**
     * Test of logP method, of class BinomialDistribution.
     */
    @Test
    public void testLogP() {
        System.out.println("logP");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(Math.log(3.234477E-16), instance.logp(0), 1.0E-5);
        Assert.assertEquals(Math.log(1.386204E-14), instance.logp(1), 1.0E-5);
        Assert.assertEquals(Math.log(1.170418E-6), instance.logp(10), 1.0E-5);
        Assert.assertEquals(Math.log(0.007575645), instance.logp(20), 1.0E-5);
        Assert.assertEquals(Math.log(0.08678386), instance.logp(30), 1.0E-5);
        Assert.assertEquals(Math.log(5.153775E-53), instance.logp(100), 1.0E-5);
    }

    /**
     * Test of cdf method, of class BinomialDistribution.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(3.234477E-16, instance.cdf(0), 1.0E-20);
        Assert.assertEquals(1.418549E-14, instance.cdf(1), 1.0E-18);
        Assert.assertEquals(1.555566E-6, instance.cdf(10), 1.0E-10);
        Assert.assertEquals(0.01646285, instance.cdf(20), 1.0E-7);
        Assert.assertEquals(0.5491236, instance.cdf(30), 1.0E-7);
        Assert.assertEquals(1.0, instance.cdf(100), 1.0E-7);
    }

    /**
     * Test of quantile method, of class BinomialDistribution.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        BinomialDistribution instance = new BinomialDistribution(100, 0.3);
        instance.rand();
        Assert.assertEquals(0, instance.quantile(0), 1.0E-7);
        Assert.assertEquals(0, instance.quantile(1.0E-17), 1.0E-7);
        Assert.assertEquals(17, instance.quantile(0.001), 1.0E-7);
        Assert.assertEquals(20, instance.quantile(0.01), 1.0E-7);
        Assert.assertEquals(24, instance.quantile(0.1), 1.0E-7);
        Assert.assertEquals(26, instance.quantile(0.2), 1.0E-7);
        Assert.assertEquals(30, instance.quantile(0.5), 1.0E-7);
        Assert.assertEquals(36, instance.quantile(0.9), 1.0E-7);
        Assert.assertEquals(100, instance.quantile(1.0), 1.0E-7);
    }
}

