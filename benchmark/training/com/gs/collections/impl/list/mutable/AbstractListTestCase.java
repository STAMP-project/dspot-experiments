/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.impl.list.mutable;


import Lists.fixedSize;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.collection.mutable.AbstractCollectionTestCase;
import com.gs.collections.impl.factory.Iterables;
import com.gs.collections.impl.lazy.ReverseIterable;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract JUnit test for {@link MutableList}s.
 */
public abstract class AbstractListTestCase extends AbstractCollectionTestCase {
    @Test
    public void randomAccess_throws() {
        Verify.assertThrows(IllegalArgumentException.class, () -> new ListAdapter<>(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void detectIndex() {
        Assert.assertEquals(1, this.newWith(1, 2, 3, 4).detectIndex(( integer) -> (integer % 2) == 0));
        Assert.assertEquals(0, this.newWith(1, 2, 3, 4).detectIndex(( integer) -> (integer % 2) != 0));
        Assert.assertEquals((-1), this.newWith(1, 2, 3, 4).detectIndex(( integer) -> (integer % 5) == 0));
        Assert.assertEquals(2, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectIndex(( integer) -> integer == 2));
        Assert.assertEquals(0, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectIndex(( integer) -> integer != 2));
        Assert.assertEquals((-1), this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectIndex(( integer) -> integer == 5));
    }

    @Test
    public void detectLastIndex() {
        Assert.assertEquals(3, this.newWith(1, 2, 3, 4).detectLastIndex(( integer) -> (integer % 2) == 0));
        Assert.assertEquals(2, this.newWith(1, 2, 3, 4).detectLastIndex(( integer) -> (integer % 2) != 0));
        Assert.assertEquals((-1), this.newWith(1, 2, 3, 4).detectLastIndex(( integer) -> (integer % 5) == 0));
        Assert.assertEquals(8, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectLastIndex(( integer) -> integer == 2));
        Assert.assertEquals(7, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectLastIndex(( integer) -> integer != 2));
        Assert.assertEquals((-1), this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectLastIndex(( integer) -> integer == 5));
    }

    @Override
    @Test
    public void asSynchronized() {
        Verify.assertInstanceOf(SynchronizedMutableList.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void toImmutable() {
        super.toImmutable();
        Verify.assertInstanceOf(ImmutableList.class, this.newWith().toImmutable());
        Assert.assertSame(this.newWith().toImmutable(), this.newWith().toImmutable());
    }

    @Override
    @Test
    public void asUnmodifiable() {
        Verify.assertInstanceOf(UnmodifiableMutableList.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void testClone() {
        MutableList<Integer> list = this.newWith(1, 2, 3);
        MutableList<Integer> list2 = list.clone();
        Verify.assertListsEqual(list, list2);
        Verify.assertShallowClone(list);
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        MutableCollection<Integer> list1 = this.newWith(1, 2, 3);
        MutableCollection<Integer> list2 = this.newWith(1, 2, 3);
        MutableCollection<Integer> list3 = this.newWith(2, 3, 4);
        MutableCollection<Integer> list4 = this.newWith(1, 2, 3, 4);
        Assert.assertNotEquals(list1, null);
        Verify.assertEqualsAndHashCode(list1, list1);
        Verify.assertEqualsAndHashCode(list1, list2);
        Verify.assertEqualsAndHashCode(new LinkedList(Arrays.asList(1, 2, 3)), list1);
        Verify.assertEqualsAndHashCode(new ArrayList(Arrays.asList(1, 2, 3)), list1);
        Verify.assertEqualsAndHashCode(ArrayAdapter.newArrayWith(1, 2, 3), list1);
        Assert.assertNotEquals(list2, list3);
        Assert.assertNotEquals(list2, list4);
        Assert.assertNotEquals(new LinkedList(Arrays.asList(1, 2, 3)), list4);
        Assert.assertNotEquals(new LinkedList(Arrays.asList(1, 2, 3, 3)), list4);
        Assert.assertNotEquals(new ArrayList(Arrays.asList(1, 2, 3)), list4);
        Assert.assertNotEquals(new ArrayList(Arrays.asList(1, 2, 3, 3)), list4);
        Assert.assertNotEquals(list4, new LinkedList(Arrays.asList(1, 2, 3)));
        Assert.assertNotEquals(list4, new LinkedList(Arrays.asList(1, 2, 3, 3)));
        Assert.assertNotEquals(list4, new ArrayList(Arrays.asList(1, 2, 3)));
        Assert.assertNotEquals(list4, new ArrayList(Arrays.asList(1, 2, 3, 3)));
        Assert.assertNotEquals(new LinkedList(Arrays.asList(1, 2, 3, 4)), list1);
        Assert.assertNotEquals(new LinkedList(Arrays.asList(1, 2, null)), list1);
        Assert.assertNotEquals(new LinkedList(Arrays.asList(1, 2)), list1);
        Assert.assertNotEquals(new ArrayList(Arrays.asList(1, 2, 3, 4)), list1);
        Assert.assertNotEquals(new ArrayList(Arrays.asList(1, 2, null)), list1);
        Assert.assertNotEquals(new ArrayList(Arrays.asList(1, 2)), list1);
        Assert.assertNotEquals(ArrayAdapter.newArrayWith(1, 2, 3, 4), list1);
    }

    @Test
    public void newListWithSize() {
        MutableList<Integer> list = this.newWith(1, 2, 3);
        Verify.assertContainsAll(list, 1, 2, 3);
    }

    @Test
    public void serialization() {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertContainsAll(deserializedCollection, 1, 2, 3, 4, 5);
        Assert.assertEquals(collection, deserializedCollection);
    }

    @Test
    public void corresponds() {
        MutableList<Integer> integers1 = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        MutableList<Integer> integers2 = this.newWith(1, 2, 3, 4);
        Assert.assertFalse(integers1.corresponds(integers2, Predicates2.alwaysTrue()));
        Assert.assertFalse(integers2.corresponds(integers1, Predicates2.alwaysTrue()));
        MutableList<Integer> integers3 = this.newWith(2, 3, 3, 4, 4, 4, 5, 5, 5, 5);
        Assert.assertTrue(integers1.corresponds(integers3, Predicates2.lessThan()));
        Assert.assertFalse(integers1.corresponds(integers3, Predicates2.greaterThan()));
        MutableList<Integer> nonRandomAccess = ListAdapter.adapt(new LinkedList(integers3));
        Assert.assertTrue(integers1.corresponds(nonRandomAccess, Predicates2.lessThan()));
        Assert.assertFalse(integers1.corresponds(nonRandomAccess, Predicates2.greaterThan()));
        Assert.assertTrue(nonRandomAccess.corresponds(integers1, Predicates2.greaterThan()));
        Assert.assertFalse(nonRandomAccess.corresponds(integers1, Predicates2.lessThan()));
        MutableList<String> nullBlanks = this.newWith(null, "", " ", null);
        Assert.assertTrue(nullBlanks.corresponds(FastList.newListWith(null, "", " ", null), Comparators::nullSafeEquals));
        Assert.assertFalse(nullBlanks.corresponds(FastList.newListWith("", null, " ", ""), Comparators::nullSafeEquals));
    }

    @Test
    public void forEachFromTo() {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> collection = FastList.newListWith(1, 2, 3, 4);
        collection.forEach(2, 3, result::add);
        Assert.assertEquals(this.newWith(3, 4), result);
        MutableList<Integer> result2 = FastList.newList();
        collection.forEach(3, 2, CollectionAddProcedure.on(result2));
        Assert.assertEquals(this.newWith(4, 3), result2);
        MutableList<Integer> result3 = FastList.newList();
        collection.forEach(0, 3, CollectionAddProcedure.on(result3));
        Assert.assertEquals(this.newWith(1, 2, 3, 4), result3);
        MutableList<Integer> result4 = FastList.newList();
        collection.forEach(3, 0, CollectionAddProcedure.on(result4));
        Assert.assertEquals(this.newWith(4, 3, 2, 1), result4);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> collection.forEach((-1), 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> collection.forEach(0, (-1), result::add));
    }

    @Test
    public void forEachFromToInReverse() {
        MutableList<Integer> result = mutable.of();
        this.newWith(1, 2, 3, 4).forEach(3, 2, result::add);
        Assert.assertEquals(FastList.newListWith(4, 3), result);
    }

    @Test
    public void reverseForEach() {
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.reverseForEach(result::add);
        Assert.assertEquals(FastList.newListWith(4, 3, 2, 1), result);
    }

    @Test
    public void reverseForEach_emptyList() {
        MutableList<Integer> integers = mutable.of();
        MutableList<Integer> results = mutable.of();
        integers.reverseForEach(results::add);
        Assert.assertEquals(integers, results);
    }

    @Test
    public void reverseThis() {
        MutableList<Integer> original = this.newWith(1, 2, 3, 4);
        MutableList<Integer> reversed = original.reverseThis();
        Assert.assertEquals(FastList.newListWith(4, 3, 2, 1), reversed);
        Assert.assertSame(original, reversed);
    }

    @Test
    public void toReversed() {
        MutableList<Integer> original = this.newWith(1, 2, 3, 4);
        MutableList<Integer> actual = original.toReversed();
        MutableList<Integer> expected = this.newWith(4, 3, 2, 1);
        Assert.assertEquals(expected, actual);
        Assert.assertNotSame(original, actual);
    }

    @Test
    public void distinct() {
        ListIterable<Integer> list = this.newWith(1, 4, 3, 2, 1, 4, 1);
        ListIterable<Integer> actual = list.distinct();
        Verify.assertListsEqual(FastList.newListWith(1, 4, 3, 2), actual.toList());
    }

    @Test
    public void distinctWithHashingStrategy() {
        ListIterable<String> list = this.newWith("a", "A", "b", "C", "b", "D", "E", "e");
        ListIterable<String> actual = list.distinct(HashingStrategies.fromFunction(String::toLowerCase));
        Verify.assertListsEqual(FastList.newListWith("a", "b", "C", "D", "E"), actual.toList());
    }

    @Override
    @Test
    public void removeIf() {
        MutableCollection<Integer> objects = this.newWith(1, 2, 3, null);
        objects.removeIf(Predicates.isNull());
        Assert.assertEquals(FastList.newListWith(1, 2, 3), objects);
    }

    @Test
    public void removeIndex() {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.remove(2);
        Assert.assertEquals(FastList.newListWith(1, 2), objects);
    }

    @Test
    public void indexOf() {
        MutableList<Integer> objects = this.newWith(1, 2, 2);
        Assert.assertEquals(1, objects.indexOf(2));
        Assert.assertEquals(0, objects.indexOf(1));
        Assert.assertEquals((-1), objects.indexOf(3));
    }

    @Test
    public void lastIndexOf() {
        MutableList<Integer> objects = this.newWith(2, 2, 3);
        Assert.assertEquals(1, objects.lastIndexOf(2));
        Assert.assertEquals(2, objects.lastIndexOf(3));
        Assert.assertEquals((-1), objects.lastIndexOf(1));
    }

    @Test
    public void set() {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Assert.assertEquals(Integer.valueOf(2), objects.set(1, 4));
        Assert.assertEquals(FastList.newListWith(1, 4, 3), objects);
    }

    @Test
    public void addAtIndex() {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.add(0, 0);
        Assert.assertEquals(FastList.newListWith(0, 1, 2, 3), objects);
    }

    @Test
    public void addAllAtIndex() {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.addAll(0, fixedSize.of(0));
        Integer one = -1;
        objects.addAll(0, new ArrayList(fixedSize.of(one)));
        objects.addAll(0, FastList.newListWith((-2)));
        objects.addAll(0, UnifiedSet.newSetWith((-3)));
        Assert.assertEquals(FastList.newListWith((-3), (-2), (-1), 0, 1, 2, 3), objects);
    }

    @Test
    public void withMethods() {
        Verify.assertContainsAll(this.newWith().with(1), 1);
        Verify.assertContainsAll(this.newWith(1), 1);
        Verify.assertContainsAll(this.newWith(1).with(2), 1, 2);
    }

    @Test
    public void sortThis_with_null() {
        MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1);
        Verify.assertStartsWith(integers.sortThis(Comparators.safeNullsLow(Integer::compareTo)), null, 1, 2, 3, 4);
    }

    @Test
    public void sortThis_small() {
        MutableList<Integer> actual = this.newWith(1, 2, 3).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis();
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith(1, 2, 3), actual);
    }

    @Test
    public void sortThis() {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis();
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), actual);
    }

    @Test
    public void sortThis_large() {
        MutableList<Integer> actual = this.newWith(Interval.oneTo(1000).toArray()).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis();
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(Interval.oneTo(1000).toList(), actual);
    }

    @Test
    public void sortThis_with_comparator_small() {
        MutableList<Integer> actual = this.newWith(1, 2, 3).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis(Collections.<Integer>reverseOrder());
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith(3, 2, 1), actual);
    }

    @Test
    public void sortThis_with_comparator() {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis(Collections.<Integer>reverseOrder());
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), actual);
    }

    @Test
    public void sortThis_with_comparator_large() {
        MutableList<Integer> actual = this.newWith(Interval.oneTo(1000).toArray()).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis(Collections.<Integer>reverseOrder());
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(Interval.fromToBy(1000, 1, (-1)).toList(), actual);
    }

    @Test
    public void sortThisBy() {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffleThis();
        MutableList<Integer> sorted = actual.sortThisBy(String::valueOf);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith(1, 10, 2, 3, 4, 5, 6, 7, 8, 9), actual);
    }

    @Test
    public void sortThisByBoolean() {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        MutableList<Integer> sorted = actual.sortThisByBoolean(( i) -> (i % 2) == 0);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith(1, 3, 5, 7, 9, 2, 4, 6, 8, 10), actual);
    }

    @Test
    public void sortThisByInt() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByInt(Integer::parseInt);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByChar() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9").shuffleThis();
        MutableList<String> sorted = actual.sortThisByChar(( s) -> s.charAt(0));
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9"), actual);
    }

    @Test
    public void sortThisByByte() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByByte(Byte::parseByte);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByShort() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByShort(Short::parseShort);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByFloat() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByFloat(Float::parseFloat);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByLong() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByLong(Long::parseLong);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByDouble() {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByDouble(Double::parseDouble);
        Assert.assertSame(actual, sorted);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Override
    @Test
    public void newEmpty() {
        Verify.assertInstanceOf(MutableList.class, this.newWith().newEmpty());
    }

    @Override
    @Test
    public void testToString() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        list.add(list);
        Assert.assertEquals((("[1, 2, 3, (this " + (list.getClass().getSimpleName())) + ")]"), list.toString());
    }

    @Override
    @Test
    public void makeString() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        list.add(list);
        Assert.assertEquals((("1, 2, 3, (this " + (list.getClass().getSimpleName())) + ')'), list.makeString());
    }

    @Override
    @Test
    public void makeStringWithSeparator() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        Assert.assertEquals("1/2/3", list.makeString("/"));
    }

    @Override
    @Test
    public void makeStringWithSeparatorAndStartAndEnd() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        Assert.assertEquals("[1/2/3]", list.makeString("[", "/", "]"));
    }

    @Override
    @Test
    public void appendString() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        list.add(list);
        Appendable builder = new StringBuilder();
        list.appendString(builder);
        Assert.assertEquals((("1, 2, 3, (this " + (list.getClass().getSimpleName())) + ')'), builder.toString());
    }

    @Override
    @Test
    public void appendStringWithSeparator() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        list.appendString(builder, "/");
        Assert.assertEquals("1/2/3", builder.toString());
    }

    @Override
    @Test
    public void appendStringWithSeparatorAndStartAndEnd() {
        MutableList<Object> list = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        list.appendString(builder, "[", "/", "]");
        Assert.assertEquals("[1/2/3]", builder.toString());
    }

    @Test
    public void forEachWithIndexWithFromTo() {
        MutableList<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(5, 7, ( each, index) -> builder.append(each).append(index));
        Assert.assertEquals("353627", builder.toString());
        StringBuilder builder2 = new StringBuilder();
        integers.forEachWithIndex(5, 5, ( each, index) -> builder2.append(each).append(index));
        Assert.assertEquals("35", builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        integers.forEachWithIndex(0, 9, ( each, index) -> builder3.append(each).append(index));
        Assert.assertEquals("40414243343536272819", builder3.toString());
        StringBuilder builder4 = new StringBuilder();
        integers.forEachWithIndex(7, 5, ( each, index) -> builder4.append(each).append(index));
        Assert.assertEquals("273635", builder4.toString());
        StringBuilder builder5 = new StringBuilder();
        integers.forEachWithIndex(9, 0, ( each, index) -> builder5.append(each).append(index));
        Assert.assertEquals("19282736353443424140", builder5.toString());
        MutableList<Integer> result = mutable.of();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex((-1), 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, (-1), new AddToList(result)));
    }

    @Test
    public void forEachWithIndexWithFromToInReverse() {
        MutableList<Integer> result = mutable.of();
        this.newWith(1, 2, 3).forEachWithIndex(2, 1, new AddToList(result));
        Assert.assertEquals(FastList.newListWith(3, 2), result);
    }

    @Test(expected = NullPointerException.class)
    public void sortThisWithNullWithNoComparator() {
        MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1);
        integers.sortThis();
    }

    @Test(expected = NullPointerException.class)
    public void sortThisWithNullWithNoComparatorOnListWithMoreThan10Elements() {
        MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1, 5, 6, 7, 8, 9, 10, 11);
        integers.sortThis();
    }

    @Test(expected = NullPointerException.class)
    public void toSortedListWithNullWithNoComparator() {
        MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1);
        integers.toSortedList();
    }

    @Test(expected = NullPointerException.class)
    public void toSortedListWithNullWithNoComparatorOnListWithMoreThan10Elements() {
        MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1, 5, 6, 7, 8, 9, 10, 11);
        integers.toSortedList();
    }

    @Test
    public void forEachOnRange() {
        MutableList<Integer> list = this.newWith();
        list.addAll(FastList.newListWith(0, 1, 2, 3));
        list.addAll(FastList.newListWith(4, 5, 6));
        list.addAll(FastList.<Integer>newList());
        list.addAll(FastList.newListWith(7, 8, 9));
        this.validateForEachOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.validateForEachOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    @Test
    public void forEachWithIndexOnRange() {
        MutableList<Integer> list = this.newWith();
        list.addAll(FastList.newListWith(0, 1, 2, 3));
        list.addAll(FastList.newListWith(4, 5, 6));
        list.addAll(FastList.<Integer>newList());
        list.addAll(FastList.newListWith(7, 8, 9));
        this.validateForEachWithIndexOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachWithIndexOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachWithIndexOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachWithIndexOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachWithIndexOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.validateForEachWithIndexOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    @Test
    public void subList() {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(1, 3);
        Verify.assertPostSerializedEqualsAndHashCode(sublist);
        Verify.assertSize(2, sublist);
        Verify.assertContainsAll(sublist, "B", "C");
        sublist.add("X");
        Verify.assertSize(3, sublist);
        Verify.assertContainsAll(sublist, "B", "C", "X");
        Verify.assertSize(5, list);
        Verify.assertContainsAll(list, "A", "B", "C", "X", "D");
        sublist.remove("X");
        Verify.assertContainsAll(sublist, "B", "C");
        Verify.assertContainsAll(list, "A", "B", "C", "D");
        Assert.assertEquals("C", sublist.set(1, "R"));
        Verify.assertContainsAll(sublist, "B", "R");
        Verify.assertContainsAll(list, "A", "B", "R", "D");
        sublist.addAll(Arrays.asList("W", "G"));
        Verify.assertContainsAll(sublist, "B", "R", "W", "G");
        Verify.assertContainsAll(list, "A", "B", "R", "W", "G", "D");
        sublist.clear();
        Verify.assertEmpty(sublist);
        Assert.assertFalse(sublist.remove("X"));
        Verify.assertEmpty(sublist);
        Verify.assertContainsAll(list, "A", "D");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void subListFromOutOfBoundsException() {
        this.newWith(1).subList((-1), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void subListToGreaterThanSizeException() {
        this.newWith(1).subList(0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subListFromGreaterThanToException() {
        this.newWith(1).subList(1, 0);
    }

    @Test
    public void getWithIndexOutOfBoundsException() {
        Object item = new Object();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.newWith(item).get(1));
    }

    @Test
    public void getWithArrayIndexOutOfBoundsException() {
        Object item = new Object();
        Verify.assertThrows(ArrayIndexOutOfBoundsException.class, () -> this.newWith(item).get((-1)));
    }

    @Test
    public void listIterator() {
        int sum = 0;
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        for (Iterator<Integer> iterator = integers.listIterator(); iterator.hasNext();) {
            Integer each = iterator.next();
            sum += each.intValue();
        }
        for (ListIterator<Integer> iterator = integers.listIterator(4); iterator.hasPrevious();) {
            Integer each = iterator.previous();
            sum += each.intValue();
        }
        Assert.assertEquals(20, sum);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void listIteratorIndexTooSmall() {
        this.newWith(1).listIterator((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void listIteratorIndexTooBig() {
        this.newWith(1).listIterator(2);
    }

    @Override
    @Test
    public void chunk() {
        super.chunk();
        MutableCollection<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups = collection.chunk(2);
        Assert.assertEquals(FastList.<RichIterable<String>>newListWith(FastList.newListWith("1", "2"), FastList.newListWith("3", "4"), FastList.newListWith("5", "6"), FastList.newListWith("7")), groups);
    }

    @Test
    public void toStack() {
        MutableStack<Integer> stack = this.newWith(1, 2, 3, 4).toStack();
        Assert.assertEquals(Stacks.mutable.of(1, 2, 3, 4), stack);
    }

    @Test
    public void take() {
        MutableList<Integer> mutableList = this.newWith(1, 2, 3, 4, 5);
        Assert.assertEquals(Iterables.iList(), mutableList.take(0));
        Assert.assertEquals(Iterables.iList(1, 2, 3), mutableList.take(3));
        Assert.assertEquals(Iterables.iList(1, 2, 3, 4), mutableList.take(((mutableList.size()) - 1)));
        ImmutableList<Integer> expectedList = Iterables.iList(1, 2, 3, 4, 5);
        Assert.assertEquals(expectedList, mutableList.take(mutableList.size()));
        Assert.assertEquals(expectedList, mutableList.take(10));
        Assert.assertEquals(expectedList, mutableList.take(Integer.MAX_VALUE));
        Assert.assertNotSame(mutableList, mutableList.take(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_throws() {
        this.newWith(1, 2, 3, 4, 5).take((-1));
    }

    @Test
    public void takeWhile() {
        Assert.assertEquals(Iterables.iList(1, 2, 3), this.newWith(1, 2, 3, 4, 5).takeWhile(Predicates.lessThan(4)));
        Assert.assertEquals(Iterables.iList(1, 2, 3, 4, 5), this.newWith(1, 2, 3, 4, 5).takeWhile(Predicates.lessThan(10)));
        Assert.assertEquals(Iterables.iList(), this.newWith(1, 2, 3, 4, 5).takeWhile(Predicates.lessThan(0)));
    }

    @Test
    public void drop() {
        MutableList<Integer> mutableList = this.newWith(1, 2, 3, 4, 5);
        Assert.assertEquals(Iterables.iList(1, 2, 3, 4, 5), mutableList.drop(0));
        Assert.assertNotSame(mutableList, mutableList.drop(0));
        Assert.assertEquals(Iterables.iList(4, 5), mutableList.drop(3));
        Assert.assertEquals(Iterables.iList(5), mutableList.drop(((mutableList.size()) - 1)));
        Assert.assertEquals(Iterables.iList(), mutableList.drop(mutableList.size()));
        Assert.assertEquals(Iterables.iList(), mutableList.drop(10));
        Assert.assertEquals(Iterables.iList(), mutableList.drop(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_throws() {
        this.newWith(1, 2, 3, 4, 5).drop((-1));
    }

    @Test
    public void dropWhile() {
        Assert.assertEquals(Iterables.iList(4, 5), this.newWith(1, 2, 3, 4, 5).dropWhile(Predicates.lessThan(4)));
        Assert.assertEquals(Iterables.iList(), this.newWith(1, 2, 3, 4, 5).dropWhile(Predicates.lessThan(10)));
        Assert.assertEquals(Iterables.iList(1, 2, 3, 4, 5), this.newWith(1, 2, 3, 4, 5).dropWhile(Predicates.lessThan(0)));
    }

    @Test
    public void partitionWhile() {
        PartitionMutableList<Integer> partition1 = this.newWith(1, 2, 3, 4, 5).partitionWhile(Predicates.lessThan(4));
        Assert.assertEquals(Iterables.iList(1, 2, 3), partition1.getSelected());
        Assert.assertEquals(Iterables.iList(4, 5), partition1.getRejected());
        PartitionMutableList<Integer> partition2 = this.newWith(1, 2, 3, 4, 5).partitionWhile(Predicates.lessThan(0));
        Assert.assertEquals(Iterables.iList(), partition2.getSelected());
        Assert.assertEquals(Iterables.iList(1, 2, 3, 4, 5), partition2.getRejected());
        PartitionMutableList<Integer> partition3 = this.newWith(1, 2, 3, 4, 5).partitionWhile(Predicates.lessThan(10));
        Assert.assertEquals(Iterables.iList(1, 2, 3, 4, 5), partition3.getSelected());
        Assert.assertEquals(Iterables.iList(), partition3.getRejected());
    }

    @Test
    public void asReversed() {
        Verify.assertInstanceOf(ReverseIterable.class, this.newWith().asReversed());
        Verify.assertIterablesEqual(Iterables.iList(4, 3, 2, 1), this.newWith(1, 2, 3, 4).asReversed());
    }

    @Test
    public void binarySearch() {
        MutableList<Integer> sortedList = this.newWith(1, 2, 3, 4, 5, 7).toList();
        Assert.assertEquals(1, sortedList.binarySearch(2));
        Assert.assertEquals((-6), sortedList.binarySearch(6));
        for (Integer integer : sortedList) {
            Assert.assertEquals(Collections.binarySearch(sortedList, integer), sortedList.binarySearch(integer));
        }
    }

    @Test
    public void binarySearchWithComparator() {
        MutableList<Integer> sortedList = this.newWith(1, 2, 3, 4, 5, 7).toSortedList(Comparators.reverseNaturalOrder());
        Assert.assertEquals(4, sortedList.binarySearch(2, Comparators.reverseNaturalOrder()));
        Assert.assertEquals((-2), sortedList.binarySearch(6, Comparators.reverseNaturalOrder()));
        for (Integer integer : sortedList) {
            Assert.assertEquals(Collections.binarySearch(sortedList, integer, Comparators.reverseNaturalOrder()), sortedList.binarySearch(integer, Comparators.reverseNaturalOrder()));
        }
    }
}

