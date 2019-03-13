/**
 * Copyright 2014 Goldman Sachs.
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
 */
package com.gs.collections.impl.bimap.mutable;


import com.gs.collections.api.bimap.MutableBiMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractMutableBiMapValuesTestCase {
    @Test(expected = UnsupportedOperationException.class)
    public void add() {
        this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3).values().add(4);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAll() {
        this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3).values().addAll(FastList.newListWith(4));
    }

    @Test
    public void clear() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3);
        map.values().clear();
        Verify.assertIterableEmpty(map);
        Verify.assertIterableEmpty(map.inverse());
        Verify.assertEmpty(map.values());
    }

    @Test
    public void contains() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, null);
        Collection<Integer> values = map.values();
        Assert.assertTrue(values.contains(1));
        Assert.assertTrue(values.contains(2));
        Assert.assertTrue(values.contains(null));
        Assert.assertFalse(values.contains(4));
        values.remove(null);
        Assert.assertFalse(values.contains(null));
        map.remove(1.0F);
        Assert.assertFalse(values.contains(1));
    }

    @Test
    public void containsAll() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, null);
        Collection<Integer> values = map.values();
        Assert.assertTrue(values.containsAll(FastList.newListWith(1, 2)));
        Assert.assertTrue(values.containsAll(FastList.newListWith(1, 2, null)));
        Assert.assertTrue(values.containsAll(FastList.newListWith(null, null)));
        Assert.assertFalse(values.containsAll(FastList.newListWith(1, 4)));
        Assert.assertFalse(values.containsAll(FastList.newListWith(5, 4)));
        values.remove(null);
        Assert.assertFalse(values.containsAll(FastList.newListWith(1, 2, null)));
        Assert.assertTrue(values.containsAll(FastList.newListWith(1, 2)));
        map.remove(1.0F);
        Assert.assertFalse(values.containsAll(FastList.newListWith(1, 2)));
        Assert.assertTrue(values.containsAll(FastList.newListWith(2)));
    }

    @Test
    public void isEmpty() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, null, 2.0F, 2, 3.0F, 3);
        Collection<Integer> values = map.values();
        Assert.assertFalse(values.isEmpty());
        HashBiMap<Float, Integer> map1 = HashBiMap.newMap();
        Collection<Integer> values1 = map1.values();
        Assert.assertTrue(values1.isEmpty());
        map1.put(1.0F, 1);
        Assert.assertFalse(values1.isEmpty());
    }

    @Test
    public void size() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3, 4.0F, null);
        Collection<Integer> values = map.values();
        Verify.assertSize(4, values);
        map.remove(1.0F);
        Verify.assertSize(3, values);
        map.put(5.0F, 5);
        Verify.assertSize(4, values);
        HashBiMap<Float, Integer> map1 = HashBiMap.newMap();
        Collection<Integer> keySet1 = map1.values();
        Verify.assertSize(0, keySet1);
        map1.put(1.0F, null);
        Verify.assertSize(1, keySet1);
    }

    @Test
    public void iterator() {
        MutableSet<String> expected = UnifiedSet.newSetWith("zero", "thirtyOne", null);
        MutableSet<String> actual = UnifiedSet.newSet();
        Iterator<String> iterator = HashBiMap.newWithKeysValues(0.0F, "zero", 31.0F, "thirtyOne", 32.0F, null).iterator();
        Assert.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assert.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(expected, actual);
        Verify.assertThrows(NoSuchElementException.class, ((Runnable) (iterator::next)));
        MutableBiMap<Float, String> map1 = this.newMapWithKeysValues(0.0F, "zero", 1.0F, null);
        Iterator<String> iterator1 = map1.iterator();
        Verify.assertThrows(IllegalStateException.class, iterator1::remove);
        iterator1.next();
        iterator1.remove();
        Assert.assertTrue(map1.toString(), ((HashBiMap.newWithKeysValues(0.0F, "zero").equals(map1)) || (HashBiMap.newWithKeysValues(1.0F, null).equals(map1))));
        Assert.assertTrue(map1.toString(), ((HashBiMap.newWithKeysValues(0.0F, "zero").inverse().equals(map1.inverse())) || (HashBiMap.newWithKeysValues(1.0F, null).inverse().equals(map1.inverse()))));
        iterator1.next();
        iterator1.remove();
        Assert.assertEquals(HashBiMap.newMap(), map1);
        Verify.assertThrows(IllegalStateException.class, iterator1::remove);
        MutableBiMap<Float, String> map2 = this.newMapWithKeysValues(0.0F, null, 9.0F, "nine");
        Iterator<String> iterator2 = map2.iterator();
        Verify.assertThrows(IllegalStateException.class, iterator2::remove);
        iterator2.next();
        iterator2.remove();
        Assert.assertTrue(map2.toString(), ((HashBiMap.newWithKeysValues(0.0F, null).equals(map2)) || (HashBiMap.newWithKeysValues(9.0F, "nine").equals(map2))));
        Assert.assertTrue(map2.toString(), ((HashBiMap.newWithKeysValues(0.0F, null).inverse().equals(map2.inverse())) || (HashBiMap.newWithKeysValues(9.0F, "nine").inverse().equals(map2.inverse()))));
        iterator2.next();
        iterator2.remove();
        Assert.assertEquals(HashBiMap.newMap(), map2);
        MutableBiMap<Float, String> map3 = this.newMapWithKeysValues(8.0F, "eight", 9.0F, null);
        Iterator<String> iterator3 = map3.iterator();
        Verify.assertThrows(IllegalStateException.class, iterator3::remove);
        iterator3.next();
        iterator3.remove();
        Assert.assertTrue(map3.toString(), ((HashBiMap.newWithKeysValues(8.0F, "eight").equals(map3)) || (HashBiMap.newWithKeysValues(9.0F, null).equals(map3))));
        iterator3.next();
        iterator3.remove();
        Assert.assertEquals(HashBiMap.newMap(), map3);
    }

    @Test
    public void values() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3);
        Verify.assertContainsAll(map.values(), 1, 2, 3);
    }

    @Test
    public void removeFromValues() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3);
        Assert.assertFalse(map.values().remove(4));
        Assert.assertTrue(map.values().remove(2));
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 3.0F, 3), map);
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 3.0F, 3).inverse(), map.inverse());
    }

    @Test
    public void removeNullFromValues() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3);
        Assert.assertFalse(map.values().remove(null));
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3), map);
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3).inverse(), map.inverse());
        map.put(4.0F, null);
        Assert.assertTrue(map.values().remove(null));
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3), map);
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3).inverse(), map.inverse());
    }

    @Test
    public void removeAllFromValues() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3);
        Assert.assertFalse(map.values().removeAll(FastList.newListWith(4)));
        Assert.assertTrue(map.values().removeAll(FastList.newListWith(2, 4)));
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 3.0F, 3), map);
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 3.0F, 3).inverse(), map.inverse());
    }

    @Test
    public void retainAllFromValues() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, 3);
        Assert.assertFalse(map.values().retainAll(FastList.newListWith(1, 2, 3, 4)));
        Assert.assertTrue(map.values().retainAll(FastList.newListWith(1, 3)));
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 3.0F, 3), map);
        Assert.assertEquals(HashBiMap.newWithKeysValues(1.0F, 1, 3.0F, 3).inverse(), map.inverse());
    }

    @Test
    public void valuesToArray() {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0F, 1, 2.0F, 2, 3.0F, null);
        HashBag<Integer> expected = HashBag.newBagWith(1, 2, null);
        Collection<Integer> values = map.values();
        Assert.assertEquals(expected, HashBag.newBagWith(values.toArray()));
        Assert.assertEquals(expected, HashBag.newBagWith(values.toArray(new Integer[values.size()])));
        Assert.assertEquals(expected, HashBag.newBagWith(values.toArray(new Integer[0])));
        expected.add(null);
        Assert.assertEquals(expected, HashBag.newBagWith(values.toArray(new Integer[(values.size()) + 1])));
    }
}

