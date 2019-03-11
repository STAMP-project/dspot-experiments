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
package smile.math.distance;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class MahalanobisDistanceTest {
    double[][] sigma = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };

    public MahalanobisDistanceTest() {
    }

    /**
     * Test of distance method, of class MahalanobisDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        double[] x = new double[]{ 1.2793, -0.1029, -1.5852 };
        double[] y = new double[]{ -0.2676, -0.1717, -1.8695 };
        MahalanobisDistance instance = new MahalanobisDistance(sigma);
        Assert.assertEquals(2.703861, instance.d(x, y), 1.0E-6);
    }
}

