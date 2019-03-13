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
public class ChiSquareDistributionTest {
    public ChiSquareDistributionTest() {
    }

    /**
     * Test of npara method, of class ChiSquareDistribution.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(1, instance.npara());
    }

    /**
     * Test of mean method, of class ChiSquareDistribution.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(20, instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class ChiSquareDistribution.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(40, instance.var(), 1.0E-7);
    }

    /**
     * Test of sd method, of class ChiSquareDistribution.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(Math.sqrt(40), instance.sd(), 1.0E-7);
    }

    /**
     * Test of entropy method, of class ChiSquareDistribution.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(3.229201359, instance.entropy(), 1.0E-7);
    }

    /**
     * Test of p method, of class ChiSquareDistribution.
     */
    @Test
    public void testP() {
        System.out.println("p");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(0.0, instance.p(0), 1.0E-7);
        Assert.assertEquals(2.559896E-18, instance.p(0.1), 1.0E-22);
        Assert.assertEquals(1.632262E-9, instance.p(1), 1.0E-15);
        Assert.assertEquals(0.01813279, instance.p(10), 1.0E-7);
        Assert.assertEquals(0.062555, instance.p(20), 1.0E-7);
        Assert.assertEquals(7.2997E-5, instance.p(50), 1.0E-10);
        Assert.assertEquals(5.190544E-13, instance.p(100), 1.0E-18);
    }

    /**
     * Test of logP method, of class ChiSquareDistribution.
     */
    @Test
    public void testLogP() {
        System.out.println("logP");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(Math.log(2.559896E-18), instance.logp(0.1), 1.0E-5);
        Assert.assertEquals(Math.log(1.632262E-9), instance.logp(1), 1.0E-5);
        Assert.assertEquals(Math.log(0.01813279), instance.logp(10), 1.0E-5);
        Assert.assertEquals(Math.log(0.062555), instance.logp(20), 1.0E-5);
        Assert.assertEquals(Math.log(7.2997E-5), instance.logp(50), 1.0E-5);
        Assert.assertEquals(Math.log(5.190544E-13), instance.logp(100), 1.0E-5);
    }

    /**
     * Test of cdf method, of class ChiSquareDistribution.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(0.0, instance.cdf(0), 1.0E-7);
        Assert.assertEquals(2.57158E-20, instance.cdf(0.1), 1.0E-25);
        Assert.assertEquals(1.70967E-10, instance.cdf(1), 1.0E-15);
        Assert.assertEquals(0.03182806, instance.cdf(10), 1.0E-7);
        Assert.assertEquals(0.5420703, instance.cdf(20), 1.0E-7);
        Assert.assertEquals(0.9997785, instance.cdf(50), 1.0E-7);
        Assert.assertEquals(1.0, instance.cdf(100), 1.0E-7);
    }

    /**
     * Test of quantile method, of class ChiSquareDistribution.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        ChiSquareDistribution instance = new ChiSquareDistribution(20);
        instance.rand();
        Assert.assertEquals(0.0, instance.quantile(0), 1.0E-7);
        Assert.assertEquals(12.44261, instance.quantile(0.1), 1.0E-5);
        Assert.assertEquals(14.57844, instance.quantile(0.2), 1.0E-5);
        Assert.assertEquals(16.26586, instance.quantile(0.3), 1.0E-5);
        Assert.assertEquals(19.33743, instance.quantile(0.5), 1.0E-5);
        Assert.assertEquals(28.41198, instance.quantile(0.9), 1.0E-5);
    }
}

