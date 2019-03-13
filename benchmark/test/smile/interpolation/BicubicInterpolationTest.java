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
package smile.interpolation;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class BicubicInterpolationTest {
    public BicubicInterpolationTest() {
    }

    /**
     * Test of interpolate method, of class BicubicInterpolation.
     */
    @Test
    public void testInterpolate() {
        System.out.println("interpolate");
        double[] x1 = new double[]{ 1950, 1960, 1970, 1980, 1990 };
        double[] x2 = new double[]{ 10, 20, 30 };
        double[][] y = new double[][]{ new double[]{ 150.697, 199.592, 187.625 }, new double[]{ 179.323, 195.072, 250.287 }, new double[]{ 203.212, 179.092, 322.767 }, new double[]{ 226.505, 153.706, 426.73 }, new double[]{ 249.633, 120.281, 598.243 } };
        BicubicInterpolation instance = new BicubicInterpolation(x1, x2, y);
        Assert.assertEquals(203.212, instance.interpolate(1970, 10), 0.001);
        Assert.assertEquals(179.092, instance.interpolate(1970, 20), 0.001);
        Assert.assertEquals(249.633, instance.interpolate(1990, 10), 0.001);
        Assert.assertEquals(598.243, instance.interpolate(1990, 30), 0.001);
        // assertEquals(182.7523, instance.interpolate(1950, 15), 1E-4);
        // assertEquals(109.0428, instance.interpolate(1990, 15), 1E-4);
        // assertEquals(504.0428, instance.interpolate(1985, 30), 1E-4);
        // assertEquals(160.1369, instance.interpolate(1975, 15), 1E-4);
        Assert.assertEquals(167.4893, instance.interpolate(1975, 20), 1.0E-4);
        // assertEquals(236.9633, instance.interpolate(1975, 25), 1E-4);
    }
}

