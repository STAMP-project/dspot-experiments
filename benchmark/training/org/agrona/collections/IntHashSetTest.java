/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona.collections;


import IntHashSet.IntIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class IntHashSetTest {
    private static final int INITIAL_CAPACITY = 100;

    private final IntHashSet testSet = new IntHashSet(IntHashSetTest.INITIAL_CAPACITY);

    @Test
    public void initiallyContainsNoElements() {
        for (int i = 0; i < 10000; i++) {
            Assert.assertFalse(testSet.contains(i));
        }
    }

    @Test
    public void initiallyContainsNoBoxedElements() {
        for (int i = 0; i < 10000; i++) {
            Assert.assertFalse(testSet.contains(Integer.valueOf(i)));
        }
    }

    @Test
    public void containsAddedElement() {
        Assert.assertTrue(testSet.add(1));
        Assert.assertTrue(testSet.contains(1));
    }

    @Test
    public void addingAnElementTwiceDoesNothing() {
        Assert.assertTrue(testSet.add(1));
        Assert.assertFalse(testSet.add(1));
    }

    @Test
    public void containsAddedBoxedElements() {
        Assert.assertTrue(testSet.add(1));
        Assert.assertTrue(testSet.add(Integer.valueOf(2)));
        Assert.assertTrue(testSet.contains(Integer.valueOf(1)));
        Assert.assertTrue(testSet.contains(2));
    }

    @Test
    public void removingAnElementFromAnEmptyListDoesNothing() {
        Assert.assertFalse(testSet.remove(0));
    }

    @Test
    public void removingAPresentElementRemovesIt() {
        Assert.assertTrue(testSet.add(1));
        Assert.assertTrue(testSet.remove(1));
        Assert.assertFalse(testSet.contains(1));
    }

    @Test
    public void sizeIsInitiallyZero() {
        Assert.assertEquals(0, testSet.size());
    }

    @Test
    public void sizeIncrementsWithNumberOfAddedElements() {
        IntHashSetTest.addTwoElements(testSet);
        Assert.assertEquals(2, testSet.size());
    }

    @Test
    @SuppressWarnings("OverwrittenKey")
    public void sizeContainsNumberOfNewElements() {
        testSet.add(1);
        testSet.add(1);
        Assert.assertEquals(1, testSet.size());
    }

    @Test
    public void iteratorsListElements() {
        IntHashSetTest.addTwoElements(testSet);
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTime() {
        iteratorsListElements();
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsListElementsWithoutHasNext() {
        IntHashSetTest.addTwoElements(testSet);
        assertIteratorHasElementsWithoutHasNext();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTimeWithoutHasNext() {
        iteratorsListElementsWithoutHasNext();
        assertIteratorHasElementsWithoutHasNext();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorsThrowNoSuchElementException() {
        IntHashSetTest.addTwoElements(testSet);
        exhaustIterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorsThrowNoSuchElementExceptionFromTheBeginningEveryTime() {
        IntHashSetTest.addTwoElements(testSet);
        try {
            exhaustIterator();
        } catch (final NoSuchElementException ignore) {
        }
        exhaustIterator();
    }

    @Test
    public void iteratorHasNoElements() {
        Assert.assertFalse(testSet.iterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorThrowExceptionForEmptySet() {
        testSet.iterator().next();
    }

    @Test
    public void clearRemovesAllElementsOfTheSet() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.clear();
        Assert.assertEquals(0, testSet.size());
        Assert.assertFalse(testSet.contains(1));
        Assert.assertFalse(testSet.contains(1001));
    }

    @Test
    public void differenceReturnsNullIfBothSetsEqual() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(other);
        Assert.assertNull(testSet.difference(other));
    }

    @Test
    public void differenceReturnsSetDifference() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet other = new IntHashSet(100);
        other.add(1);
        final IntHashSet diff = testSet.difference(other);
        Assert.assertThat(diff, containsInAnyOrder(1001));
    }

    @Test
    public void copiesOtherIntHashSet() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet other = new IntHashSet(100);
        other.copy(testSet);
        IntHashSetTest.assertContainsElements(other);
    }

    @Test
    public void twoEmptySetsAreEqual() {
        final IntHashSet other = new IntHashSet(100);
        Assert.assertEquals(testSet, other);
    }

    @Test
    public void setsWithTheSameValuesAreEqual() {
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(testSet);
        IntHashSetTest.addTwoElements(other);
        Assert.assertEquals(testSet, other);
    }

    @Test
    public void setsWithTheDifferentSizesAreNotEqual() {
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(testSet);
        other.add(1001);
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void setsWithTheDifferentValuesAreNotEqual() {
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(testSet);
        other.add(2);
        other.add(1001);
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void twoEmptySetsHaveTheSameHashcode() {
        Assert.assertEquals(testSet.hashCode(), new IntHashSet(100).hashCode());
    }

    @Test
    public void setsWithTheSameValuesHaveTheSameHashcode() {
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(testSet);
        IntHashSetTest.addTwoElements(other);
        Assert.assertEquals(testSet.hashCode(), other.hashCode());
    }

    @Test
    public void reducesSizeWhenElementRemoved() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.remove(1001);
        Assert.assertEquals(1, testSet.size());
    }

    @Test(expected = ArrayStoreException.class)
    @SuppressWarnings("SuspiciousToArrayCall")
    public void toArrayThrowsArrayStoreExceptionForWrongType() {
        testSet.toArray(new String[1]);
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void toArrayThrowsNullPointerExceptionForNullArgument() {
        final Integer[] into = null;
        testSet.toArray(into);
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArrayCopiesElementsIntoSufficientlySizedArray() {
        IntHashSetTest.addTwoElements(testSet);
        final Integer[] result = testSet.toArray(new Integer[testSet.size()]);
        IntHashSetTest.assertArrayContainingElements(result);
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArrayCopiesElementsIntoNewArray() {
        IntHashSetTest.addTwoElements(testSet);
        final Integer[] result = testSet.toArray(new Integer[testSet.size()]);
        IntHashSetTest.assertArrayContainingElements(result);
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArraySupportsEmptyCollection() {
        final Integer[] result = testSet.toArray(new Integer[testSet.size()]);
        Assert.assertArrayEquals(result, new Integer[]{  });
    }

    // Test case from usage bug.
    @Test
    public void chainCompactionShouldNotCauseElementsToBeMovedBeforeTheirHash() {
        final IntHashSet requiredFields = new IntHashSet(14);
        requiredFields.add(8);
        requiredFields.add(9);
        requiredFields.add(35);
        requiredFields.add(49);
        requiredFields.add(56);
        Assert.assertTrue("Failed to remove 8", requiredFields.remove(8));
        Assert.assertTrue("Failed to remove 9", requiredFields.remove(9));
        Assert.assertThat(requiredFields, containsInAnyOrder(35, 49, 56));
    }

    @Test
    public void shouldResizeWhenItHitsCapacity() {
        for (int i = 0; i < (2 * (IntHashSetTest.INITIAL_CAPACITY)); i++) {
            Assert.assertTrue(testSet.add(i));
        }
        for (int i = 0; i < (2 * (IntHashSetTest.INITIAL_CAPACITY)); i++) {
            Assert.assertTrue(testSet.contains(i));
        }
    }

    @Test
    public void containsEmptySet() {
        final IntHashSet other = new IntHashSet(100);
        Assert.assertTrue(testSet.containsAll(other));
        Assert.assertTrue(testSet.containsAll(((Collection<?>) (other))));
    }

    @Test
    public void containsSubset() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet subset = new IntHashSet(100);
        subset.add(1);
        Assert.assertTrue(testSet.containsAll(subset));
        Assert.assertTrue(testSet.containsAll(((Collection<?>) (subset))));
    }

    @Test
    public void doesNotContainDisjointSet() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet other = new IntHashSet(100);
        other.add(1);
        other.add(1002);
        Assert.assertFalse(testSet.containsAll(other));
        Assert.assertFalse(testSet.containsAll(((Collection<?>) (other))));
    }

    @Test
    public void doesNotContainSuperset() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet superset = new IntHashSet(100);
        IntHashSetTest.addTwoElements(superset);
        superset.add(15);
        Assert.assertFalse(testSet.containsAll(superset));
        Assert.assertFalse(testSet.containsAll(((Collection<?>) (superset))));
    }

    @Test
    public void addingEmptySetDoesNothing() {
        IntHashSetTest.addTwoElements(testSet);
        Assert.assertFalse(testSet.addAll(new IntHashSet(100)));
        Assert.assertFalse(testSet.addAll(new HashSet()));
        IntHashSetTest.assertContainsElements(testSet);
    }

    @Test
    public void addingSubsetDoesNothing() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet subset = new IntHashSet(100);
        subset.add(1);
        final HashSet<Integer> subSetCollection = new HashSet(subset);
        Assert.assertFalse(testSet.addAll(subset));
        Assert.assertFalse(testSet.addAll(subSetCollection));
        IntHashSetTest.assertContainsElements(testSet);
    }

    @Test
    public void addingEqualSetDoesNothing() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet equal = new IntHashSet(100);
        IntHashSetTest.addTwoElements(equal);
        final HashSet<Integer> equalCollection = new HashSet(equal);
        Assert.assertFalse(testSet.addAll(equal));
        Assert.assertFalse(testSet.addAll(equalCollection));
        IntHashSetTest.assertContainsElements(testSet);
    }

    @Test
    public void containsValuesAddedFromDisjointSetPrimitive() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet disjoint = new IntHashSet(100);
        disjoint.add(2);
        disjoint.add(1002);
        Assert.assertTrue(testSet.addAll(disjoint));
        Assert.assertTrue(testSet.contains(1));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertTrue(testSet.containsAll(disjoint));
    }

    @Test
    public void containsValuesAddedFromDisjointSet() {
        IntHashSetTest.addTwoElements(testSet);
        final HashSet<Integer> disjoint = new HashSet<>();
        disjoint.add(2);
        disjoint.add(1002);
        Assert.assertTrue(testSet.addAll(disjoint));
        Assert.assertTrue(testSet.contains(1));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertTrue(testSet.containsAll(disjoint));
    }

    @Test
    public void containsValuesAddedFromIntersectingSetPrimitive() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet intersecting = new IntHashSet(100);
        intersecting.add(1);
        intersecting.add(1002);
        Assert.assertTrue(testSet.addAll(intersecting));
        Assert.assertTrue(testSet.contains(1));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertTrue(testSet.containsAll(intersecting));
    }

    @Test
    public void containsValuesAddedFromIntersectingSet() {
        IntHashSetTest.addTwoElements(testSet);
        final HashSet<Integer> intersecting = new HashSet<>();
        intersecting.add(1);
        intersecting.add(1002);
        Assert.assertTrue(testSet.addAll(intersecting));
        Assert.assertTrue(testSet.contains(1));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertTrue(testSet.containsAll(intersecting));
    }

    @Test
    public void removingEmptySetDoesNothing() {
        IntHashSetTest.addTwoElements(testSet);
        Assert.assertFalse(testSet.removeAll(new IntHashSet(100)));
        Assert.assertFalse(testSet.removeAll(new HashSet<Integer>()));
        IntHashSetTest.assertContainsElements(testSet);
    }

    @Test
    public void removingDisjointSetDoesNothing() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet disjoint = new IntHashSet(100);
        disjoint.add(2);
        disjoint.add(1002);
        Assert.assertFalse(testSet.removeAll(disjoint));
        Assert.assertFalse(testSet.removeAll(new HashSet<Integer>()));
        IntHashSetTest.assertContainsElements(testSet);
    }

    @Test
    public void doesNotContainRemovedIntersectingSetPrimitive() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet intersecting = new IntHashSet(100);
        intersecting.add(1);
        intersecting.add(1002);
        Assert.assertTrue(testSet.removeAll(intersecting));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertFalse(testSet.containsAll(intersecting));
    }

    @Test
    public void doesNotContainRemovedIntersectingSet() {
        IntHashSetTest.addTwoElements(testSet);
        final HashSet<Integer> intersecting = new HashSet<>();
        intersecting.add(1);
        intersecting.add(1002);
        Assert.assertTrue(testSet.removeAll(intersecting));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertFalse(testSet.containsAll(intersecting));
    }

    @Test
    public void isEmptyAfterRemovingEqualSetPrimitive() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet equal = new IntHashSet(100);
        IntHashSetTest.addTwoElements(equal);
        Assert.assertTrue(testSet.removeAll(equal));
        Assert.assertTrue(testSet.isEmpty());
    }

    @Test
    public void isEmptyAfterRemovingEqualSet() {
        IntHashSetTest.addTwoElements(testSet);
        final HashSet<Integer> equal = new HashSet<>();
        IntHashSetTest.addTwoElements(equal);
        Assert.assertTrue(testSet.removeAll(equal));
        Assert.assertTrue(testSet.isEmpty());
    }

    @Test
    public void removeElementsFromIterator() {
        IntHashSetTest.addTwoElements(testSet);
        final IntHashSet.IntIterator iterator = testSet.iterator();
        while (iterator.hasNext()) {
            if ((iterator.nextValue()) == 1) {
                iterator.remove();
            }
        } 
        Assert.assertThat(testSet, contains(1001));
        Assert.assertThat(testSet, hasSize(1));
    }

    @Test
    public void shouldNotContainMissingValueInitially() {
        Assert.assertFalse(testSet.contains(IntHashSet.MISSING_VALUE));
    }

    @Test
    public void shouldAllowMissingValue() {
        Assert.assertTrue(testSet.add(IntHashSet.MISSING_VALUE));
        Assert.assertTrue(testSet.contains(IntHashSet.MISSING_VALUE));
        Assert.assertFalse(testSet.add(IntHashSet.MISSING_VALUE));
    }

    @Test
    public void shouldAllowRemovalOfMissingValue() {
        Assert.assertTrue(testSet.add(IntHashSet.MISSING_VALUE));
        Assert.assertTrue(testSet.remove(IntHashSet.MISSING_VALUE));
        Assert.assertFalse(testSet.contains(IntHashSet.MISSING_VALUE));
        Assert.assertFalse(testSet.remove(IntHashSet.MISSING_VALUE));
    }

    @Test
    public void sizeAccountsForMissingValue() {
        testSet.add(1);
        testSet.add(IntHashSet.MISSING_VALUE);
        Assert.assertEquals(2, testSet.size());
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArrayCopiesElementsIntoNewArrayIncludingMissingValue() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.add(IntHashSet.MISSING_VALUE);
        final Integer[] result = testSet.toArray(new Integer[testSet.size()]);
        Assert.assertThat(result, arrayContainingInAnyOrder(1, 1001, IntHashSet.MISSING_VALUE));
    }

    @Test
    public void toObjectArrayCopiesElementsIntoNewArrayIncludingMissingValue() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.add(IntHashSet.MISSING_VALUE);
        final Object[] result = testSet.toArray();
        Assert.assertThat(result, arrayContainingInAnyOrder(1, 1001, IntHashSet.MISSING_VALUE));
    }

    @Test
    public void equalsAccountsForMissingValue() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.add(IntHashSet.MISSING_VALUE);
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(other);
        Assert.assertNotEquals(testSet, other);
        other.add(IntHashSet.MISSING_VALUE);
        Assert.assertEquals(testSet, other);
        testSet.remove(IntHashSet.MISSING_VALUE);
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void consecutiveValuesShouldBeCorrectlyStored() {
        for (int i = 0; i < 10000; i++) {
            testSet.add(i);
        }
        Assert.assertThat(testSet, hasSize(10000));
        int distinctElements = 0;
        for (final int ignore : testSet) {
            distinctElements++;
        }
        Assert.assertThat(distinctElements, is(10000));
    }

    @Test
    public void hashCodeAccountsForMissingValue() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.add(IntHashSet.MISSING_VALUE);
        final IntHashSet other = new IntHashSet(100);
        IntHashSetTest.addTwoElements(other);
        Assert.assertNotEquals(testSet.hashCode(), other.hashCode());
        other.add(IntHashSet.MISSING_VALUE);
        Assert.assertEquals(testSet.hashCode(), other.hashCode());
        testSet.remove(IntHashSet.MISSING_VALUE);
        Assert.assertNotEquals(testSet.hashCode(), other.hashCode());
    }

    @Test
    public void iteratorAccountsForMissingValue() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.add(IntHashSet.MISSING_VALUE);
        int missingValueCount = 0;
        final IntHashSet.IntIterator iterator = testSet.iterator();
        while (iterator.hasNext()) {
            if ((iterator.nextValue()) == (IntHashSet.MISSING_VALUE)) {
                missingValueCount++;
            }
        } 
        Assert.assertEquals(1, missingValueCount);
    }

    @Test
    public void iteratorCanRemoveMissingValue() {
        IntHashSetTest.addTwoElements(testSet);
        testSet.add(IntHashSet.MISSING_VALUE);
        final IntHashSet.IntIterator iterator = testSet.iterator();
        while (iterator.hasNext()) {
            if ((iterator.nextValue()) == (IntHashSet.MISSING_VALUE)) {
                iterator.remove();
            }
        } 
        Assert.assertFalse(testSet.contains(IntHashSet.MISSING_VALUE));
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, -1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            testSet.add(testEntry);
        }
        final String mapAsAString = "{1, 19, 11, 7, 3, 12, -1}";
        Assert.assertThat(testSet.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void shouldRemoveMissingValueWhenCleared() {
        Assert.assertTrue(testSet.add(IntHashSet.MISSING_VALUE));
        testSet.clear();
        Assert.assertFalse(testSet.contains(IntHashSet.MISSING_VALUE));
    }

    @Test
    public void shouldHaveCompatibleEqualsAndHashcode() {
        final HashSet<Integer> compatibleSet = new HashSet<>();
        final long seed = System.nanoTime();
        final Random r = new Random(seed);
        for (int i = 0; i < 1024; i++) {
            final int value = r.nextInt();
            compatibleSet.add(value);
            testSet.add(value);
        }
        if (r.nextBoolean()) {
            compatibleSet.add(IntHashSet.MISSING_VALUE);
            testSet.add(IntHashSet.MISSING_VALUE);
        }
        Assert.assertEquals(("Fail with seed:" + seed), testSet, compatibleSet);
        Assert.assertEquals(("Fail with seed:" + seed), compatibleSet, testSet);
        Assert.assertEquals(("Fail with seed:" + seed), compatibleSet.hashCode(), testSet.hashCode());
    }
}

