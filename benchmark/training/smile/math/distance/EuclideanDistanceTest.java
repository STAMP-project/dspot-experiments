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
import smile.math.SparseArray;


/**
 *
 *
 * @author Haifeng Li
 */
public class EuclideanDistanceTest {
    public EuclideanDistanceTest() {
    }

    /**
     * Test of distance method, of class EuclideanDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        double[] x = new double[]{ 1.0, 2.0, 3.0, 4.0 };
        double[] y = new double[]{ 4.0, 3.0, 2.0, 1.0 };
        Assert.assertEquals(4.472136, new EuclideanDistance().d(x, y), 1.0E-6);
        double[] w = new double[]{ -2.1968219, -0.9559913, -0.0431738, 1.0567679, 0.3853515 };
        double[] v = new double[]{ -1.7781325, -0.6659839, 0.9526148, -0.9460919, -0.39253 };
        Assert.assertEquals(2.422302, new EuclideanDistance().d(w, v), 1.0E-6);
        SparseArray s = new SparseArray();
        s.append(1, 1.0);
        s.append(2, 2.0);
        s.append(3, 3.0);
        s.append(4, 4.0);
        SparseArray t = new SparseArray();
        t.append(1, 4.0);
        t.append(2, 3.0);
        t.append(3, 2.0);
        t.append(4, 1.0);
        Assert.assertEquals(4.472136, new SparseEuclideanDistance().d(s, t), 1.0E-6);
        s = new SparseArray();
        s.append(2, 2.0);
        s.append(3, 3.0);
        s.append(4, 4.0);
        t = new SparseArray();
        t.append(1, 4.0);
        t.append(2, 3.0);
        t.append(3, 2.0);
        Assert.assertEquals(5.830951, new SparseEuclideanDistance().d(s, t), 1.0E-6);
        s = new SparseArray();
        s.append(1, 1.0);
        t = new SparseArray();
        t.append(3, 2.0);
        Assert.assertEquals(2.236067, new SparseEuclideanDistance().d(s, t), 1.0E-6);
    }
}

