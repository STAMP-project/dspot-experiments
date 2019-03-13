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
import AddFunction.STRING;
import Lists.fixedSize;
import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.ObjectIntProcedures;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.factory.StringFunctions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Iterables;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


public class IterateTest {
    private MutableList<Iterable<Integer>> iterables;

    @Test
    public void addAllTo() {
        Verify.assertContainsAll(Iterate.addAllTo(FastList.newListWith(1, 2, 3), FastList.newList()), 1, 2, 3);
    }

    @Test
    public void removeAllFrom() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(1, 3, 5);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Assert.assertEquals(immutable.with(2, 4, 6), sourceFastList);
    }

    @Test
    public void removeAllFromEmptyTest() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllFromEmptyListTest() {
        MutableList<Integer> sourceFastList = FastList.newList();
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllFromNoElementRemovedTest() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(7, 8, 9);
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Assert.assertEquals(immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void removeAllFromEmptyTargetListTest() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newList();
        Iterate.removeAllFrom(removeFastList, sourceFastList);
        Assert.assertEquals(immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void removeAllIterableFrom() {
        MutableList<Integer> sourceFastList1 = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList1 = FastList.newListWith(1, 3, 5);
        Assert.assertTrue(Iterate.removeAllIterable(removeFastList1, sourceFastList1));
        Assert.assertEquals(immutable.with(2, 4, 6), sourceFastList1);
        Assert.assertFalse(Iterate.removeAllIterable(FastList.newListWith(0), sourceFastList1));
        Assert.assertEquals(immutable.with(2, 4, 6), sourceFastList1);
        MutableList<Integer> sourceFastList2 = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableSetMultimap<Integer, Integer> multimap = UnifiedSetMultimap.newMultimap(Tuples.pair(1, 1), Tuples.pair(1, 3), Tuples.pair(1, 5));
        Assert.assertTrue(Iterate.removeAllIterable(multimap.valuesView(), sourceFastList2));
        Assert.assertEquals(immutable.with(2, 4, 6), sourceFastList2);
        Assert.assertFalse(Iterate.removeAllIterable(UnifiedSetMultimap.newMultimap(Tuples.pair(0, 0)).valuesView(), sourceFastList2));
        Assert.assertEquals(immutable.with(2, 4, 6), sourceFastList2);
    }

    @Test
    public void removeAllIterableFromEmptyTest() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        Assert.assertTrue(Iterate.removeAllIterable(removeFastList, sourceFastList));
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllIterableFromEmptyListTest() {
        MutableList<Integer> sourceFastList = FastList.newList();
        MutableList<Integer> removeFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        Assert.assertFalse(Iterate.removeAllIterable(removeFastList, sourceFastList));
        Verify.assertEmpty(sourceFastList);
    }

    @Test
    public void removeAllIterableFromNoElementRemovedTest() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newListWith(7, 8, 9);
        Assert.assertFalse(Iterate.removeAllIterable(removeFastList, sourceFastList));
        Assert.assertEquals(immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void removeAllIterableFromEmptyTargetListTest() {
        MutableList<Integer> sourceFastList = FastList.newListWith(1, 2, 3, 4, 5, 6);
        MutableList<Integer> removeFastList = FastList.newList();
        Assert.assertFalse(Iterate.removeAllIterable(removeFastList, sourceFastList));
        Assert.assertEquals(immutable.with(1, 2, 3, 4, 5, 6), sourceFastList);
    }

    @Test
    public void sizeOf() {
        Assert.assertEquals(5, Iterate.sizeOf(Interval.oneTo(5)));
        Assert.assertEquals(5, Iterate.sizeOf(Interval.oneTo(5).toList()));
        Assert.assertEquals(5, Iterate.sizeOf(Interval.oneTo(5).asLazy()));
        Assert.assertEquals(3, Iterate.sizeOf(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3)));
        Assert.assertEquals(5, Iterate.sizeOf(new IterateTest.IterableAdapter(Interval.oneTo(5))));
    }

    @Test
    public void toArray() {
        Object[] expected = new Object[]{ 1, 2, 3, 4, 5 };
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5)));
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toList()));
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).asLazy()));
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toSortedMap(( a) -> a, ( a) -> a)));
        Assert.assertArrayEquals(expected, Iterate.toArray(new IterateTest.IterableAdapter(Interval.oneTo(5))));
    }

    @Test(expected = NullPointerException.class)
    public void toArray_NullParameter() {
        Iterate.toArray(null);
    }

    @Test
    public void toArray_with_array() {
        Object[] expected = new Object[]{ 1, 2, 3, 4, 5 };
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5), new Object[5]));
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toList(), new Object[5]));
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).asLazy(), new Object[5]));
        Assert.assertArrayEquals(expected, Iterate.toArray(Interval.oneTo(5).toSortedMap(( a) -> a, ( a) -> a), new Object[5]));
        Assert.assertArrayEquals(expected, Iterate.toArray(new IterateTest.IterableAdapter(Interval.oneTo(5)), new Object[5]));
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3, 4, 5, 6, 7 }, Iterate.toArray(new IterateTest.IterableAdapter(Interval.oneTo(7)), new Object[5]));
    }

    @Test
    public void fromToDoit() {
        MutableList<Integer> list = mutable.of();
        Interval.fromTo(6, 10).forEach(Procedures.cast(list::add));
        Verify.assertContainsAll(list, 6, 10);
    }

    @Test
    public void injectInto() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertEquals(Integer.valueOf(15), Iterate.injectInto(0, each, AddFunction.INTEGER))));
    }

    @Test
    public void injectIntoInt() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertEquals(15, Iterate.injectInto(0, each, AddFunction.INTEGER_TO_INT))));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0, null, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertEquals(15L, Iterate.injectInto(0L, each, AddFunction.INTEGER_TO_LONG))));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0L, null, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertEquals(15.0, Iterate.injectInto(0.0, each, AddFunction.INTEGER_TO_DOUBLE), 0.001)));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0.0, null, AddFunction.INTEGER_TO_DOUBLE));
    }

    @Test
    public void injectIntoFloat() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertEquals(15.0, Iterate.injectInto(0.0F, each, AddFunction.INTEGER_TO_FLOAT), 0.001)));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.injectInto(0.0F, null, AddFunction.INTEGER_TO_FLOAT));
    }

    @Test
    public void injectInto2() {
        Assert.assertEquals(new Double(7), Iterate.injectInto(1.0, Iterables.iList(1.0, 2.0, 3.0), DOUBLE));
    }

    @Test
    public void injectIntoString() {
        Assert.assertEquals("0123", Iterate.injectInto("0", Iterables.iList("1", "2", "3"), STRING));
    }

    @Test
    public void injectIntoMaxString() {
        Assert.assertEquals(Integer.valueOf(3), Iterate.injectInto(Integer.MIN_VALUE, Iterables.iList("1", "12", "123"), MaxSizeFunction.STRING));
    }

    @Test
    public void injectIntoMinString() {
        Assert.assertEquals(Integer.valueOf(1), Iterate.injectInto(Integer.MAX_VALUE, Iterables.iList("1", "12", "123"), MinSizeFunction.STRING));
    }

    @Test
    public void flatCollectFromAttributes() {
        MutableList<IterateTest.ListContainer<String>> list = Iterables.mList(new IterateTest.ListContainer(mutable.of("One", "Two")), new IterateTest.ListContainer(mutable.of("Two-and-a-half", "Three", "Four")), new IterateTest.ListContainer(mutable.<String>of()), new IterateTest.ListContainer(mutable.of("Five")));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(list, IterateTest.ListContainer.<String>getListFunction()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(Collections.synchronizedList(list), IterateTest.ListContainer.getListFunction()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(Collections.synchronizedCollection(list), IterateTest.ListContainer.getListFunction()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(LazyIterate.adapt(list), IterateTest.ListContainer.<String>getListFunction()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(new ArrayList(list), IterateTest.ListContainer.getListFunction()));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.flatCollect(null, null));
    }

    @Test
    public void flatCollectFromAttributesWithTarget() {
        MutableList<IterateTest.ListContainer<String>> list = fixedSize.of(new IterateTest.ListContainer(mutable.of("One", "Two")), new IterateTest.ListContainer(mutable.of("Two-and-a-half", "Three", "Four")), new IterateTest.ListContainer(mutable.<String>of()), new IterateTest.ListContainer(mutable.of("Five")));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(list, IterateTest.ListContainer.<String>getListFunction(), FastList.newList()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(Collections.synchronizedList(list), IterateTest.ListContainer.<String>getListFunction(), FastList.newList()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(Collections.synchronizedCollection(list), IterateTest.ListContainer.<String>getListFunction(), FastList.newList()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(LazyIterate.adapt(list), IterateTest.ListContainer.<String>getListFunction(), FastList.newList()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(new ArrayList(list), IterateTest.ListContainer.<String>getListFunction(), FastList.newList()));
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), Iterate.flatCollect(new IterateTest.IterableAdapter(new ArrayList(list)), IterateTest.ListContainer.getListFunction(), FastList.newList()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void flatCollectFromAttributesWithTarget_NullParameter() {
        Iterate.flatCollect(null, null, UnifiedSet.newSet());
    }

    @Test
    public void flatCollectFromAttributesUsingStringFunction() {
        MutableList<IterateTest.ListContainer<String>> list = mutable.of(new IterateTest.ListContainer(mutable.of("One", "Two")), new IterateTest.ListContainer(mutable.of("Two-and-a-half", "Three", "Four")), new IterateTest.ListContainer(mutable.<String>of()), new IterateTest.ListContainer(mutable.of("Five")));
        Function<IterateTest.ListContainer<String>, List<String>> function = IterateTest.ListContainer::getList;
        Collection<String> result = Iterate.flatCollect(list, function);
        FastList<String> result2 = Iterate.flatCollect(list, function, FastList.<String>newList());
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result);
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result2);
    }

    @Test
    public void flatCollectOneLevel() {
        MutableList<MutableList<String>> list = mutable.of(mutable.of("One", "Two"), mutable.of("Two-and-a-half", "Three", "Four"), mutable.<String>of(), mutable.of("Five"));
        Collection<String> result = Iterate.flatten(list);
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result);
    }

    @Test
    public void flatCollectOneLevel_target() {
        MutableList<MutableList<String>> list = mutable.of(mutable.of("One", "Two"), mutable.of("Two-and-a-half", "Three", "Four"), mutable.<String>of(), mutable.of("Five"));
        MutableList<String> target = mutable.of();
        Collection<String> result = Iterate.flatten(list, target);
        Assert.assertSame(result, target);
        Assert.assertEquals(Iterables.iList("One", "Two", "Two-and-a-half", "Three", "Four", "Five"), result);
    }

    @Test
    public void groupBy() {
        FastList<String> source = FastList.newListWith("Ted", "Sally", "Mary", "Bob", "Sara");
        Multimap<Character, String> result1 = Iterate.groupBy(source, StringFunctions.firstLetter());
        Multimap<Character, String> result2 = Iterate.groupBy(Collections.synchronizedList(source), StringFunctions.firstLetter());
        Multimap<Character, String> result3 = Iterate.groupBy(Collections.synchronizedCollection(source), StringFunctions.firstLetter());
        Multimap<Character, String> result4 = Iterate.groupBy(LazyIterate.adapt(source), StringFunctions.firstLetter());
        Multimap<Character, String> result5 = Iterate.groupBy(new ArrayList(source), StringFunctions.firstLetter());
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.put('S', "Sally");
        expected.put('M', "Mary");
        expected.put('B', "Bob");
        expected.put('S', "Sara");
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
        Assert.assertEquals(expected, result3);
        Assert.assertEquals(expected, result4);
        Assert.assertEquals(expected, result5);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.groupBy(null, null));
    }

    @Test
    public void groupByEach() {
        MutableList<String> source = FastList.newListWith("Ted", "Sally", "Sally", "Mary", "Bob", "Sara");
        Function<String, Set<Character>> uppercaseSetFunction = new IterateTest.WordToItsLetters();
        Multimap<Character, String> result1 = Iterate.groupByEach(source, uppercaseSetFunction);
        Multimap<Character, String> result2 = Iterate.groupByEach(Collections.synchronizedList(source), uppercaseSetFunction);
        Multimap<Character, String> result3 = Iterate.groupByEach(Collections.synchronizedCollection(source), uppercaseSetFunction);
        Multimap<Character, String> result4 = Iterate.groupByEach(LazyIterate.adapt(source), uppercaseSetFunction);
        Multimap<Character, String> result5 = Iterate.groupByEach(new ArrayList(source), uppercaseSetFunction);
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.putAll('E', FastList.newListWith("Ted"));
        expected.put('D', "Ted");
        expected.putAll('S', FastList.newListWith("Sally", "Sally", "Sara"));
        expected.putAll('A', FastList.newListWith("Sally", "Sally", "Mary", "Sara"));
        expected.putAll('L', FastList.newListWith("Sally", "Sally"));
        expected.putAll('Y', FastList.newListWith("Sally", "Sally", "Mary"));
        expected.put('M', "Mary");
        expected.putAll('R', FastList.newListWith("Mary", "Sara"));
        expected.put('B', "Bob");
        expected.put('O', "Bob");
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
        Assert.assertEquals(expected, result3);
        Assert.assertEquals(expected, result4);
        Assert.assertEquals(expected, result5);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.groupByEach(null, null));
    }

    @Test
    public void groupByWithTarget() {
        FastList<String> source = FastList.newListWith("Ted", "Sally", "Mary", "Bob", "Sara");
        Multimap<Character, String> result1 = Iterate.groupBy(source, StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result2 = Iterate.groupBy(Collections.synchronizedList(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result3 = Iterate.groupBy(Collections.synchronizedCollection(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result4 = Iterate.groupBy(LazyIterate.adapt(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        Multimap<Character, String> result5 = Iterate.groupBy(new ArrayList(source), StringFunctions.firstLetter(), FastListMultimap.newMultimap());
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.put('S', "Sally");
        expected.put('M', "Mary");
        expected.put('B', "Bob");
        expected.put('S', "Sara");
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
        Assert.assertEquals(expected, result3);
        Assert.assertEquals(expected, result4);
        Assert.assertEquals(expected, result5);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.groupBy(null, null, null));
    }

    @Test
    public void groupByEachWithTarget() {
        MutableList<String> source = FastList.newListWith("Ted", "Sally", "Sally", "Mary", "Bob", "Sara");
        Function<String, Set<Character>> uppercaseSetFunction = StringIterate::asUppercaseSet;
        Multimap<Character, String> result1 = Iterate.groupByEach(source, uppercaseSetFunction, FastListMultimap.<Character, String>newMultimap());
        Multimap<Character, String> result2 = Iterate.groupByEach(Collections.synchronizedList(source), uppercaseSetFunction, FastListMultimap.<Character, String>newMultimap());
        Multimap<Character, String> result3 = Iterate.groupByEach(Collections.synchronizedCollection(source), uppercaseSetFunction, FastListMultimap.<Character, String>newMultimap());
        Multimap<Character, String> result4 = Iterate.groupByEach(LazyIterate.adapt(source), uppercaseSetFunction, FastListMultimap.<Character, String>newMultimap());
        Multimap<Character, String> result5 = Iterate.groupByEach(new ArrayList(source), uppercaseSetFunction, FastListMultimap.<Character, String>newMultimap());
        MutableMultimap<Character, String> expected = FastListMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.putAll('E', FastList.newListWith("Ted"));
        expected.put('D', "Ted");
        expected.putAll('S', FastList.newListWith("Sally", "Sally", "Sara"));
        expected.putAll('A', FastList.newListWith("Sally", "Sally", "Mary", "Sara"));
        expected.putAll('L', FastList.newListWith("Sally", "Sally"));
        expected.putAll('Y', FastList.newListWith("Sally", "Sally"));
        expected.put('M', "Mary");
        expected.putAll('R', FastList.newListWith("Mary", "Sara"));
        expected.put('Y', "Mary");
        expected.put('B', "Bob");
        expected.put('O', "Bob");
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
        Assert.assertEquals(expected, result3);
        Assert.assertEquals(expected, result4);
        Assert.assertEquals(expected, result5);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.groupByEach(null, null, null));
    }

    @Test
    public void toList() {
        Assert.assertEquals(FastList.newListWith(1, 2, 3), FastList.newList(Interval.oneTo(3)));
    }

    @Test
    public void contains() {
        Assert.assertTrue(Iterate.contains(FastList.newListWith(1, 2, 3), 1));
        Assert.assertFalse(Iterate.contains(FastList.newListWith(1, 2, 3), 4));
        Assert.assertTrue(Iterate.contains(FastList.newListWith(1, 2, 3).asLazy(), 1));
        Assert.assertTrue(Iterate.contains(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), 1));
        Assert.assertFalse(Iterate.contains(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), 4));
        Assert.assertTrue(Iterate.contains(Interval.oneTo(3), 1));
        Assert.assertFalse(Iterate.contains(Interval.oneTo(3), 4));
    }

    public static final class ListContainer<T> {
        private final List<T> list;

        private ListContainer(List<T> list) {
            this.list = list;
        }

        private static <V> Function<IterateTest.ListContainer<V>, List<V>> getListFunction() {
            return ( anObject) -> anObject.list;
        }

        public List<T> getList() {
            return this.list;
        }
    }

    @Test
    public void getFirstAndLast() {
        MutableList<Boolean> list = Iterables.mList(Boolean.TRUE, null, Boolean.FALSE);
        Assert.assertEquals(Boolean.TRUE, Iterate.getFirst(list));
        Assert.assertEquals(Boolean.FALSE, Iterate.getLast(list));
        Assert.assertEquals(Boolean.TRUE, Iterate.getFirst(Collections.unmodifiableList(list)));
        Assert.assertEquals(Boolean.FALSE, Iterate.getLast(Collections.unmodifiableList(list)));
        Assert.assertEquals(Boolean.TRUE, Iterate.getFirst(new IterateTest.IterableAdapter(list)));
        Assert.assertEquals(Boolean.FALSE, Iterate.getLast(new IterateTest.IterableAdapter(list)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFirst_null_throws() {
        Iterate.getFirst(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getList_null_throws() {
        Iterate.getLast(null);
    }

    @Test
    public void getFirstAndLastInterval() {
        Assert.assertEquals(Integer.valueOf(1), Iterate.getFirst(Interval.oneTo(5)));
        Assert.assertEquals(Integer.valueOf(5), Iterate.getLast(Interval.oneTo(5)));
    }

    @Test
    public void getFirstAndLastLinkedList() {
        List<Boolean> list = new LinkedList(Iterables.mList(Boolean.TRUE, null, Boolean.FALSE));
        Assert.assertEquals(Boolean.TRUE, Iterate.getFirst(list));
        Assert.assertEquals(Boolean.FALSE, Iterate.getLast(list));
    }

    @Test
    public void getFirstAndLastTreeSet() {
        Set<String> set = new TreeSet(Iterables.mList("1", "2"));
        Assert.assertEquals("1", Iterate.getFirst(set));
        Assert.assertEquals("2", Iterate.getLast(set));
    }

    @Test
    public void getFirstAndLastCollection() {
        Collection<Boolean> list = Iterables.mList(Boolean.TRUE, null, Boolean.FALSE).asSynchronized();
        Assert.assertEquals(Boolean.TRUE, Iterate.getFirst(list));
        Assert.assertEquals(Boolean.FALSE, Iterate.getLast(list));
    }

    @Test
    public void getFirstAndLastOnEmpty() {
        Assert.assertNull(Iterate.getFirst(Iterables.mList()));
        Assert.assertNull(Iterate.getLast(Iterables.mList()));
    }

    @Test
    public void getFirstAndLastForSet() {
        Set<Integer> orderedSet = new TreeSet<>();
        orderedSet.add(1);
        orderedSet.add(2);
        orderedSet.add(3);
        Assert.assertEquals(Integer.valueOf(1), Iterate.getFirst(orderedSet));
        Assert.assertEquals(Integer.valueOf(3), Iterate.getLast(orderedSet));
    }

    @Test
    public void getFirstAndLastOnEmptyForSet() {
        MutableSet<Object> set = UnifiedSet.newSet();
        Assert.assertNull(Iterate.getFirst(set));
        Assert.assertNull(Iterate.getLast(set));
    }

    @Test
    public void selectDifferentTargetCollection() {
        MutableSet<Integer> set = this.getIntegerSet();
        List<Integer> list = Iterate.select(set, Integer.class::isInstance, new ArrayList());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), list);
    }

    @Test
    public void rejectWithDifferentTargetCollection() {
        MutableSet<Integer> set = this.getIntegerSet();
        Collection<Integer> result = Iterate.reject(set, Integer.class::isInstance, FastList.newList());
        Verify.assertEmpty(result);
    }

    @Test
    public void count_empty() {
        Assert.assertEquals(0, Iterate.count(Iterables.iList(), ( ignored) -> true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void count_null_throws() {
        Iterate.count(null, ( ignored) -> true);
    }

    @Test
    public void toMap() {
        MutableSet<Integer> set = UnifiedSet.newSet(this.getIntegerSet());
        MutableMap<String, Integer> map = Iterate.toMap(set, String::valueOf);
        Verify.assertSize(5, map);
        Object expectedValue = 1;
        Object expectedKey = "1";
        Verify.assertContainsKeyValue(expectedKey, expectedValue, map);
        Verify.assertContainsKeyValue("2", 2, map);
        Verify.assertContainsKeyValue("3", 3, map);
        Verify.assertContainsKeyValue("4", 4, map);
        Verify.assertContainsKeyValue("5", 5, map);
    }

    @Test
    public void addToMap() {
        MutableSet<Integer> set = Interval.toSet(1, 5);
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.put("the answer", 42);
        Iterate.addToMap(set, String::valueOf, map);
        Verify.assertSize(6, map);
        Verify.assertContainsAllKeyValues(map, "the answer", 42, "1", 1, "2", 2, "3", 3, "4", 4, "5", 5);
    }

    @Test
    public void toMapSelectingKeyAndValue() {
        MutableSet<Integer> set = UnifiedSet.newSet(this.getIntegerSet());
        MutableMap<String, Integer> map = Iterate.toMap(set, String::valueOf, ( object) -> 10 * object);
        Verify.assertSize(5, map);
        Verify.assertContainsKeyValue("1", 10, map);
        Verify.assertContainsKeyValue("2", 20, map);
        Verify.assertContainsKeyValue("3", 30, map);
        Verify.assertContainsKeyValue("4", 40, map);
        Verify.assertContainsKeyValue("5", 50, map);
    }

    @Test
    public void forEachWithIndex() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            UnifiedSet<Integer> set = UnifiedSet.newSet();
            Iterate.forEachWithIndex(each, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(set)));
            Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), set);
        }));
    }

    @Test
    public void selectPairs() {
        ImmutableList<Twin<String>> twins = Iterables.iList(Tuples.twin("1", "2"), Tuples.twin("2", "1"), Tuples.twin("3", "3"));
        Collection<Twin<String>> results = Iterate.select(twins, new com.gs.collections.impl.block.predicate.PairPredicate<String, String>() {
            public boolean accept(String argument1, String argument2) {
                return ("1".equals(argument1)) || ("1".equals(argument2));
            }
        });
        Verify.assertSize(2, results);
    }

    @Test
    public void detect() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Integer result = Iterate.detect(each, Predicates.instanceOf(.class));
            Assert.assertTrue(UnifiedSet.newSet(each).contains(result));
        }));
    }

    @Test
    public void detectIndex() {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assert.assertEquals(4, Iterate.detectIndex(list, Integer.valueOf(1)::equals));
        Assert.assertEquals(0, Iterate.detectIndex(list, Integer.valueOf(5)::equals));
        Assert.assertEquals((-1), Iterate.detectIndex(immutable.of(), Integer.valueOf(1)::equals));
        Assert.assertEquals((-1), Iterate.detectIndex(Sets.immutable.of(), Integer.valueOf(1)::equals));
    }

    @Test
    public void detectIndexWithTreeSet() {
        Set<Integer> treeSet = new TreeSet(Interval.toReverseList(1, 5));
        Assert.assertEquals(0, Iterate.detectIndex(treeSet, Integer.valueOf(1)::equals));
        Assert.assertEquals(4, Iterate.detectIndex(treeSet, Integer.valueOf(5)::equals));
    }

    @Test
    public void detectIndexWith() {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assert.assertEquals(4, Iterate.detectIndexWith(list, Object::equals, 1));
        Assert.assertEquals(0, Iterate.detectIndexWith(list, Object::equals, 5));
        Assert.assertEquals((-1), Iterate.detectIndexWith(Iterables.iList(), Object::equals, 5));
        Assert.assertEquals((-1), Iterate.detectIndexWith(Iterables.iSet(), Object::equals, 5));
    }

    @Test
    public void detectIndexWithWithTreeSet() {
        Set<Integer> treeSet = new TreeSet(Interval.toReverseList(1, 5));
        Assert.assertEquals(0, Iterate.detectIndexWith(treeSet, Object::equals, 1));
        Assert.assertEquals(4, Iterate.detectIndexWith(treeSet, Object::equals, 5));
    }

    @Test
    public void detectWithIfNone() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Integer result = Iterate.detectWithIfNone(each, Predicates2.instanceOf(), .class, 5);
            Verify.assertContains(result, UnifiedSet.newSet(each));
        }));
    }

    @Test
    public void selectWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.selectWith(each, Predicates2.greaterThan(), 3);
            Assert.assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.selectWith(null, null, null));
    }

    @Test
    public void selectWithWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.selectWith(each, Predicates2.greaterThan(), 3, FastList.<Integer>newList());
            Assert.assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.selectWith(null, null, null, null));
    }

    @Test
    public void rejectWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.rejectWith(each, Predicates2.greaterThan(), 3);
            Assert.assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.rejectWith(null, null, null));
    }

    @Test
    public void rejectWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.rejectWith(each, Predicates2.greaterThan(), 3, FastList.<Integer>newList());
            Assert.assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.rejectWith(null, null, null, null));
    }

    @Test
    public void selectAndRejectWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Twin<MutableList<Integer>> result = Iterate.selectAndRejectWith(each, Predicates2.greaterThan(), 3);
            Assert.assertEquals(iBag(4, 5), result.getOne().toBag());
            Assert.assertEquals(iBag(1, 2, 3), result.getTwo().toBag());
        }));
    }

    @Test
    public void partition() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            PartitionIterable<Integer> result = Iterate.partition(each, Predicates.greaterThan(3));
            Assert.assertEquals(iBag(4, 5), result.getSelected().toBag());
            Assert.assertEquals(iBag(1, 2, 3), result.getRejected().toBag());
        }));
    }

    @Test
    public void partitionWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            PartitionIterable<Integer> result = Iterate.partitionWith(each, Predicates2.greaterThan(), 3);
            Assert.assertEquals(iBag(4, 5), result.getSelected().toBag());
            Assert.assertEquals(iBag(1, 2, 3), result.getRejected().toBag());
        }));
    }

    @Test
    public void rejectTargetCollection() {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = Iterate.reject(list, Integer.class::isInstance, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectOnRandomAccessTargetCollection() {
        List<Integer> list = Collections.synchronizedList(Interval.toReverseList(1, 5));
        MutableList<Integer> results = Iterate.reject(list, Integer.class::isInstance, FastList.<Integer>newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectWithTargetCollection() {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = Iterate.rejectWith(list, Predicates2.instanceOf(), Integer.class, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void rejectWithOnRandomAccessTargetCollection() {
        List<Integer> list = Collections.synchronizedList(Interval.toReverseList(1, 5));
        MutableList<Integer> results = Iterate.rejectWith(list, Predicates2.instanceOf(), Integer.class, FastList.newList());
        Verify.assertEmpty(results);
    }

    @Test
    public void anySatisfy() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertTrue(Iterate.anySatisfy(each, .class::isInstance))));
    }

    @Test
    public void anySatisfyWith() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertTrue(Iterate.anySatisfyWith(each, Predicates2.instanceOf(), .class))));
    }

    @Test
    public void allSatisfy() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertTrue(Iterate.allSatisfy(each, .class::isInstance))));
    }

    @Test
    public void allSatisfyWith() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertTrue(Iterate.allSatisfyWith(each, Predicates2.instanceOf(), .class))));
    }

    @Test
    public void noneSatisfy() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertTrue(Iterate.noneSatisfy(each, .class::isInstance))));
    }

    @Test
    public void noneSatisfyWith() {
        this.iterables.forEach(Procedures.cast(( each) -> Assert.assertTrue(Iterate.noneSatisfyWith(each, Predicates2.instanceOf(), .class))));
    }

    @Test
    public void selectWithSet() {
        Verify.assertSize(1, Iterate.selectWith(this.getIntegerSet(), Object::equals, 1));
        Verify.assertSize(1, Iterate.selectWith(this.getIntegerSet(), Object::equals, 1, FastList.<Integer>newList()));
    }

    @Test
    public void rejectWithSet() {
        Verify.assertSize(4, Iterate.rejectWith(this.getIntegerSet(), Object::equals, 1));
        Verify.assertSize(4, Iterate.rejectWith(this.getIntegerSet(), Object::equals, 1, FastList.<Integer>newList()));
    }

    @Test
    public void detectWithSet() {
        Assert.assertEquals(Integer.valueOf(1), Iterate.detectWith(this.getIntegerSet(), Object::equals, 1));
    }

    @Test
    public void detectWithRandomAccess() {
        List<Integer> list = Collections.synchronizedList(Interval.oneTo(5));
        Assert.assertEquals(Integer.valueOf(1), Iterate.detectWith(list, Object::equals, 1));
    }

    @Test
    public void anySatisfyWithSet() {
        Assert.assertTrue(Iterate.anySatisfyWith(this.getIntegerSet(), Object::equals, 1));
    }

    @Test
    public void allSatisfyWithSet() {
        Assert.assertFalse(Iterate.allSatisfyWith(this.getIntegerSet(), Object::equals, 1));
        Assert.assertTrue(Iterate.allSatisfyWith(this.getIntegerSet(), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void noneSatisfyWithSet() {
        Assert.assertTrue(Iterate.noneSatisfyWith(this.getIntegerSet(), Object::equals, 100));
        Assert.assertFalse(Iterate.noneSatisfyWith(this.getIntegerSet(), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void selectAndRejectWithSet() {
        Twin<MutableList<Integer>> result = Iterate.selectAndRejectWith(this.getIntegerSet(), Predicates2.in(), Iterables.iList(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Test
    public void forEach() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            UnifiedSet<Integer> set = UnifiedSet.newSet();
            Iterate.forEach(each, Procedures.cast(set::add));
            Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), set);
        }));
    }

    @Test
    public void collectIf() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<String> result = Iterate.collectIf(each, Predicates.greaterThan(3), String::valueOf);
            Assert.assertTrue(result.containsAll(FastList.newListWith("4", "5")));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectIf(null, null, null));
    }

    @Test
    public void collectIfTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<String> result = Iterate.collectIf(each, Predicates.greaterThan(3), String::valueOf, FastList.newList());
            Assert.assertTrue(result.containsAll(FastList.newListWith("4", "5")));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectIf(null, null, null, null));
    }

    @Test
    public void collect() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<String> result = Iterate.collect(each, String::valueOf);
            Assert.assertTrue(result.containsAll(FastList.newListWith("1", "2", "3", "4", "5")));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collect(null, ( a) -> a));
    }

    @Test
    public void collectBoolean() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableBooleanCollection result = Iterate.collectBoolean(each, PrimitiveFunctions.integerIsPositive());
            Assert.assertTrue(result.containsAll(true, true, true, true, true));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectBoolean(null, PrimitiveFunctions.integerIsPositive()));
    }

    @Test
    public void collectBooleanWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableBooleanCollection expected = new BooleanArrayList();
            MutableBooleanCollection actual = Iterate.collectBoolean(each, PrimitiveFunctions.integerIsPositive(), expected);
            Assert.assertTrue(expected.containsAll(true, true, true, true, true));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectBoolean(null, PrimitiveFunctions.integerIsPositive(), new BooleanArrayList()));
    }

    @Test
    public void collectByte() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableByteCollection result = Iterate.collectByte(each, PrimitiveFunctions.unboxIntegerToByte());
            Assert.assertTrue(result.containsAll(((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5))));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectByte(null, PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Test
    public void collectByteWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableByteCollection expected = new ByteArrayList();
            MutableByteCollection actual = Iterate.collectByte(each, PrimitiveFunctions.unboxIntegerToByte(), expected);
            Assert.assertTrue(actual.containsAll(((byte) (1)), ((byte) (2)), ((byte) (3)), ((byte) (4)), ((byte) (5))));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectByte(null, PrimitiveFunctions.unboxIntegerToByte(), new ByteArrayList()));
    }

    @Test
    public void collectChar() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableCharCollection result = Iterate.collectChar(each, PrimitiveFunctions.unboxIntegerToChar());
            Assert.assertTrue(result.containsAll(((char) (1)), ((char) (2)), ((char) (3)), ((char) (4)), ((char) (5))));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectChar(null, PrimitiveFunctions.unboxIntegerToChar()));
    }

    @Test
    public void collectCharWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableCharCollection expected = new CharArrayList();
            MutableCharCollection actual = Iterate.collectChar(each, PrimitiveFunctions.unboxIntegerToChar(), expected);
            Assert.assertTrue(actual.containsAll(((char) (1)), ((char) (2)), ((char) (3)), ((char) (4)), ((char) (5))));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectChar(null, PrimitiveFunctions.unboxIntegerToChar(), new CharArrayList()));
    }

    @Test
    public void collectDouble() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableDoubleCollection result = Iterate.collectDouble(each, PrimitiveFunctions.unboxIntegerToDouble());
            Assert.assertTrue(result.containsAll(1.0, 2.0, 3.0, 4.0, 5.0));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectDouble(null, PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Test
    public void collectDoubleWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableDoubleCollection expected = new DoubleArrayList();
            MutableDoubleCollection actual = Iterate.collectDouble(each, PrimitiveFunctions.unboxIntegerToDouble(), expected);
            Assert.assertTrue(actual.containsAll(1.0, 2.0, 3.0, 4.0, 5.0));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectDouble(null, PrimitiveFunctions.unboxIntegerToDouble(), new DoubleArrayList()));
    }

    @Test
    public void collectFloat() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableFloatCollection result = Iterate.collectFloat(each, PrimitiveFunctions.unboxIntegerToFloat());
            Assert.assertTrue(result.containsAll(1.0F, 2.0F, 3.0F, 4.0F, 5.0F));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectFloat(null, PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Test
    public void collectFloatWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableFloatCollection expected = new FloatArrayList();
            MutableFloatCollection actual = Iterate.collectFloat(each, PrimitiveFunctions.unboxIntegerToFloat(), expected);
            Assert.assertTrue(actual.containsAll(1.0F, 2.0F, 3.0F, 4.0F, 5.0F));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectFloat(null, PrimitiveFunctions.unboxIntegerToFloat(), new FloatArrayList()));
    }

    @Test
    public void collectInt() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableIntCollection result = Iterate.collectInt(each, PrimitiveFunctions.unboxIntegerToInt());
            Assert.assertTrue(result.containsAll(1, 2, 3, 4, 5));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectInt(null, PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Test
    public void collectIntWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableIntCollection expected = new IntArrayList();
            MutableIntCollection actual = Iterate.collectInt(each, PrimitiveFunctions.unboxIntegerToInt(), expected);
            Assert.assertTrue(actual.containsAll(1, 2, 3, 4, 5));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectInt(null, PrimitiveFunctions.unboxIntegerToInt(), new IntArrayList()));
    }

    @Test
    public void collectLong() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableLongCollection result = Iterate.collectLong(each, PrimitiveFunctions.unboxIntegerToLong());
            Assert.assertTrue(result.containsAll(1L, 2L, 3L, 4L, 5L));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectLong(null, PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Test
    public void collectLongWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableLongCollection expected = new LongArrayList();
            MutableLongCollection actual = Iterate.collectLong(each, PrimitiveFunctions.unboxIntegerToLong(), expected);
            Assert.assertTrue(actual.containsAll(1L, 2L, 3L, 4L, 5L));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectLong(null, PrimitiveFunctions.unboxIntegerToLong(), new LongArrayList()));
    }

    @Test
    public void collectShort() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableShortCollection result = Iterate.collectShort(each, PrimitiveFunctions.unboxIntegerToShort());
            Assert.assertTrue(result.containsAll(((short) (1)), ((short) (2)), ((short) (3)), ((short) (4)), ((short) (5))));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectShort(null, PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Test
    public void collectShortWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            MutableShortCollection expected = new ShortArrayList();
            MutableShortCollection actual = Iterate.collectShort(each, PrimitiveFunctions.unboxIntegerToShort(), expected);
            Assert.assertTrue(actual.containsAll(((short) (1)), ((short) (2)), ((short) (3)), ((short) (4)), ((short) (5))));
            Assert.assertSame("Target list sent as parameter not returned", expected, actual);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectShort(null, PrimitiveFunctions.unboxIntegerToShort(), new ShortArrayList()));
    }

    @Test
    public void collect_sortedSetSource() {
        class Foo implements Comparable<Foo> {
            private final int value;

            Foo(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }

            @Override
            public int compareTo(Foo that) {
                return Comparators.naturalOrder().compare(this.value, that.value);
            }

            @Override
            public boolean equals(Object o) {
                if ((this) == o) {
                    return true;
                }
                if ((o == null) || ((this.getClass()) != (o.getClass()))) {
                    return false;
                }
                Foo foo = ((Foo) (o));
                return (this.value) == (foo.value);
            }

            @Override
            public int hashCode() {
                return this.value;
            }
        }
        class Bar {
            private final int value;

            Bar(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }

            @Override
            public boolean equals(Object o) {
                if ((this) == o) {
                    return true;
                }
                if ((o == null) || ((this.getClass()) != (o.getClass()))) {
                    return false;
                }
                Bar bar = ((Bar) (o));
                return (this.value) == (bar.value);
            }

            @Override
            public int hashCode() {
                return this.value;
            }
        }
        Set<Foo> foos = new TreeSet<>();
        foos.add(new Foo(1));
        foos.add(new Foo(2));
        Collection<Bar> bars = Iterate.collect(foos, ( foo) -> new Bar(foo.getValue()));
        Assert.assertEquals(FastList.newListWith(new Bar(1), new Bar(2)), bars);
    }

    @Test
    public void collectTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<String> result = Iterate.collect(each, String::valueOf, UnifiedSet.<String>newSet());
            Assert.assertTrue(result.containsAll(FastList.newListWith("1", "2", "3", "4", "5")));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collect(null, ( a) -> a, null));
    }

    @Test
    public void collectWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<String> result = Iterate.collectWith(each, ( each1, parm) -> each1 + parm, " ");
            Assert.assertTrue(result.containsAll(FastList.newListWith("1 ", "2 ", "3 ", "4 ", "5 ")));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectWith(null, null, null));
    }

    @Test
    public void collectWithWithTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<String> result = Iterate.collectWith(each, ( each1, parm) -> each1 + parm, " ", UnifiedSet.newSet());
            Assert.assertTrue(result.containsAll(FastList.newListWith("1 ", "2 ", "3 ", "4 ", "5 ")));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.collectWith(null, null, null, null));
    }

    @Test
    public void removeIf() {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.assertRemoveIfFromList(integers);
        MutableList<Integer> integers2 = Interval.oneTo(5).toList();
        this.assertRemoveIfFromList(Collections.synchronizedList(integers2));
        MutableList<Integer> integers3 = Interval.oneTo(5).toList();
        this.assertRemoveIfFromList(FastList.newList(integers3));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.removeIf(null, null));
    }

    @Test
    public void removeIfWith() {
        MutableList<Integer> objects = FastList.newList(fixedSize.of(1, 2, 3, null));
        Assert.assertTrue(Iterate.removeIfWith(objects, ( each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        MutableList<Integer> objects2 = FastList.newList(fixedSize.of(null, 1, 2, 3));
        Assert.assertTrue(Iterate.removeIfWith(objects2, ( each, ignored) -> each == null, null));
        Verify.assertSize(3, objects2);
        Verify.assertContainsAll(objects2, 1, 2, 3);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.removeIfWith(null, null, null));
    }

    @Test
    public void removeIfLinkedList() {
        List<Integer> integers = new LinkedList(Interval.oneTo(5).toList());
        this.assertRemoveIfFromList(integers);
    }

    @Test
    public void removeIfAll() {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        Assert.assertTrue(Iterate.removeIf(integers, ( ignored) -> true));
        Verify.assertSize(0, integers);
    }

    @Test
    public void removeIfNone() {
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        Assert.assertFalse(Iterate.removeIf(integers, ( ignored) -> false));
        Verify.assertSize(5, integers);
    }

    @Test
    public void removeIfFromSet() {
        MutableSet<Integer> integers = Interval.toSet(1, 5);
        Assert.assertTrue(Iterate.removeIf(integers, IntegerPredicates.isEven()));
        Verify.assertContainsAll(integers, 1, 3, 5);
        Verify.assertSize(3, integers);
    }

    @Test
    public void removeIfWithFastList() {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3, null);
        Assert.assertTrue(Iterate.removeIfWith(objects, ( each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        MutableList<Integer> objects1 = FastList.newListWith(null, 1, 2, 3);
        Assert.assertTrue(Iterate.removeIfWith(objects1, ( each, ignored) -> each == null, null));
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);
    }

    @Test
    public void removeIfWithRandomAccess() {
        List<Integer> objects = Collections.synchronizedList(new ArrayList(fixedSize.of(1, 2, 3, null)));
        Assert.assertTrue(Iterate.removeIfWith(objects, ( each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        List<Integer> objects1 = Collections.synchronizedList(new ArrayList(fixedSize.of(null, 1, 2, 3)));
        Assert.assertTrue(Iterate.removeIfWith(objects1, ( each, ignored) -> each == null, null));
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);
    }

    @Test
    public void removeIfWithLinkedList() {
        List<Integer> objects = new LinkedList(fixedSize.of(1, 2, 3, null));
        Assert.assertTrue(Iterate.removeIfWith(objects, ( each1, ignored1) -> each1 == null, null));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
        List<Integer> objects1 = new LinkedList(fixedSize.of(null, 1, 2, 3));
        Assert.assertTrue(Iterate.removeIfWith(objects1, ( each, ignored) -> each == null, null));
        Verify.assertSize(3, objects1);
        Verify.assertContainsAll(objects1, 1, 2, 3);
    }

    @Test
    public void injectIntoIfProcedure() {
        Integer newItemToIndex = 99;
        MutableMap<String, Integer> index1 = IterateTest.createPretendIndex(1);
        MutableMap<String, Integer> index2 = IterateTest.createPretendIndex(2);
        MutableMap<String, Integer> index3 = IterateTest.createPretendIndex(3);
        MutableMap<String, Integer> index4 = IterateTest.createPretendIndex(4);
        MutableMap<String, Integer> index5 = IterateTest.createPretendIndex(5);
        MutableMap<String, MutableMap<String, Integer>> allIndexes = UnifiedMap.newMapWith(Tuples.pair("pretend index 1", index1), Tuples.pair("pretend index 2", index2), Tuples.pair("pretend index 3", index3), Tuples.pair("pretend index 4", index4), Tuples.pair("pretend index 5", index5));
        MutableSet<MutableMap<String, Integer>> systemIndexes = Sets.fixedSize.of(index3, index5);
        MapIterate.injectIntoIf(newItemToIndex, allIndexes, Predicates.notIn(systemIndexes), ( itemToAdd, index) -> {
            index.put(itemToAdd.toString(), itemToAdd);
            return itemToAdd;
        });
        Verify.assertSize(5, allIndexes);
        Verify.assertContainsKey("pretend index 2", allIndexes);
        Verify.assertContainsKey("pretend index 3", allIndexes);
        Verify.assertContainsKeyValue("99", newItemToIndex, index1);
        Verify.assertContainsKeyValue("99", newItemToIndex, index2);
        Verify.assertNotContainsKey("99", index3);
        Verify.assertContainsKeyValue("99", newItemToIndex, index4);
        Verify.assertNotContainsKey("99", index5);
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(Iterate.isEmpty(null));
        Assert.assertTrue(Iterate.isEmpty(fixedSize.of()));
        Assert.assertFalse(Iterate.isEmpty(fixedSize.of("1")));
        Assert.assertTrue(Iterate.isEmpty(Maps.fixedSize.of()));
        Assert.assertFalse(Iterate.isEmpty(Maps.fixedSize.of("1", "1")));
        Assert.assertTrue(Iterate.isEmpty(new IterateTest.IterableAdapter(fixedSize.of())));
        Assert.assertFalse(Iterate.isEmpty(new IterateTest.IterableAdapter(fixedSize.of("1"))));
        Assert.assertTrue(Iterate.isEmpty(fixedSize.of().asLazy()));
        Assert.assertFalse(Iterate.isEmpty(fixedSize.of("1").asLazy()));
    }

    @Test
    public void notEmpty() {
        Assert.assertFalse(Iterate.notEmpty(null));
        Assert.assertFalse(Iterate.notEmpty(fixedSize.of()));
        Assert.assertTrue(Iterate.notEmpty(fixedSize.of("1")));
        Assert.assertFalse(Iterate.notEmpty(Maps.fixedSize.of()));
        Assert.assertTrue(Iterate.notEmpty(Maps.fixedSize.of("1", "1")));
        Assert.assertFalse(Iterate.notEmpty(new IterateTest.IterableAdapter(fixedSize.of())));
        Assert.assertTrue(Iterate.notEmpty(new IterateTest.IterableAdapter(fixedSize.of("1"))));
        Assert.assertFalse(Iterate.notEmpty(fixedSize.of().asLazy()));
        Assert.assertTrue(Iterate.notEmpty(fixedSize.of("1").asLazy()));
    }

    @Test
    public void toSortedList() {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> sorted = Iterate.toSortedList(list);
        Verify.assertStartsWith(sorted, 1, 2, 3, 4, 5);
    }

    @Test
    public void toSortedListWithComparator() {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        MutableList<Integer> sorted = Iterate.toSortedList(list, Collections.reverseOrder());
        Verify.assertStartsWith(sorted, 5, 4, 3, 2, 1);
    }

    @Test
    public void select() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.select(each, Predicates.greaterThan(3));
            Assert.assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.select(null, null));
    }

    @Test
    public void selectTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.select(each, Predicates.greaterThan(3), FastList.newList());
            Assert.assertTrue(result.containsAll(FastList.newListWith(4, 5)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.select(null, null, null));
    }

    @Test
    public void reject() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.reject(each, Predicates.greaterThan(3));
            Assert.assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.reject(null, null));
    }

    @Test
    public void rejectTarget() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.reject(each, Predicates.greaterThan(3), FastList.newList());
            Assert.assertTrue(result.containsAll(FastList.newListWith(1, 2, 3)));
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.reject(null, null, null));
    }

    @Test
    public void selectInstancesOf() {
        Iterable<Number> numbers1 = Collections.unmodifiableList(new ArrayList<Number>(FastList.newListWith(1, 2.0, 3, 4.0, 5)));
        Iterable<Number> numbers2 = Collections.unmodifiableCollection(new ArrayList<Number>(FastList.newListWith(1, 2.0, 3, 4.0, 5)));
        Verify.assertContainsAll(Iterate.selectInstancesOf(numbers1, Integer.class), 1, 3, 5);
        Verify.assertContainsAll(Iterate.selectInstancesOf(numbers2, Integer.class), 1, 3, 5);
    }

    @Test
    public void count() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            int result = Iterate.count(each, Predicates.greaterThan(3));
            Assert.assertEquals(2, result);
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void count_null_null_throws() {
        Iterate.count(null, null);
    }

    @Test
    public void countWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            int result = Iterate.countWith(each, Predicates2.greaterThan(), 3);
            Assert.assertEquals(2, result);
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void countWith_null_throws() {
        Iterate.countWith(null, null, null);
    }

    @Test
    public void injectIntoWith() {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        MutableList<Integer> integers = Interval.oneTo(5).toList();
        this.basicTestDoubleSum(result, integers, parameter);
    }

    @Test
    public void injectIntoWithRandomAccess() {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        MutableList<Integer> integers = Interval.oneTo(5).toList().asSynchronized();
        this.basicTestDoubleSum(result, integers, parameter);
    }

    @Test
    public void injectIntoWithHashSet() {
        Sum result = new IntegerSum(0);
        Integer parameter = 2;
        MutableSet<Integer> integers = Interval.toSet(1, 5);
        this.basicTestDoubleSum(result, integers, parameter);
    }

    @Test
    public void forEachWith() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            Sum result = new IntegerSum(0);
            Iterate.forEachWith(each, ( integer, parm) -> result.add(((integer.intValue()) * (parm.intValue()))), 2);
            Assert.assertEquals(30, result.getValue().intValue());
        }));
    }

    @Test
    public void forEachWithSets() {
        Sum result = new IntegerSum(0);
        MutableSet<Integer> integers = Interval.toSet(1, 5);
        Iterate.forEachWith(integers, ( each, parm) -> result.add(((each.intValue()) * (parm.intValue()))), 2);
        Assert.assertEquals(30, result.getValue().intValue());
    }

    @Test
    public void sortThis() {
        MutableList<Integer> list = Interval.oneTo(5).toList().shuffleThis();
        Verify.assertStartsWith(Iterate.sortThis(list), 1, 2, 3, 4, 5);
        List<Integer> list3 = Interval.oneTo(5).addAllTo(new LinkedList());
        Collections.shuffle(list3);
        Verify.assertStartsWith(Iterate.sortThis(list3), 1, 2, 3, 4, 5);
        List<Integer> listOfSizeOne = new LinkedList<>();
        listOfSizeOne.add(1);
        Iterate.sortThis(listOfSizeOne);
        Assert.assertEquals(FastList.newListWith(1), listOfSizeOne);
    }

    @Test
    public void sortThisWithPredicate() {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        Interval.oneTo(5).addAllTo(list);
        list.shuffleThis();
        Verify.assertStartsWith(Iterate.sortThis(list, Predicates2.<Integer>lessThan()), 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
        MutableList<Integer> list2 = Interval.oneTo(5).toList();
        Interval.oneTo(5).addAllTo(list2);
        list2.shuffleThis();
        Verify.assertStartsWith(Iterate.sortThis(list2, Predicates2.<Integer>greaterThan()), 5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
        List<Integer> list3 = Interval.oneTo(5).addAllTo(new LinkedList());
        Interval.oneTo(5).addAllTo(list3);
        Collections.shuffle(list3);
        Verify.assertStartsWith(Iterate.sortThis(list3, Predicates2.<Integer>lessThan()), 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
    }

    @Test
    public void sortThisBy() {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        Interval.oneTo(5).addAllTo(list);
        list.shuffleThis();
        MutableList<Integer> sortedList = Iterate.sortThisBy(list, String::valueOf);
        Assert.assertSame(list, sortedList);
        Assert.assertEquals(immutable.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5), list);
    }

    @Test
    public void sortThisWithComparator() {
        MutableList<Integer> list = Interval.oneTo(5).toList();
        Verify.assertStartsWith(Iterate.sortThis(list, Collections.reverseOrder()), 5, 4, 3, 2, 1);
        List<Integer> list3 = Interval.oneTo(5).addAllTo(new LinkedList());
        Verify.assertStartsWith(Iterate.sortThis(list3, Collections.reverseOrder()), 5, 4, 3, 2, 1);
    }

    @Test
    public void take() {
        MutableSet<Integer> set = this.getIntegerSet();
        Verify.assertEmpty(Iterate.take(set, 0));
        Verify.assertSize(1, Iterate.take(set, 1));
        Verify.assertSize(2, Iterate.take(set, 2));
        Verify.assertSize(4, Iterate.take(set, 4));
        Verify.assertSize(5, Iterate.take(set, 5));
        Verify.assertSize(5, Iterate.take(set, 10));
        Verify.assertSize(5, Iterate.take(set, Integer.MAX_VALUE));
        MutableSet<Integer> set2 = UnifiedSet.newSet();
        Verify.assertEmpty(Iterate.take(set2, 2));
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.take(each, 2);
            Verify.assertSize(2, result);
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_null_throws() {
        Iterate.take(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_negative_throws() {
        Iterate.take(this.getIntegerSet(), (-1));
    }

    @Test
    public void drop() {
        MutableSet<Integer> set = this.getIntegerSet();
        Verify.assertSize(5, Iterate.drop(set, 0));
        Verify.assertSize(4, Iterate.drop(set, 1));
        Verify.assertSize(3, Iterate.drop(set, 2));
        Verify.assertSize(1, Iterate.drop(set, 4));
        Verify.assertEmpty(Iterate.drop(set, 5));
        Verify.assertEmpty(Iterate.drop(set, 6));
        Verify.assertEmpty(Iterate.drop(set, Integer.MAX_VALUE));
        MutableSet<Integer> set2 = UnifiedSet.newSet();
        Verify.assertEmpty(Iterate.drop(set2, 2));
        this.iterables.forEach(Procedures.cast(( each) -> {
            Collection<Integer> result = Iterate.drop(each, 2);
            Verify.assertSize(3, result);
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_null_throws() {
        Iterate.drop(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_negative_throws() {
        Iterate.drop(this.getIntegerSet(), (-1));
    }

    @Test
    public void getOnlySingleton() {
        Object value = new Object();
        Assert.assertSame(value, Iterate.getOnly(fixedSize.of(value)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOnlyEmpty() {
        Iterate.getOnly(fixedSize.<String>of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOnlyMultiple() {
        Iterate.getOnly(fixedSize.of(new Object(), new Object()));
    }

    private static final class IterableAdapter<E> implements Iterable<E> {
        private final Iterable<E> iterable;

        private IterableAdapter(Iterable<E> newIterable) {
            this.iterable = newIterable;
        }

        @Override
        public Iterator<E> iterator() {
            return this.iterable.iterator();
        }
    }

    @Test
    public void zip() {
        this.zip(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"));
        this.zip(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        this.zip(new java.util.HashSet(FastList.newListWith("1", "2", "3", "4", "5", "6", "7")));
        this.zip(FastList.newListWith("1", "2", "3", "4", "5", "6", "7").asLazy());
        this.zip(new ArrayList(Interval.oneTo(101).collect(String::valueOf).toList()));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.zip(null, null));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.zip(null, null, null));
    }

    @Test
    public void zipWithIndex() {
        this.zipWithIndex(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"));
        this.zipWithIndex(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
        this.zipWithIndex(new java.util.HashSet(FastList.newListWith("1", "2", "3", "4", "5", "6", "7")));
        this.zipWithIndex(FastList.newListWith("1", "2", "3", "4", "5", "6", "7").asLazy());
        this.zipWithIndex(immutable.of("1", "2", "3", "4", "5", "6", "7"));
        this.zipWithIndex(new ArrayList(Interval.oneTo(101).collect(String::valueOf).toList()));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.zipWithIndex(null));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.zipWithIndex(null, null));
    }

    @Test
    public void chunk() {
        FastList<String> fastList = FastList.newListWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups1 = Iterate.chunk(fastList, 2);
        RichIterable<Integer> sizes1 = groups1.collect(Functions.getSizeOf());
        Assert.assertEquals(FastList.newListWith(2, 2, 2, 1), sizes1);
        ArrayList<String> arrayList = new ArrayList(fastList);
        RichIterable<RichIterable<String>> groups2 = Iterate.chunk(arrayList, 2);
        RichIterable<Integer> sizes2 = groups1.collect(Functions.getSizeOf());
        Assert.assertEquals(FastList.newListWith(2, 2, 2, 1), sizes2);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.chunk(null, 1));
    }

    @Test
    public void getOnly() {
        Assert.assertEquals("first", Iterate.getOnly(FastList.newListWith("first")));
        Assert.assertEquals("first", Iterate.getOnly(FastList.newListWith("first").asLazy()));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.getOnly(FastList.newListWith("first", "second")));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.getOnly(null));
    }

    private static class WordToItsLetters implements Function<String, Set<Character>> {
        private static final long serialVersionUID = 1L;

        @Override
        public Set<Character> valueOf(String name) {
            return StringIterate.asUppercaseSet(name);
        }
    }

    @Test
    public void makeString() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            String result = Iterate.makeString(each);
            Assert.assertEquals("1, 2, 3, 4, 5", result);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.makeString(null));
    }

    @Test
    public void appendString() {
        this.iterables.forEach(Procedures.cast(( each) -> {
            StringBuilder stringBuilder = new StringBuilder();
            Iterate.appendString(each, stringBuilder);
            String result = stringBuilder.toString();
            Assert.assertEquals("1, 2, 3, 4, 5", result);
        }));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.appendString(null, new StringBuilder()));
    }

    @Test
    public void aggregateByMutating() {
        this.aggregateByMutableResult(FastList.newListWith(1, 1, 1, 2, 2, 3));
        this.aggregateByMutableResult(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3));
        this.aggregateByMutableResult(HashBag.newBagWith(1, 1, 1, 2, 2, 3));
        this.aggregateByMutableResult(new java.util.HashSet(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByMutableResult(new LinkedList(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByMutableResult(new ArrayList(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByMutableResult(Arrays.asList(1, 1, 1, 2, 2, 3));
        Verify.assertThrows(IllegalArgumentException.class, () -> this.aggregateByMutableResult(null));
    }

    @Test
    public void aggregateByImmutableResult() {
        this.aggregateByImmutableResult(FastList.newListWith(1, 1, 1, 2, 2, 3));
        this.aggregateByImmutableResult(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3));
        this.aggregateByImmutableResult(HashBag.newBagWith(1, 1, 1, 2, 2, 3));
        this.aggregateByImmutableResult(new java.util.HashSet(UnifiedSet.newSetWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByImmutableResult(new LinkedList(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByImmutableResult(new ArrayList(FastList.newListWith(1, 1, 1, 2, 2, 3)));
        this.aggregateByImmutableResult(Arrays.asList(1, 1, 1, 2, 2, 3));
        Verify.assertThrows(IllegalArgumentException.class, () -> this.aggregateByImmutableResult(null));
    }

    @Test
    public void sumOfInt() {
        this.iterables.each(( each) -> Assert.assertEquals(15L, Iterate.sumOfLong(each, ( value) -> value)));
        long result = Iterate.sumOfInt(FastList.<Integer>newListWith(2000000000, 2000000000, 2000000000), ( e) -> e);
        Assert.assertEquals(6000000000L, result);
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfInt(null, null));
    }

    @Test
    public void sumOfLong() {
        this.iterables.each(( each) -> Assert.assertEquals(15L, Iterate.sumOfLong(each, Integer::longValue)));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfLong(null, null));
    }

    @Test
    public void sumOfFloat() {
        this.iterables.each(( each) -> Assert.assertEquals(15.0, Iterate.sumOfFloat(each, Integer::floatValue), 0.0));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfFloat(null, null));
        double result = Iterate.sumOfFloat(FastList.<Integer>newListWith(2000000000, 2000000000, 2000000000), ( e) -> e);
        Assert.assertEquals(((double) (6000000000L)), result, 0.0);
    }

    @Test
    public void sumOfDouble() {
        this.iterables.each(( each) -> Assert.assertEquals(15.0, Iterate.sumOfDouble(each, Integer::doubleValue), 0.0));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfDouble(null, null));
    }

    @Test
    public void sumOfBigDecimal() {
        this.iterables.each(( each) -> Assert.assertEquals(new BigDecimal(15), Iterate.sumOfBigDecimal(each, BigDecimal::new)));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfBigDecimal(null, null));
    }

    @Test
    public void sumOfBigInteger() {
        this.iterables.each(( each) -> Assert.assertEquals(new BigInteger("15"), Iterate.sumOfBigInteger(each, ( integer) -> new BigInteger(integer.toString()))));
        Verify.assertThrows(IllegalArgumentException.class, () -> Iterate.sumOfBigDecimal(null, null));
    }

    @Test
    public void sumByBigDecimal() {
        this.iterables.each(( each) -> {
            MutableMap<Integer, BigDecimal> result = Iterate.sumByBigDecimal(each, ( e) -> e % 2, BigDecimal::new);
            Assert.assertEquals(new BigDecimal(9), result.get(1));
            Assert.assertEquals(new BigDecimal(6), result.get(0));
        });
    }

    @Test
    public void sumByBigInteger() {
        this.iterables.each(( each) -> {
            MutableMap<Integer, BigInteger> result = Iterate.sumByBigInteger(each, ( e) -> e % 2, ( i) -> new BigInteger(i.toString()));
            Assert.assertEquals(new BigInteger("9"), result.get(1));
            Assert.assertEquals(new BigInteger("6"), result.get(0));
        });
    }

    @Test
    public void sumByInt() {
        this.iterables.each(( each) -> {
            ObjectLongMap<Integer> result = Iterate.sumByInt(each, ( i) -> i % 2, ( e) -> e);
            Assert.assertEquals(9, result.get(1));
            Assert.assertEquals(6, result.get(0));
        });
        com.gs.collections.api.map.primitive.ObjectLongMap<Integer> map = Iterate.sumByInt(FastList.<Integer>newListWith(2000000000, 2000000001, 2000000000, 2000000001, 2000000000), ( i) -> i % 2, ( e) -> e);
        Assert.assertEquals(4000000002L, map.get(1));
        Assert.assertEquals(6000000000L, map.get(0));
    }

    @Test
    public void sumByFloat() {
        this.iterables.each(( each) -> {
            ObjectDoubleMap<Integer> result = Iterate.sumByFloat(each, ( f) -> f % 2, ( e) -> e);
            Assert.assertEquals(9.0, result.get(1), 0.0);
            Assert.assertEquals(6.0, result.get(0), 0.0);
        });
        com.gs.collections.api.map.primitive.ObjectDoubleMap<Integer> map = Iterate.sumByFloat(FastList.<Integer>newListWith(2000000000, 2000001, 2000000000, 2000001, 2000000000), ( i) -> i % 2, ( e) -> e);
        Assert.assertEquals(((double) (4000002L)), map.get(1), 1.0E-5);
        Assert.assertEquals(((double) (6000000000L)), map.get(0), 1.0E-5);
    }

    @Test
    public void sumByLong() {
        this.iterables.each(( each) -> {
            ObjectLongMap<Integer> result = Iterate.sumByLong(each, ( l) -> l % 2, ( e) -> e);
            Assert.assertEquals(9, result.get(1));
            Assert.assertEquals(6, result.get(0));
        });
    }

    @Test
    public void sumByDouble() {
        this.iterables.each(( each) -> {
            ObjectDoubleMap<Integer> result = Iterate.sumByDouble(each, ( d) -> d % 2, ( e) -> e);
            Assert.assertEquals(9.0, result.get(1), 0.0);
            Assert.assertEquals(6.0, result.get(0), 0.0);
        });
    }

    @Test
    public void minBy() {
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(1, 2, 3), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(3, 2, 1), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(1, 2, 3).asSynchronized(), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(FastList.newListWith(3, 2, 1).asSynchronized(), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(Arrays.asList(1, 2, 3), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(Arrays.asList(3, 2, 1), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(new LinkedList(Arrays.asList(1, 2, 3)), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(1), Iterate.minBy(new LinkedList(Arrays.asList(3, 2, 1)), Functions.getIntegerPassThru()));
    }

    @Test
    public void minByThrowsOnEmpty() {
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.minBy(FastList.<Integer>newList(), Functions.getIntegerPassThru()));
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.minBy(FastList.<Integer>newList().asSynchronized(), Functions.getIntegerPassThru()));
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.minBy(Arrays.<Integer>asList(), Functions.getIntegerPassThru()));
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.minBy(new LinkedList<>(), Functions.getIntegerPassThru()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void minByThrowsOnNull() {
        Iterate.minBy(null, null);
    }

    @Test
    public void maxBy() {
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(1, 2, 3), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(3, 2, 1), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(1, 2, 3).asSynchronized(), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(FastList.newListWith(3, 2, 1).asSynchronized(), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(Arrays.asList(1, 2, 3), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(Arrays.asList(3, 2, 1), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(new LinkedList(Arrays.asList(1, 2, 3)), Functions.getIntegerPassThru()));
        Assert.assertEquals(Integer.valueOf(3), Iterate.maxBy(new LinkedList(Arrays.asList(3, 2, 1)), Functions.getIntegerPassThru()));
    }

    @Test
    public void maxByThrowsOnEmpty() {
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(FastList.<Integer>newList(), Functions.getIntegerPassThru()));
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(FastList.<Integer>newList().asSynchronized(), Functions.getIntegerPassThru()));
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(Arrays.<Integer>asList(), Functions.getIntegerPassThru()));
        Verify.assertThrows(NoSuchElementException.class, () -> Iterate.maxBy(new LinkedList<>(), Functions.getIntegerPassThru()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxByThrowsOnNull() {
        Iterate.maxBy(null, null);
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(Iterate.class);
    }

    @Test
    public void groupByUniqueKey() {
        Assert.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), Iterate.groupByUniqueKey(FastList.newListWith(1, 2, 3), ( id) -> id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupByUniqueKey_throws_for_null() {
        Iterate.groupByUniqueKey(null, ( id) -> id);
    }

    @Test
    public void groupByUniqueKey_target() {
        Assert.assertEquals(UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3), Iterate.groupByUniqueKey(FastList.newListWith(1, 2, 3), ( id) -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupByUniqueKey_target_throws_for_null() {
        Iterate.groupByUniqueKey(null, ( id) -> id, UnifiedMap.newMap());
    }
}

