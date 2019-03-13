/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.junit.Assert;
import org.junit.Test;


public class VolatileSizeArrayListTest {
    @Test
    public void normal() {
        List<Integer> list = new VolatileSizeArrayList<Integer>();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(0, list.size());
        Assert.assertFalse(list.contains(1));
        Assert.assertFalse(list.remove(((Integer) (1))));
        list = new VolatileSizeArrayList<Integer>(16);
        Assert.assertTrue(list.add(1));
        Assert.assertTrue(list.addAll(Arrays.asList(3, 4, 7)));
        list.add(1, 2);
        Assert.assertTrue(list.addAll(4, Arrays.asList(5, 6)));
        Assert.assertTrue(list.contains(2));
        Assert.assertFalse(list.remove(((Integer) (10))));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(7, list.size());
        Iterator<Integer> it = list.iterator();
        for (int i = 1; i < 8; i++) {
            Assert.assertEquals(i, it.next().intValue());
        }
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3, 4, 5, 6, 7 }, list.toArray());
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3, 4, 5, 6, 7 }, list.toArray(new Integer[7]));
        Assert.assertTrue(list.containsAll(Arrays.asList(2, 4, 6)));
        Assert.assertFalse(list.containsAll(Arrays.asList(2, 4, 6, 10)));
        Assert.assertFalse(list.removeAll(Arrays.asList(10, 11, 12)));
        Assert.assertFalse(list.retainAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
        Assert.assertEquals(7, list.size());
        for (int i = 1; i < 8; i++) {
            Assert.assertEquals(i, list.get((i - 1)).intValue());
        }
        for (int i = 1; i < 8; i++) {
            Assert.assertEquals(i, list.set((i - 1), i).intValue());
        }
        Assert.assertEquals(2, list.indexOf(3));
        Assert.assertEquals(5, list.lastIndexOf(6));
        ListIterator<Integer> lit = list.listIterator(7);
        for (int i = 7; i > 0; i--) {
            Assert.assertEquals(i, lit.previous().intValue());
        }
        Assert.assertEquals(Arrays.asList(3, 4, 5), list.subList(2, 5));
        VolatileSizeArrayList<Integer> list2 = new VolatileSizeArrayList<Integer>();
        list2.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        Assert.assertFalse(list2.equals(list));
        Assert.assertFalse(list.equals(list2));
        list2.add(7);
        Assert.assertTrue(list2.equals(list));
        Assert.assertTrue(list.equals(list2));
        List<Integer> list3 = new ArrayList<Integer>();
        list3.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        Assert.assertFalse(list3.equals(list));
        Assert.assertFalse(list.equals(list3));
        list3.add(7);
        Assert.assertTrue(list3.equals(list));
        Assert.assertTrue(list.equals(list3));
        Assert.assertEquals(list.hashCode(), list3.hashCode());
        Assert.assertEquals(list.toString(), list3.toString());
        list.remove(0);
        Assert.assertEquals(6, list.size());
        list.clear();
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(list.isEmpty());
    }
}

