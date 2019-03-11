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


import java.util.BitSet;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class HammingDistanceTest {
    public HammingDistanceTest() {
    }

    /**
     * Test of distance method, of class HammingDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        int x = 93;
        int y = 73;
        Assert.assertEquals(2, HammingDistance.d(x, y));
    }

    /**
     * Test of distance method, of class HammingDistance.
     */
    @Test
    public void testDistanceArray() {
        System.out.println("distance");
        byte[] x = new byte[]{ 1, 0, 1, 1, 1, 0, 1 };
        byte[] y = new byte[]{ 1, 0, 0, 1, 0, 0, 1 };
        Assert.assertEquals(2, HammingDistance.d(x, y));
    }

    /**
     * Test of distance method, of class HammingDistance.
     */
    @Test
    public void testDistanceBitSet() {
        System.out.println("distance");
        BitSet x = new BitSet();
        x.set(1);
        x.set(3);
        x.set(4);
        x.set(5);
        x.set(7);
        BitSet y = new BitSet();
        y.set(1);
        y.set(4);
        y.set(7);
        Assert.assertEquals(2, HammingDistance.d(x, y), 1.0E-9);
    }
}

