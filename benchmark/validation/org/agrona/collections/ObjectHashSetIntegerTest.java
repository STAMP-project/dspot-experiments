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


import ObjectHashSet.ObjectIterator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ObjectHashSetIntegerTest {
    private static final int INITIAL_CAPACITY = 100;

    private final ObjectHashSet<Integer> testSet;

    public ObjectHashSetIntegerTest(final ObjectHashSet<Integer> testSet) {
        this.testSet = testSet;
    }

    @Test
    public void initiallyContainsNoElements() {
        for (int i = 0; i < 10000; i++) {
            Assert.assertFalse(testSet.contains(i));
        }
    }

    @Test
    public void initiallyContainsNoBoxedElements() {
        for (int i = 0; i < 10000; i++) {
            // noinspection UnnecessaryBoxing
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
        // noinspection UnnecessaryBoxing
        Assert.assertTrue(testSet.add(Integer.valueOf(2)));
        // noinspection UnnecessaryBoxing
        Assert.assertTrue(testSet.contains(Integer.valueOf(1)));
        Assert.assertTrue(testSet.contains(2));
    }

    @Test
    public void doesNotContainMissingValue() {
        Assert.assertFalse(testSet.contains(2048));
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
        ObjectHashSetIntegerTest.addTwoElements(testSet);
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
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTime() {
        iteratorsListElements();
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsListElementsWithoutHasNext() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        assertIteratorHasElementsWithoutHasNext();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTimeWithoutHasNext() {
        iteratorsListElementsWithoutHasNext();
        assertIteratorHasElementsWithoutHasNext();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorsThrowNoSuchElementException() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        exhaustIterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorsThrowNoSuchElementExceptionFromTheBeginningEveryTime() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
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
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        testSet.clear();
        Assert.assertEquals(0, testSet.size());
        Assert.assertFalse(testSet.contains(1));
        Assert.assertFalse(testSet.contains(1001));
    }

    @Test
    public void differenceReturnsNullIfBothSetsEqual() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(other);
        Assert.assertNull(testSet.difference(other));
    }

    @Test
    public void differenceReturnsSetDifference() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        other.add(1);
        final ObjectHashSet<Integer> diff = testSet.difference(other);
        Assert.assertThat(diff, containsInAnyOrder(1001));
    }

    @Test
    public void copiesOtherIntHashSet() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        other.copy(testSet);
        ObjectHashSetIntegerTest.assertContainsElements(other);
    }

    @Test
    public void twoEmptySetsAreEqual() {
        final ObjectHashSet other = new ObjectHashSet(100);
        Assert.assertEquals(testSet, other);
    }

    @Test
    public void setsWithTheSameValuesAreEqual() {
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        ObjectHashSetIntegerTest.addTwoElements(other);
        Assert.assertEquals(testSet, other);
    }

    @Test
    public void setsWithTheDifferentSizesAreNotEqual() {
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        other.add(1001);
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void setsWithTheDifferentValuesAreNotEqual() {
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        other.add(2);
        other.add(1001);
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void twoEmptySetsHaveTheSameHashcode() {
        Assert.assertEquals(testSet.hashCode(), new ObjectHashSet<Integer>(100).hashCode());
    }

    @Test
    public void setsWithTheSameValuesHaveTheSameHashcode() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> secondSet = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(secondSet);
        Assert.assertEquals(testSet.hashCode(), secondSet.hashCode());
    }

    @Test
    public void reducesSizeWhenElementRemoved() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        testSet.remove(1001);
        Assert.assertEquals(1, testSet.size());
    }

    @Test(expected = NullPointerException.class)
    public void toArrayThrowsNullPointerExceptionForNullArgument() {
        final Integer[] into = null;
        testSet.toArray(into);
    }

    @Test
    public void toArrayCopiesElementsIntoSufficientlySizedArray() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final Integer[] result = testSet.toArray(new Integer[0]);
        ObjectHashSetIntegerTest.assertArrayContainingElements(result);
    }

    @Test
    public void toArrayCopiesElementsIntoNewArray() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final Integer[] result = testSet.toArray(new Integer[0]);
        ObjectHashSetIntegerTest.assertArrayContainingElements(result);
    }

    @Test
    public void toArraySupportsEmptyCollection() {
        final Integer[] result = testSet.toArray(new Integer[0]);
        Assert.assertArrayEquals(result, new Integer[]{  });
    }

    @Test
    public void chainCompactionShouldNotCauseElementsToBeMovedBeforeTheirHash() {
        final ObjectHashSet<Integer> requiredFields = new ObjectHashSet(14);
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
        for (int i = 0; i < (2 * (ObjectHashSetIntegerTest.INITIAL_CAPACITY)); i++) {
            Assert.assertTrue(testSet.add(i));
        }
        for (int i = 0; i < (2 * (ObjectHashSetIntegerTest.INITIAL_CAPACITY)); i++) {
            Assert.assertTrue(testSet.contains(i));
        }
    }

    @Test
    public void containsEmptySet() {
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        Assert.assertTrue(testSet.containsAll(other));
    }

    @Test
    public void containsSubset() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> subset = new ObjectHashSet(100);
        subset.add(1);
        Assert.assertTrue(testSet.containsAll(subset));
    }

    @Test
    public void doesNotContainDisjointSet() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> other = new ObjectHashSet(100);
        other.add(1);
        other.add(1002);
        Assert.assertFalse(testSet.containsAll(other));
    }

    @Test
    public void doesNotContainSuperset() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> superset = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(superset);
        superset.add(15);
        Assert.assertFalse(testSet.containsAll(superset));
    }

    @Test
    public void addingEmptySetDoesNothing() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        Assert.assertFalse(testSet.addAll(new ObjectHashSet(100)));
        ObjectHashSetIntegerTest.assertContainsElements(testSet);
    }

    @Test
    public void addingSubsetDoesNothing() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> subset = new ObjectHashSet(100);
        subset.add(1);
        Assert.assertFalse(testSet.addAll(subset));
        ObjectHashSetIntegerTest.assertContainsElements(testSet);
    }

    @Test
    public void addingEqualSetDoesNothing() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> equal = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(equal);
        Assert.assertFalse(testSet.addAll(equal));
        ObjectHashSetIntegerTest.assertContainsElements(testSet);
    }

    @Test
    public void containsValuesAddedFromDisjointSet() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> disjoint = new ObjectHashSet(100);
        disjoint.add(2);
        disjoint.add(1002);
        Assert.assertTrue(testSet.addAll(disjoint));
        Assert.assertTrue(testSet.contains(1));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertTrue(testSet.containsAll(disjoint));
    }

    @Test
    public void containsValuesAddedFromIntersectingSet() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> intersecting = new ObjectHashSet(100);
        intersecting.add(1);
        intersecting.add(1002);
        Assert.assertTrue(testSet.addAll(intersecting));
        Assert.assertTrue(testSet.contains(1));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertTrue(testSet.containsAll(intersecting));
    }

    @Test
    public void removingEmptySetDoesNothing() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        Assert.assertFalse(testSet.removeAll(new ObjectHashSet(100)));
        ObjectHashSetIntegerTest.assertContainsElements(testSet);
    }

    @Test
    public void removingDisjointSetDoesNothing() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> disjoint = new ObjectHashSet(100);
        disjoint.add(2);
        disjoint.add(1002);
        Assert.assertFalse(testSet.removeAll(disjoint));
        ObjectHashSetIntegerTest.assertContainsElements(testSet);
    }

    @Test
    public void doesNotContainRemovedIntersectingSet() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> intersecting = new ObjectHashSet(100);
        intersecting.add(1);
        intersecting.add(1002);
        Assert.assertTrue(testSet.removeAll(intersecting));
        Assert.assertTrue(testSet.contains(1001));
        Assert.assertFalse(testSet.containsAll(intersecting));
    }

    @Test
    public void isEmptyAfterRemovingEqualSet() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet<Integer> equal = new ObjectHashSet(100);
        ObjectHashSetIntegerTest.addTwoElements(equal);
        Assert.assertTrue(testSet.removeAll(equal));
        Assert.assertTrue(testSet.isEmpty());
    }

    @Test
    public void removeElementsFromIterator() {
        ObjectHashSetIntegerTest.addTwoElements(testSet);
        final ObjectHashSet.ObjectIterator intIterator = testSet.iterator();
        // noinspection Java8CollectionRemoveIf
        while (intIterator.hasNext()) {
            if (intIterator.next().equals(1)) {
                intIterator.remove();
            }
        } 
        Assert.assertThat(testSet, contains(1001));
        Assert.assertThat(testSet, hasSize(1));
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, -1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            testSet.add(testEntry);
        }
        final String mapAsAString = "{1, 3, 7, 11, 12, 19, -1}";
        Assert.assertThat(testSet.toString(), IsEqual.equalTo(mapAsAString));
    }

    @Test
    public void shouldIterateOverExpandedSet() {
        final HashSet<Integer> refSet = new HashSet<>(5);
        final ObjectHashSet<Integer> testSet = new ObjectHashSet(5);
        for (int i = 0; i < 20; i++) {
            refSet.add(i);
            testSet.add(i);
        }
        final ObjectHashSet.ObjectIterator iter = testSet.iterator();
        for (int i = 0; i < 20; i++) {
            Assert.assertTrue(iter.hasNext());
            Assert.assertTrue(refSet.contains(iter.next()));
        }
        Assert.assertFalse(iter.hasNext());
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
        Assert.assertEquals(("Fail with seed:" + seed), testSet, compatibleSet);
        Assert.assertEquals(("Fail with seed:" + seed), compatibleSet, testSet);
        Assert.assertEquals(("Fail with seed:" + seed), compatibleSet.hashCode(), testSet.hashCode());
    }
}

