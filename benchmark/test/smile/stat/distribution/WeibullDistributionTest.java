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
import smile.math.special.Gamma;


/**
 *
 *
 * @author Haifeng Li
 */
public class WeibullDistributionTest {
    public WeibullDistributionTest() {
    }

    /**
     * Test of npara method, of class WeibullDistribution.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(2, instance.npara());
    }

    /**
     * Test of mean method, of class WeibullDistribution.
     */
    @Test
    public void testMean() {
        System.out.println("mean");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(Gamma.gamma((1 + (1 / 1.5))), instance.mean(), 1.0E-7);
    }

    /**
     * Test of var method, of class WeibullDistribution.
     */
    @Test
    public void testVar() {
        System.out.println("var");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(0.37569028, instance.var(), 1.0E-7);
    }

    /**
     * Test of sd method, of class WeibullDistribution.
     */
    @Test
    public void testSd() {
        System.out.println("sd");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(0.61293579, instance.sd(), 1.0E-7);
    }

    /**
     * Test of entropy method, of class WeibullDistribution.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(0.78694011, instance.entropy(), 1.0E-7);
    }

    /**
     * Test of p method, of class WeibullDistribution.
     */
    @Test
    public void testP() {
        System.out.println("p");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(0.0, instance.p(0.0), 1.0E-7);
        Assert.assertEquals(0.4595763, instance.p(0.1), 1.0E-7);
        Assert.assertEquals(0.6134254, instance.p(0.2), 1.0E-7);
        Assert.assertEquals(0.7447834, instance.p(0.5), 1.0E-7);
        Assert.assertEquals(0.2926085, instance.p(1.5), 1.0E-7);
        Assert.assertEquals(0.0455367, instance.p(2.5), 1.0E-7);
        Assert.assertEquals(4.677527E-5, instance.p(5.0), 1.0E-10);
    }

    /**
     * Test of logP method, of class WeibullDistribution.
     */
    @Test
    public void testLogP() {
        System.out.println("logP");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(Math.log(0.4595763), instance.logp(0.1), 1.0E-5);
        Assert.assertEquals(Math.log(0.6134254), instance.logp(0.2), 1.0E-5);
        Assert.assertEquals(Math.log(0.7447834), instance.logp(0.5), 1.0E-5);
        Assert.assertEquals(Math.log(0.2926085), instance.logp(1.5), 1.0E-5);
        Assert.assertEquals(Math.log(0.0455367), instance.logp(2.5), 1.0E-5);
        Assert.assertEquals(Math.log(4.677527E-5), instance.logp(5.0), 1.0E-5);
    }

    /**
     * Test of cdf method, of class WeibullDistribution.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(0.0, instance.cdf(0.0), 1.0E-7);
        Assert.assertEquals(0.03112801, instance.cdf(0.1), 1.0E-7);
        Assert.assertEquals(0.08555936, instance.cdf(0.2), 1.0E-7);
        Assert.assertEquals(0.2978115, instance.cdf(0.5), 1.0E-7);
        Assert.assertEquals(0.8407241, instance.cdf(1.5), 1.0E-7);
        Assert.assertEquals(0.9808, instance.cdf(2.5), 1.0E-7);
        Assert.assertEquals(0.999986, instance.cdf(5.0), 1.0E-6);
    }

    /**
     * Test of quantile method, of class WeibullDistribution.
     */
    @Test
    public void testQuantile() {
        System.out.println("quantile");
        WeibullDistribution instance = new WeibullDistribution(1.5, 1.0);
        instance.rand();
        Assert.assertEquals(0.0, instance.p(0.0), 1.0E-7);
        Assert.assertEquals(0.2230755, instance.quantile(0.1), 1.0E-7);
        Assert.assertEquals(0.3678942, instance.quantile(0.2), 1.0E-7);
        Assert.assertEquals(0.7832198, instance.quantile(0.5), 1.0E-7);
        Assert.assertEquals(1.743722, instance.quantile(0.9), 1.0E-6);
        Assert.assertEquals(2.767985, instance.quantile(0.99), 1.0E-6);
        Assert.assertEquals(3.627087, instance.quantile(0.999), 1.0E-6);
    }
}

