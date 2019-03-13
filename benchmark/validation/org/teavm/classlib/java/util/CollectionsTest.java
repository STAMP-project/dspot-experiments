/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class CollectionsTest {
    @Test
    public void listSorted() {
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(2, 5, 7, 3, 5, 6));
        Collections.sort(list);
        Assert.assertEquals(Integer.valueOf(2), list.get(0));
        Assert.assertEquals(Integer.valueOf(3), list.get(1));
        Assert.assertEquals(Integer.valueOf(5), list.get(2));
        Assert.assertEquals(Integer.valueOf(5), list.get(3));
        Assert.assertEquals(Integer.valueOf(6), list.get(4));
        Assert.assertEquals(Integer.valueOf(7), list.get(5));
    }

    @Test
    public void binarySearchWorks() {
        List<Integer> list = new ArrayList<>(Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16));
        Assert.assertEquals(3, Collections.binarySearch(list, 8));
        Assert.assertEquals(7, Collections.binarySearch(list, 16));
        Assert.assertEquals(0, Collections.binarySearch(list, 2));
        Assert.assertEquals((-1), Collections.binarySearch(list, 1));
        Assert.assertEquals((-2), Collections.binarySearch(list, 3));
        Assert.assertEquals((-3), Collections.binarySearch(list, 5));
        Assert.assertEquals((-8), Collections.binarySearch(list, 15));
        Assert.assertEquals((-9), Collections.binarySearch(list, 17));
    }

    @Test
    public void findsMinimum() {
        List<Integer> list = Arrays.asList(6, 5, 7, 3, 5, 6);
        Assert.assertEquals(((Integer) (3)), Collections.min(list));
    }

    @Test
    public void findsMaximum() {
        List<Integer> list = Arrays.asList(6, 5, 7, 3, 5, 6);
        Assert.assertEquals(((Integer) (7)), Collections.max(list));
    }

    @Test
    public void fills() {
        List<Integer> list = new ArrayList<>(Arrays.asList(6, 5, 7, 3, 5, 6));
        Collections.fill(list, 9);
        Assert.assertEquals(6, list.size());
        Assert.assertEquals(((Integer) (9)), list.get(0));
        Assert.assertEquals(((Integer) (9)), list.get(5));
        Assert.assertEquals(((Integer) (9)), list.get(2));
    }

    @Test
    public void copies() {
        List<Integer> list = new ArrayList<>(Arrays.asList(6, 5, 7, 3, 5, 6));
        List<Integer> dest = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        Collections.copy(dest, list);
        Assert.assertEquals(7, dest.size());
        Assert.assertEquals(((Integer) (6)), dest.get(0));
        Assert.assertEquals(((Integer) (5)), dest.get(1));
        Assert.assertEquals(((Integer) (5)), dest.get(4));
        Assert.assertEquals(((Integer) (6)), dest.get(5));
        Assert.assertEquals(((Integer) (7)), dest.get(6));
    }

    @Test
    public void rotates() {
        List<Integer> list = new ArrayList<>(Arrays.asList(2, 5, 7, 3, 5, 6));
        Collections.rotate(list, 2);
        Assert.assertArrayEquals(new Integer[]{ 5, 6, 2, 5, 7, 3 }, list.toArray(new Integer[0]));
    }

    @Test
    public void replaces() {
        List<Integer> list = new ArrayList<>(Arrays.asList(2, 5, 7, 3, 5, 6));
        Assert.assertTrue(Collections.replaceAll(list, 5, 9));
        Assert.assertArrayEquals(new Integer[]{ 2, 9, 7, 3, 9, 6 }, list.toArray(new Integer[0]));
    }

    @Test
    public void findIndex() {
        List<Integer> list = new ArrayList<>(Arrays.asList(2, 5, 6, 3, 5, 6));
        Assert.assertEquals(1, Collections.indexOfSubList(list, Arrays.asList(5, 6)));
        Assert.assertEquals((-1), Collections.indexOfSubList(list, Arrays.asList(5, 1)));
        Assert.assertEquals(0, Collections.indexOfSubList(list, list));
    }

    @Test
    public void findsLastIndex() {
        List<Integer> list = new ArrayList<>(Arrays.asList(2, 5, 6, 3, 5, 6));
        Assert.assertEquals(4, Collections.lastIndexOfSubList(list, Arrays.asList(5, 6)));
        Assert.assertEquals((-1), Collections.lastIndexOfSubList(list, Arrays.asList(5, 1)));
        Assert.assertEquals(0, Collections.lastIndexOfSubList(list, list));
    }

    @Test
    public void shuffleWorksOnArrayAsList() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Collections.shuffle(list);
        for (int i = 1; i <= 4; ++i) {
            Assert.assertTrue(("List expected to contain " + i), list.contains(i));
        }
    }
}

