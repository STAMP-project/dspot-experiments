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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IntHashSetTest extends HazelcastTestSupport {
    @Rule
    public final ExpectedException rule = ExpectedException.none();

    private final IntHashSet set = new IntHashSet(1000, (-1));

    @Test
    public void initiallyContainsNoElements() throws Exception {
        for (int i = 0; i < 10000; i++) {
            HazelcastTestSupport.assertNotContains(set, i);
        }
    }

    @Test
    @SuppressWarnings("UnnecessaryBoxing")
    public void initiallyContainsNoBoxedElements() {
        for (int i = 0; i < 10000; i++) {
            HazelcastTestSupport.assertNotContains(set, Integer.valueOf(i));
        }
    }

    @Test
    public void containsAddedBoxedElement() {
        Assert.assertTrue(set.add(1));
        HazelcastTestSupport.assertContains(set, 1);
    }

    @Test
    public void addingAnElementTwiceDoesNothing() {
        Assert.assertTrue(set.add(1));
        Assert.assertFalse(set.add(1));
    }

    @Test
    @SuppressWarnings("UnnecessaryBoxing")
    public void containsAddedBoxedElements() {
        Assert.assertTrue(set.add(1));
        Assert.assertTrue(set.add(Integer.valueOf(2)));
        HazelcastTestSupport.assertContains(set, Integer.valueOf(1));
        HazelcastTestSupport.assertContains(set, 2);
    }

    @Test
    public void removingAnElementFromAnEmptyListDoesNothing() {
        Assert.assertFalse(set.remove(0));
    }

    @Test
    public void removingAPresentElementRemovesIt() {
        final Set<Integer> jdkSet = new HashSet<Integer>();
        final Random rnd = new Random();
        for (int i = 0; i < 1000; i++) {
            final int value = rnd.nextInt();
            set.add(value);
            jdkSet.add(value);
        }
        Assert.assertEquals(jdkSet, set);
        for (Iterator<Integer> iter = jdkSet.iterator(); iter.hasNext();) {
            final int value = iter.next();
            HazelcastTestSupport.assertContains(set, value);
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
        HazelcastTestSupport.assertNotContains(set, 1);
        HazelcastTestSupport.assertNotContains(set, 2);
    }

    @Test
    public void differenceReturnsNullIfBothSetsEqual() {
        set.add(1);
        set.add(2);
        final IntHashSet other = new IntHashSet(100, (-1));
        other.add(1);
        other.add(2);
        Assert.assertNull(set.difference(other));
    }

    @Test
    public void differenceReturnsSetDifference() {
        set.add(1);
        set.add(2);
        final IntHashSet other = new IntHashSet(100, (-1));
        other.add(1);
        final IntHashSet diff = set.difference(other);
        Assert.assertEquals(1, diff.size());
        HazelcastTestSupport.assertContains(diff, 2);
    }

    @Test
    public void copiesOtherIntHashSet() {
        set.add(1);
        set.add(2);
        final IntHashSet other = new IntHashSet(1000, (-1));
        other.copy(set);
        HazelcastTestSupport.assertContains(other, 1);
        HazelcastTestSupport.assertContains(other, 2);
    }

    @Test
    public void twoEmptySetsAreEqual() {
        final IntHashSet other = new IntHashSet(100, (-1));
        Assert.assertEquals(set, other);
    }

    @Test
    public void equalityRequiresTheSameMissingValue() {
        final IntHashSet other = new IntHashSet(100, 1);
        Assert.assertNotEquals(set, other);
    }

    @Test
    public void setsWithTheSameValuesAreEqual() {
        final IntHashSet other = new IntHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(1);
        other.add(1001);
        Assert.assertEquals(set, other);
    }

    @Test
    public void setsWithTheDifferentSizesAreNotEqual() {
        final IntHashSet other = new IntHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(1001);
        Assert.assertNotEquals(set, other);
    }

    @Test
    public void setsWithTheDifferentValuesAreNotEqual() {
        final IntHashSet other = new IntHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(2);
        other.add(1001);
        Assert.assertNotEquals(set, other);
    }

    @Test
    public void twoEmptySetsHaveTheSameHashcode() {
        final IntHashSet other = new IntHashSet(100, (-1));
        Assert.assertEquals(set.hashCode(), other.hashCode());
    }

    @Test
    public void setsWithTheSameValuesHaveTheSameHashcode() {
        final IntHashSet other = new IntHashSet(100, (-1));
        set.add(1);
        set.add(1001);
        other.add(1);
        other.add(1001);
        Assert.assertEquals(set.hashCode(), other.hashCode());
    }

    @Test
    public void worksCorrectlyWhenFull() {
        final IntHashSet set = new IntHashSet(2, 0);
        set.add(1);
        set.add(2);
        HazelcastTestSupport.assertContains(set, 2);
        HazelcastTestSupport.assertNotContains(set, 3);
    }

    @Test
    public void failsWhenOverCapacity() {
        final IntHashSet set = new IntHashSet(1, 0);
        set.add(1);
        rule.expect(IllegalStateException.class);
        set.add(2);
    }

    @Test
    public void toArrayReturnsArrayOfAllElements() {
        final IntHashSet initial = new IntHashSet(100, (-1));
        initial.add(1);
        initial.add(13);
        final Object[] ary = initial.toArray();
        final Set<Object> fromArray = new HashSet<Object>(Arrays.asList(ary));
        Assert.assertEquals(new HashSet<Object>(initial), fromArray);
    }

    @Test
    public void intoArrayReturnsArrayOfAllElements() {
        final IntHashSet initial = new IntHashSet(100, (-1));
        initial.add(1);
        initial.add(13);
        final Object[] ary = initial.toArray(new Integer[0]);
        final Set<Object> fromArray = new HashSet<Object>(Arrays.asList(ary));
        Assert.assertEquals(new HashSet<Object>(initial), fromArray);
    }
}

