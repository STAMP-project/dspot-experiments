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


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class JaccardDistanceTest {
    public JaccardDistanceTest() {
    }

    /**
     * Test of distance method, of class JaccardDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        Set<Integer> a = new HashSet<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        Set<Integer> b = new HashSet<>();
        b.add(3);
        b.add(4);
        b.add(5);
        b.add(6);
        Assert.assertEquals(0.6666667, JaccardDistance.d(a, b), 1.0E-7);
    }
}

