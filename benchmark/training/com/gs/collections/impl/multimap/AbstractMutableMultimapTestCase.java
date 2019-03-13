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
package com.gs.collections.impl.multimap;


import Bags.mutable;
import Lists.fixedSize;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.ImmutableMultimap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;


/**
 * Helper class for testing {@link Multimap}s.
 */
public abstract class AbstractMutableMultimapTestCase extends AbstractMultimapTestCase {
    @Test
    public void testPutAndGrowMultimap() {
        MutableMultimap<Integer, Integer> multimap = this.newMultimap();
        multimap.put(1, 1);
        multimap.put(2, 2);
        Verify.assertContainsEntry(1, 1, multimap);
        Verify.assertContainsEntry(2, 2, multimap);
    }

    @Test
    public void testAddAndGrowMultimap() {
        MutableMultimap<Integer, String> multimap = this.newMultimap();
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");
        Assert.assertTrue(multimap.add(pair1));
        Verify.assertContainsEntry(1, "One", multimap);
        Assert.assertTrue(multimap.add(pair2));
        Verify.assertContainsEntry(2, "Two", multimap);
        Assert.assertTrue(multimap.add(pair3));
        Verify.assertContainsEntry(3, "Three", multimap);
        Assert.assertTrue(multimap.add(pair4));
        Verify.assertContainsEntry(4, "Four", multimap);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(pair1, pair2, pair3, pair4), multimap.keyValuePairsView().toSet());
    }

    @Test
    public void testClear() {
        MutableMultimap<Integer, Object> multimap = this.<Integer, Object>newMultimapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        Verify.assertNotEmpty(multimap);
        multimap.clear();
        Verify.assertEmpty(multimap);
    }

    @Test
    public void testRemoveObject() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        multimap.removeAll("Two");
        Verify.assertContainsAllEntries(multimap, "One", 1, "Three", 3);
    }

    @Override
    @Test
    public void forEachKey() {
        MutableBag<Integer> collection = mutable.of();
        Multimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "1", 2, "2", 3, "3");
        multimap.forEachKey(CollectionAddProcedure.on(collection));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3), collection);
    }

    @Test
    public void testPutAll() {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "2");
        Multimap<Integer, String> toAdd = this.newMultimapWithKeysValues(2, "Two", 3, "Three");
        Multimap<Integer, String> toAddImmutable = this.newMultimapWithKeysValues(4, "Four", 5, "Five");
        Assert.assertTrue(multimap.putAll(toAdd));
        Assert.assertTrue(multimap.putAll(toAddImmutable));
        MutableMultimap<Integer, String> expected = this.newMultimapWithKeysValues(1, "One", 2, "2", 2, "Two", 3, "Three");
        expected.put(4, "Four");
        expected.put(5, "Five");
        Assert.assertEquals(expected, multimap);
    }

    @Test
    public void testPutAllFromCollection() {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two");
        Assert.assertTrue(multimap.putAll(1, fixedSize.of("Three", "Four")));
        Assert.assertEquals(this.newMultimapWithKeysValues(1, "One", 2, "Two", 1, "Three", 1, "Four"), multimap);
        Assert.assertFalse(multimap.putAll(1, UnifiedSet.<String>newSet()));
        Assert.assertEquals(this.newMultimapWithKeysValues(1, "One", 2, "Two", 1, "Three", 1, "Four"), multimap);
    }

    @Test
    public void testPutAllFromIterable() {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two");
        Assert.assertTrue(multimap.putAll(1, fixedSize.of("Three", "Four").asLazy()));
        Assert.assertEquals(this.newMultimapWithKeysValues(1, "One", 2, "Two", 1, "Three", 1, "Four"), multimap);
    }

    @Test
    public void testRemoveKey() {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "1", 2, "Two");
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1"), UnifiedSet.newSet(multimap.removeAll(1)));
        Verify.assertSize(1, multimap);
        Assert.assertFalse(multimap.containsKey(1));
        Verify.assertIterableEmpty(multimap.removeAll(42));
        Verify.assertSize(1, multimap);
        Verify.assertSetsEqual(UnifiedSet.newSetWith("Two"), UnifiedSet.newSet(multimap.removeAll(2)));
        Verify.assertEmpty(multimap);
    }

    @Test
    public void testContainsValue() {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two");
        Assert.assertTrue(multimap.containsValue("Two"));
        Assert.assertFalse(multimap.containsValue("Three"));
    }

    @Test
    public void testGetIfAbsentPut() {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertIterableEmpty(multimap.get(4));
        Assert.assertTrue(multimap.put(4, "4"));
        Verify.assertContainsEntry(4, "4", multimap);
    }

    @Test
    public void testRemove() {
        MutableMultimap<Integer, Integer> map = this.newMultimapWithKeysValues(1, 1, 1, 2, 3, 3, 4, 5);
        Assert.assertFalse(map.remove(4, 4));
        Assert.assertEquals(this.newMultimapWithKeysValues(1, 1, 1, 2, 3, 3, 4, 5), map);
        Assert.assertTrue(map.remove(4, 5));
        Assert.assertEquals(this.newMultimapWithKeysValues(1, 1, 1, 2, 3, 3), map);
        Assert.assertTrue(map.remove(1, 2));
        Assert.assertEquals(this.newMultimapWithKeysValues(1, 1, 3, 3), map);
    }

    @Test
    public void testReplaceValues() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        RichIterable<Integer> oldValues2 = multimap.replaceValues("Two", UnifiedSet.newSetWith(4));
        Assert.assertEquals(mutable.of(2), oldValues2.toBag());
        Verify.assertEqualsAndHashCode(this.newMultimapWithKeysValues("One", 1, "Two", 4, "Three", 3), multimap);
        RichIterable<Integer> oldValues3 = multimap.replaceValues("Three", UnifiedSet.<Integer>newSet());
        Assert.assertEquals(mutable.of(3), oldValues3.toBag());
        Verify.assertEqualsAndHashCode(this.newMultimapWithKeysValues("One", 1, "Two", 4), multimap);
    }

    @Test
    public void testReplaceValues_absent_key() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        RichIterable<Integer> oldValues = multimap.replaceValues("Four", UnifiedSet.newSetWith(4));
        Assert.assertEquals(HashBag.<Integer>newBag(), oldValues.toBag());
        Verify.assertEqualsAndHashCode(this.newMultimapWithKeysValues("One", 1, "Two", 2, "Three", 3, "Four", 4), multimap);
    }

    @Test
    public void toMap() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        UnifiedMap<String, RichIterable<Integer>> expected = UnifiedMap.newMap();
        expected.put("One", this.createCollection(1));
        expected.put("Two", this.createCollection(2, 2));
        MutableMap<String, RichIterable<Integer>> toMap = multimap.toMap();
        Assert.assertEquals(expected, toMap);
        MutableMap<String, RichIterable<Integer>> newToMap = multimap.toMap();
        Assert.assertEquals(toMap.get("One"), newToMap.get("One"));
        Assert.assertNotSame(toMap.get("One"), newToMap.get("One"));
    }

    @Test
    public void toImmutable() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        ImmutableMultimap<String, Integer> actual = multimap.toImmutable();
        Assert.assertNotNull(actual);
        Assert.assertEquals(multimap, actual);
    }

    @Test
    public void toMapWithTarget() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        MutableMap<String, RichIterable<Integer>> expected = UnifiedMap.newMap();
        expected.put("One", UnifiedSet.newSetWith(1));
        expected.put("Two", UnifiedSet.newSetWith(2, 2));
        MutableMap<String, MutableSet<Integer>> map = multimap.toMap(UnifiedSet::new);
        Assert.assertEquals(expected, map);
    }

    @Test
    public void toMutable() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2, "Two", 2);
        MutableMultimap<String, Integer> mutableCopy = multimap.toMutable();
        Assert.assertNotSame(multimap, mutableCopy);
        Assert.assertEquals(multimap, mutableCopy);
    }

    @Test
    public void testToString() {
        MutableMultimap<String, Integer> multimap = this.newMultimapWithKeysValues("One", 1, "Two", 2);
        Assert.assertTrue((("{One=[1], Two=[2]}".equals(multimap.toString())) || ("{Two=[2], One=[1]}".equals(multimap.toString()))));
    }
}

