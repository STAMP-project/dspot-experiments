/**
 * Original work Copyright 2015 Real Logic Ltd.
 * Modified work Copyright (c) 2015-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.util.collection;


import com.google.common.collect.Lists;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LongHashSetTest {
    @Rule
    public final ExpectedException rule = ExpectedException.none();

    private final LongHashSet set = new LongHashSet(1000, (-1));

    @Test
    public void initiallyContainsNoElements() throws Exception {
        for (int i = 0; i < 10000; i++) {
            Assert.assertFalse(set.contains(i));
        }
    }

    @Test
    public void initiallyContainsNoBoxedElements() {
        for (int i = 0; i < 10000; i++) {
            Assert.assertFalse(set.contains(Long.valueOf(i)));
        }
    }

    @Test
    public void containsAddedBoxedElement() {
        Assert.assertTrue(set.add(1));
        Assert.assertTrue(set.contains(1));
    }

    @Test
    public void addingAnElementTwiceDoesNothing() {
        Assert.assertTrue(set.add(1));
        Assert.assertFalse(set.add(1));
    }

    @Test
    public void containsAddedBoxedElements() {
        Assert.assertTrue(set.add(1));
        Assert.assertTrue(set.add(Long.valueOf(2)));
        Assert.assertTrue(set.contains(Long.valueOf(1)));
        Assert.assertTrue(set.contains(2));
    }

    @Test
    public void removingAnElementFromAnEmptyListDoesNothing() {
        Assert.assertFalse(set.remove(0));
    }

    @Test
    public void removingAPresentElementRemovesIt() {
        final Set<Long> jdkSet = new HashSet<Long>();
        final Random rnd = new Random();
        for (int i = 0; i < 1000; i++) {
            final long value = rnd.nextInt();
            set.add(value);
            jdkSet.add(value);
        }
        Assert.assertEquals(jdkSet, set);
        for (Iterator<Long> iter = jdkSet.iterator(); iter.hasNext();) {
            final long value = iter.next();
            Assert.assertTrue(("Set suddenly doesn't contain " + value), set.contains(value));
            Assert.assertTrue(("Didn't remove " + value), set.remove(value));
            iter.remove();
        }
    }

    @Test
    public void sizeIsInitiallyZero() {
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void sizeIncrementsWithNumberOfAddedElements() {
        set.add(1);
        set.add(2);
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void sizeContainsNumberOfNewElements() {
        set.add(1);
        set.add(1);
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void iteratorsListElements() {
        set.add(1);
        set.add(2);
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTime() {
        iteratorsListElements();
        assertIteratorHasElements();
    }

    @Test
    public void clearRemovesAllElementsOfTheSet() {
        set.add(1);
        set.add(2);
        set.clear();
        Assert.assertEquals(0, set.size());
        Assert.assertFalse(set.contains(1));
        Assert.assertFalse(set.contains(2));
    }

    @Test
    public void differenceReturnsNullIfBothSetsEqual() {
        set.add(1);
        set.add(2);
        final LongHashSet other = new LongHashSet(100, (-1));
        other.add(1);
        other.add(2);
        Assert.assertNull(set.difference(other));
    }

    @Test
    public void differenceReturnsSetDifference() {
        set.add(1);
        set.add(2);
        final LongHashSet other = new LongHashSet(100, (-1));
        other.add(1);
        final LongHashSet diff = set.difference(other);
        Assert.assertEquals(1, diff.size());
        Assert.assertTrue(diff.contains(2));
    }

    @Test
    public void copiesOtherLongHashSet() {
        set.add(1);
        set.add(2);
        final LongHashSet other = new LongHashSet(1000, (-1));
        other.copy(set);
        Assert.assertThat(other, Matchers.contains(2L, 1L));
    }

    @Test
    public void twoEmptySetsAreEqual() {
        final LongHashSet other = new LongHashSet(100, (-1));
        Assert.assertEquals(set, other);
    }

    @Test
    public void equalityRequiresTheSameMissingValue() {
        final LongHashSet other = new LongHashSet(100, 1);
        Assert.assertNotEquals(set, other);
    }

    @Test
    public void setsWithTheSameValuesAreEqual() {
        final LongHashSet other = new LongHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(1);
        other.add(1001);
        Assert.assertEquals(set, other);
    }

    @Test
    public void setsWithTheDifferentSizesAreNotEqual() {
        final LongHashSet other = new LongHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(1001);
        Assert.assertNotEquals(set, other);
    }

    @Test
    public void setsWithTheDifferentValuesAreNotEqual() {
        final LongHashSet other = new LongHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(2);
        other.add(1001);
        Assert.assertNotEquals(set, other);
    }

    @Test
    public void twoEmptySetsHaveTheSameHashcode() {
        final LongHashSet other = new LongHashSet(100, (-1));
        Assert.assertEquals(set.hashCode(), other.hashCode());
    }

    @Test
    public void setsWithTheSameValuesHaveTheSameHashcode() {
        final LongHashSet other = new LongHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(1);
        other.add(1001);
        Assert.assertEquals(set.hashCode(), other.hashCode());
    }

    @Test
    public void worksCorrectlyWhenFull() {
        final LongHashSet set = new LongHashSet(2, 0);
        set.add(1);
        set.add(2);
        Assert.assertTrue(set.contains(2));
        Assert.assertFalse(set.contains(3));
    }

    @Test
    public void failsWhenOverCapacity() {
        final LongHashSet set = new LongHashSet(1, 0);
        set.add(1);
        rule.expect(IllegalStateException.class);
        set.add(2);
    }

    @Test
    public void toArrayReturnsArrayOfAllElements() {
        final LongHashSet initial = new LongHashSet(100, (-1));
        initial.add(1);
        initial.add(13);
        final Object[] ary = initial.toArray();
        final Set<Object> fromArray = new HashSet<Object>(Arrays.asList(ary));
        Assert.assertEquals(new HashSet<Object>(initial), fromArray);
    }

    @Test
    public void intoArrayReturnsArrayOfAllElements() {
        final LongHashSet initial = new LongHashSet(100, (-1));
        initial.add(1);
        initial.add(13);
        final Object[] ary = initial.toArray(new Long[0]);
        final Set<Object> fromArray = new HashSet<Object>(Arrays.asList(ary));
        Assert.assertEquals(new HashSet<Object>(initial), fromArray);
    }

    @Test
    public void testLongArrayConstructor() {
        long[] items = new long[]{ 42L, 43L };
        long missingValue = -1L;
        final LongHashSet hashSet = new LongHashSet(items, missingValue);
        set.add(42L);
        set.add(43L);
        Assert.assertEquals(set, hashSet);
    }

    @Test
    public void testRemove_whenValuePassedAsObject() {
        final LongHashSet initial = new LongHashSet(100, (-1));
        initial.add(42);
        initial.remove(Long.valueOf(42));
        Assert.assertFalse(initial.contains(Long.valueOf(42)));
        Assert.assertTrue(initial.isEmpty());
    }

    @Test
    public void testAddAll_whenCollectionGiven() {
        Long[] items = new Long[]{ 43L, 44L };
        final List<Long> expected = Lists.asList(42L, items);
        final LongHashSet actual = new LongHashSet(100, (-1));
        actual.addAll(expected);
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testRemoveAll_whenCollectionGiven() {
        Long[] items = new Long[]{ 43L, 44L };
        final List<Long> toBeRemoved = Lists.asList(42L, items);
        final LongHashSet actual = new LongHashSet(100, (-1));
        actual.addAll(toBeRemoved);
        Assert.assertEquals(toBeRemoved.size(), actual.size());
        actual.removeAll(toBeRemoved);
        Assert.assertTrue(actual.isEmpty());
    }
}

