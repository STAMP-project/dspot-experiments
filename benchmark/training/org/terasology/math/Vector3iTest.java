/**
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.math;


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;


/**
 *
 */
public class Vector3iTest {
    private Vector3i v1;

    private Vector3i v2;

    private Vector3i v3;

    public Vector3iTest() {
    }

    @Test
    public void testEmptyConstructor() {
        Vector3i v = new Vector3i();
        Assert.assertEquals(0, v.x);
        Assert.assertEquals(0, v.y);
        Assert.assertEquals(0, v.z);
    }

    @Test
    public void testTripleConstructor() {
        Vector3i v = new Vector3i(1, 2, 3);
        Assert.assertEquals(1, v.x);
        Assert.assertEquals(2, v.y);
        Assert.assertEquals(3, v.z);
    }

    @Test
    public void testOffsetConstructor() {
        Vector3f vOrig = new Vector3f(0.1F, 0.6F, 7.2F);
        Vector3i v = new Vector3i(vOrig, 0.5F);
        Assert.assertEquals(new Vector3i(0, 1, 7), v);
    }

    @Test
    public void testOffsetConstructorWithNegatives() {
        Vector3f vOrig = new Vector3f((-0.1F), (-0.6F), (-1.4F));
        Vector3i v = new Vector3i(vOrig, 0.5F);
        Assert.assertEquals(new Vector3i(0, (-1), (-1)), v);
    }

    @Test
    public void testCopyConstructor() {
        Vector3i copy = new Vector3i(v1);
        Assert.assertEquals(v1.x, copy.x);
        Assert.assertEquals(v1.y, copy.y);
        Assert.assertEquals(v1.z, copy.z);
    }

    @Test
    public void testEquals() {
        Assert.assertFalse(v1.equals(v2));
        Assert.assertTrue(v1.equals(new Vector3i(v1.x, v1.y, v1.z)));
        Assert.assertFalse(v1.equals(null));
    }

    @Test
    public void testSetTriple() {
        Vector3i v = new Vector3i(v1);
        v.set(v2.x, v2.y, v2.z);
        Assert.assertEquals(v2, v);
    }

    @Test
    public void testSetCopy() {
        Vector3i v = new Vector3i();
        v.set(v2);
        Assert.assertEquals(v2, v);
    }

    @Test
    public void testAdd() {
        Vector3i v = new Vector3i(v1);
        v.add(v2);
        Assert.assertEquals(v3, v);
    }

    @Test
    public void testAddTriple() {
        Vector3i v = new Vector3i(v1);
        v.add(v2.x, v2.y, v2.z);
        Assert.assertEquals(v3, v);
    }

    @Test
    public void testMin() {
        Vector3i v = new Vector3i(v1);
        v.min(new Vector3i(v1.z, v1.y, v1.x));
        Assert.assertEquals(Math.min(v1.x, v1.z), v.x);
        Assert.assertEquals(v1.y, v.y);
        Assert.assertEquals(Math.min(v1.x, v1.z), v.z);
    }

    @Test
    public void testMax() {
        Vector3i v = new Vector3i(v1);
        v.max(new Vector3i(v1.z, v1.y, v1.x));
        Assert.assertEquals(Math.max(v1.x, v1.z), v.x);
        Assert.assertEquals(v1.y, v.y);
        Assert.assertEquals(Math.max(v1.x, v1.z), v.z);
    }

    @Test
    public void testManhattanDistance() {
        Assert.assertEquals(0, Vector3i.zero().gridDistance(Vector3i.zero()));
        Assert.assertEquals(1, Vector3i.zero().gridDistance(Vector3i.west()));
        Assert.assertEquals(1, Vector3i.zero().gridDistance(Vector3i.up()));
        Assert.assertEquals(1, Vector3i.zero().gridDistance(Vector3i.north()));
        Assert.assertEquals(3, Vector3i.zero().gridDistance(Vector3i.one()));
        Assert.assertEquals(3, Vector3i.zero().gridDistance(new Vector3i(1, (-1), 1)));
    }

    @Test
    public void testHash() {
        // k = 59
        // j = k+1 = 60
        // i = (k+k^2)(1+k)^2 = not important for collision
        // hash = (k+k^2)(1+k)^2 + x(1+k)^2 + (1+k)y + z
        // hash = i + xj^2 + jy + z
        // with x := 0
        // hash = i + jy + z
        // set i + jy + z = i + jy' + z'
        // jy + z = jy' + z'
        // set z' := z + j -> z'-z = j
        // jy = jy' + j -> y' = y - 1
        Vector3i a = new Vector3i(0, 10, 10);
        Vector3i b = new Vector3i(0, 9, 70);
        Assert.assertFalse(((a.hashCode()) == (b.hashCode())));
        Assert.assertTrue(((new Vector3i(0, 10, 10).hashCode()) == (new Vector3i(0, 10, 10).hashCode())));
        Assert.assertTrue(((new Vector3i((-100), 10, 10).hashCode()) == (new Vector3i((-100), 10, 10).hashCode())));
        Assert.assertTrue(((new Vector3i(0, (-5), (-5)).hashCode()) == (new Vector3i(0, (-5), (-5)).hashCode())));
        Assert.assertFalse(((new Vector3i(1, 10, 10).hashCode()) == (new Vector3i(0, 10, 10).hashCode())));
        Assert.assertFalse(((new Vector3i((-101), 10, 10).hashCode()) == (new Vector3i((-100), 10, 10).hashCode())));
        Assert.assertFalse(((new Vector3i(0, (-1), (-5)).hashCode()) == (new Vector3i(0, (-5), (-5)).hashCode())));
    }

    @Test
    public void testHashCollisions() {
        int range = 50;
        Set<Integer> alreadyUsedHashes = new HashSet<>();
        for (int x = -range; x < range; ++x) {
            for (int y = -range; y < range; ++y) {
                for (int z = -range; z < range; ++z) {
                    int hash = new Vector3i(x, y, z).hashCode();
                    if (alreadyUsedHashes.contains(hash)) {
                        Assert.fail(String.format("duplicate hash %d at: %d,%d,%d", hash, x, y, z));
                    } else {
                        alreadyUsedHashes.add(hash);
                    }
                }
            }
        }
    }
}

