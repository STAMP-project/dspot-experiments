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
package smile.stat.hypothesis;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class FTestTest {
    public FTestTest() {
    }

    /**
     * Test of test method, of class FTest.
     */
    @Test
    public void testTest() {
        System.out.println("test");
        double[] x = new double[]{ 0.48074284, -0.52975023, 1.28590721, 0.63456079, -0.41761197, 2.76072411, 1.30321095, -1.16454533, 2.27210509, 1.46394553, -0.31713164, 1.26247543, 2.6588643, 0.4077345, 1.1805544, -0.39611251, 2.13557687, 0.4087886, 1.28461394, -0.02906355 };
        double[] y = new double[]{ 1.7495879, 1.9359727, 3.1294928, 0.0861894, 2.1643415, 0.1913219, -0.3947444, 1.6910837, 1.1548294, 0.2763955, 0.4794719, 3.1805501, 1.5700497, 2.686019, -0.4410879, 1.8900183, 1.3422381, -0.1701592 };
        FTest result = FTest.test(x, y);
        Assert.assertEquals(17, result.df1, 1.0E-10);
        Assert.assertEquals(19, result.df2, 1.0E-10);
        Assert.assertEquals((1 / 0.9126), result.f, 1.0E-4);
        Assert.assertEquals(0.8415, result.pvalue, 1.0E-4);
        double[] z = new double[]{ 0.6621329, 0.4688975, -0.1553013, 0.4564548, 2.2776146, 2.1543678, 2.8555142, 1.5852899, 0.909129, 1.6060025, 1.0111968, 1.2479493, 0.9407034, 1.7167572, 0.5380608, 2.1290007, 1.8695506, 1.2139096 };
        result = FTest.test(x, z);
        Assert.assertEquals(19, result.df1, 1.0E-10);
        Assert.assertEquals(17, result.df2, 1.0E-10);
        Assert.assertEquals(2.046, result.f, 0.001);
        Assert.assertEquals(0.1438, result.pvalue, 1.0E-4);
    }
}

