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
public class KrigingInterpolationTest {
    public KrigingInterpolationTest() {
    }

    /**
     * Test of interpolate method, of class KrigingInterpolation.
     */
    @Test
    public void testInterpolate() {
        System.out.println("interpolate");
        double[][] x = new double[][]{ new double[]{ 0, 0 }, new double[]{ 1, 1 } };
        double[] y = new double[]{ 0, 1 };
        KrigingInterpolation instance = new KrigingInterpolation(x, y);
        double[] x1 = new double[]{ 0.5, 0.5 };
        Assert.assertEquals(0, instance.interpolate(x[0]), 1.0E-7);
        Assert.assertEquals(1, instance.interpolate(x[1]), 1.0E-7);
        Assert.assertEquals(0.5, instance.interpolate(x1), 1.0E-7);
    }
}

