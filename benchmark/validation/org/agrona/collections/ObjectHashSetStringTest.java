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
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ObjectHashSetStringTest {
    private static final int INITIAL_CAPACITY = 100;

    private final ObjectHashSet<String> testSet;

    public ObjectHashSetStringTest(final ObjectHashSet<String> testSet) {
        this.testSet = testSet;
    }

    @Test
    public void containsAddedElement() {
        Assert.assertTrue(testSet.add("1"));
        Assert.assertTrue(testSet.contains("1"));
    }

    @Test
    public void addingAnElementTwiceDoesNothing() {
        Assert.assertTrue(testSet.add("1"));
        Assert.assertFalse(testSet.add("1"));
    }

    @Test
    public void removingAnElementFromAnEmptyListDoesNothing() {
        Assert.assertFalse(testSet.remove("0"));
    }

    @Test
    public void removingAPresentElementRemovesIt() {
        Assert.assertTrue(testSet.add("1"));
        Assert.assertTrue(testSet.remove("1"));
        Assert.assertFalse(testSet.contains("1"));
    }

    @Test
    public void sizeIsInitiallyZero() {
        Assert.assertEquals(0, testSet.size());
    }

    @Test
    public void sizeIncrementsWithNumberOfAddedElements() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        Assert.assertEquals(2, testSet.size());
    }

    @Test
    public void sizeContainsNumberOfNewElements() {
        testSet.add("1");
        testSet.add("1");
        Assert.assertEquals(1, testSet.size());
    }

    @Test
    public void iteratorsListElements() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTime() {
        iteratorsListElements();
        assertIteratorHasElements();
    }

    @Test
    public void iteratorsListElementsWithoutHasNext() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        assertIteratorHasElementsWithoutHasNext();
    }

    @Test
    public void iteratorsStartFromTheBeginningEveryTimeWithoutHasNext() {
        iteratorsListElementsWithoutHasNext();
        assertIteratorHasElementsWithoutHasNext();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorsThrowNoSuchElementException() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        exhaustIterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorsThrowNoSuchElementExceptionFromTheBeginningEveryTime() {
        ObjectHashSetStringTest.addTwoElements(testSet);
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
        ObjectHashSetStringTest.addTwoElements(testSet);
        testSet.clear();
        Assert.assertEquals(0, testSet.size());
        Assert.assertFalse(testSet.contains("1"));
        Assert.assertFalse(testSet.contains("1001"));
    }

    @Test
    public void differenceReturnsNullIfBothSetsEqual() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> other = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(other);
        Assert.assertNull(testSet.difference(other));
    }

    @Test
    public void differenceReturnsSetDifference() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> other = new ObjectHashSet(100);
        other.add("1");
        final ObjectHashSet<String> diff = testSet.difference(other);
        Assert.assertThat(diff, containsInAnyOrder("1001"));
    }

    @Test
    public void twoEmptySetsAreEqual() {
        final ObjectHashSet other = new ObjectHashSet(100);
        Assert.assertEquals(testSet, other);
    }

    @Test
    public void setsWithTheSameValuesAreEqual() {
        final ObjectHashSet<String> other = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(testSet);
        ObjectHashSetStringTest.addTwoElements(other);
        Assert.assertEquals(testSet, other);
    }

    @Test
    public void setsWithTheDifferentSizesAreNotEqual() {
        final ObjectHashSet<String> other = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(testSet);
        other.add("1001");
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void setsWithTheDifferentValuesAreNotEqual() {
        final ObjectHashSet<String> other = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(testSet);
        other.add("2");
        other.add("1001");
        Assert.assertNotEquals(testSet, other);
    }

    @Test
    public void twoEmptySetsHaveTheSameHashcode() {
        Assert.assertEquals(testSet.hashCode(), new ObjectHashSet<String>(100).hashCode());
    }

    @Test
    public void reducesSizeWhenElementRemoved() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        testSet.remove("1001");
        Assert.assertEquals(1, testSet.size());
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArrayCopiesElementsIntoSufficientlySizedArray() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final String[] result = testSet.toArray(new String[testSet.size()]);
        ObjectHashSetStringTest.assertArrayContainingElements(result);
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArrayCopiesElementsIntoNewArray() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final String[] result = testSet.toArray(new String[testSet.size()]);
        ObjectHashSetStringTest.assertArrayContainingElements(result);
    }

    @Test
    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    public void toArraySupportsEmptyCollection() {
        final String[] result = testSet.toArray(new String[testSet.size()]);
        Assert.assertArrayEquals(result, new String[]{  });
    }

    // Test case from usage bug.
    @Test
    public void chainCompactionShouldNotCauseElementsToBeMovedBeforeTheirHash() {
        final ObjectHashSet<String> requiredFields = new ObjectHashSet(14);
        requiredFields.add("8");
        requiredFields.add("9");
        requiredFields.add("35");
        requiredFields.add("49");
        requiredFields.add("56");
        Assert.assertTrue("Failed to remove 8", requiredFields.remove("8"));
        Assert.assertTrue("Failed to remove 9", requiredFields.remove("9"));
        Assert.assertThat(requiredFields, containsInAnyOrder("35", "49", "56"));
    }

    @Test
    public void shouldResizeWhenItHitsCapacity() {
        for (int i = 0; i < (2 * (ObjectHashSetStringTest.INITIAL_CAPACITY)); i++) {
            Assert.assertTrue(testSet.add(String.valueOf(i)));
        }
        for (int i = 0; i < (2 * (ObjectHashSetStringTest.INITIAL_CAPACITY)); i++) {
            Assert.assertTrue(testSet.contains(String.valueOf(i)));
        }
    }

    @Test
    public void containsSubset() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> subset = new ObjectHashSet(100);
        subset.add("1");
        Assert.assertTrue(testSet.containsAll(subset));
    }

    @Test
    public void doesNotContainDisjointSet() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> other = new ObjectHashSet(100);
        other.add("1");
        other.add("1002");
        Assert.assertFalse(testSet.containsAll(other));
    }

    @Test
    public void doesNotContainSuperset() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> superset = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(superset);
        superset.add("15");
        Assert.assertFalse(testSet.containsAll(superset));
    }

    @Test
    public void addingEmptySetDoesNothing() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        Assert.assertFalse(testSet.addAll(new ObjectHashSet(100)));
        ObjectHashSetStringTest.assertContainsElements(testSet);
    }

    @Test
    public void addingSubsetDoesNothing() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> subset = new ObjectHashSet(100);
        subset.add("1");
        Assert.assertFalse(testSet.addAll(subset));
        ObjectHashSetStringTest.assertContainsElements(testSet);
    }

    @Test
    public void addingEqualSetDoesNothing() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> equal = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(equal);
        Assert.assertFalse(testSet.addAll(equal));
        ObjectHashSetStringTest.assertContainsElements(testSet);
    }

    @Test
    public void containsValuesAddedFromDisjointSet() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> disjoint = new ObjectHashSet(100);
        disjoint.add("2");
        disjoint.add("1002");
        Assert.assertTrue(testSet.addAll(disjoint));
        Assert.assertTrue(testSet.contains("1"));
        Assert.assertTrue(testSet.contains("1001"));
        Assert.assertTrue(testSet.containsAll(disjoint));
    }

    @Test
    public void containsValuesAddedFromIntersectingSet() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> intersecting = new ObjectHashSet(100);
        intersecting.add("1");
        intersecting.add("1002");
        Assert.assertTrue(testSet.addAll(intersecting));
        Assert.assertTrue(testSet.contains("1"));
        Assert.assertTrue(testSet.contains("1001"));
        Assert.assertTrue(testSet.containsAll(intersecting));
    }

    @Test
    public void removingEmptySetDoesNothing() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        Assert.assertFalse(testSet.removeAll(new ObjectHashSet(100)));
        ObjectHashSetStringTest.assertContainsElements(testSet);
    }

    @Test
    public void removingDisjointSetDoesNothing() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> disjoint = new ObjectHashSet(100);
        disjoint.add("2");
        disjoint.add("1002");
        Assert.assertFalse(testSet.removeAll(disjoint));
        ObjectHashSetStringTest.assertContainsElements(testSet);
    }

    @Test
    public void doesNotContainRemovedIntersectingSet() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> intersecting = new ObjectHashSet(100);
        intersecting.add("1");
        intersecting.add("1002");
        Assert.assertTrue(testSet.removeAll(intersecting));
        Assert.assertTrue(testSet.contains("1001"));
        Assert.assertFalse(testSet.containsAll(intersecting));
    }

    @Test
    public void isEmptyAfterRemovingEqualSet() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet<String> equal = new ObjectHashSet(100);
        ObjectHashSetStringTest.addTwoElements(equal);
        Assert.assertTrue(testSet.removeAll(equal));
        Assert.assertTrue(testSet.isEmpty());
    }

    @Test
    public void removeElementsFromIterator() {
        ObjectHashSetStringTest.addTwoElements(testSet);
        final ObjectHashSet.ObjectIterator iter = testSet.iterator();
        // noinspection Java8CollectionRemoveIf
        while (iter.hasNext()) {
            if (iter.next().equals("1")) {
                iter.remove();
            }
        } 
        Assert.assertThat(testSet, contains("1001"));
        Assert.assertThat(testSet, hasSize(1));
    }
}

