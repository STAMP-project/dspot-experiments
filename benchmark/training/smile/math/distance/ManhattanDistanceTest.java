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
public class ManhattanDistanceTest {
    public ManhattanDistanceTest() {
    }

    /**
     * Test of distance method, of class ManhattanDistance.
     */
    @Test
    public void testDistanceInt() {
        System.out.println("distance");
        int[] x = new int[]{ 1, 2, 3, 4 };
        int[] y = new int[]{ 4, 3, 2, 1 };
        Assert.assertEquals(8, new ManhattanDistance().d(x, y), 1.0E-6);
    }

    /**
     * Test of distance method, of class ManhattanDistance.
     */
    @Test
    public void testDistanceDouble() {
        System.out.println("distance");
        double[] x = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] y = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(4.485227, new ManhattanDistance().d(x, y), 1.0E-6);
    }
}

