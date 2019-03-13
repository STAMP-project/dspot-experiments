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
package com.gs.collections.test;


import AddFunction.INTEGER;
import AddFunction.INTEGER_TO_DOUBLE;
import AddFunction.INTEGER_TO_FLOAT;
import AddFunction.INTEGER_TO_INT;
import AddFunction.INTEGER_TO_LONG;
import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.Counter;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ByteHashBag;
import com.gs.collections.impl.bag.mutable.primitive.CharHashBag;
import com.gs.collections.impl.bag.mutable.primitive.DoubleHashBag;
import com.gs.collections.impl.bag.mutable.primitive.FloatHashBag;
import com.gs.collections.impl.bag.mutable.primitive.IntHashBag;
import com.gs.collections.impl.bag.mutable.primitive.LongHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ShortHashBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.tuple.Tuples;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface RichIterableTestCase extends IterableTestCase {
    @Test
    default void newMutable_sanity() {
        IterableTestCase.assertEquals(this.getExpectedFiltered(3, 2, 1), this.newMutableForFilter(3, 2, 1));
    }

    @Test
    default void InternalIterable_forEach() {
        RichIterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEach(Procedures.cast(( i) -> result.add((i + 10))));
        IterableTestCase.assertEquals(this.newMutableForFilter(13, 13, 13, 12, 12, 11), result);
    }

    @Test
    default void InternalIterable_forEachWith() {
        RichIterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEachWith(( argument1, argument2) -> result.add((argument1 + argument2)), 10);
        IterableTestCase.assertEquals(this.newMutableForFilter(13, 13, 13, 12, 12, 11), result);
    }

    @Test
    default void RichIterable_size_empty() {
        IterableTestCase.assertEquals(0, this.newWith().size());
    }

    @Test
    default void RichIterable_isEmpty() {
        Assert.assertFalse(this.newWith(3, 2, 1).isEmpty());
        Assert.assertTrue(this.newWith().isEmpty());
    }

    @Test
    default void RichIterable_notEmpty() {
        Assert.assertTrue(this.newWith(3, 2, 1).notEmpty());
        Assert.assertFalse(this.newWith().notEmpty());
    }

    @Test
    default void RichIterable_getFirst_empty_null() {
        Assert.assertNull(this.newWith().getFirst());
    }

    @Test
    default void RichIterable_getLast_empty_null() {
        Assert.assertNull(this.newWith().getLast());
    }

    @Test
    default void RichIterable_getFirst() {
        RichIterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        Integer first = iterable.getFirst();
        Assert.assertThat(first, Matchers.isOneOf(3, 2, 1));
        IterableTestCase.assertEquals(iterable.iterator().next(), first);
    }

    @Test
    default void RichIterable_getLast() {
        RichIterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        Integer last = iterable.getLast();
        Assert.assertThat(last, Matchers.isOneOf(3, 2, 1));
        Iterator<Integer> iterator = iterable.iterator();
        Integer iteratorLast = null;
        while (iterator.hasNext()) {
            iteratorLast = iterator.next();
        } 
        IterableTestCase.assertEquals(iteratorLast, last);
    }

    @Test
    default void RichIterable_getFirst_and_getLast() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertNotEquals(iterable.getFirst(), iterable.getLast());
    }

    @Test
    default void RichIterable_contains() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertTrue(iterable.contains(3));
        Assert.assertTrue(iterable.contains(2));
        Assert.assertTrue(iterable.contains(1));
        Assert.assertFalse(iterable.contains(0));
    }

    @Test
    default void RichIterable_containsAllIterable() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertTrue(iterable.containsAllIterable(immutable.of(3)));
        Assert.assertTrue(iterable.containsAllIterable(immutable.of(3, 2, 1)));
        Assert.assertTrue(iterable.containsAllIterable(immutable.of(3, 3, 3)));
        Assert.assertTrue(iterable.containsAllIterable(immutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        Assert.assertFalse(iterable.containsAllIterable(immutable.of(4)));
        Assert.assertFalse(iterable.containsAllIterable(immutable.of(4, 4, 5)));
        Assert.assertFalse(iterable.containsAllIterable(immutable.of(3, 2, 1, 0)));
    }

    @Test
    default void RichIterable_containsAll() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertTrue(iterable.containsAll(mutable.of(3)));
        Assert.assertTrue(iterable.containsAll(mutable.of(3, 2, 1)));
        Assert.assertTrue(iterable.containsAll(mutable.of(3, 3, 3)));
        Assert.assertTrue(iterable.containsAll(mutable.of(3, 3, 3, 3, 2, 2, 2, 1, 1)));
        Assert.assertFalse(iterable.containsAll(mutable.of(4)));
        Assert.assertFalse(iterable.containsAll(mutable.of(4, 4, 5)));
        Assert.assertFalse(iterable.containsAll(mutable.of(3, 2, 1, 0)));
    }

    @Test
    default void RichIterable_containsAllArguments() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertTrue(iterable.containsAllArguments(3));
        Assert.assertTrue(iterable.containsAllArguments(3, 2, 1));
        Assert.assertTrue(iterable.containsAllArguments(3, 3, 3));
        Assert.assertTrue(iterable.containsAllArguments(3, 3, 3, 3, 2, 2, 2, 1, 1));
        Assert.assertFalse(iterable.containsAllArguments(4));
        Assert.assertFalse(iterable.containsAllArguments(4, 4, 5));
        Assert.assertFalse(iterable.containsAllArguments(3, 2, 1, 0));
    }

    @Test
    default void RichIterable_iterator_iterationOrder() {
        MutableCollection<Integer> iterationOrder = this.newMutableForFilter();
        Iterator<Integer> iterator = this.getInstanceUnderTest().iterator();
        while (iterator.hasNext()) {
            iterationOrder.add(iterator.next());
        } 
        IterableTestCase.assertEquals(this.expectedIterationOrder(), iterationOrder);
        MutableCollection<Integer> expectedIterationOrder = this.expectedIterationOrder();
        MutableCollection<Integer> forEachWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().forEachWith(( each, param) -> forEachWithIterationOrder.add(each), null);
        IterableTestCase.assertEquals(expectedIterationOrder, forEachWithIterationOrder);
        MutableCollection<Integer> forEachWithIndexIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().forEachWithIndex(( each, index) -> forEachWithIndexIterationOrder.add(each));
        IterableTestCase.assertEquals(expectedIterationOrder, forEachWithIndexIterationOrder);
    }

    @Test
    default void RichIterable_iterationOrder() {
        MutableCollection<Integer> expectedIterationOrder = this.expectedIterationOrder();
        Procedure<Object> noop = ( each) -> {
        };
        MutableCollection<Integer> selectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().select(selectIterationOrder::add).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, selectIterationOrder);
        MutableCollection<Integer> selectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().select(selectTargetIterationOrder::add, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, selectTargetIterationOrder);
        MutableCollection<Integer> selectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().selectWith(( each, param) -> selectWithIterationOrder.add(each), null).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, selectWithIterationOrder);
        MutableCollection<Integer> selectWithTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().selectWith(( each, param) -> selectWithTargetIterationOrder.add(each), null, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, selectWithTargetIterationOrder);
        MutableCollection<Integer> rejectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().reject(rejectIterationOrder::add).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, rejectIterationOrder);
        MutableCollection<Integer> rejectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().reject(rejectTargetIterationOrder::add, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, rejectTargetIterationOrder);
        MutableCollection<Integer> rejectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().rejectWith(( each, param) -> rejectWithIterationOrder.add(each), null).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, rejectWithIterationOrder);
        MutableCollection<Integer> rejectWithTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().rejectWith(( each, param) -> rejectWithTargetIterationOrder.add(each), null, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, rejectWithTargetIterationOrder);
        MutableCollection<Integer> partitionIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().partition(partitionIterationOrder::add);
        IterableTestCase.assertEquals(expectedIterationOrder, partitionIterationOrder);
        MutableCollection<Integer> partitionWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().partitionWith(( each, param) -> partitionWithIterationOrder.add(each), null);
        IterableTestCase.assertEquals(expectedIterationOrder, partitionWithIterationOrder);
        MutableCollection<Integer> collectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collect(collectIterationOrder::add).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, collectIterationOrder);
        MutableCollection<Integer> collectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collect(collectTargetIterationOrder::add, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectTargetIterationOrder);
        MutableCollection<Integer> collectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectWith(( each, param) -> collectWithIterationOrder.add(each), null).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, collectWithIterationOrder);
        MutableCollection<Integer> collectWithTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectWith(( each, param) -> collectWithTargetIterationOrder.add(each), null, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectWithTargetIterationOrder);
        MutableCollection<Integer> collectIfPredicateIterationOrder = this.newMutableForFilter();
        MutableCollection<Integer> collectIfFunctionIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectIf(collectIfPredicateIterationOrder::add, collectIfFunctionIterationOrder::add).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, collectIfPredicateIterationOrder);
        IterableTestCase.assertEquals(expectedIterationOrder, collectIfFunctionIterationOrder);
        MutableCollection<Integer> collectIfPredicateTargetIterationOrder = this.newMutableForFilter();
        MutableCollection<Integer> collectIfFunctionTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectIf(collectIfPredicateTargetIterationOrder::add, collectIfFunctionTargetIterationOrder::add, new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectIfPredicateTargetIterationOrder);
        IterableTestCase.assertEquals(expectedIterationOrder, collectIfFunctionTargetIterationOrder);
        MutableCollection<Integer> collectBooleanIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectBoolean(collectBooleanIterationOrder::add).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectBooleanIterationOrder);
        MutableCollection<Integer> collectBooleanTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectBoolean(collectBooleanTargetIterationOrder::add, new BooleanHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectBooleanTargetIterationOrder);
        MutableCollection<Integer> collectByteIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectByte((Integer each) -> {
            collectByteIterationOrder.add(each);
            return ((byte) (0));
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectByteIterationOrder);
        MutableCollection<Integer> collectByteTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectByte((Integer each) -> {
            collectByteTargetIterationOrder.add(each);
            return ((byte) (0));
        }, new ByteHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectByteTargetIterationOrder);
        MutableCollection<Integer> collectCharIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectChar((Integer each) -> {
            collectCharIterationOrder.add(each);
            return ' ';
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectCharIterationOrder);
        MutableCollection<Integer> collectCharTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectChar((Integer each) -> {
            collectCharTargetIterationOrder.add(each);
            return ' ';
        }, new CharHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectCharTargetIterationOrder);
        MutableCollection<Integer> collectDoubleIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectDouble((Integer each) -> {
            collectDoubleIterationOrder.add(each);
            return 0.0;
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectDoubleIterationOrder);
        MutableCollection<Integer> collectDoubleTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectDouble((Integer each) -> {
            collectDoubleTargetIterationOrder.add(each);
            return 0.0;
        }, new DoubleHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectDoubleTargetIterationOrder);
        MutableCollection<Integer> collectFloatIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectFloat((Integer each) -> {
            collectFloatIterationOrder.add(each);
            return 0.0F;
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectFloatIterationOrder);
        MutableCollection<Integer> collectFloatTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectFloat((Integer each) -> {
            collectFloatTargetIterationOrder.add(each);
            return 0.0F;
        }, new FloatHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectFloatTargetIterationOrder);
        MutableCollection<Integer> collectIntIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectInt((Integer each) -> {
            collectIntIterationOrder.add(each);
            return 0;
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectIntIterationOrder);
        MutableCollection<Integer> collectIntTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectInt((Integer each) -> {
            collectIntTargetIterationOrder.add(each);
            return 0;
        }, new IntHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectIntTargetIterationOrder);
        MutableCollection<Integer> collectLongIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectLong((Integer each) -> {
            collectLongIterationOrder.add(each);
            return 0L;
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectLongIterationOrder);
        MutableCollection<Integer> collectLongTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectLong((Integer each) -> {
            collectLongTargetIterationOrder.add(each);
            return 0L;
        }, new LongHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectLongTargetIterationOrder);
        MutableCollection<Integer> collectShortIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectShort(new com.gs.collections.api.block.function.primitive.ShortFunction<Integer>() {
            @Override
            public short shortValueOf(Integer each) {
                collectShortIterationOrder.add(each);
                return 0;
            }
        }).forEach(( each) -> {
        });
        IterableTestCase.assertEquals(expectedIterationOrder, collectShortIterationOrder);
        MutableCollection<Integer> collectShortTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().collectShort(new com.gs.collections.api.block.function.primitive.ShortFunction<Integer>() {
            @Override
            public short shortValueOf(Integer each) {
                collectShortTargetIterationOrder.add(each);
                return 0;
            }
        }, new ShortHashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, collectShortTargetIterationOrder);
        MutableCollection<Integer> flatCollectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().flatCollect(( each) -> Lists.immutable.with(flatCollectIterationOrder.add(each))).forEach(noop);
        IterableTestCase.assertEquals(expectedIterationOrder, flatCollectIterationOrder);
        MutableCollection<Integer> flatCollectTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().flatCollect(( each) -> Lists.immutable.with(flatCollectTargetIterationOrder.add(each)), new HashBag());
        IterableTestCase.assertEquals(expectedIterationOrder, flatCollectTargetIterationOrder);
        MutableCollection<Integer> countIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().count(countIterationOrder::add);
        IterableTestCase.assertEquals(expectedIterationOrder, countIterationOrder);
        MutableCollection<Integer> countWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().countWith(( each, param) -> countWithIterationOrder.add(each), null);
        IterableTestCase.assertEquals(expectedIterationOrder, countWithIterationOrder);
        MutableCollection<Integer> anySatisfyIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().anySatisfy(( each) -> {
            anySatisfyIterationOrder.add(each);
            return false;
        });
        IterableTestCase.assertEquals(expectedIterationOrder, anySatisfyIterationOrder);
        MutableCollection<Integer> anySatisfyWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().anySatisfyWith(( each, param) -> {
            anySatisfyWithIterationOrder.add(each);
            return false;
        }, null);
        IterableTestCase.assertEquals(expectedIterationOrder, anySatisfyWithIterationOrder);
        MutableCollection<Integer> allSatisfyIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().allSatisfy(( each) -> {
            allSatisfyIterationOrder.add(each);
            return true;
        });
        IterableTestCase.assertEquals(expectedIterationOrder, allSatisfyIterationOrder);
        MutableCollection<Integer> allSatisfyWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().allSatisfyWith(( each, param) -> {
            allSatisfyWithIterationOrder.add(each);
            return true;
        }, null);
        IterableTestCase.assertEquals(expectedIterationOrder, allSatisfyWithIterationOrder);
        MutableCollection<Integer> noneSatisfyIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().noneSatisfy(( each) -> {
            noneSatisfyIterationOrder.add(each);
            return false;
        });
        IterableTestCase.assertEquals(expectedIterationOrder, noneSatisfyIterationOrder);
        MutableCollection<Integer> noneSatisfyWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().noneSatisfyWith(( each, param) -> {
            noneSatisfyWithIterationOrder.add(each);
            return false;
        }, null);
        IterableTestCase.assertEquals(expectedIterationOrder, noneSatisfyWithIterationOrder);
        MutableCollection<Integer> detectIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detect(( each) -> {
            detectIterationOrder.add(each);
            return false;
        });
        IterableTestCase.assertEquals(expectedIterationOrder, detectIterationOrder);
        MutableCollection<Integer> detectWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detectWith(( each, param) -> {
            detectWithIterationOrder.add(each);
            return false;
        }, null);
        IterableTestCase.assertEquals(expectedIterationOrder, detectWithIterationOrder);
        MutableCollection<Integer> detectIfNoneIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detectIfNone(( each) -> {
            detectIfNoneIterationOrder.add(each);
            return false;
        }, () -> 0);
        IterableTestCase.assertEquals(expectedIterationOrder, detectIfNoneIterationOrder);
        MutableCollection<Integer> detectWithIfNoneIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().detectWithIfNone(( each, param) -> {
            detectWithIfNoneIterationOrder.add(each);
            return false;
        }, null, () -> 0);
        IterableTestCase.assertEquals(expectedIterationOrder, detectWithIfNoneIterationOrder);
        MutableCollection<Integer> minComparatorIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().min(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (minComparatorIterationOrder.isEmpty()) {
                    minComparatorIterationOrder.add(o2);
                }
                minComparatorIterationOrder.add(o1);
                return 0;
            }
        });
        IterableTestCase.assertEquals(expectedIterationOrder, minComparatorIterationOrder);
        MutableCollection<Integer> maxComparatorIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().max(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (maxComparatorIterationOrder.isEmpty()) {
                    maxComparatorIterationOrder.add(o2);
                }
                maxComparatorIterationOrder.add(o1);
                return 0;
            }
        });
        IterableTestCase.assertEquals(expectedIterationOrder, maxComparatorIterationOrder);
        MutableCollection<Integer> minByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().minBy(minByIterationOrder::add);
        IterableTestCase.assertEquals(expectedIterationOrder, minByIterationOrder);
        MutableCollection<Integer> maxByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().maxBy(maxByIterationOrder::add);
        IterableTestCase.assertEquals(expectedIterationOrder, maxByIterationOrder);
        MutableCollection<Integer> groupByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupBy(groupByIterationOrder::add);
        IterableTestCase.assertEquals(expectedIterationOrder, groupByIterationOrder);
        MutableCollection<Integer> groupByTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupBy(groupByTargetIterationOrder::add, new com.gs.collections.impl.multimap.bag.HashBagMultimap());
        IterableTestCase.assertEquals(expectedIterationOrder, groupByTargetIterationOrder);
        MutableCollection<Integer> groupByEachIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupByEach(( each) -> {
            groupByEachIterationOrder.add(each);
            return Lists.immutable.with(each);
        });
        IterableTestCase.assertEquals(expectedIterationOrder, groupByEachIterationOrder);
        MutableCollection<Integer> groupByEachTargetIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().groupByEach(( each) -> {
            groupByEachTargetIterationOrder.add(each);
            return Lists.immutable.with(each);
        }, new com.gs.collections.impl.multimap.bag.HashBagMultimap<Integer, Integer>());
        IterableTestCase.assertEquals(expectedIterationOrder, groupByEachTargetIterationOrder);
        MutableCollection<Integer> sumOfFloatIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfFloat(( each) -> {
            sumOfFloatIterationOrder.add(each);
            return each.floatValue();
        });
        IterableTestCase.assertEquals(expectedIterationOrder, sumOfFloatIterationOrder);
        MutableCollection<Integer> sumOfDoubleIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfDouble(( each) -> {
            sumOfDoubleIterationOrder.add(each);
            return each.doubleValue();
        });
        IterableTestCase.assertEquals(expectedIterationOrder, sumOfDoubleIterationOrder);
        MutableCollection<Integer> sumOfIntIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfInt(( each) -> {
            sumOfIntIterationOrder.add(each);
            return each.intValue();
        });
        IterableTestCase.assertEquals(expectedIterationOrder, sumOfIntIterationOrder);
        MutableCollection<Integer> sumOfLongIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().sumOfLong(( each) -> {
            sumOfLongIterationOrder.add(each);
            return each.longValue();
        });
        IterableTestCase.assertEquals(expectedIterationOrder, sumOfLongIterationOrder);
        MutableCollection<Integer> injectIntoIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0, new com.gs.collections.api.block.function.Function2<Integer, Integer, Integer>() {
            public Integer value(Integer argument1, Integer argument2) {
                injectIntoIterationOrder.add(argument2);
                return argument1 + argument2;
            }
        });
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoIterationOrder);
        MutableCollection<Integer> injectIntoIntIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0, new com.gs.collections.api.block.function.primitive.IntObjectToIntFunction<Integer>() {
            public int intValueOf(int intParameter, Integer objectParameter) {
                injectIntoIntIterationOrder.add(objectParameter);
                return intParameter + objectParameter;
            }
        });
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoIntIterationOrder);
        MutableCollection<Integer> injectIntoLongIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0L, new com.gs.collections.api.block.function.primitive.LongObjectToLongFunction<Integer>() {
            public long longValueOf(long longParameter, Integer objectParameter) {
                injectIntoLongIterationOrder.add(objectParameter);
                return longParameter + objectParameter;
            }
        });
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoLongIterationOrder);
        MutableCollection<Integer> injectIntoDoubleIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0L, new com.gs.collections.api.block.function.primitive.DoubleObjectToDoubleFunction<Integer>() {
            public double doubleValueOf(double doubleParameter, Integer objectParameter) {
                injectIntoDoubleIterationOrder.add(objectParameter);
                return doubleParameter + objectParameter;
            }
        });
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoDoubleIterationOrder);
        MutableCollection<Integer> injectIntoFloatIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectInto(0L, new com.gs.collections.api.block.function.primitive.FloatObjectToFloatFunction<Integer>() {
            public float floatValueOf(float floatParameter, Integer objectParameter) {
                injectIntoFloatIterationOrder.add(objectParameter);
                return floatParameter + objectParameter;
            }
        });
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoFloatIterationOrder);
        Counter toSortedListCount = new Counter();
        this.getInstanceUnderTest().toSortedList(( o1, o2) -> {
            toSortedListCount.increment();
            return 0;
        });
        IterableTestCase.assertEquals(((expectedIterationOrder.size()) - 1), toSortedListCount.getCount());
        /* MutableCollection<Integer> toSortedListByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().toSortedListBy(toSortedListByIterationOrder::add);
        assertEquals(expectedIterationOrder.size(), toSortedListByIterationOrder.size());
         */
        Counter toSortedSetCount = new Counter();
        this.getInstanceUnderTest().toSortedSet(( o1, o2) -> {
            toSortedSetCount.increment();
            return 0;
        });
        IterableTestCase.assertEquals(expectedIterationOrder.size(), toSortedSetCount.getCount());
        /* MutableCollection<Integer> toSortedSetByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().toSortedSetBy(toSortedSetByIterationOrder::add);
        assertEquals(expectedIterationOrder.size(), toSortedSetByIterationOrder.size());
         */
        Counter toSortedBagCount = new Counter();
        this.getInstanceUnderTest().toSortedBag(( o1, o2) -> {
            toSortedBagCount.increment();
            return 0;
        });
        IterableTestCase.assertEquals(expectedIterationOrder.size(), toSortedBagCount.getCount());
        /* MutableCollection<Integer> toSortedBagByIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().toSortedBagBy(toSortedBagByIterationOrder::add);
        assertEquals(expectedIterationOrder.size(), toSortedBagByIterationOrder.size());
         */
    }

    @Test
    default void RichIterable_select_reject() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 4, 4, 4, 2, 2), iterable.select(IntegerPredicates.isEven()));
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 2, 2), iterable.select(IntegerPredicates.isEven(), this.<Integer>newMutableForFilter()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 4, 4, 4, 3, 3, 3), iterable.selectWith(Predicates2.greaterThan(), 2));
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3), iterable.selectWith(Predicates2.<Integer>greaterThan(), 2, this.<Integer>newMutableForFilter()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 4, 4, 4, 2, 2), iterable.reject(IntegerPredicates.isOdd()));
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 2, 2), iterable.reject(IntegerPredicates.isOdd(), this.<Integer>newMutableForFilter()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 4, 4, 4, 3, 3, 3), iterable.rejectWith(Predicates2.lessThan(), 3));
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3), iterable.rejectWith(Predicates2.<Integer>lessThan(), 3, this.<Integer>newMutableForFilter()));
    }

    @Test
    default void RichIterable_partition() {
        RichIterable<Integer> iterable = newWith((-3), (-3), (-3), (-2), (-2), (-1), 0, 1, 2, 2, 3, 3, 3);
        PartitionIterable<Integer> partition = iterable.partition(IntegerPredicates.isEven());
        IterableTestCase.assertEquals(this.getExpectedFiltered((-2), (-2), 0, 2, 2), partition.getSelected());
        IterableTestCase.assertEquals(this.getExpectedFiltered((-3), (-3), (-3), (-1), 1, 3, 3, 3), partition.getRejected());
        PartitionIterable<Integer> partitionWith = iterable.partitionWith(Predicates2.greaterThan(), 0);
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 2, 2, 3, 3, 3), partitionWith.getSelected());
        IterableTestCase.assertEquals(this.getExpectedFiltered((-3), (-3), (-3), (-2), (-2), (-1), 0), partitionWith.getRejected());
    }

    @Test
    default void RichIterable_selectInstancesOf() {
        RichIterable<Number> iterable = this.<Number>newWith(1, 2.0, 2.0, 3, 3, 3, 4.0, 4.0, 4.0, 4.0);
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 3, 3, 3), iterable.selectInstancesOf(Integer.class));
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 2.0, 2.0, 3, 3, 3, 4.0, 4.0, 4.0, 4.0), iterable.selectInstancesOf(Number.class));
    }

    @Test
    default void RichIterable_collect() {
        RichIterable<Integer> iterable = newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collect(( i) -> i % 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collect(( i) -> i % 10, this.newMutableForTransform()));
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collectWith(( i, mod) -> i % mod, 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collectWith(( i, mod) -> i % mod, 10, this.newMutableForTransform()));
    }

    @Test
    default void RichIterable_collectIf() {
        RichIterable<Integer> iterable = newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);
        IterableTestCase.assertEquals(this.getExpectedTransformed(3, 3, 1, 1, 3, 3, 1, 1), iterable.collectIf(( i) -> (i % 2) != 0, ( i) -> i % 10));
        IterableTestCase.assertEquals(this.newMutableForTransform(3, 3, 1, 1, 3, 3, 1, 1), iterable.collectIf(( i) -> (i % 2) != 0, ( i) -> i % 10, this.newMutableForTransform()));
    }

    @Test
    default void RichIterable_collectPrimitive() {
        IterableTestCase.assertEquals(this.getExpectedBoolean(false, false, true, true, false, false), this.newWith(3, 3, 2, 2, 1, 1).collectBoolean(( each) -> (each % 2) == 0));
        IterableTestCase.assertEquals(this.newBooleanForTransform(false, false, true, true, false, false), this.newWith(3, 3, 2, 2, 1, 1).collectBoolean(( each) -> (each % 2) == 0, this.newBooleanForTransform()));
        RichIterable<Integer> iterable = newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);
        IterableTestCase.assertEquals(this.getExpectedByte(((byte) (3)), ((byte) (3)), ((byte) (2)), ((byte) (2)), ((byte) (1)), ((byte) (1)), ((byte) (3)), ((byte) (3)), ((byte) (2)), ((byte) (2)), ((byte) (1)), ((byte) (1))), iterable.collectByte(( each) -> ((byte) (each % 10))));
        IterableTestCase.assertEquals(this.newByteForTransform(((byte) (3)), ((byte) (3)), ((byte) (2)), ((byte) (2)), ((byte) (1)), ((byte) (1)), ((byte) (3)), ((byte) (3)), ((byte) (2)), ((byte) (2)), ((byte) (1)), ((byte) (1))), iterable.collectByte(( each) -> ((byte) (each % 10)), this.newByteForTransform()));
        IterableTestCase.assertEquals(this.getExpectedChar(((char) (3)), ((char) (3)), ((char) (2)), ((char) (2)), ((char) (1)), ((char) (1)), ((char) (3)), ((char) (3)), ((char) (2)), ((char) (2)), ((char) (1)), ((char) (1))), iterable.collectChar(( each) -> ((char) (each % 10))));
        IterableTestCase.assertEquals(this.newCharForTransform(((char) (3)), ((char) (3)), ((char) (2)), ((char) (2)), ((char) (1)), ((char) (1)), ((char) (3)), ((char) (3)), ((char) (2)), ((char) (2)), ((char) (1)), ((char) (1))), iterable.collectChar(( each) -> ((char) (each % 10)), this.newCharForTransform()));
        IterableTestCase.assertEquals(this.getExpectedDouble(3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0), iterable.collectDouble(( each) -> ((double) (each % 10))));
        IterableTestCase.assertEquals(this.newDoubleForTransform(3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0), iterable.collectDouble(( each) -> ((double) (each % 10)), this.newDoubleForTransform()));
        IterableTestCase.assertEquals(this.getExpectedFloat(3.0F, 3.0F, 2.0F, 2.0F, 1.0F, 1.0F, 3.0F, 3.0F, 2.0F, 2.0F, 1.0F, 1.0F), iterable.collectFloat(( each) -> ((float) (each % 10))));
        IterableTestCase.assertEquals(this.newFloatForTransform(3.0F, 3.0F, 2.0F, 2.0F, 1.0F, 1.0F, 3.0F, 3.0F, 2.0F, 2.0F, 1.0F, 1.0F), iterable.collectFloat(( each) -> ((float) (each % 10)), this.newFloatForTransform()));
        IterableTestCase.assertEquals(this.getExpectedInt(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collectInt(( each) -> each % 10));
        IterableTestCase.assertEquals(this.newIntForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collectInt(( each) -> each % 10, this.newIntForTransform()));
        IterableTestCase.assertEquals(this.getExpectedLong(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collectLong(( each) -> each % 10));
        IterableTestCase.assertEquals(this.newLongForTransform(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1), iterable.collectLong(( each) -> each % 10, this.newLongForTransform()));
        IterableTestCase.assertEquals(this.getExpectedShort(((short) (3)), ((short) (3)), ((short) (2)), ((short) (2)), ((short) (1)), ((short) (1)), ((short) (3)), ((short) (3)), ((short) (2)), ((short) (2)), ((short) (1)), ((short) (1))), iterable.collectShort(( each) -> ((short) (each % 10))));
        IterableTestCase.assertEquals(this.newShortForTransform(((short) (3)), ((short) (3)), ((short) (2)), ((short) (2)), ((short) (1)), ((short) (1)), ((short) (3)), ((short) (3)), ((short) (2)), ((short) (2)), ((short) (1)), ((short) (1))), iterable.collectShort(( each) -> ((short) (each % 10)), this.newShortForTransform()));
    }

    @Test
    default void RichIterable_flatCollect() {
        IterableTestCase.assertEquals(this.getExpectedTransformed(1, 2, 3, 1, 2, 1, 2, 1), this.newWith(3, 2, 2, 1).flatCollect(Interval::oneTo));
        IterableTestCase.assertEquals(this.newMutableForTransform(1, 2, 3, 1, 2, 1, 2, 1), this.newWith(3, 2, 2, 1).flatCollect(Interval::oneTo, this.newMutableForTransform()));
    }

    @Test
    default void RichIterable_count() {
        RichIterable<Integer> iterable = newWith(3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(3, iterable.count(Integer.valueOf(3)::equals));
        IterableTestCase.assertEquals(2, iterable.count(Integer.valueOf(2)::equals));
        IterableTestCase.assertEquals(1, iterable.count(Integer.valueOf(1)::equals));
        IterableTestCase.assertEquals(0, iterable.count(Integer.valueOf(0)::equals));
        IterableTestCase.assertEquals(4, iterable.count(( i) -> (i % 2) != 0));
        IterableTestCase.assertEquals(6, iterable.count(( i) -> i > 0));
        IterableTestCase.assertEquals(3, iterable.countWith(Object::equals, 3));
        IterableTestCase.assertEquals(2, iterable.countWith(Object::equals, 2));
        IterableTestCase.assertEquals(1, iterable.countWith(Object::equals, 1));
        IterableTestCase.assertEquals(0, iterable.countWith(Object::equals, 0));
        IterableTestCase.assertEquals(6, iterable.countWith(Predicates2.greaterThan(), 0));
    }

    @Test
    default void RichIterable_anySatisfy_allSatisfy_noneSatisfy() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertTrue(iterable.anySatisfy(Predicates.greaterThan(0)));
        Assert.assertTrue(iterable.anySatisfy(Predicates.greaterThan(1)));
        Assert.assertTrue(iterable.anySatisfy(Predicates.greaterThan(2)));
        Assert.assertFalse(iterable.anySatisfy(Predicates.greaterThan(3)));
        Assert.assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 0));
        Assert.assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 1));
        Assert.assertTrue(iterable.anySatisfyWith(Predicates2.greaterThan(), 2));
        Assert.assertFalse(iterable.anySatisfyWith(Predicates2.greaterThan(), 3));
        Assert.assertTrue(iterable.allSatisfy(Predicates.greaterThan(0)));
        Assert.assertFalse(iterable.allSatisfy(Predicates.greaterThan(1)));
        Assert.assertFalse(iterable.allSatisfy(Predicates.greaterThan(2)));
        Assert.assertFalse(iterable.allSatisfy(Predicates.greaterThan(3)));
        Assert.assertTrue(iterable.allSatisfyWith(Predicates2.greaterThan(), 0));
        Assert.assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 1));
        Assert.assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 2));
        Assert.assertFalse(iterable.allSatisfyWith(Predicates2.greaterThan(), 3));
        Assert.assertFalse(iterable.noneSatisfy(Predicates.greaterThan(0)));
        Assert.assertFalse(iterable.noneSatisfy(Predicates.greaterThan(1)));
        Assert.assertFalse(iterable.noneSatisfy(Predicates.greaterThan(2)));
        Assert.assertTrue(iterable.noneSatisfy(Predicates.greaterThan(3)));
        Assert.assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 0));
        Assert.assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 1));
        Assert.assertFalse(iterable.noneSatisfyWith(Predicates2.greaterThan(), 2));
        Assert.assertTrue(iterable.noneSatisfyWith(Predicates2.greaterThan(), 3));
    }

    @Test
    default void RichIterable_detect() {
        RichIterable<Integer> iterable = newWith(3, 2, 1);
        Assert.assertThat(iterable.detect(Predicates.greaterThan(0)), Matchers.is(3));
        Assert.assertThat(iterable.detect(Predicates.greaterThan(1)), Matchers.is(3));
        Assert.assertThat(iterable.detect(Predicates.greaterThan(2)), Matchers.is(3));
        Assert.assertThat(iterable.detect(Predicates.greaterThan(3)), Matchers.nullValue());
        Assert.assertThat(iterable.detect(Predicates.lessThan(1)), Matchers.nullValue());
        Assert.assertThat(iterable.detect(Predicates.lessThan(2)), Matchers.is(1));
        Assert.assertThat(iterable.detect(Predicates.lessThan(3)), Matchers.is(2));
        Assert.assertThat(iterable.detect(Predicates.lessThan(4)), Matchers.is(3));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 0), Matchers.is(3));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 1), Matchers.is(3));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 2), Matchers.is(3));
        Assert.assertThat(iterable.detectWith(Predicates2.greaterThan(), 3), Matchers.nullValue());
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 1), Matchers.nullValue());
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 2), Matchers.is(1));
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 3), Matchers.is(2));
        Assert.assertThat(iterable.detectWith(Predicates2.lessThan(), 4), Matchers.is(3));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(0), () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(1), () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(2), () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectIfNone(Predicates.greaterThan(3), () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(1), () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(2), () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(3), () -> 4), Matchers.is(2));
        Assert.assertThat(iterable.detectIfNone(Predicates.lessThan(4), () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 0, () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 1, () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 2, () -> 4), Matchers.is(3));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.greaterThan(), 3, () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 1, () -> 4), Matchers.is(4));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 2, () -> 4), Matchers.is(1));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 3, () -> 4), Matchers.is(2));
        Assert.assertThat(iterable.detectWithIfNone(Predicates2.lessThan(), 4, () -> 4), Matchers.is(3));
    }

    @Test
    default void RichIterable_min_max() {
        IterableTestCase.assertEquals(Integer.valueOf((-1)), this.newWith((-1), 0, 1).min());
        IterableTestCase.assertEquals(Integer.valueOf((-1)), this.newWith(1, 0, (-1)).min());
        assertThrows(NoSuchElementException.class, () -> this.newWith().min());
        IterableTestCase.assertEquals(Integer.valueOf(1), this.newWith((-1), 0, 1).max());
        IterableTestCase.assertEquals(Integer.valueOf(1), this.newWith(1, 0, (-1)).max());
        assertThrows(NoSuchElementException.class, () -> this.newWith().max());
        IterableTestCase.assertEquals(Integer.valueOf(1), this.newWith((-1), 0, 1).min(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(Integer.valueOf(1), this.newWith(1, 0, (-1)).min(Comparators.reverseNaturalOrder()));
        assertThrows(NoSuchElementException.class, () -> this.newWith().min(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(Integer.valueOf((-1)), this.newWith((-1), 0, 1).max(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(Integer.valueOf((-1)), this.newWith(1, 0, (-1)).max(Comparators.reverseNaturalOrder()));
        assertThrows(NoSuchElementException.class, () -> this.newWith().max(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void RichIterable_min_max_non_comparable() {
        Object sentinel = new Object();
        Assert.assertSame(sentinel, this.newWith(sentinel).min());
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).min());
        Assert.assertSame(sentinel, this.newWith(sentinel).max());
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).max());
        Assert.assertSame(sentinel, this.newWith(sentinel).min(Comparators.reverseNaturalOrder()));
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).min(Comparators.reverseNaturalOrder()));
        Assert.assertSame(sentinel, this.newWith(sentinel).max(Comparators.reverseNaturalOrder()));
        assertThrows(ClassCastException.class, () -> this.newWith(new Object(), new Object()).max(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void RichIterable_minBy_maxBy() {
        IterableTestCase.assertEquals("da", this.newWith("ed", "da", "ca", "bc", "ab").minBy(( string) -> string.charAt(((string.length()) - 1))));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().minBy(( string) -> string.charAt(((string.length()) - 1))));
        IterableTestCase.assertEquals("dz", this.newWith("ew", "dz", "cz", "bx", "ay").maxBy(( string) -> string.charAt(((string.length()) - 1))));
        assertThrows(NoSuchElementException.class, () -> this.<String>newWith().maxBy(( string) -> string.charAt(((string.length()) - 1))));
    }

    @Test
    default void RichIterable_groupBy() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Function<Integer, Boolean> groupByFunction = ( object) -> IntegerPredicates.isOdd().accept(object);
        MutableMap<Boolean, RichIterable<Integer>> expectedGroupBy = UnifiedMap.newWithKeysValues(Boolean.TRUE, this.newMutableForFilter(3, 3, 3, 1), Boolean.FALSE, this.newMutableForFilter(4, 4, 4, 4, 2, 2));
        IterableTestCase.assertEquals(expectedGroupBy, iterable.groupBy(groupByFunction).toMap());
        Function<Integer, Boolean> function = (Integer object) -> true;
        MutableMultimap<Boolean, Integer> multimap2 = iterable.groupBy(groupByFunction, this.<Integer>newWith().groupBy(function).toMutable());
        IterableTestCase.assertEquals(expectedGroupBy, multimap2.toMap());
        Function<Integer, Iterable<Integer>> groupByEachFunction = ( integer) -> Interval.fromTo((-1), (-integer));
        MutableMap<Integer, RichIterable<Integer>> expectedGroupByEach = UnifiedMap.newWithKeysValues((-4), this.newMutableForFilter(4, 4, 4, 4), (-3), this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3), (-2), this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2), (-1), this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1));
        IterableTestCase.assertEquals(expectedGroupByEach, iterable.groupByEach(groupByEachFunction).toMap());
        Multimap<Integer, Integer> actualWithTarget = iterable.groupByEach(groupByEachFunction, this.<Integer>newWith().groupByEach(groupByEachFunction).toMutable());
        IterableTestCase.assertEquals(expectedGroupByEach, actualWithTarget.toMap());
    }

    @Test
    default void RichIterable_aggregateBy_aggregateInPlaceBy() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        MapIterable<String, Integer> aggregateBy = iterable.aggregateBy(Object::toString, () -> 0, ( integer1, integer2) -> integer1 + integer2);
        IterableTestCase.assertEquals(16, aggregateBy.get("4").intValue());
        IterableTestCase.assertEquals(9, aggregateBy.get("3").intValue());
        IterableTestCase.assertEquals(4, aggregateBy.get("2").intValue());
        IterableTestCase.assertEquals(1, aggregateBy.get("1").intValue());
        MapIterable<String, AtomicInteger> aggregateInPlaceBy = iterable.aggregateInPlaceBy(String::valueOf, AtomicInteger::new, AtomicInteger::addAndGet);
        IterableTestCase.assertEquals(16, aggregateInPlaceBy.get("4").intValue());
        IterableTestCase.assertEquals(9, aggregateInPlaceBy.get("3").intValue());
        IterableTestCase.assertEquals(4, aggregateInPlaceBy.get("2").intValue());
        IterableTestCase.assertEquals(1, aggregateInPlaceBy.get("1").intValue());
    }

    @Test
    default void RichIterable_sumOfPrimitive() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Assert.assertEquals(30.0F, iterable.sumOfFloat(Integer::floatValue), 0.001);
        Assert.assertEquals(30.0, iterable.sumOfDouble(Integer::doubleValue), 0.001);
        Assert.assertEquals(30, iterable.sumOfInt(( integer) -> integer));
        Assert.assertEquals(30L, iterable.sumOfLong(Integer::longValue));
    }

    @Test
    default void RichIterable_injectInto() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(Integer.valueOf(31), iterable.injectInto(1, INTEGER));
        IterableTestCase.assertEquals(Integer.valueOf(30), iterable.injectInto(0, INTEGER));
    }

    @Test
    default void RichIterable_injectInto_primitive() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Assert.assertEquals(31, iterable.injectInto(1, INTEGER_TO_INT));
        Assert.assertEquals(30, iterable.injectInto(0, INTEGER_TO_INT));
        Assert.assertEquals(31L, iterable.injectInto(1, INTEGER_TO_LONG));
        Assert.assertEquals(30L, iterable.injectInto(0, INTEGER_TO_LONG));
        Assert.assertEquals(31.0, iterable.injectInto(1, INTEGER_TO_DOUBLE), 0.001);
        Assert.assertEquals(30.0, iterable.injectInto(0, INTEGER_TO_DOUBLE), 0.001);
        Assert.assertEquals(31.0F, iterable.injectInto(1, INTEGER_TO_FLOAT), 0.001F);
        Assert.assertEquals(30.0F, iterable.injectInto(0, INTEGER_TO_FLOAT), 0.001F);
    }

    @Test
    default void RichIterable_makeString_appendString() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals("4, 4, 4, 4, 3, 3, 3, 2, 2, 1", iterable.makeString());
        IterableTestCase.assertEquals("4/4/4/4/3/3/3/2/2/1", iterable.makeString("/"));
        IterableTestCase.assertEquals("[4/4/4/4/3/3/3/2/2/1]", iterable.makeString("[", "/", "]"));
        StringBuilder builder1 = new StringBuilder();
        iterable.appendString(builder1);
        IterableTestCase.assertEquals("4, 4, 4, 4, 3, 3, 3, 2, 2, 1", builder1.toString());
        StringBuilder builder2 = new StringBuilder();
        iterable.appendString(builder2, "/");
        IterableTestCase.assertEquals("4/4/4/4/3/3/3/2/2/1", builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        iterable.appendString(builder3, "[", "/", "]");
        IterableTestCase.assertEquals("[4/4/4/4/3/3/3/2/2/1]", builder3.toString());
    }

    @Test
    default void RichIterable_toString() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals("[4, 4, 4, 4, 3, 3, 3, 2, 2, 1]", iterable.toString());
    }

    @Test
    default void RichIterable_toList() {
        IterableTestCase.assertEquals(immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).toList());
    }

    @Test
    default void RichIterable_toSortedList() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), iterable.toSortedList());
        IterableTestCase.assertEquals(immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), iterable.toSortedList(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), iterable.toSortedListBy(Functions.identity()));
        IterableTestCase.assertEquals(immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), iterable.toSortedListBy(( each) -> each * (-1)));
    }

    @Test
    default void RichIterable_toSet() {
        IterableTestCase.assertEquals(Sets.immutable.with(4, 3, 2, 1), this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).toSet());
    }

    @Test
    default void RichIterable_toSortedSet() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(SortedSets.immutable.with(1, 2, 3, 4), iterable.toSortedSet());
        IterableTestCase.assertEquals(SortedSets.immutable.with(Comparators.reverseNaturalOrder(), 4, 3, 2, 1), iterable.toSortedSet(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(SortedSets.immutable.with(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4), iterable.toSortedSetBy(Functions.identity()));
        IterableTestCase.assertEquals(SortedSets.immutable.with(Comparators.byFunction((Integer each) -> each * (-1)), 4, 3, 2, 1), iterable.toSortedSetBy(( each) -> each * (-1)));
    }

    @Test
    default void RichIterable_toBag() {
        IterableTestCase.assertEquals(Bags.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1).toBag());
    }

    @Test
    default void RichIterable_toSortedBag() {
        RichIterable<Integer> iterable = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(TreeBag.newBagWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), iterable.toSortedBag());
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1), iterable.toSortedBag(Comparators.reverseNaturalOrder()));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.byFunction(Functions.identity()), 1, 2, 2, 3, 3, 3, 4, 4, 4, 4), iterable.toSortedBagBy(Functions.identity()));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.byFunction((Integer each) -> each * (-1)), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1), iterable.toSortedBagBy(( each) -> each * (-1)));
    }

    @Test
    default void RichIterable_toMap() {
        RichIterable<Integer> iterable = newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);
        IterableTestCase.assertEquals(UnifiedMap.newMapWith(Tuples.pair("13", 3), Tuples.pair("12", 2), Tuples.pair("11", 1), Tuples.pair("3", 3), Tuples.pair("2", 2), Tuples.pair("1", 1)), iterable.toMap(Object::toString, ( each) -> each % 10));
    }

    @Test
    default void RichIterable_toSortedMap() {
        RichIterable<Integer> iterable = newWith(13, 13, 12, 12, 11, 11, 3, 3, 2, 2, 1, 1);
        Pair<String, Integer>[] pairs = new Pair[]{ Tuples.pair("13", 3), Tuples.pair("12", 2), Tuples.pair("11", 1), Tuples.pair("3", 3), Tuples.pair("2", 2), Tuples.pair("1", 1) };
        IterableTestCase.assertEquals(TreeSortedMap.newMapWith(pairs), iterable.toSortedMap(Object::toString, ( each) -> each % 10));
        IterableTestCase.assertEquals(TreeSortedMap.newMapWith(Comparators.reverseNaturalOrder(), pairs), iterable.toSortedMap(Comparators.reverseNaturalOrder(), Object::toString, ( each) -> each % 10));
    }

    @Test
    default void RichIterable_toArray() {
        Object[] array = this.newWith(3, 3, 3, 2, 2, 1).toArray();
        IterableTestCase.assertEquals(Bags.immutable.with(3, 3, 3, 2, 2, 1), HashBag.newBagWith(array));
    }

    class Holder<T extends Comparable<? super T>> implements Comparable<RichIterableTestCase.Holder<T>> {
        private final T field;

        Holder(T field) {
            this.field = field;
        }

        @Override
        public int compareTo(RichIterableTestCase.Holder<T> other) {
            return this.field.compareTo(other.field);
        }
    }
}

