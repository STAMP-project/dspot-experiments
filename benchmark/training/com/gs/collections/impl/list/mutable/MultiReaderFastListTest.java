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


import AddFunction.DOUBLE_TO_DOUBLE;
import AddFunction.INTEGER;
import AddFunction.STRING;
import Lists.fixedSize;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.MaxSizeFunction;
import com.gs.collections.impl.block.function.MinSizeFunction;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.ListIterate;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link MultiReaderFastList}.
 */
public class MultiReaderFastListTest extends AbstractListTestCase {
    @Override
    @Test
    public void newEmpty() {
        Verify.assertInstanceOf(MultiReaderFastList.class, MultiReaderFastList.newList().newEmpty());
        Verify.assertEmpty(MultiReaderFastList.<Integer>newListWith(null, null).newEmpty());
    }

    @Test
    public void fastListNewWith() {
        Assert.assertEquals(FastList.newListWith("Alice", "Bob", "Cooper", "Dio"), MultiReaderFastList.newListWith("Alice", "Bob", "Cooper", "Dio"));
    }

    @Override
    @Test
    public void forEach() {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> collection = MultiReaderFastList.newListWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), result);
    }

    @Override
    @Test
    public void injectInto() {
        MutableList<Integer> list = MultiReaderFastList.newListWith(1, 2, 3);
        Assert.assertEquals(Integer.valueOf(7), list.injectInto(1, INTEGER));
    }

    @Test
    public void injectIntoDouble2() {
        MutableList<Double> list = MultiReaderFastList.newListWith(1.0, 2.0, 3.0);
        Assert.assertEquals(7.0, list.injectInto(1.0, DOUBLE_TO_DOUBLE), 0.001);
    }

    @Test
    public void injectIntoString() {
        MutableList<String> list = MultiReaderFastList.newListWith("1", "2", "3");
        Assert.assertEquals("0123", list.injectInto("0", STRING));
    }

    @Test
    public void injectIntoMaxString() {
        MutableList<String> list = MultiReaderFastList.newListWith("1", "12", "123");
        Function2<Integer, String, Integer> function = MaxSizeFunction.STRING;
        Assert.assertEquals(Integer.valueOf(3), list.injectInto(Integer.MIN_VALUE, function));
    }

    @Test
    public void injectIntoMinString() {
        MutableList<String> list = MultiReaderFastList.newListWith("1", "12", "123");
        Function2<Integer, String, Integer> function = MinSizeFunction.STRING;
        Assert.assertEquals(Integer.valueOf(1), list.injectInto(Integer.MAX_VALUE, function));
    }

    @Override
    @Test
    public void collect() {
        MutableList<Boolean> list = MultiReaderFastList.newListWith(Boolean.TRUE, Boolean.FALSE, null);
        MutableList<String> newCollection = list.collect(String::valueOf);
        Assert.assertEquals(FastList.newListWith("true", "false", "null"), newCollection);
    }

    @Override
    @Test
    public void forEachWithIndex() {
        super.forEachWithIndex();
        MutableList<Integer> list = MultiReaderFastList.newList(Interval.oneTo(5));
        list.forEachWithIndex(( object, index) -> Assert.assertEquals(index, (object - 1)));
    }

    @Test
    public void forEachInBoth() {
        MutableList<Pair<String, String>> list = MultiReaderFastList.newList();
        MutableList<String> list1 = MultiReaderFastList.newListWith("1", "2");
        MutableList<String> list2 = MultiReaderFastList.newListWith("a", "b");
        ListIterate.forEachInBoth(list1, list2, ( argument1, argument2) -> list.add(Tuples.pair(argument1, argument2)));
        Assert.assertEquals(FastList.newListWith(Tuples.pair("1", "a"), Tuples.pair("2", "b")), list);
    }

    @Override
    @Test
    public void detect() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertEquals(Integer.valueOf(1), list.detect(Integer.valueOf(1)::equals));
        MutableList<Integer> list2 = MultiReaderFastList.newListWith(1, 2, 2);
        Assert.assertSame(list2.get(1), list2.detect(Integer.valueOf(2)::equals));
    }

    @Override
    @Test
    public void detectWith() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertEquals(Integer.valueOf(1), list.detectWith(Object::equals, 1));
        MutableList<Integer> list2 = MultiReaderFastList.newListWith(1, 2, 2);
        Assert.assertSame(list2.get(1), list2.detectWith(Object::equals, 2));
    }

    @Test
    public void detectWithIfNone() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertNull(list.detectWithIfNone(Object::equals, 6, new com.gs.collections.impl.block.function.PassThruFunction0(null)));
        Assert.assertEquals(Integer.valueOf(1), list.detectWithIfNone(Object::equals, Integer.valueOf(1), new com.gs.collections.impl.block.function.PassThruFunction0(Integer.valueOf(10000))));
    }

    @Override
    @Test
    public void select() {
        MutableList<Integer> list = this.getIntegerList();
        MutableList<Integer> results = list.select(Integer.class::isInstance);
        Verify.assertSize(5, results);
    }

    @Override
    @Test
    public void selectWith() {
        MutableList<Integer> list = this.getIntegerList();
        MutableList<Integer> results = list.selectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(5, results);
    }

    @Override
    @Test
    public void rejectWith() {
        MutableList<Integer> list = this.getIntegerList();
        MutableList<Integer> results = list.rejectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Override
    @Test
    public void selectAndRejectWith() {
        MutableList<Integer> list = this.getIntegerList();
        Twin<MutableList<Integer>> result = list.selectAndRejectWith(Predicates2.in(), fixedSize.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Override
    @Test
    public void anySatisfyWith() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(list.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(list.anySatisfyWith(Predicates2.instanceOf(), Double.class));
    }

    @Override
    @Test
    public void anySatisfy() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(Predicates.<Integer>anySatisfy(Integer.class::isInstance).accept(list));
        Assert.assertFalse(Predicates.<Integer>anySatisfy(Double.class::isInstance).accept(list));
    }

    @Override
    @Test
    public void allSatisfyWith() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(list.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        Assert.assertFalse(list.allSatisfyWith(greaterThanPredicate, 2));
    }

    @Override
    @Test
    public void allSatisfy() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(Predicates.<Integer>allSatisfy(Integer.class::isInstance).accept(list));
        Assert.assertFalse(Predicates.allSatisfy(Predicates.greaterThan(2)).accept(list));
    }

    @Override
    @Test
    public void noneSatisfy() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(Predicates.<Integer>noneSatisfy(String.class::isInstance).accept(list));
        Assert.assertFalse(Predicates.noneSatisfy(Predicates.greaterThan(0)).accept(list));
    }

    @Override
    @Test
    public void noneSatisfyWith() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(list.noneSatisfyWith(Predicates2.instanceOf(), String.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        Assert.assertFalse(list.noneSatisfyWith(greaterThanPredicate, 0));
    }

    @Override
    @Test
    public void count() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertEquals(5, list.count(Integer.class::isInstance));
        Assert.assertEquals(0, list.count(Double.class::isInstance));
    }

    @Override
    @Test
    public void countWith() {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertEquals(5, list.countWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertEquals(0, list.countWith(Predicates2.instanceOf(), Double.class));
    }

    @Override
    @Test
    public void detectIfNone() {
        Function0<Integer> defaultResultFunction = new com.gs.collections.impl.block.function.PassThruFunction0(6);
        Assert.assertEquals(Integer.valueOf(3), MultiReaderFastList.newListWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(3)::equals, defaultResultFunction));
        Assert.assertEquals(Integer.valueOf(6), MultiReaderFastList.newListWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(6)::equals, defaultResultFunction));
    }

    @Override
    @Test
    public void forEachWith() {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> collection = MultiReaderFastList.newListWith(1, 2, 3, 4);
        collection.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 0);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), result);
    }

    @Override
    @Test
    public void getFirst() {
        Assert.assertNull(MultiReaderFastList.newList().getFirst());
        Assert.assertEquals(Integer.valueOf(1), MultiReaderFastList.newListWith(1, 2, 3).getFirst());
    }

    @Override
    @Test
    public void getLast() {
        Assert.assertNull(MultiReaderFastList.newList().getLast());
        Assert.assertNotEquals(Integer.valueOf(1), MultiReaderFastList.newListWith(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(3), MultiReaderFastList.newListWith(1, 2, 3).getLast());
    }

    @Override
    @Test
    public void isEmpty() {
        Verify.assertEmpty(MultiReaderFastList.newList());
        Verify.assertNotEmpty(MultiReaderFastList.newListWith(1, 2));
        Assert.assertTrue(MultiReaderFastList.newListWith(1, 2).notEmpty());
    }

    @Override
    @Test
    public void collectIf() {
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), MultiReaderFastList.newListWith(1, 2, 3).collectIf(Integer.class::isInstance, String::valueOf));
        Assert.assertEquals(FastList.newListWith("1", "2", "3"), MultiReaderFastList.newListWith(1, 2, 3).collectIf(Integer.class::isInstance, String::valueOf, FastList.<String>newList()));
    }

    @Override
    @Test
    public void collectWith() {
        Function2<Integer, Integer, Integer> addZeroFunction = ( each, parameter) -> each + parameter;
        Verify.assertContainsAll(MultiReaderFastList.newListWith(1, 2, 3).collectWith(addZeroFunction, 0), 1, 2, 3);
        Verify.assertContainsAll(MultiReaderFastList.newListWith(1, 2, 3).collectWith(addZeroFunction, 0, FastList.<Integer>newList()), 1, 2, 3);
    }

    @Override
    @Test
    public void injectIntoWith() {
        MutableList<Integer> objects = MultiReaderFastList.newListWith(1, 2, 3);
        Integer result = objects.injectIntoWith(1, ( injectedValued, item, parameter) -> (injectedValued + item) + parameter, 0);
        Assert.assertEquals(Integer.valueOf(7), result);
    }

    @Test
    public void removeUsingPredicate() {
        MutableList<Integer> objects = MultiReaderFastList.newListWith(1, 2, 3, null);
        Assert.assertTrue(objects.removeIf(Predicates.isNull()));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
    }

    @Override
    @Test
    public void removeIf() {
        MutableList<Integer> objects = MultiReaderFastList.newListWith(1, 2, 3, null);
        Assert.assertTrue(objects.removeIf(Predicates.cast(( each) -> each == null)));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
    }

    @Override
    @Test
    public void removeIfWith() {
        MutableList<Integer> objects = MultiReaderFastList.newListWith(1, 2, 3, null);
        Assert.assertTrue(objects.removeIfWith(( each, ignored) -> each == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
    }

    @Override
    @Test
    public void removeAll() {
        super.removeAll();
        MutableList<Integer> objects = MultiReaderFastList.newListWith(1, 2, 3);
        objects.removeAll(fixedSize.of(1, 2));
        Verify.assertSize(1, objects);
        Verify.assertContains(3, objects);
        MutableList<Integer> objects2 = MultiReaderFastList.newListWith(1, 2, 3);
        objects2.removeAll(fixedSize.of(1));
        Verify.assertSize(2, objects2);
        Verify.assertContainsAll(objects2, 2, 3);
        MutableList<Integer> objects3 = MultiReaderFastList.newListWith(1, 2, 3);
        objects3.removeAll(fixedSize.of(3));
        Verify.assertSize(2, objects3);
        Verify.assertContainsAll(objects3, 1, 2);
        MutableList<Integer> objects4 = MultiReaderFastList.newListWith(1, 2, 3);
        objects4.removeAll(fixedSize.of());
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);
        MutableList<Integer> objects5 = MultiReaderFastList.newListWith(1, 2, 3);
        objects5.removeAll(fixedSize.of(1, 2, 3));
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = MultiReaderFastList.newListWith(1, 2, 3);
        objects6.removeAll(fixedSize.of(2));
        Verify.assertSize(2, objects6);
        Verify.assertContainsAll(objects6, 1, 3);
    }

    @Override
    @Test
    public void removeAllIterable() {
        super.removeAllIterable();
        MutableList<Integer> objects = MultiReaderFastList.newListWith(1, 2, 3);
        objects.removeAllIterable(fixedSize.of(1, 2));
        Verify.assertSize(1, objects);
        Verify.assertContains(3, objects);
        MutableList<Integer> objects2 = MultiReaderFastList.newListWith(1, 2, 3);
        objects2.removeAllIterable(fixedSize.of(1));
        Verify.assertSize(2, objects2);
        Verify.assertContainsAll(objects2, 2, 3);
        MutableList<Integer> objects3 = MultiReaderFastList.newListWith(1, 2, 3);
        objects3.removeAllIterable(fixedSize.of(3));
        Verify.assertSize(2, objects3);
        Verify.assertContainsAll(objects3, 1, 2);
        MutableList<Integer> objects4 = MultiReaderFastList.newListWith(1, 2, 3);
        objects4.removeAllIterable(fixedSize.of());
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);
        MutableList<Integer> objects5 = MultiReaderFastList.newListWith(1, 2, 3);
        objects5.removeAllIterable(fixedSize.of(1, 2, 3));
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = MultiReaderFastList.newListWith(1, 2, 3);
        objects6.removeAllIterable(fixedSize.of(2));
        Verify.assertSize(2, objects6);
        Verify.assertContainsAll(objects6, 1, 3);
    }

    @Test
    public void removeAllWithWeakReference() {
        String fred = new String("Fred");// Deliberate String copy for unit test purpose

        String wilma = new String("Wilma");// Deliberate String copy for unit test purpose

        MutableList<String> objects = MultiReaderFastList.newListWith(fred, wilma);
        objects.removeAll(fixedSize.of("Fred"));
        objects.remove(0);
        Verify.assertEmpty(objects);
        WeakReference<String> ref = new WeakReference<>(wilma);
        // noinspection ReuseOfLocalVariable
        fred = null;// Deliberate null of a local variable for unit test purpose

        // noinspection ReuseOfLocalVariable
        wilma = null;// Deliberate null of a local variable for unit test purpose

        System.gc();
        Thread.yield();
        System.gc();
        Thread.yield();
        Assert.assertNull(ref.get());
    }

    @Override
    @Test
    public void retainAll() {
        super.retainAll();
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.retainAll(fixedSize.of(1, 2));
        Verify.assertSize(2, objects);
        Verify.assertContainsAll(objects, 1, 2);
        MutableList<Integer> objects2 = this.newWith(1, 2, 3);
        objects2.retainAll(fixedSize.of(1));
        Verify.assertSize(1, objects2);
        Verify.assertContainsAll(objects2, 1);
        MutableList<Integer> objects3 = this.newWith(1, 2, 3);
        objects3.retainAll(fixedSize.of(3));
        Verify.assertSize(1, objects3);
        Verify.assertContainsAll(objects3, 3);
        MutableList<Integer> objects4 = this.newWith(1, 2, 3);
        objects4.retainAll(fixedSize.of(2));
        Verify.assertSize(1, objects4);
        Verify.assertContainsAll(objects4, 2);
        MutableList<Integer> objects5 = this.newWith(1, 2, 3);
        objects5.retainAll(fixedSize.of());
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = this.newWith(1, 2, 3);
        objects6.retainAll(fixedSize.of(1, 2, 3));
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Override
    @Test
    public void retainAllIterable() {
        super.retainAllIterable();
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.retainAllIterable(fixedSize.of(1, 2));
        Verify.assertSize(2, objects);
        Verify.assertContainsAll(objects, 1, 2);
        MutableList<Integer> objects2 = this.newWith(1, 2, 3);
        objects2.retainAllIterable(fixedSize.of(1));
        Verify.assertSize(1, objects2);
        Verify.assertContainsAll(objects2, 1);
        MutableList<Integer> objects3 = this.newWith(1, 2, 3);
        objects3.retainAllIterable(fixedSize.of(3));
        Verify.assertSize(1, objects3);
        Verify.assertContainsAll(objects3, 3);
        MutableList<Integer> objects4 = this.newWith(1, 2, 3);
        objects4.retainAllIterable(fixedSize.of(2));
        Verify.assertSize(1, objects4);
        Verify.assertContainsAll(objects4, 2);
        MutableList<Integer> objects5 = this.newWith(1, 2, 3);
        objects5.retainAllIterable(fixedSize.of());
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = this.newWith(1, 2, 3);
        objects6.retainAllIterable(fixedSize.of(1, 2, 3));
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Override
    @Test
    public void reject() {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Override
    @Test
    public void serialization() {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2, 3, 4, 5);
        Assert.assertEquals(collection, deserializedCollection);
    }

    @Test
    public void serializationOfEmpty() {
        MutableList<Integer> collection = MultiReaderFastList.newList();
        Verify.assertPostSerializedEqualsAndHashCode(collection);
    }

    @Test
    public void serializationOfSublist() {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection.subList(0, 2));
        Verify.assertSize(2, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2);
        Assert.assertEquals(collection.subList(0, 2), deserializedCollection);
    }

    @Override
    @Test
    public void addAll() {
        super.addAll();
        MutableList<Integer> integers = MultiReaderFastList.newList();
        Assert.assertTrue(integers.addAll(fixedSize.of(1, 2, 3, 4)));
        Verify.assertContainsAll(integers, 1, 2, 3, 4);
        Assert.assertTrue(integers.addAll(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 1, 2, 3, 4);
        Assert.assertTrue(integers.addAll(Sets.fixedSize.of(5)));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 1, 2, 3, 4, 5);
    }

    @Override
    @Test
    public void addAllIterable() {
        super.addAllIterable();
        MutableList<Integer> integers = MultiReaderFastList.newList();
        Assert.assertTrue(integers.addAllIterable(fixedSize.of(1, 2, 3, 4)));
        Verify.assertContainsAll(integers, 1, 2, 3, 4);
        Assert.assertTrue(integers.addAllIterable(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 1, 2, 3, 4);
        Assert.assertTrue(integers.addAllIterable(Sets.fixedSize.of(5)));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 1, 2, 3, 4, 5);
    }

    @Test
    public void addAllEmpty() {
        MutableList<Integer> integers = MultiReaderFastList.newList();
        integers.addAll(fixedSize.<Integer>of());
        Verify.assertEmpty(integers);
        integers.addAll(Sets.fixedSize.<Integer>of());
        Verify.assertEmpty(integers);
        integers.addAll(FastList.<Integer>newList());
        Verify.assertEmpty(integers);
        integers.addAll(ArrayAdapter.<Integer>newArray());
        Verify.assertEmpty(integers);
    }

    @Override
    @Test
    public void addAllAtIndex() {
        MutableList<Integer> integers = this.newWith(5);
        integers.addAll(0, fixedSize.of(1, 2, 3, 4));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5);
        integers.addAll(0, this.newWith((-3), (-2), (-1), 0));
        Verify.assertStartsWith(integers, (-3), (-2), (-1), 0, 1, 2, 3, 4, 5);
    }

    @Test
    public void addAllAtIndexEmpty() {
        MutableList<Integer> integers = this.newWith(5);
        integers.addAll(0, fixedSize.<Integer>of());
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        integers.addAll(0, FastList.<Integer>newList(4));
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        integers.addAll(0, Sets.fixedSize.<Integer>of());
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        FastList<String> zeroSizedList = FastList.newList(0);
        zeroSizedList.addAll(0, this.newWith("1", "2"));
    }

    @Override
    @Test
    public void addAtIndex() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 5);
        integers.add(3, 4);
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5);
        integers.add(5, 6);
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5, 6);
        integers.add(0, 0);
        Verify.assertStartsWith(integers, 0, 1, 2, 3, 4, 5, 6);
        FastList<String> zeroSizedList = FastList.newList(0);
        zeroSizedList.add(0, "1");
        Verify.assertStartsWith(zeroSizedList, "1");
        zeroSizedList.add(1, "3");
        Verify.assertStartsWith(zeroSizedList, "1", "3");
        zeroSizedList.add(1, "2");
        Verify.assertStartsWith(zeroSizedList, "1", "2", "3");
        MutableList<Integer> midList = FastList.<Integer>newList(2).with(1, 3);
        midList.add(1, 2);
        Verify.assertStartsWith(midList, 1, 2, 3);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> midList.add((-1), (-1)));
    }

    @Override
    @Test
    public void subList() {
        super.subList();
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
        Verify.assertContainsAll(list, "A", "D");
    }

    @Test
    public void subListSort() {
        MutableList<Integer> list = Interval.from(0).to(20).addAllTo(MultiReaderFastList.<Integer>newList()).subList(2, 18).sortThis();
        Assert.assertEquals(FastList.newList(list), Interval.from(2).to(17));
    }

    @Test
    public void subListOfSubList() {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(0, 3);
        MutableList<String> sublist2 = sublist.subList(0, 2);
        Verify.assertSize(2, sublist2);
        Verify.assertContainsAll(sublist, "A", "B");
        sublist2.add("X");
        Verify.assertSize(3, sublist2);
        Verify.assertStartsWith(sublist2, "A", "B", "X");
        Verify.assertContainsAll(sublist, "A", "B", "C", "X");
        Assert.assertEquals("X", sublist2.remove(2));
        Verify.assertSize(2, sublist2);
        Verify.assertContainsNone(sublist, "X");
        Verify.assertContainsNone(sublist2, "X");
    }

    @Test
    public void setAtIndex() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 5);
        Assert.assertEquals(Integer.valueOf(5), integers.set(3, 4));
        Verify.assertStartsWith(integers, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void indexOf() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Assert.assertEquals(2, integers.indexOf(3));
        Assert.assertEquals((-1), integers.indexOf(0));
        Assert.assertEquals((-1), integers.indexOf(null));
        MutableList<Integer> integers2 = this.newWith(null, 2, 3, 4);
        Assert.assertEquals(0, integers2.indexOf(null));
    }

    @Override
    @Test
    public void lastIndexOf() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Assert.assertEquals(2, integers.lastIndexOf(3));
        Assert.assertEquals((-1), integers.lastIndexOf(0));
        Assert.assertEquals((-1), integers.lastIndexOf(null));
        MutableList<Integer> integers2 = this.newWith(null, 2, 3, 4);
        Assert.assertEquals(0, integers2.lastIndexOf(null));
    }

    @Test
    public void outOfBoundsCondition() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.get(4));
    }

    @Override
    @Test
    public void clear() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Verify.assertNotEmpty(integers);
        integers.clear();
        Verify.assertEmpty(integers);
    }

    @Override
    @Test
    public void testClone() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableList<Integer> clone = integers.clone();
        Assert.assertEquals(integers, clone);
        Verify.assertInstanceOf(MultiReaderFastList.class, clone);
    }

    @Override
    @Test
    public void toArray() {
        Object[] typelessArray = this.newWith(1, 2, 3, 4).toArray();
        Assert.assertArrayEquals(typelessArray, new Object[]{ 1, 2, 3, 4 });
        Integer[] typedArray = this.newWith(1, 2, 3, 4).toArray(new Integer[0]);
        Assert.assertArrayEquals(typedArray, new Integer[]{ 1, 2, 3, 4 });
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        MutableList<Integer> integers = this.newWith(1, 2, 3);
        MutableList<Integer> integers2 = this.newWith(1, 2, 3);
        MutableList<Integer> integers3 = this.newWith(1, null, 3, 4, 5);
        MutableList<Integer> integers4 = this.newWith(1, null, 3, 4, 5);
        MutableList<Integer> integers5 = this.newWith(1, null, 3);
        MutableList<Integer> randomAccessList = fixedSize.of(1, 2, 3);
        MutableList<Integer> randomAccessList2 = fixedSize.of(2, 3, 4);
        Verify.assertEqualsAndHashCode(integers, integers);
        Verify.assertPostSerializedEqualsAndHashCode(integers);
        Verify.assertEqualsAndHashCode(integers, integers2);
        Verify.assertEqualsAndHashCode(integers, randomAccessList);
        Assert.assertNotEquals(integers, integers3);
        Assert.assertNotEquals(integers, integers5);
        Assert.assertNotEquals(integers, randomAccessList2);
        Assert.assertNotEquals(integers, Sets.fixedSize.of());
        Verify.assertEqualsAndHashCode(integers3, integers4);
        Verify.assertEqualsAndHashCode(integers3, ArrayAdapter.newArrayWith(1, null, 3, 4, 5));
        Assert.assertEquals(integers, integers2);
        Assert.assertNotEquals(integers, integers3);
    }

    @Override
    @Test
    public void removeObject() {
        super.removeObject();
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Integer doesExist = 1;
        integers.remove(doesExist);
        Verify.assertStartsWith(integers, 2, 3, 4);
        Integer doesNotExist = 5;
        Assert.assertFalse(integers.remove(doesNotExist));
    }

    @Override
    @Test
    public void toList() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableList<Integer> list = integers.toList();
        Verify.assertStartsWith(list, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void toSet() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void toMap() {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableMap<String, String> map = integers.toMap(String::valueOf, String::valueOf);
        Assert.assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void sortThisOnListWithLessThan10Elements() {
        MutableList<Integer> integers = this.newWith(2, 3, 4, 1, 7, 9, 6, 8, 5);
        Verify.assertStartsWith(integers.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
        MutableList<Integer> integers2 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(integers2.sortThis(Collections.<Integer>reverseOrder()), 9, 8, 7, 6, 5, 4, 3, 2, 1);
        MutableList<Integer> integers3 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(integers3.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertInstanceOf(MultiReaderFastList.class, integers3.sortThis());
    }

    @Test
    public void sortThisOnListWithMoreThan9Elements() {
        MutableList<Integer> integers = this.newWith(2, 3, 4, 1, 5, 7, 6, 8, 10, 9);
        Verify.assertStartsWith(integers.sortThis(), 1, 2, 3, 4);
        MutableList<Integer> integers2 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Verify.assertStartsWith(integers2.sortThis(Collections.<Integer>reverseOrder()), 10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
        MutableList<Integer> integers3 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Verify.assertStartsWith(integers3.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void newListWithCollection() {
        Verify.assertEmpty(MultiReaderFastList.newList(fixedSize.of()));
        Verify.assertEmpty(MultiReaderFastList.newList(Sets.fixedSize.of()));
        Verify.assertEmpty(MultiReaderFastList.newList(FastList.newList()));
        Verify.assertEmpty(MultiReaderFastList.newList(FastList.newList(4)));
        MutableList<Integer> setToList = MultiReaderFastList.newList(UnifiedSet.newSetWith(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(setToList);
        Verify.assertSize(5, setToList);
        Verify.assertContainsAll(setToList, 1, 2, 3, 4, 5);
        MutableList<Integer> arrayListToList = MultiReaderFastList.newList(fixedSize.of(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(arrayListToList);
        Verify.assertSize(5, arrayListToList);
        Verify.assertStartsWith(arrayListToList, 1, 2, 3, 4, 5);
        MutableList<Integer> fastListToList = MultiReaderFastList.newList(FastList.<Integer>newList().with(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(fastListToList);
        Verify.assertSize(5, fastListToList);
        Verify.assertStartsWith(fastListToList, 1, 2, 3, 4, 5);
    }

    @Test
    public void containsAll() {
        MutableList<Integer> list = this.newWith(1, 2, 3, 4, 5, null);
        Assert.assertTrue(list.containsAll(fixedSize.of(1, 3, 5, null)));
        Assert.assertFalse(list.containsAll(fixedSize.of(2, null, 6)));
        Assert.assertTrue(list.containsAll(FastList.<Integer>newList().with(1, 3, 5, null)));
        Assert.assertFalse(list.containsAll(FastList.<Integer>newList().with(2, null, 6)));
    }

    @Override
    @Test
    public void iterator() {
        MultiReaderFastList<Integer> integers = this.newWith(1, 2, 3, 4);
        Verify.assertThrows(UnsupportedOperationException.class, ((Runnable) (integers::iterator)));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void listIterator() {
        MultiReaderFastList<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.listIterator();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listIteratorWithIndex() {
        MultiReaderFastList<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.listIterator(2);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void listIteratorIndexTooSmall() {
        this.newWith(1).listIterator((-1));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void listIteratorIndexTooBig() {
        this.newWith(1).listIterator(2);
    }

    @Test
    public void withWritelockAndDelegate() {
        MultiReaderFastList<Integer> list = MultiReaderFastList.newList(2);
        AtomicReference<MutableList<?>> delegateList = new AtomicReference<>();
        AtomicReference<MutableList<?>> subLists = new AtomicReference<>();
        AtomicReference<Iterator<?>> iterator = new AtomicReference<>();
        AtomicReference<Iterator<?>> listIterator = new AtomicReference<>();
        AtomicReference<Iterator<?>> listIteratorWithPosition = new AtomicReference<>();
        list.withWriteLockAndDelegate(( delegate) -> {
            delegate.add(1);
            delegate.add(2);
            delegate.add(3);
            delegate.add(4);
            delegateList.set(delegate);
            subLists.set(delegate.subList(1, 3));
            iterator.set(delegate.iterator());
            listIterator.set(delegate.listIterator());
            listIteratorWithPosition.set(delegate.listIterator(3));
        });
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), list);
        this.assertIteratorThrows(delegateList.get());
        this.assertIteratorThrows(subLists.get());
        this.assertIteratorThrows(iterator.get());
        this.assertIteratorThrows(listIterator.get());
        this.assertIteratorThrows(listIteratorWithPosition.get());
    }

    @Test
    public void withReadLockAndDelegate() {
        MultiReaderFastList<Integer> list = this.newWith(1);
        Object[] result = new Object[1];
        list.withReadLockAndDelegate(( delegate) -> {
            result[0] = delegate.getFirst();
            this.verifyDelegateIsUnmodifiable(delegate);
        });
        Assert.assertNotNull(result[0]);
    }

    @Override
    @Test
    public void testToString() {
        Assert.assertEquals("[1, 2, 3]", this.newWith(1, 2, 3).toString());
    }

    @Override
    @Test
    public void makeString() {
        Assert.assertEquals("1, 2, 3", this.newWith(1, 2, 3).makeString());
    }

    @Override
    @Test
    public void appendString() {
        Appendable builder = new StringBuilder();
        this.newWith(1, 2, 3).appendString(builder);
        Assert.assertEquals("1, 2, 3", builder.toString());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void iterator_throws() {
        this.newWith(1, 2, 3).iterator();
    }

    @Override
    @Test
    public void asReversed() {
        MultiReaderFastList<Integer> multiReaderFastList = this.newWith(1, 2, 3, 4);
        multiReaderFastList.withReadLockAndDelegate(( delegate) -> Verify.assertIterablesEqual(iList(4, 3, 2, 1), delegate.asReversed()));
    }
}

