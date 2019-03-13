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
package com.gs.collections.impl.list.fixed;


import AddFunction.INTEGER;
import Lists.fixedSize;
import Lists.mutable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.SynchronizedMutableList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link SingletonList}.
 */
public class SingletonListTest extends AbstractMemoryEfficientMutableListTestCase {
    @Test
    public void equalsAndHashCode() {
        Verify.assertEqualsAndHashCode(this.list, FastList.newList(this.list));
        Verify.assertPostSerializedEqualsAndHashCode(this.list);
    }

    @Test
    public void asSynchronized() {
        Verify.assertInstanceOf(SynchronizedMutableList.class, this.list.asSynchronized());
    }

    @Test
    public void testClone() {
        MutableList<String> clone = this.list.clone();
        Verify.assertEqualsAndHashCode(this.list, clone);
        Verify.assertInstanceOf(SingletonList.class, clone);
    }

    @Test
    public void contains() {
        Assert.assertTrue(this.list.contains("1"));
        Assert.assertFalse(this.list.contains("2"));
    }

    @Test
    public void remove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.remove(0));
    }

    @Test
    public void addAtIndex() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.add(0, "1"));
    }

    @Test
    public void add() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.add("1"));
    }

    @Test
    public void addingAllToOtherList() {
        MutableList<String> newList = FastList.newList(this.list);
        newList.add("2");
        Verify.assertItemAtIndex("1", 0, newList);
        Verify.assertItemAtIndex("2", 1, newList);
    }

    @Test
    public void get() {
        Verify.assertItemAtIndex("1", 0, this.list);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(1));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get((-1)));
    }

    @Test
    public void tap() {
        MutableList<Integer> tapResult = mutable.of();
        MutableList<Integer> collection = SingletonListTest.newWith(1);
        Assert.assertSame(collection, collection.tap(tapResult::add));
        Assert.assertEquals(collection.toList(), tapResult);
    }

    @Test
    public void forEach() {
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> collection = SingletonListTest.newWith(1);
        collection.forEach(CollectionAddProcedure.on(result));
        Assert.assertEquals(FastList.newListWith(1), result);
    }

    @Test
    public void forEachWith() {
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> collection = SingletonListTest.newWith(1);
        collection.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 0);
        Assert.assertEquals(FastList.newListWith(1), result);
    }

    @Test
    public void forEachWithIndex() {
        MutableList<Integer> result = mutable.of();
        MutableList<Integer> collection = SingletonListTest.newWith(1);
        collection.forEachWithIndex(( object, index) -> result.add((object + index)));
        Verify.assertContainsAll(result, 1);
    }

    @Test
    public void set() {
        Assert.assertEquals("1", this.list.set(0, "2"));
        Assert.assertEquals(FastList.newListWith("2"), this.list);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.list.set(1, "2"));
    }

    @Test
    public void select() {
        Verify.assertContainsAll(SingletonListTest.newWith(1).select(Predicates.lessThan(3)), 1);
        Verify.assertEmpty(SingletonListTest.newWith(1).select(Predicates.greaterThan(3)));
    }

    @Test
    public void selectWith() {
        Verify.assertContainsAll(SingletonListTest.newWith(1).selectWith(Predicates2.<Integer>lessThan(), 3), 1);
        Verify.assertEmpty(SingletonListTest.newWith(1).selectWith(Predicates2.<Integer>greaterThan(), 3));
    }

    @Test
    public void reject() {
        Verify.assertEmpty(SingletonListTest.newWith(1).reject(Predicates.lessThan(3)));
        Verify.assertContainsAll(SingletonListTest.newWith(1).reject(Predicates.greaterThan(3), UnifiedSet.<Integer>newSet()), 1);
    }

    @Test
    public void rejectWith() {
        Verify.assertEmpty(SingletonListTest.newWith(1).rejectWith(Predicates2.<Integer>lessThan(), 3));
        Verify.assertContainsAll(SingletonListTest.newWith(1).rejectWith(Predicates2.<Integer>greaterThan(), 3, UnifiedSet.<Integer>newSet()), 1);
    }

    @Test
    public void collect() {
        Verify.assertContainsAll(SingletonListTest.newWith(1).collect(String::valueOf), "1");
        Verify.assertContainsAll(SingletonListTest.newWith(1).collect(String::valueOf, UnifiedSet.<String>newSet()), "1");
    }

    @Test
    public void flatCollect() {
        Function<Integer, MutableSet<String>> function = ( object) -> UnifiedSet.newSetWith(object.toString());
        Verify.assertListsEqual(FastList.newListWith("1"), SingletonListTest.newWith(1).flatCollect(function));
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1"), SingletonListTest.newWith(1).flatCollect(function, UnifiedSet.<String>newSet()));
    }

    @Test
    public void detect() {
        Assert.assertEquals(Integer.valueOf(1), SingletonListTest.newWith(1).detect(Integer.valueOf(1)::equals));
        Assert.assertNull(SingletonListTest.newWith(1).detect(Integer.valueOf(6)::equals));
    }

    @Test
    public void detectWith() {
        Assert.assertEquals(Integer.valueOf(1), SingletonListTest.newWith(1).detectWith(Object::equals, 1));
        Assert.assertNull(SingletonListTest.newWith(1).detectWith(Object::equals, 6));
    }

    @Test
    public void detectIfNone() {
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(6);
        Assert.assertEquals(Integer.valueOf(1), SingletonListTest.newWith(1).detectIfNone(Integer.valueOf(1)::equals, function));
        Assert.assertEquals(Integer.valueOf(6), SingletonListTest.newWith(1).detectIfNone(Integer.valueOf(6)::equals, function));
    }

    @Test
    public void detectWithIfNone() {
        Function0<Integer> function = new com.gs.collections.impl.block.function.PassThruFunction0(6);
        Assert.assertEquals(Integer.valueOf(1), SingletonListTest.newWith(1).detectWithIfNone(Object::equals, 1, function));
        Assert.assertEquals(Integer.valueOf(6), SingletonListTest.newWith(1).detectWithIfNone(Object::equals, 6, function));
    }

    @Test
    public void allSatisfy() {
        Assert.assertTrue(SingletonListTest.newWith(1).allSatisfy(Integer.class::isInstance));
        Assert.assertFalse(SingletonListTest.newWith(1).allSatisfy(Integer.valueOf(2)::equals));
    }

    @Test
    public void allSatisfyWith() {
        Assert.assertTrue(SingletonListTest.newWith(1).allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(SingletonListTest.newWith(1).allSatisfyWith(Object::equals, 2));
    }

    @Test
    public void anySatisfy() {
        Assert.assertFalse(SingletonListTest.newWith(1).anySatisfy(String.class::isInstance));
        Assert.assertTrue(SingletonListTest.newWith(1).anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void anySatisfyWith() {
        Assert.assertFalse(SingletonListTest.newWith(1).anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertTrue(SingletonListTest.newWith(1).anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void noneSatisfy() {
        Assert.assertTrue(SingletonListTest.newWith(1).noneSatisfy(String.class::isInstance));
        Assert.assertFalse(SingletonListTest.newWith(1).noneSatisfy(Integer.valueOf(1)::equals));
    }

    @Test
    public void noneSatisfyWith() {
        Assert.assertTrue(SingletonListTest.newWith(1).noneSatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertFalse(SingletonListTest.newWith(1).noneSatisfyWith(Object::equals, 1));
    }

    @Test
    public void count() {
        Assert.assertEquals(1, SingletonListTest.newWith(1).count(Integer.class::isInstance));
        Assert.assertEquals(0, SingletonListTest.newWith(1).count(String.class::isInstance));
    }

    @Test
    public void countWith() {
        Assert.assertEquals(1, SingletonListTest.newWith(1).countWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertEquals(0, SingletonListTest.newWith(1).countWith(Predicates2.instanceOf(), String.class));
    }

    @Test
    public void collectIf() {
        Verify.assertContainsAll(SingletonListTest.newWith(1).collectIf(Integer.class::isInstance, String::valueOf), "1");
        Verify.assertContainsAll(SingletonListTest.newWith(1).collectIf(Integer.class::isInstance, String::valueOf, FastList.<String>newList()), "1");
    }

    @Test
    public void collectWith() {
        Assert.assertEquals(FastList.newListWith(2), SingletonListTest.newWith(1).collectWith(INTEGER, 1));
        Assert.assertEquals(FastList.newListWith(2), SingletonListTest.newWith(1).collectWith(INTEGER, 1, FastList.<Integer>newList()));
    }

    @Test
    public void getFirst() {
        Assert.assertEquals(Integer.valueOf(1), SingletonListTest.newWith(1).getFirst());
    }

    @Test
    public void getLast() {
        Assert.assertEquals(Integer.valueOf(1), SingletonListTest.newWith(1).getLast());
    }

    @Test
    public void isEmpty() {
        Verify.assertNotEmpty(SingletonListTest.newWith(1));
        Assert.assertTrue(SingletonListTest.newWith(1).notEmpty());
    }

    @Test
    public void removeAll() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Verify.assertThrows(UnsupportedOperationException.class, () -> objects.removeAll(Lists.fixedSize.of(1, 2)));
    }

    @Test
    public void retainAll() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Verify.assertThrows(UnsupportedOperationException.class, () -> objects.retainAll(SingletonListTest.newWith(2)));
    }

    @Test
    public void clear() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Verify.assertThrows(UnsupportedOperationException.class, objects::clear);
    }

    @Test
    public void iterator() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); (i--) > 0;) {
            Integer integer = iterator.next();
            Assert.assertEquals(1, ((integer.intValue()) + i));
        }
    }

    @Test
    public void injectInto() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Integer result = objects.injectInto(1, INTEGER);
        Assert.assertEquals(Integer.valueOf(2), result);
    }

    @Test
    public void injectIntoWith() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Integer result = objects.injectIntoWith(1, ( injectedValued, item, parameter) -> (injectedValued + item) + parameter, 0);
        Assert.assertEquals(Integer.valueOf(2), result);
    }

    @Test
    public void toArray() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Object[] array = objects.toArray();
        Verify.assertSize(1, array);
        Integer[] array2 = objects.toArray(new Integer[1]);
        Verify.assertSize(1, array2);
    }

    @Test
    public void selectAndRejectWith() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Twin<MutableList<Integer>> result = objects.selectAndRejectWith(Object::equals, 1);
        Verify.assertSize(1, result.getOne());
        Verify.assertEmpty(result.getTwo());
    }

    @Test
    public void removeIf() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Verify.assertThrows(UnsupportedOperationException.class, () -> objects.removeIf(Predicates.isNull()));
    }

    @Test
    public void removeIfWith() {
        MutableList<Integer> objects = SingletonListTest.newWith(1);
        Verify.assertThrows(UnsupportedOperationException.class, () -> objects.removeIfWith(Predicates2.isNull(), null));
    }

    @Test
    public void toList() {
        MutableList<Integer> list = SingletonListTest.newWith(1).toList();
        list.add(2);
        list.add(3);
        list.add(4);
        Verify.assertContainsAll(list, 1, 2, 3, 4);
    }

    @Test
    public void toSortedList() {
        MutableList<Integer> integers = SingletonListTest.newWith(1);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Verify.assertStartsWith(list, 1);
        Assert.assertNotSame(integers, list);
        MutableList<Integer> list2 = integers.toSortedList();
        Verify.assertStartsWith(list2, 1);
        Assert.assertNotSame(integers, list2);
    }

    @Test
    public void toSortedListBy() {
        MutableList<Integer> integers = SingletonListTest.newWith(1);
        MutableList<Integer> list = integers.toSortedListBy(Functions.getIntegerPassThru());
        Assert.assertEquals(FastList.newListWith(1), list);
        Assert.assertNotSame(integers, list);
    }

    @Test
    public void toSet() {
        MutableList<Integer> integers = SingletonListTest.newWith(1);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1);
    }

    @Test
    public void toMap() {
        MutableList<Integer> integers = SingletonListTest.newWith(1);
        MutableMap<Integer, Integer> map = integers.toMap(Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertContainsAll(map.keySet(), 1);
        Verify.assertContainsAll(map.values(), 1);
    }

    @Test
    public void forLoop() {
        MutableList<String> list = SingletonListTest.newWith("one");
        MutableList<String> upperList = SingletonListTest.newWith("ONE");
        for (String each : list) {
            Verify.assertContains(each.toUpperCase(), upperList);
        }
    }

    @Test
    public void subList() {
        MutableList<String> list = SingletonListTest.newWith("one");
        MutableList<String> subList = list.subList(0, 1);
        MutableList<String> upperList = SingletonListTest.newWith("ONE");
        for (String each : subList) {
            Verify.assertContains(each.toUpperCase(), upperList);
        }
        Assert.assertEquals("one", subList.getFirst());
        Assert.assertEquals("one", subList.getLast());
    }

    @Test
    public void testToString() {
        MutableList<MutableList<?>> list = fixedSize.<MutableList<?>>of(fixedSize.of());
        list.set(0, list);
        Assert.assertEquals("[(this SingletonList)]", list.toString());
    }

    @Test
    public void min_null_throws() {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.classUnderTestWithNull().min(Integer::compareTo);
    }

    @Test
    public void max_null_throws() {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.classUnderTestWithNull().max(Integer::compareTo);
    }

    @Test
    public void min() {
        Assert.assertEquals(Integer.valueOf(1), this.newList().min(Integer::compareTo));
    }

    @Test
    public void max() {
        Assert.assertEquals(Integer.valueOf(1), this.newList().max(Comparators.reverse(Integer::compareTo)));
    }

    @Test
    public void min_null_throws_without_comparator() {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.classUnderTestWithNull().min();
    }

    @Test
    public void max_null_throws_without_comparator() {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.classUnderTestWithNull().max();
    }

    @Test
    public void min_without_comparator() {
        Assert.assertEquals(Integer.valueOf(1), this.newList().min());
    }

    @Test
    public void max_without_comparator() {
        Assert.assertEquals(Integer.valueOf(this.newList().size()), this.newList().max());
    }

    @Test
    public void minBy() {
        Assert.assertEquals(Integer.valueOf(1), this.newList().minBy(String::valueOf));
    }

    @Test
    public void maxBy() {
        Assert.assertEquals(Integer.valueOf(1), this.newList().maxBy(String::valueOf));
    }

    @Test
    public void without() {
        MutableList<Integer> list = new SingletonList(2);
        Assert.assertSame(list, list.without(9));
        list = list.without(2);
        Verify.assertListsEqual(mutable.of(), list);
        Verify.assertInstanceOf(EmptyList.class, list);
    }
}

