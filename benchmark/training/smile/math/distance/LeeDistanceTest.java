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
public class LeeDistanceTest {
    public LeeDistanceTest() {
    }

    /**
     * Test of distance method, of class LeeDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        int[] x = new int[]{ 3, 3, 4, 0 };
        int[] y = new int[]{ 2, 5, 4, 3 };
        LeeDistance instance = new LeeDistance(6);
        Assert.assertEquals(6.0, instance.d(x, y), 1.0E-9);
    }
}

