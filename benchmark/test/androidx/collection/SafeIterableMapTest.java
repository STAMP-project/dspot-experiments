/**
 * Copyright 2018 The Android Open Source Project
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
package androidx.collection;


import androidx.arch.core.internal.SafeIterableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SafeIterableMapTest {
    @Test
    public void testToString() {
        SafeIterableMap<Integer, String> map = SafeIterableMapTest.from(1, 2, 3, 4).to("a", "b", "c", "d");
        MatcherAssert.assertThat(map.toString(), CoreMatchers.is("[1=a, 2=b, 3=c, 4=d]"));
    }

    @Test
    public void testEmptyToString() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        MatcherAssert.assertThat(map.toString(), CoreMatchers.is("[]"));
    }

    @Test
    public void testOneElementToString() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1);
        MatcherAssert.assertThat(map.toString(), CoreMatchers.is("[1=true]"));
    }

    @Test
    public void testEquality1() {
        SafeIterableMap<Integer, Integer> map1 = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        SafeIterableMap<Integer, Integer> map2 = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        MatcherAssert.assertThat(map1.equals(map2), CoreMatchers.is(true));
    }

    @Test
    public void testEquality2() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        // noinspection ObjectEqualsNull
        MatcherAssert.assertThat(map.equals(null), CoreMatchers.is(false));
    }

    @Test
    public void testEquality3() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        // noinspection EqualsBetweenInconvertibleTypes
        MatcherAssert.assertThat(map.equals(new ArrayList()), CoreMatchers.is(false));
    }

    @Test
    public void testEquality4() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        MatcherAssert.assertThat(map.equals(new SafeIterableMap<Integer, Boolean>()), CoreMatchers.is(false));
    }

    @Test
    public void testEquality5() {
        SafeIterableMap<Integer, Boolean> map1 = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        SafeIterableMap<Integer, Boolean> map2 = SafeIterableMapTest.mapOf(1);
        MatcherAssert.assertThat(map1.equals(map2), CoreMatchers.is(false));
    }

    @Test
    public void testEquality6() {
        SafeIterableMap<Integer, Boolean> map1 = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        SafeIterableMap<Integer, Boolean> map2 = SafeIterableMapTest.mapOf(1, 2, 3, 5);
        MatcherAssert.assertThat(map1.equals(map2), CoreMatchers.is(false));
    }

    @Test
    public void testEquality7() {
        SafeIterableMap<Integer, Integer> map1 = SafeIterableMapTest.from(1, 2, 3, 4).to(1, 2, 3, 4);
        SafeIterableMap<Integer, Integer> map2 = SafeIterableMapTest.from(1, 2, 3, 4).to(1, 2, 3, 5);
        MatcherAssert.assertThat(map1.equals(map2), CoreMatchers.is(false));
    }

    @Test
    public void testEquality8() {
        SafeIterableMap<Integer, Boolean> map1 = SafeIterableMapTest.mapOf();
        SafeIterableMap<Integer, Boolean> map2 = SafeIterableMapTest.mapOf();
        MatcherAssert.assertThat(map1.equals(map2), CoreMatchers.is(true));
    }

    @Test
    public void testEqualityRespectsOrder() {
        SafeIterableMap<Integer, Boolean> map1 = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        SafeIterableMap<Integer, Boolean> map2 = SafeIterableMapTest.mapOf(1, 3, 2, 4);
        MatcherAssert.assertThat(map1.equals(map2), CoreMatchers.is(false));
    }

    @Test
    public void testPut() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        MatcherAssert.assertThat(map.putIfAbsent(5, 10), CoreMatchers.is(((Integer) (null))));
        MatcherAssert.assertThat(map, CoreMatchers.is(SafeIterableMapTest.from(1, 2, 3, 4, 5).to(10, 20, 30, 40, 10)));
    }

    @Test
    public void testAddExisted() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 261, 40);
        MatcherAssert.assertThat(map.putIfAbsent(3, 239), CoreMatchers.is(261));
        MatcherAssert.assertThat(map, CoreMatchers.is(SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 261, 40)));
    }

    @Test
    public void testRemoveLast() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        MatcherAssert.assertThat(map.remove(4), CoreMatchers.is(40));
        MatcherAssert.assertThat(map, CoreMatchers.is(SafeIterableMapTest.from(1, 2, 3).to(10, 20, 30)));
    }

    @Test
    public void testRemoveFirst() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        MatcherAssert.assertThat(map.remove(1), CoreMatchers.is(true));
        MatcherAssert.assertThat(map, CoreMatchers.is(SafeIterableMapTest.mapOf(2, 3, 4)));
    }

    @Test
    public void testRemoveMiddle() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        MatcherAssert.assertThat(map.remove(2), CoreMatchers.is(20));
        MatcherAssert.assertThat(map.remove(3), CoreMatchers.is(30));
        MatcherAssert.assertThat(map, CoreMatchers.is(SafeIterableMapTest.from(1, 4).to(10, 40)));
    }

    @Test
    public void testRemoveNotExisted() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        MatcherAssert.assertThat(map.remove(5), CoreMatchers.is(((Boolean) (null))));
        MatcherAssert.assertThat(map, CoreMatchers.is(SafeIterableMapTest.mapOf(1, 2, 3, 4)));
    }

    @Test
    public void testRemoveSole() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1).to(261);
        MatcherAssert.assertThat(map.remove(1), CoreMatchers.is(261));
        MatcherAssert.assertThat(map, CoreMatchers.is(new SafeIterableMap<Integer, Integer>()));
    }

    @Test
    public void testRemoveDuringIteration1() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        int index = 0;
        int[] expected = new int[]{ 1, 4 };
        for (Map.Entry<Integer, Integer> i : map) {
            MatcherAssert.assertThat(i.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                MatcherAssert.assertThat(map.remove(2), CoreMatchers.is(20));
                MatcherAssert.assertThat(map.remove(3), CoreMatchers.is(30));
            }
        }
    }

    @Test
    public void testRemoveDuringIteration2() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2).to(10, 20);
        Iterator<Map.Entry<Integer, Integer>> iter = map.iterator();
        MatcherAssert.assertThat(map.remove(2), CoreMatchers.is(20));
        MatcherAssert.assertThat(map.remove(1), CoreMatchers.is(10));
        MatcherAssert.assertThat(iter.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testRemoveDuringIteration3() {
        SafeIterableMap<Integer, Integer> map = SafeIterableMapTest.from(1, 2, 3, 4).to(10, 20, 30, 40);
        int index = 0;
        Iterator<Map.Entry<Integer, Integer>> iter = map.iterator();
        MatcherAssert.assertThat(map.remove(1), CoreMatchers.is(10));
        MatcherAssert.assertThat(map.remove(2), CoreMatchers.is(20));
        int[] expected = new int[]{ 3, 4 };
        while (iter.hasNext()) {
            MatcherAssert.assertThat(iter.next().getKey(), CoreMatchers.is(expected[(index++)]));
        } 
    }

    @Test
    public void testRemoveDuringIteration4() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2);
        int[] expected = new int[]{ 1, 2 };
        int index = 0;
        for (Map.Entry<Integer, Boolean> entry : map) {
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                map.remove(1);
            }
        }
        MatcherAssert.assertThat(index, CoreMatchers.is(2));
    }

    @Test
    public void testAdditionDuringIteration() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 1, 2, 3, 4 };
        int index = 0;
        for (Map.Entry<Integer, Boolean> entry : map) {
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                map.putIfAbsent(5, true);
            }
        }
    }

    @Test
    public void testReAdditionDuringIteration() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 1, 2, 4 };
        int index = 0;
        for (Map.Entry<Integer, Boolean> entry : map) {
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                map.remove(3);
                map.putIfAbsent(3, true);
            }
        }
    }

    @Test
    public void testSize() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(4));
        map.putIfAbsent(5, true);
        map.putIfAbsent(6, true);
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(6));
        map.remove(5);
        map.remove(5);
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(5));
        map.remove(1);
        map.remove(2);
        map.remove(4);
        map.remove(3);
        map.remove(6);
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(0));
        map.putIfAbsent(4, true);
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(SafeIterableMapTest.mapOf().size(), CoreMatchers.is(0));
    }

    @Test
    public void testIteratorWithAdditions1() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 1, 2, 3, 5 };
        int index = 0;
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iteratorWithAdditions();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 3) {
                map.remove(4);
                map.putIfAbsent(5, true);
            }
        } 
    }

    @Test
    public void testIteratorWithAdditions2() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1);
        int[] expected = new int[]{ 1, 2, 3 };
        int index = 0;
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iteratorWithAdditions();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                map.putIfAbsent(2, true);
                map.putIfAbsent(3, true);
            }
        } 
        MatcherAssert.assertThat(index, CoreMatchers.is(3));
    }

    @Test
    public void testIteratorWithAdditions3() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3);
        int[] expected = new int[]{ 1 };
        int index = 0;
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iteratorWithAdditions();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            map.remove(2);
            map.remove(3);
        } 
        MatcherAssert.assertThat(index, CoreMatchers.is(1));
    }

    @Test
    public void testIteratorWithAdditions4() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        int[] expected = new int[]{ 1, 2, 3 };
        int index = 0;
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iteratorWithAdditions();
        map.putIfAbsent(1, true);
        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                map.putIfAbsent(2, false);
            }
            if (index == 2) {
                map.putIfAbsent(3, false);
            }
        } 
        MatcherAssert.assertThat(index, CoreMatchers.is(3));
    }

    @Test
    public void testIteratorWithAddition5() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2);
        int[] expected = new int[]{ 1, 2 };
        int index = 0;
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iteratorWithAdditions();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            MatcherAssert.assertThat(entry.getKey(), CoreMatchers.is(expected[(index++)]));
            if (index == 1) {
                map.remove(1);
            }
        } 
        MatcherAssert.assertThat(index, CoreMatchers.is(2));
    }

    @Test
    public void testDescendingIteration() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 4, 3, 2, 1 };
        int index = 0;
        for (Iterator<Map.Entry<Integer, Boolean>> iter = map.descendingIterator(); iter.hasNext();) {
            MatcherAssert.assertThat(iter.next().getKey(), CoreMatchers.is(expected[(index++)]));
        }
        MatcherAssert.assertThat(index, CoreMatchers.is(4));
    }

    @Test
    public void testDescendingIterationRemove1() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 4, 3, 2 };
        int index = 0;
        for (Iterator<Map.Entry<Integer, Boolean>> iter = map.descendingIterator(); iter.hasNext();) {
            if (index == 1) {
                map.remove(1);
            }
            MatcherAssert.assertThat(iter.next().getKey(), CoreMatchers.is(expected[(index++)]));
        }
        MatcherAssert.assertThat(index, CoreMatchers.is(3));
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(3));
    }

    @Test
    public void testDescendingIterationRemove2() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 3, 2, 1 };
        int index = 0;
        for (Iterator<Map.Entry<Integer, Boolean>> iter = map.descendingIterator(); iter.hasNext();) {
            if (index == 0) {
                map.remove(4);
            }
            MatcherAssert.assertThat(iter.next().getKey(), CoreMatchers.is(expected[(index++)]));
        }
        MatcherAssert.assertThat(index, CoreMatchers.is(3));
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(3));
    }

    @Test
    public void testDescendingIterationRemove3() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 4, 1 };
        int index = 0;
        for (Iterator<Map.Entry<Integer, Boolean>> iter = map.descendingIterator(); iter.hasNext();) {
            if (index == 1) {
                map.remove(3);
                map.remove(2);
            }
            MatcherAssert.assertThat(iter.next().getKey(), CoreMatchers.is(expected[(index++)]));
        }
        MatcherAssert.assertThat(index, CoreMatchers.is(2));
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(2));
    }

    @Test
    public void testDescendingIterationAddition() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf(1, 2, 3, 4);
        int[] expected = new int[]{ 4, 3, 2, 1 };
        int index = 0;
        for (Iterator<Map.Entry<Integer, Boolean>> iter = map.descendingIterator(); iter.hasNext();) {
            if (index == 0) {
                map.putIfAbsent(5, false);
            }
            MatcherAssert.assertThat(iter.next().getKey(), CoreMatchers.is(expected[(index++)]));
        }
        MatcherAssert.assertThat(index, CoreMatchers.is(4));
        MatcherAssert.assertThat(map.size(), CoreMatchers.is(5));
    }

    @Test
    public void testDescendingIteratorEmpty() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.descendingIterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testIteratorEmpty() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testIteratorWithAdditionEmpty() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        Iterator<Map.Entry<Integer, Boolean>> iterator = map.iteratorWithAdditions();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testEldest() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        MatcherAssert.assertThat(map.eldest(), CoreMatchers.nullValue());
        map.putIfAbsent(1, false);
        MatcherAssert.assertThat(map.eldest().getKey(), CoreMatchers.is(1));
        map.putIfAbsent(2, false);
        MatcherAssert.assertThat(map.eldest().getKey(), CoreMatchers.is(1));
        map.remove(1);
        MatcherAssert.assertThat(map.eldest().getKey(), CoreMatchers.is(2));
        map.remove(2);
        MatcherAssert.assertThat(map.eldest(), CoreMatchers.nullValue());
    }

    @Test
    public void testNewest() {
        SafeIterableMap<Integer, Boolean> map = SafeIterableMapTest.mapOf();
        MatcherAssert.assertThat(map.newest(), CoreMatchers.nullValue());
        map.putIfAbsent(1, false);
        MatcherAssert.assertThat(map.newest().getKey(), CoreMatchers.is(1));
        map.putIfAbsent(2, false);
        MatcherAssert.assertThat(map.newest().getKey(), CoreMatchers.is(2));
        map.remove(2);
        MatcherAssert.assertThat(map.eldest().getKey(), CoreMatchers.is(1));
        map.remove(1);
        MatcherAssert.assertThat(map.newest(), CoreMatchers.nullValue());
    }

    private static class MapBuilder<K> {
        final K[] mKeys;

        MapBuilder(K[] keys) {
            this.mKeys = keys;
        }

        @SafeVarargs
        public final <V> SafeIterableMap<K, V> to(V... values) {
            MatcherAssert.assertThat("Failed to build Map", mKeys.length, CoreMatchers.is(values.length));
            SafeIterableMap<K, V> map = new SafeIterableMap();
            for (int i = 0; i < (mKeys.length); i++) {
                map.putIfAbsent(mKeys[i], values[i]);
            }
            return map;
        }
    }
}

