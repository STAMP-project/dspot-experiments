/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math;


import Vector.Element;
import java.util.Iterator;
import org.junit.Test;


public class PermutedVectorViewTest extends MahoutTestCase {
    @Test
    public void testViewBasics() {
        Vector v = PermutedVectorViewTest.randomVector();
        int[] pivot = PermutedVectorViewTest.pivot();
        Vector pvv = new PermutedVectorView(v, pivot);
        // verify the view has the same contents
        for (int i = 0; i < 20; i++) {
            assertEquals(("Element " + i), v.get(pivot[i]), pvv.get(i), 0);
        }
        // change a view element or two on each side
        pvv.set(6, 321);
        v.set(9, 512);
        // verify again
        for (int i = 0; i < 20; i++) {
            assertEquals(("Element " + i), v.get(pivot[i]), pvv.get(i), 0);
        }
    }

    @Test
    public void testIterators() {
        int[] pivot = PermutedVectorViewTest.pivot();
        int[] unpivot = PermutedVectorViewTest.unpivot();
        Vector v = PermutedVectorViewTest.randomVector();
        PermutedVectorView pvv = new PermutedVectorView(v, pivot);
        // check a simple operation and thus an iterator
        assertEquals(v.zSum(), pvv.zSum(), 0);
        assertEquals(v.getNumNondefaultElements(), pvv.getNumNondefaultElements());
        v.set(11, 0);
        assertEquals(v.getNumNondefaultElements(), pvv.getNumNondefaultElements());
        Iterator<Vector.Element> vi = pvv.iterator();
        int i = 0;
        while (vi.hasNext()) {
            Vector.Element e = vi.next();
            assertEquals(("Index " + i), i, pivot[e.index()]);
            assertEquals(("Reverse Index " + i), unpivot[i], e.index());
            assertEquals(("Self-value " + i), e.get(), pvv.get(e.index()), 0);
            // note that we iterate in the original vector order
            assertEquals(("Value " + i), v.get(i), e.get(), 0);
            i++;
        } 
    }
}

