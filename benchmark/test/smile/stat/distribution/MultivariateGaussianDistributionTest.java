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
public class MultivariateGaussianDistributionTest {
    double[] mu = new double[]{ 1.0, 0.0, -1.0 };

    double[][] sigma = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    double[][] x = new double[][]{ new double[]{ 1.2793, -0.1029, -1.5852 }, new double[]{ -0.2676, -0.1717, -1.8695 }, new double[]{ 1.6777, 0.7642, -1.0226 }, new double[]{ 2.5402, 1.0887, 0.8989 }, new double[]{ 0.3437, 0.4407, -1.9424 }, new double[]{ 1.814, 0.7413, -0.1129 }, new double[]{ 2.1897, 1.2047, 0.0128 }, new double[]{ -0.5119, -1.3545, -2.6181 }, new double[]{ -0.367, -0.6188, -3.1594 }, new double[]{ 1.5418, 0.1519, -0.6054 } };

    double[] pdf = new double[]{ 0.057, 0.0729, 0.0742, 0.0178, 0.0578, 0.1123, 0.0511, 0.0208, 0.0078, 0.1955 };

    double[] cdf = new double[]{ 0.1752, 0.06, 0.4545, 0.9005, 0.1143, 0.6974, 0.8178, 0.005, 0.0051, 0.4419 };

    public MultivariateGaussianDistributionTest() {
    }

    /**
     * Test of constructor, of class MultivariateGaussianDistribution.
     */
    @Test
    public void testMultivariateGaussianDistribution() {
        System.out.println("MultivariateGaussianDistribution");
        MultivariateGaussianDistribution instance = new MultivariateGaussianDistribution(mu, sigma[0]);
        double[][] data = new double[1000][];
        for (int i = 0; i < (data.length); i++) {
            data[i] = instance.rand();
        }
        MultivariateGaussianDistribution est = new MultivariateGaussianDistribution(data, true);
        for (int i = 0; i < (mu.length); i++) {
            Assert.assertEquals(mu[i], est.mean()[i], 0.15);
        }
        for (int i = 0; i < (mu.length); i++) {
            Assert.assertEquals(sigma[0][i], est.cov()[i][i], 0.15);
            for (int j = 0; j < (mu.length); j++) {
                if (i != j) {
                    Assert.assertEquals(0, est.cov()[i][j], 1.0E-10);
                }
            }
        }
        instance = new MultivariateGaussianDistribution(mu, sigma);
        data = new double[1000][];
        for (int i = 0; i < (data.length); i++) {
            data[i] = instance.rand();
        }
        est = new MultivariateGaussianDistribution(data);
        for (int i = 0; i < (mu.length); i++) {
            Assert.assertEquals(mu[i], est.mean()[i], 0.1);
        }
        for (int i = 0; i < (mu.length); i++) {
            for (int j = 0; j < (mu.length); j++) {
                Assert.assertEquals(sigma[i][j], est.cov()[i][j], 0.1);
            }
        }
        est = new MultivariateGaussianDistribution(data, true);
        for (int i = 0; i < (mu.length); i++) {
            Assert.assertEquals(mu[i], est.mean()[i], 0.1);
        }
        for (int i = 0; i < (mu.length); i++) {
            for (int j = 0; j < (mu.length); j++) {
                if (i == j) {
                    Assert.assertEquals(sigma[i][j], est.cov()[i][j], 0.1);
                } else {
                    Assert.assertEquals(0.0, est.cov()[i][j], 1.0E-10);
                }
            }
        }
    }

    /**
     * Test of isDiagonal method, of class MultivariateGaussian.
     */
    @Test
    public void testIsDiagonal() {
        System.out.println("isDiagonal");
        MultivariateGaussianDistribution instance = new MultivariateGaussianDistribution(mu, 1.0);
        Assert.assertEquals(true, instance.isDiagonal());
        instance = new MultivariateGaussianDistribution(mu, sigma[0]);
        Assert.assertEquals(true, instance.isDiagonal());
        instance = new MultivariateGaussianDistribution(mu, sigma);
        Assert.assertEquals(false, instance.isDiagonal());
    }

    /**
     * Test of npara method, of class MultivariateGaussian.
     */
    @Test
    public void testNpara() {
        System.out.println("npara");
        MultivariateGaussianDistribution instance = new MultivariateGaussianDistribution(mu, 1.0);
        Assert.assertEquals(4, instance.npara());
        instance = new MultivariateGaussianDistribution(mu, sigma[0]);
        Assert.assertEquals(6, instance.npara());
        instance = new MultivariateGaussianDistribution(mu, sigma);
        Assert.assertEquals(9, instance.npara());
    }

    /**
     * Test of entropy method, of class MultivariateGaussian.
     */
    @Test
    public void testEntropy() {
        System.out.println("entropy");
        MultivariateGaussianDistribution instance = new MultivariateGaussianDistribution(mu, sigma);
        Assert.assertEquals(2.954971, instance.entropy(), 1.0E-6);
    }

    /**
     * Test of pdf method, of class MultivariateGaussian.
     */
    @Test
    public void testPdf() {
        System.out.println("pdf");
        MultivariateGaussianDistribution instance = new MultivariateGaussianDistribution(mu, sigma);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(pdf[i], instance.p(x[i]), 1.0E-4);
        }
    }

    /**
     * Test of cdf method, of class MultivariateGaussian.
     */
    @Test
    public void testCdf() {
        System.out.println("cdf");
        MultivariateGaussianDistribution instance = new MultivariateGaussianDistribution(mu, sigma);
        for (int i = 0; i < (x.length); i++) {
            Assert.assertEquals(cdf[i], instance.cdf(x[i]), 0.005);
        }
    }

    /**
     * Test of cdf method, of class MultivariateGaussian.
     */
    @Test
    public void testCdf2() {
        System.out.println("cdf2");
        double[][] S = new double[][]{ new double[]{ 3.260127902272362, 2.343938296424249, 0.1409050254343716, -0.1628775438743266 }, new double[]{ 2.343938296424249, 4.21303499138833, 1.3997210599608563, 0.3373448510018783 }, new double[]{ 0.1409050254343716, 1.3997210599608563, 4.604248526367794, 0.0807267064408651 }, new double[]{ -0.1628775438743266, 0.3373448510018783, 0.0807267064408651, 5.495094921589067 } };
        double[] M = new double[]{ -0.683477474844462, 1.480296478403701, 1.008431991316523, 0.448404211078558 };
        double[] X = new double[]{ 0.713919336274493, 0.584408785741822, 0.263119200077829, 0.732513610871908 };
        MultivariateGaussianDistribution mvn = new MultivariateGaussianDistribution(M, S);
        // According to R, the result should be 0.0904191282120575
        double tol = 0.001;
        Assert.assertEquals(0.0904191282120575, mvn.cdf(X), 0.001);
    }
}

