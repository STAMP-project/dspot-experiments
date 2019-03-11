/**
 * Copyright 2014 MovingBlocks
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
package org.terasology.utilities.tree;


import java.util.Collection;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class SpaceTreeTest {
    @Test
    public void test2DTreeErrors() {
        SpaceTree<Object> tree = new SpaceTree(2);
        try {
            tree.add(null, new Object());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
        try {
            tree.add(new float[1], new Object());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
        try {
            tree.add(new float[3], new Object());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
        try {
            tree.add(new float[2], null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
        try {
            tree.remove(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
        try {
            tree.remove(new float[1]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
        try {
            tree.remove(new float[3]);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException exp) {
            // Expected
        }
    }

    @Test
    public void test2DTreeValues() {
        SpaceTree<Object> tree = new SpaceTree(2);
        Assert.assertNull(tree.remove(new float[2]));
        Object obj1 = new Object();
        Object obj2 = new Object();
        Assert.assertNull(tree.add(new float[2], obj1));
        Assert.assertSame(obj1, tree.add(new float[2], obj2));
        Assert.assertSame(obj2, tree.remove(new float[2]));
        Assert.assertNull(tree.remove(new float[2]));
    }

    @Test
    public void test3DTreeBasicProximity() {
        SpaceTree<Object> tree = new SpaceTree(3);
        Object obj1 = new Object();
        Object obj2 = new Object();
        float[] location1 = new float[]{ 0.0F, 0.0F, 0.0F };
        float[] location2 = new float[]{ 10.0F, 10.0F, 10.0F };
        tree.add(location1, obj1);
        tree.add(location2, obj2);
        Assert.assertSame(obj1, tree.findNearest(new float[]{ 3.0F, 3.0F, 3.0F }).value);
        Assert.assertSame(obj2, tree.findNearest(new float[]{ 100.0F, 100.0F, 0.0F }).value);
        float[] location3 = new float[]{ 5.0F, 5.0F, 5.0F };
        Object obj3 = new Object();
        tree.add(location3, obj3);
        Assert.assertSame(obj3, tree.findNearest(new float[]{ 3.0F, 3.0F, 3.0F }).value);
        Assert.assertSame(obj2, tree.findNearest(new float[]{ 100.0F, 100.0F, 0.0F }).value);
    }

    @Test
    public void test3DProximityTest() {
        SpaceTree<Object> tree = new SpaceTree(3);
        Object[] objects = new Object[]{ new Object(), new Object(), new Object(), new Object(), new Object(), new Object() };
        float[][] locations = new float[][]{ new float[]{ 0.0F, 0.0F, 0.0F }, new float[]{ 0.0F, 0.0F, 1.0F }, new float[]{ 0.0F, 0.0F, 2.0F }, new float[]{ 0.0F, 0.0F, 3.0F }, new float[]{ 0.0F, 0.0F, 4.0F }, new float[]{ 0.0F, 0.0F, 5.0F } };
        for (int i = 0; i < (objects.length); i++) {
            tree.add(locations[i], objects[i]);
        }
        float delta = 1.0E-7F;
        for (int i = 0; i < (objects.length); i++) {
            DimensionalMap.Entry<Object> nearest = tree.findNearest(locations[i]);
            Assert.assertSame(objects[i], nearest.value);
            Assert.assertEquals(0.0F, nearest.distance, delta);
        }
        DimensionalMap.Entry<Object> nearestOne = tree.findNearest(new float[]{ 0.0F, 0.0F, -1.0F });
        Assert.assertSame(objects[0], nearestOne.value);
        Assert.assertEquals(1.0F, nearestOne.distance, delta);
        DimensionalMap.Entry<Object> nearestRoot = tree.findNearest(new float[]{ 0.0F, 1.0F, 6.0F });
        Assert.assertSame(objects[5], nearestRoot.value);
        Assert.assertEquals(((float) (Math.sqrt(2))), nearestRoot.distance, delta);
        Assert.assertNull(tree.findNearest(new float[]{ 0.0F, 0.0F, -1.0F }, 0.5F));
        Collection<DimensionalMap.Entry<Object>> nearestTwo = tree.findNearest(new float[]{ 0.0F, 0.0F, 0.0F }, 2);
        Assert.assertEquals(2, nearestTwo.size());
        Iterator<DimensionalMap.Entry<Object>> nearestTwoIterator = nearestTwo.iterator();
        DimensionalMap.Entry<Object> firstNearest = nearestTwoIterator.next();
        DimensionalMap.Entry<Object> secondNearest = nearestTwoIterator.next();
        Assert.assertSame(objects[0], firstNearest.value);
        Assert.assertSame(objects[1], secondNearest.value);
        Assert.assertEquals(0, firstNearest.distance, delta);
        Assert.assertEquals(1, secondNearest.distance, delta);
        Collection<DimensionalMap.Entry<Object>> nearestThree = tree.findNearest(new float[]{ 0.0F, 0.0F, 5.0F }, 3, 1.0F);
        Assert.assertEquals(2, nearestThree.size());
        Iterator<DimensionalMap.Entry<Object>> nearestThreeIterator = nearestThree.iterator();
        firstNearest = nearestThreeIterator.next();
        secondNearest = nearestThreeIterator.next();
        Assert.assertSame(objects[5], firstNearest.value);
        Assert.assertSame(objects[4], secondNearest.value);
        Assert.assertEquals(0, firstNearest.distance, delta);
        Assert.assertEquals(1, secondNearest.distance, delta);
    }

    @Test
    public void testSearchDuplicationByDistance() {
        SpaceTree<Object> tree = new SpaceTree(3);
        Object[] objects = new Object[]{ new Object(), new Object(), new Object(), new Object(), new Object(), new Object() };
        float[][] locations = new float[][]{ new float[]{ 0.0F, 0.0F, 0.0F }, new float[]{ 0.0F, 0.0F, 1.0F }, new float[]{ 0.0F, 0.0F, 2.0F }, new float[]{ 0.0F, 0.0F, 3.0F }, new float[]{ 0.0F, 0.0F, 4.0F }, new float[]{ 0.0F, 0.0F, 5.0F } };
        for (int i = 0; i < (objects.length); i++) {
            tree.add(locations[i], objects[i]);
        }
        float delta = 1.0E-7F;
        Collection<DimensionalMap.Entry<Object>> nearest = tree.findNearest(locations[2], 3);
        Assert.assertEquals(3, nearest.size());
        Iterator<DimensionalMap.Entry<Object>> nearestIterator = nearest.iterator();
        DimensionalMap.Entry<Object> firstNearest = nearestIterator.next();
        DimensionalMap.Entry<Object> secondNearest = nearestIterator.next();
        DimensionalMap.Entry<Object> thirdNearest = nearestIterator.next();
        Assert.assertSame(objects[2], firstNearest.value);
        Assert.assertTrue((((objects[1]) == (secondNearest.value)) || ((objects[3]) == (secondNearest.value))));
        Assert.assertTrue((((objects[1]) == (thirdNearest.value)) || ((objects[3]) == (thirdNearest.value))));
        Assert.assertNotSame(secondNearest.value, thirdNearest.value);
        Assert.assertEquals(0, firstNearest.distance, delta);
        Assert.assertEquals(1, secondNearest.distance, delta);
        Assert.assertEquals(1, thirdNearest.distance, delta);
    }
}

