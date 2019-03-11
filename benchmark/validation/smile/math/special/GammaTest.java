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
package smile.math.special;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class GammaTest {
    public GammaTest() {
    }

    /**
     * Test of gamma method, of class Gamma.
     */
    @Test
    public void testGamma() {
        System.out.println("gamma");
        Assert.assertTrue(Double.isInfinite(Gamma.gamma(0)));
        Assert.assertEquals(1.0, Gamma.gamma(1), 1.0E-7);
        Assert.assertEquals(1.0, Gamma.gamma(2), 1.0E-7);
        Assert.assertEquals(2.0, Gamma.gamma(3), 1.0E-7);
        Assert.assertEquals(6.0, Gamma.gamma(4), 1.0E-7);
        Assert.assertEquals(0.886227, Gamma.gamma(1.5), 1.0E-6);
        Assert.assertEquals(1.32934, Gamma.gamma(2.5), 1.0E-6);
        Assert.assertEquals(3.323351, Gamma.gamma(3.5), 1.0E-6);
        Assert.assertEquals(11.63173, Gamma.gamma(4.5), 1.0E-5);
    }

    /**
     * Test of lgamma method, of class Gamma.
     */
    @Test
    public void testLogGamma() {
        System.out.println("lgamma");
        Assert.assertTrue(Double.isInfinite(Gamma.lgamma(0)));
        Assert.assertEquals(0.0, Gamma.lgamma(1), 1.0E-7);
        Assert.assertEquals(0, Gamma.lgamma(2), 1.0E-7);
        Assert.assertEquals(Math.log(2.0), Gamma.lgamma(3), 1.0E-7);
        Assert.assertEquals(Math.log(6.0), Gamma.lgamma(4), 1.0E-7);
        Assert.assertEquals((-0.1207822), Gamma.lgamma(1.5), 1.0E-7);
        Assert.assertEquals(0.2846829, Gamma.lgamma(2.5), 1.0E-7);
        Assert.assertEquals(1.200974, Gamma.lgamma(3.5), 1.0E-6);
        Assert.assertEquals(2.453737, Gamma.lgamma(4.5), 1.0E-6);
    }

    /**
     * Test of incompleteGamma method, of class Gamma.
     */
    @Test
    public void testIncompleteGamma() {
        System.out.println("incompleteGamma");
        Assert.assertEquals(0.7807, Gamma.regularizedIncompleteGamma(2.1, 3), 1.0E-4);
        Assert.assertEquals(0.3504, Gamma.regularizedIncompleteGamma(3, 2.1), 1.0E-4);
    }

    /**
     * Test of upperIncompleteGamma method, of class Gamma.
     */
    @Test
    public void testUpperIncompleteGamma() {
        System.out.println("incompleteGamma");
        Assert.assertEquals(0.2193, Gamma.regularizedUpperIncompleteGamma(2.1, 3), 1.0E-4);
        Assert.assertEquals(0.6496, Gamma.regularizedUpperIncompleteGamma(3, 2.1), 1.0E-4);
    }
}

