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
public class CubicSplineInterpolation1DTest {
    public CubicSplineInterpolation1DTest() {
    }

    /**
     * Test of interpolate method, of class CubicSplineInterpolation.
     */
    @Test
    public void testInterpolate() {
        System.out.println("interpolate");
        double[] x = new double[]{ 0, 1, 2, 3, 4, 5, 6 };
        double[] y = new double[]{ 0, 0.8415, 0.9093, 0.1411, -0.7568, -0.9589, -0.2794 };
        CubicSplineInterpolation1D instance = new CubicSplineInterpolation1D(x, y);
        Assert.assertEquals(0.5962098, instance.interpolate(2.5), 1.0E-7);
    }
}

