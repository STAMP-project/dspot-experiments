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
package com.gs.collections.impl.utility;


import AddFunction.DOUBLE;
import AddFunction.DOUBLE_TO_DOUBLE;
import AddFunction.FLOAT;
import AddFunction.FLOAT_TO_FLOAT;
import AddFunction.INTEGER;
import AddFunction.INTEGER_TO_INT;
import AddFunction.INTEGER_TO_LONG;
import AddFunction.STRING;
import Lists.fixedSize;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.ObjectIntProcedures;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.MaxSizeFunction;
import com.gs.collections.impl.block.function.MinSizeFunction;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Iterables;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ListIterateTest {
    @Test
    public void injectInto() {
        MutableList<Integer> list = fixedSize.of(1, 2, 3);
        List<Integer> linked = new LinkedList(list);
        Assert.assertEquals(Integer.valueOf(7), ListIterate.injectInto(1, list, INTEGER));
        Assert.assertEquals(Integer.valueOf(7), ListIterate.injectInto(1, linked, INTEGER));
    }

    @Test
    public void toArray() {
        LinkedList<Integer> notAnArrayList = new LinkedList(Interval.oneTo(10));
        Integer[] target1 = new Integer[]{ 1, 2, null, null };
        ListIterate.toArray(notAnArrayList, target1, 2, 2);
        Assert.assertArrayEquals(target1, new Integer[]{ 1, 2, 1, 2 });
        ArrayList<Integer> arrayList = new ArrayList(Interval.oneTo(10));
        Integer[] target2 = new Integer[]{ 1, 2, null, null };
        ListIterate.toArray(arrayList, target2, 2, 2);
        Assert.assertArrayEquals(target2, new Integer[]{ 1, 2, 1, 2 });
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void toArray_throws() {
        LinkedList<Integer> notAnArrayList = new LinkedList(Interval.oneTo(10));
        Integer[] target1 = new Integer[]{ 1, 2, null, null };
        ListIterate.toArray(notAnArrayList, target1, 2, 10);
    }

    @Test
    public void detectIndexWith() {
        List<Integer> list = new LinkedList(Interval.fromTo(5, 1));
        Assert.assertEquals(4, Iterate.detectIndexWith(list, Object::equals, 1));
        Assert.assertEquals(0, Iterate.detectIndexWith(list, Object::equals, 5));
        Assert.assertEquals((-1), Iterate.detectIndexWith(Iterables.iList(), Object::equals, 5));
        Assert.assertEquals((-1), Iterate.detectIndexWith(Iterables.iSet(), Object::equals, 5));
    }

    @Test
    public void forEachWith() {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> list = FastList.newListWith(1, 2, 3, 4);
        ListIterate.forEachWith(list, ( argument1, argument2) -> result.add((argument1 + argument2)), 1);
        Assert.assertEquals(FastList.newListWith(2, 3, 4, 5), result);
        List<Integer> result2 = new LinkedList<>();
        List<Integer> linkedList = new LinkedList(FastList.newListWith(1, 2, 3, 4));
        ListIterate.forEachWith(linkedList, ( argument1, argument2) -> result2.add((argument1 + argument2)), 1);
        Assert.assertEquals(FastList.newListWith(2, 3, 4, 5), result2);
    }

    @Test
    public void injectIntoInt() {
        MutableList<Integer> list = fixedSize.of(1, 2, 3);
        List<Integer> linked = new LinkedList(list);
        Assert.assertEquals(7, ListIterate.injectInto(1, list, INTEGER_TO_INT));
        Assert.assertEquals(7, ListIterate.injectInto(1, linked, INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong() {
        MutableList<Integer> list = fixedSize.of(1, 2, 3);
        List<Integer> linked = new LinkedList(list);
        Assert.assertEquals(7, ListIterate.injectInto(1, list, INTEGER_TO_LONG));
        Assert.assertEquals(7, ListIterate.injectInto(1, linked, INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble() {
        MutableList<Double> list = fixedSize.of(1.0, 2.0, 3.0);
        List<Double> linked = new LinkedList(list);
        Assert.assertEquals(7.0, ListIterate.injectInto(1.0, list, DOUBLE), 0.001);
        Assert.assertEquals(7.0, ListIterate.injectInto(1.0, linked, DOUBLE), 0.001);
        Assert.assertEquals(7.0, ListIterate.injectInto(1.0, list, DOUBLE_TO_DOUBLE), 0.001);
        Assert.assertEquals(7.0, ListIterate.injectInto(1.0, linked, DOUBLE_TO_DOUBLE), 0.001);
    }

    @Test
    public void injectIntoFloat() {
        MutableList<Float> list = fixedSize.of(1.0F, 2.0F, 3.0F);
        List<Float> linked = new LinkedList(list);
        Assert.assertEquals(7.0F, ListIterate.injectInto(1.0F, list, FLOAT), 0.001);
        Assert.assertEquals(7.0F, ListIterate.injectInto(1.0F, linked, FLOAT), 0.001);
        Assert.assertEquals(7.0F, ListIterate.injectInto(1.0F, list, FLOAT_TO_FLOAT), 0.001);
        Assert.assertEquals(7.0F, ListIterate.injectInto(1.0F, linked, FLOAT_TO_FLOAT), 0.001);
    }

    @Test
    public void injectIntoString() {
        MutableList<String> list = fixedSize.of("1", "2", "3");
        List<String> linked = new LinkedList(list);
        Assert.assertEquals("0123", ListIterate.injectInto("0", list, STRING));
        Assert.assertEquals("0123", ListIterate.injectInto("0", linked, STRING));
    }

    @Test
    public void injectIntoMaxString() {
        MutableList<String> list = fixedSize.of("1", "12", "123");
        List<String> linked = new LinkedList(list);
        Function2<Integer, String, Integer> function = MaxSizeFunction.STRING;
        Assert.assertEquals(Integer.valueOf(3), ListIterate.injectInto(Integer.MIN_VALUE, list, function));
        Assert.assertEquals(Integer.valueOf(3), ListIterate.injectInto(Integer.MIN_VALUE, linked, function));
    }

    @Test
    public void injectIntoMinString() {
        MutableList<String> list = fixedSize.of("1", "12", "123");
        List<String> linked = new LinkedList(list);
        Function2<Integer, String, Integer> function = MinSizeFunction.STRING;
        Assert.assertEquals(Integer.valueOf(1), ListIterate.injectInto(Integer.MAX_VALUE, list, function));
        Assert.assertEquals(Integer.valueOf(1), ListIterate.injectInto(Integer.MAX_VALUE, linked, function));
    }

    @Test
    public void collect() {
        MutableList<Boolean> list = fixedSize.of(true, false, null);
        List<Boolean> linked = new LinkedList(list);
        this.assertCollect(list);
        this.assertCollect(linked);
    }

    @Test
    public void flatten() {
        MutableList<MutableList<Boolean>> list = fixedSize.<MutableList<Boolean>>of(fixedSize.of(true, false), fixedSize.of(true, null));
        List<MutableList<Boolean>> linked = new LinkedList(list);
        this.assertFlatten(list);
        this.assertFlatten(linked);
    }

    @Test
    public void getFirstAndLast() {
        Assert.assertNull(ListIterate.getFirst(null));
        Assert.assertNull(ListIterate.getLast(null));
        MutableList<Boolean> list = fixedSize.of(true, null, false);
        Assert.assertEquals(Boolean.TRUE, ListIterate.getFirst(list));
        Assert.assertEquals(Boolean.FALSE, ListIterate.getLast(list));
        List<Boolean> linked = new LinkedList(list);
        Assert.assertEquals(Boolean.TRUE, ListIterate.getFirst(linked));
        Assert.assertEquals(Boolean.FALSE, ListIterate.getLast(linked));
        List<Boolean> arrayList = new ArrayList(list);
        Assert.assertEquals(Boolean.TRUE, ListIterate.getFirst(arrayList));
        Assert.assertEquals(Boolean.FALSE, ListIterate.getLast(arrayList));
    }

    @Test
    public void getFirstAndLastOnEmpty() {
        List<?> list = new ArrayList<>();
        Assert.assertNull(ListIterate.getFirst(list));
        Assert.assertNull(ListIterate.getLast(list));
        List<?> linked = new LinkedList<>();
        Assert.assertNull(ListIterate.getFirst(linked));
        Assert.assertNull(ListIterate.getLast(linked));
        List<?> synchronizedList = Collections.synchronizedList(linked);
        Assert.assertNull(ListIterate.getFirst(synchronizedList));
        Assert.assertNull(ListIterate.getLast(synchronizedList));
    }

    @Test
    public void occurrencesOfAttributeNamedOnList() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertOccurrencesOfAttributeNamedOnList(list);
        this.assertOccurrencesOfAttributeNamedOnList(new LinkedList(list));
    }

    @Test
    public void forEachWithIndex() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertForEachWithIndex(list);
        this.assertForEachWithIndex(new LinkedList(list));
    }

    @Test
    public void forEachUsingFromTo() {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.assertForEachUsingFromTo(integers);
        MutableList<Integer> reverseResults = mutable.of();
        this.assertReverseForEachUsingFromTo(integers, reverseResults, reverseResults::add);
        this.assertForEachUsingFromTo(new LinkedList(integers));
    }

    @Test
    public void forEachWithIndexUsingFromTo() {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.assertForEachWithIndexUsingFromTo(integers);
        this.assertForEachWithIndexUsingFromTo(new LinkedList(integers));
        MutableList<Integer> reverseResults = mutable.of();
        ObjectIntProcedure<Integer> objectIntProcedure = ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(reverseResults));
        this.assertReverseForEachIndexUsingFromTo(integers, reverseResults, objectIntProcedure);
    }

    @Test
    public void reverseForEach() {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        MutableList<Integer> reverseResults = mutable.of();
        ListIterate.reverseForEach(integers, CollectionAddProcedure.on(reverseResults));
        Assert.assertEquals(ListIterate.reverseThis(integers), reverseResults);
    }

    @Test
    public void reverseForEach_emptyList() {
        MutableList<Integer> integers = mutable.of();
        MutableList<Integer> results = mutable.of();
        ListIterate.reverseForEach(integers, CollectionAddProcedure.on(results));
        Assert.assertEquals(integers, results);
    }

    @Test
    public void reverseForEachWithIndex() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertReverseForEachWithIndex(list);
        this.assertReverseForEachWithIndex(new LinkedList(list));
        ListIterate.reverseForEachWithIndex(mutable.empty(), ( ignored1, index) -> Assert.fail());
    }

    @Test
    public void forEachInBoth() {
        MutableList<String> list1 = fixedSize.of("1", "2");
        MutableList<String> list2 = fixedSize.of("a", "b");
        this.assertForEachInBoth(list1, list2);
        this.assertForEachInBoth(list1, new LinkedList(list2));
        this.assertForEachInBoth(new LinkedList(list1), list2);
        this.assertForEachInBoth(new LinkedList(list1), new LinkedList(list2));
        ListIterate.forEachInBoth(null, null, ( argument1, argument2) -> Assert.fail());
    }

    @Test(expected = RuntimeException.class)
    public void forEachInBothThrowsOnDifferentLengthLists() {
        ListIterate.forEachInBoth(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2), ( argument1, argument2) -> Assert.fail());
    }

    @Test
    public void detectWith() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertDetectWith(list);
        this.assertDetectWith(new LinkedList(list));
    }

    @Test
    public void detectIfNone() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertNull(ListIterate.detectIfNone(list, Integer.valueOf(6)::equals, null));
        Assert.assertEquals(Integer.valueOf(5), ListIterate.detectIfNone(list, Integer.valueOf(5)::equals, 0));
        Assert.assertNull(ListIterate.detectIfNone(new LinkedList(list), Integer.valueOf(6)::equals, null));
    }

    @Test
    public void detectWithIfNone() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertNull(ListIterate.detectWithIfNone(list, Object::equals, 6, null));
        Assert.assertEquals(Integer.valueOf(5), ListIterate.detectWithIfNone(list, Object::equals, 5, 0));
        Assert.assertNull(ListIterate.detectWithIfNone(new LinkedList(list), Object::equals, 6, null));
    }

    @Test
    public void selectWith() {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertSize(5, ListIterate.selectWith(list, Predicates2.instanceOf(), Integer.class));
        Verify.assertSize(5, ListIterate.selectWith(new LinkedList(list), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void distinct() {
        List<Integer> list = FastList.newListWith(5, 2, 3, 5, 4, 2);
        List<Integer> expectedList = FastList.newListWith(5, 2, 3, 4);
        List<Integer> actualList = FastList.newList();
        Verify.assertListsEqual(expectedList, ListIterate.distinct(list, actualList));
        Verify.assertListsEqual(expectedList, actualList);
        actualList.clear();
        Verify.assertListsEqual(this.getIntegerList(), ListIterate.distinct(this.getIntegerList(), actualList));
        Verify.assertListsEqual(this.getIntegerList(), actualList);
    }

    @Test
    public void rejectWith() {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertEmpty(ListIterate.rejectWith(list, Predicates2.instanceOf(), Integer.class));
        Verify.assertEmpty(ListIterate.rejectWith(new LinkedList(list), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void selectAndRejectWith() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertSelectAndRejectWith(list);
        this.assertSelectAndRejectWith(new LinkedList(list));
    }

    @Test
    public void partitionWith() {
        List<Integer> list = new LinkedList(Interval.oneTo(101));
        PartitionMutableList<Integer> partition = ListIterate.partitionWith(list, Predicates2.in(), Interval.oneToBy(101, 2));
        Assert.assertEquals(Interval.oneToBy(101, 2), partition.getSelected());
        Assert.assertEquals(Interval.fromToBy(2, 100, 2), partition.getRejected());
    }

    @Test
    public void anySatisfyWith() {
        MutableList<Integer> undertest = this.getIntegerList();
        this.assertAnySatisyWith(undertest);
        this.assertAnySatisyWith(new LinkedList(undertest));
    }

    @Test
    public void allSatisfyWith() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertAllSatisfyWith(list);
        this.assertAllSatisfyWith(new LinkedList(list));
    }

    @Test
    public void noneSatisfyWith() {
        MutableList<Integer> list = this.getIntegerList();
        this.assertNoneSatisfyWith(list);
        this.assertNoneSatisfyWith(new LinkedList(list));
    }

    @Test
    public void countWith() {
        Assert.assertEquals(5, ListIterate.countWith(this.getIntegerList(), Predicates2.instanceOf(), Integer.class));
        Assert.assertEquals(5, ListIterate.countWith(new LinkedList(this.getIntegerList()), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void collectIf() {
        MutableList<Integer> integers = fixedSize.of(1, 2, 3);
        this.assertCollectIf(integers);
        this.assertCollectIf(new LinkedList(integers));
    }

    @Test
    public void reverseThis() {
        Assert.assertEquals(FastList.newListWith(2, 3, 1), ListIterate.reverseThis(fixedSize.of(1, 3, 2)));
        Assert.assertEquals(FastList.newListWith(2, 3, 1), ListIterate.reverseThis(new LinkedList(fixedSize.of(1, 3, 2))));
    }

    @Test
    public void take() {
        MutableList<Integer> integers = this.getIntegerList();
        this.assertTake(integers);
        this.assertTake(new LinkedList(integers));
        Verify.assertSize(0, ListIterate.take(fixedSize.of(), 2));
        Verify.assertSize(0, ListIterate.take(new LinkedList(), 2));
        Verify.assertSize(0, ListIterate.take(new LinkedList(), Integer.MAX_VALUE));
        Verify.assertThrows(IllegalArgumentException.class, () -> ListIterate.take(this.getIntegerList(), (-1)));
        Verify.assertThrows(IllegalArgumentException.class, () -> ListIterate.take(this.getIntegerList(), (-1), FastList.newList()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_throws() {
        ListIterate.take(this.getIntegerList(), (-1));
        ListIterate.take(this.getIntegerList(), (-1), FastList.newList());
    }

    @Test
    public void drop() {
        MutableList<Integer> integers = this.getIntegerList();
        this.assertDrop(integers);
        this.assertDrop(new LinkedList(integers));
        Verify.assertSize(0, ListIterate.drop(fixedSize.<Integer>of(), 2));
        Verify.assertSize(0, ListIterate.drop(new LinkedList(), 2));
        Verify.assertSize(0, ListIterate.drop(new LinkedList(), Integer.MAX_VALUE));
        Verify.assertThrows(IllegalArgumentException.class, () -> ListIterate.drop(FastList.newList(), (-1)));
        Verify.assertThrows(IllegalArgumentException.class, () -> ListIterate.drop(FastList.newList(), (-1), FastList.newList()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_throws() {
        ListIterate.drop(this.getIntegerList(), (-1));
        ListIterate.drop(this.getIntegerList(), (-1), FastList.newList());
    }

    @Test
    public void chunk() {
        MutableList<String> list = FastList.newListWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups = ListIterate.chunk(list, 2);
        RichIterable<Integer> sizes = groups.collect(RichIterable::size);
        Assert.assertEquals(FastList.newListWith(2, 2, 2, 1), sizes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunkWithIllegalSize() {
        ListIterate.chunk(FastList.newList(), 0);
    }

    @Test
    public void removeIfWithProcedure() {
        MutableList<Integer> list1 = FastList.newListWith(1, 2, 3, 4, 5);
        MutableList<Integer> resultList1 = mutable.of();
        List<Integer> list2 = new LinkedList(list1);
        MutableList<Integer> resultList2 = mutable.of();
        ListIterate.removeIf(list1, IntegerPredicates.isEven(), CollectionAddProcedure.on(resultList1));
        Assert.assertEquals(FastList.newListWith(1, 3, 5), list1);
        Assert.assertEquals(FastList.newListWith(2, 4), resultList1);
        ListIterate.removeIf(list2, IntegerPredicates.isEven(), CollectionAddProcedure.on(resultList2));
        Assert.assertEquals(FastList.newListWith(1, 3, 5), list2);
        Assert.assertEquals(FastList.newListWith(2, 4), resultList2);
    }

    @Test
    public void removeIf() {
        MutableList<Integer> list1 = FastList.newListWith(1, 2, 3, 4, 5);
        List<Integer> list2 = new LinkedList(list1);
        ListIterate.removeIf(list1, IntegerPredicates.isEven());
        Assert.assertEquals(FastList.newListWith(1, 3, 5), list1);
        ListIterate.removeIf(list2, IntegerPredicates.isEven());
        Assert.assertEquals(FastList.newListWith(1, 3, 5), list2);
    }

    @Test
    public void removeIfWith() {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3, 4);
        ListIterate.removeIfWith(objects, Predicates2.<Integer>lessThan(), 3);
        Assert.assertEquals(FastList.newListWith(3, 4), objects);
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(ListIterate.class);
    }
}

