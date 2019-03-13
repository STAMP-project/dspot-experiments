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
package com.gs.collections.impl.set.mutable;


import Lists.fixedSize;
import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.Pool;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Key;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.utility.ArrayIterate;
import java.util.HashSet;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test suite for {@link UnifiedSetWithHashingStrategy}.
 */
public class UnifiedSetWithHashingStrategyTest extends AbstractUnifiedSetTestCase {
    // Not using the static factory method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> INTEGER_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>() {
        public int computeHashCode(Integer object) {
            return object.hashCode();
        }

        public boolean equals(Integer object1, Integer object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>() {
        public int computeHashCode(String object) {
            return object.hashCode();
        }

        public boolean equals(String object1, String object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);

    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final Person JOHNSMITH = new Person("John", "Smith");

    private static final Person JANESMITH = new Person("Jane", "Smith");

    private static final Person JOHNDOE = new Person("John", "Doe");

    private static final Person JANEDOE = new Person("Jane", "Doe");

    private static final ImmutableList<Person> PEOPLE = immutable.of(UnifiedSetWithHashingStrategyTest.JOHNSMITH, UnifiedSetWithHashingStrategyTest.JANESMITH, UnifiedSetWithHashingStrategyTest.JOHNDOE, UnifiedSetWithHashingStrategyTest.JANEDOE);

    private static final ImmutableSet<Person> LAST_NAME_HASHED_SET = Sets.immutable.of(UnifiedSetWithHashingStrategyTest.JOHNSMITH, UnifiedSetWithHashingStrategyTest.JOHNDOE);

    @Test
    public void newSet_throws() {
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<Integer>(INTEGER_HASHING_STRATEGY, (-1), 0.5F));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<Integer>(INTEGER_HASHING_STRATEGY, 1, (-0.5F)));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<Integer>(INTEGER_HASHING_STRATEGY, 1, 1.5F));
    }

    @Override
    @Test
    public void tap() {
        super.tap();
        MutableList<Person> tapResult = mutable.of();
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY).withAll(UnifiedSetWithHashingStrategyTest.PEOPLE.castToList());
        Assert.assertSame(people, people.tap(tapResult::add));
        Assert.assertEquals(people.toList(), tapResult);
    }

    @Override
    @Test
    public void select() {
        super.select();
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY).withAll(UnifiedSetWithHashingStrategyTest.PEOPLE.castToList());
        Verify.assertSetsEqual(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHED_SET.castToSet(), people);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(UnifiedSetWithHashingStrategyTest.JOHNSMITH), people.select(( each) -> "Smith".equals(each.getLastName())).with(UnifiedSetWithHashingStrategyTest.JANESMITH));
    }

    @Override
    @Test
    public void reject() {
        super.reject();
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY).withAll(UnifiedSetWithHashingStrategyTest.PEOPLE.castToList());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(UnifiedSetWithHashingStrategyTest.JOHNSMITH), people.reject(( each) -> "Doe".equals(each.getLastName())).with(UnifiedSetWithHashingStrategyTest.JANESMITH));
    }

    /**
     *
     *
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazyCollectForEach() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<String> select = integers.lazyCollect(String::valueOf);
        Procedure<String> builder = Procedures.append(new StringBuilder());
        select.forEach(builder);
        String result = builder.toString();
        Verify.assertContains("1", result);
        Verify.assertContains("2", result);
        Verify.assertContains("3", result);
        Verify.assertContains("4", result);
        Verify.assertContains("5", result);
    }

    /**
     *
     *
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazyRejectForEach() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<Integer> select = integers.lazyReject(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure(sum));
        Assert.assertEquals(5L, sum.getValue().intValue());
    }

    /**
     *
     *
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazySelectForEach() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<Integer> select = integers.lazySelect(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure(sum));
        Assert.assertEquals(10, sum.getValue().intValue());
    }

    @Override
    @Test
    public void with() {
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY, "1"), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY).with("1"));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY, "1", "2"), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY).with("1", "2"));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY, "1", "2", "3"), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY).with("1", "2", "3"));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY, "1", "2", "3", "4"), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY).with("1", "2", "3", "4"));
        MutableSet<String> list = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY).with("A").withAll(fixedSize.of("1", "2")).withAll(fixedSize.<String>of()).withAll(Sets.fixedSize.of("3", "4"));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.STRING_HASHING_STRATEGY, "A", "1", "2", "3", "4"), list);
    }

    @Test
    public void newSetWithIterable() {
        // testing collection
        MutableSet<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, Interval.oneTo(3));
        Assert.assertEquals(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 2, 3), integers);
        // testing iterable
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set1 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, FastList.newListWith(1, 2, 3).asLazy());
        Assert.assertEquals(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, 2, 3), set1);
        // testing null
        Verify.assertThrows(NullPointerException.class, () -> com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, null));
    }

    @Override
    @Test
    public void add() {
        super.add();
        // force rehashing at each step of adding a new colliding entry
        for (int i = 0; i < (AbstractMutableSetTestCase.COLLISIONS.size()); i++) {
            com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> unifiedSet = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, i, 0.75F).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            if (i == 2) {
                unifiedSet.add(Integer.valueOf(1));
            }
            if (i == 4) {
                unifiedSet.add(Integer.valueOf(1));
                unifiedSet.add(Integer.valueOf(2));
            }
            Integer value = AbstractMutableSetTestCase.COLLISIONS.get(i);
            Assert.assertTrue(unifiedSet.add(value));
        }
        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the trigging element goes in the bucket
        // set up a chained bucket
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> caseA = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        // clear the bucket to one element
        caseA.remove(AbstractMutableSetTestCase.COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));
        // add the colliding value back and force the rehash
        Assert.assertTrue(caseA.add(AbstractMutableSetTestCase.COLLISION_2));
        // Rehashing Case B: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element is not in the chain
        // set up a chained bucket
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> caseB = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        // clear the bucket to one element
        caseB.remove(AbstractMutableSetTestCase.COLLISION_2);
        // increase the occupied count to the threshold
        caseB.add(Integer.valueOf(1));
        caseB.add(Integer.valueOf(2));
        // add a new value and force the rehash
        Assert.assertTrue(caseB.add(3));
    }

    @Test
    public void add_with_hashingStrategy() {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>() {
            public int computeHashCode(Integer object) {
                return object % 1000;
            }

            public boolean equals(Integer object1, Integer object2) {
                return object1.equals(object2);
            }
        });
        // Same as case A above except with a different hashing strategy
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> caseA = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2);
        // Adding an element to a slot
        Assert.assertTrue(caseA.add(AbstractMutableSetTestCase.COLLISION_1));
        // Setting up a chained bucked by forcing a collision
        Assert.assertTrue(caseA.add(((AbstractMutableSetTestCase.COLLISION_1) + 1000)));
        // Increasing the occupied to the thresh hold
        Assert.assertTrue(caseA.add(((AbstractMutableSetTestCase.COLLISION_1) + 2000)));
        // Forcing a rehash where the element that forced the rehash goes in the chained bucket
        Assert.assertTrue(caseA.add(null));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, ((AbstractMutableSetTestCase.COLLISION_1) + 1000), ((AbstractMutableSetTestCase.COLLISION_1) + 2000), null), caseA);
        // Same as case B above except with a different hashing strategy
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> caseB = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2);
        // Adding an element to a slot
        Assert.assertTrue(caseB.add(null));
        // Setting up a chained bucked by forcing a collision
        Assert.assertTrue(caseB.add(1));
        // Increasing the occupied to the threshold
        Assert.assertTrue(caseB.add(2));
        // Forcing a rehash where the element that forced the rehash does not go in the chained bucket
        Assert.assertTrue(caseB.add(3));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(null, 1, 2, 3), caseB);
        // Testing add throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).add(null));
    }

    @Override
    @Test
    public void addAllIterable() {
        super.addAllIterable();
        // test adding a fully populated chained bucket
        MutableSet<Integer> expected = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Assert.assertTrue(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY).addAllIterable(expected));
        // add an odd-sized collection to a set with a small max to ensure that its capacity is maintained after the operation.
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> tiny = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 0);
        Assert.assertTrue(tiny.addAllIterable(FastList.newListWith(AbstractMutableSetTestCase.COLLISION_1)));
        // Testing copying set with 3rd slot in chained bucket == null
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY);
        integers.remove(AbstractMutableSetTestCase.COLLISION_4);
        Assert.assertTrue(set.addAllIterable(integers));
        Assert.assertEquals(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), set);
        // Testing copying set with 2nd slot in chained bucket == null
        integers.remove(AbstractMutableSetTestCase.COLLISION_3);
        Assert.assertFalse(set.addAllIterable(integers));
        // Testing copying set with the 1st slot in chained bucket == null
        integers.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertFalse(set.addAllIterable(integers));
        Assert.assertEquals(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), set);
    }

    @Test
    public void addALLIterable_with_hashingStrategy() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(HashingStrategies.nullSafeHashingStrategy(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY), 2);
        // Testing adding an iterable
        Assert.assertTrue(people.addAllIterable(UnifiedSetWithHashingStrategyTest.PEOPLE));
        Verify.assertSetsEqual(UnifiedSet.newSet(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHED_SET), people);
        // Testing the set uses its own hashing strategy and not the target sets
        Assert.assertFalse(people.addAllIterable(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.FIRST_NAME_HASHING_STRATEGY, UnifiedSetWithHashingStrategyTest.PEOPLE)));
        Verify.assertSize(2, people);
        // Testing adding with null where the call to addALLIterable forces a rehash
        Person notInSet = new Person("Not", "InSet");
        Assert.assertTrue(people.addAllIterable(UnifiedSet.newSetWith(notInSet, null)));
        Verify.assertSetsEqual(UnifiedSet.newSet(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHED_SET).with(notInSet, null), people);
        // Testing addAllIterable throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).addAllIterable(UnifiedSet.newSetWith(((Person) (null)))));
    }

    @Test
    public void get() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.COLLISIONS);
        set.removeAll(AbstractMutableSetTestCase.COLLISIONS);
        for (Integer integer : AbstractMutableSetTestCase.COLLISIONS) {
            Assert.assertNull(set.get(integer));
            Assert.assertNull(set.get(null));
            set.add(integer);
            // noinspection UnnecessaryBoxing,CachedNumberConstructorCall,BoxingBoxedValue
            Assert.assertSame(integer, set.get(new Integer(integer)));
        }
        Assert.assertEquals(AbstractMutableSetTestCase.COLLISIONS.toSet(), set);
        // the pool interface supports getting null keys
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> chainedWithNull = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, null, AbstractMutableSetTestCase.COLLISION_1);
        Verify.assertContains(null, chainedWithNull);
        Assert.assertNull(chainedWithNull.get(null));
        // getting a non-existent from a chain with one slot should short-circuit to return null
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> chainedWithOneSlot = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        chainedWithOneSlot.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertNull(chainedWithOneSlot.get(AbstractMutableSetTestCase.COLLISION_2));
    }

    @Test
    public void get_with_hashingStrategy() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(HashingStrategies.nullSafeHashingStrategy(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY), 2).withAll(UnifiedSetWithHashingStrategyTest.PEOPLE.castToList());
        // Putting null then testing geting a null
        Verify.assertSize(3, people.with(((Person) (null))));
        Assert.assertNull(people.get(null));
        // Testing it is getting the same reference
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNSMITH, people.get(UnifiedSetWithHashingStrategyTest.JANESMITH));
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNSMITH, people.get(UnifiedSetWithHashingStrategyTest.JOHNSMITH));
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNDOE, people.get(UnifiedSetWithHashingStrategyTest.JANEDOE));
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNDOE, people.get(UnifiedSetWithHashingStrategyTest.JOHNDOE));
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNSMITH, people.get(new Person("Anything", "Smith")));
        Assert.assertNull(people.get(new Person("John", "NotHere")));
        // Testing get throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).get(null));
    }

    @Test
    public void put() {
        int size = AbstractMutableSetTestCase.MORE_COLLISIONS.size();
        for (int i = 1; i <= size; i++) {
            Pool<Integer> unifiedSet = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1).withAll(AbstractMutableSetTestCase.MORE_COLLISIONS.subList(0, (i - 1)));
            Integer newValue = AbstractMutableSetTestCase.MORE_COLLISIONS.get((i - 1));
            Assert.assertSame(newValue, unifiedSet.put(newValue));
            // noinspection UnnecessaryBoxing,CachedNumberConstructorCall,BoxingBoxedValue
            Assert.assertSame(newValue, unifiedSet.put(new Integer(newValue)));
        }
        // assert that all redundant puts into a each position of chain bucket return the original element added
        Pool<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 4).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        for (int i = 0; i < (set.size()); i++) {
            Integer value = AbstractMutableSetTestCase.COLLISIONS.get(i);
            Assert.assertSame(value, set.put(value));
        }
        // force rehashing at each step of putting a new colliding entry
        for (int i = 0; i < (AbstractMutableSetTestCase.COLLISIONS.size()); i++) {
            Pool<Integer> pool = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, i).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            if (i == 2) {
                pool.put(Integer.valueOf(1));
            }
            if (i == 4) {
                pool.put(Integer.valueOf(1));
                pool.put(Integer.valueOf(2));
            }
            Integer value = AbstractMutableSetTestCase.COLLISIONS.get(i);
            Assert.assertSame(value, pool.put(value));
        }
        // cover one case not covered in the above: a bucket with only one entry and a low capacity forcing a rehash
        // set up a chained bucket
        Pool<Integer> pool = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 2).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        // clear the bucket to one element
        pool.removeFromPool(AbstractMutableSetTestCase.COLLISION_2);
        // increase the occupied count to the threshold
        pool.put(Integer.valueOf(1));
        pool.put(Integer.valueOf(2));
        // put the colliding value back and force the rehash
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_2, pool.put(AbstractMutableSetTestCase.COLLISION_2));
        // put chained items into a pool without causing a rehash
        Pool<Integer> olympicPool = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY);
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_1, olympicPool.put(AbstractMutableSetTestCase.COLLISION_1));
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_2, olympicPool.put(AbstractMutableSetTestCase.COLLISION_2));
    }

    @Test
    public void put_with_hashingStrategy() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(HashingStrategies.nullSafeHashingStrategy(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY), 2).withAll(UnifiedSetWithHashingStrategyTest.PEOPLE.castToList());
        // Testing if element already exists, returns the instance in the set
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNSMITH, people.put(new Person("Anything", "Smith")));
        Verify.assertSize(2, people);
        // Testing if the element doesn't exist, returns the element itself
        Person notInSet = new Person("Not", "inSet");
        Assert.assertSame(notInSet, people.put(notInSet));
        Verify.assertSize(3, people);
        // Testing putting a null to force a rehash
        Assert.assertNull(people.put(null));
        Verify.assertSize(4, people);
        // Testing put throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).put(null));
    }

    @Test
    public void remove_with_hashingStrategy() {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>() {
            public int computeHashCode(Integer object) {
                return object % 1000;
            }

            public boolean equals(Integer object1, Integer object2) {
                return object1.equals(object2);
            }
        });
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2).with(AbstractMutableSetTestCase.COLLISION_1, ((AbstractMutableSetTestCase.COLLISION_1) + 1000), ((AbstractMutableSetTestCase.COLLISION_1) + 2000), null);
        // Testing remove null from the end of the chain
        Assert.assertTrue(integers.remove(null));
        // Adding null back and creating a deep chain.
        integers.with(null, ((AbstractMutableSetTestCase.COLLISION_1) + 3000), ((AbstractMutableSetTestCase.COLLISION_1) + 4000), ((AbstractMutableSetTestCase.COLLISION_1) + 5000));
        // Removing null from the first position of a bucket in the deep chain
        Assert.assertTrue(integers.remove(null));
        Assert.assertFalse(integers.remove(null));
        // Removing from the end of the deep chain
        Assert.assertTrue(integers.remove(((AbstractMutableSetTestCase.COLLISION_1) + 4000)));
        // Removing from the first spot of the chain
        Assert.assertTrue(integers.remove(AbstractMutableSetTestCase.COLLISION_1));
        Verify.assertSize(4, integers);
        // Testing removing a non existent element from a non bucket slot
        integers.add(2);
        integers.add(4);
        Assert.assertFalse(integers.remove(1002));
        // Testing removeIf
        Assert.assertTrue(integers.removeIf(IntegerPredicates.isEven()));
        Verify.assertEmpty(integers);
    }

    @Test
    public void removeFromPool() {
        Pool<Integer> unifiedSet = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 8).withAll(AbstractMutableSetTestCase.COLLISIONS);
        AbstractMutableSetTestCase.COLLISIONS.reverseForEach(( each) -> {
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Assert.assertSame(each, unifiedSet.removeFromPool(each));
            Assert.assertNull(unifiedSet.removeFromPool(each));
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Assert.assertNull(unifiedSet.removeFromPool(AbstractMutableSetTestCase.COLLISION_10));
        });
        Assert.assertEquals(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY), unifiedSet);
        AbstractMutableSetTestCase.COLLISIONS.forEach(Procedures.cast(( each) -> {
            Pool<Integer> unifiedSet2 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 8).withAll(AbstractMutableSetTestCase.COLLISIONS);
            Assert.assertNull(unifiedSet2.removeFromPool(null));
            Assert.assertSame(each, unifiedSet2.removeFromPool(each));
            Assert.assertNull(unifiedSet2.removeFromPool(each));
            Assert.assertNull(unifiedSet2.removeFromPool(null));
            Assert.assertNull(unifiedSet2.removeFromPool(AbstractMutableSetTestCase.COLLISION_10));
        }));
        // search a chain for a non-existent element
        Pool<Integer> chain = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        Assert.assertNull(chain.removeFromPool(AbstractMutableSetTestCase.COLLISION_5));
        // search a deep chain for a non-existent element
        Pool<Integer> deepChain = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Assert.assertNull(deepChain.removeFromPool(AbstractMutableSetTestCase.COLLISION_8));
        // search for a non-existent element
        Pool<Integer> empty = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1);
        Assert.assertNull(empty.removeFromPool(AbstractMutableSetTestCase.COLLISION_2));
    }

    @Test
    public void removeFromPool_with_hashingStrategy() {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>() {
            public int computeHashCode(Integer object) {
                return object % 1000;
            }

            public boolean equals(Integer object1, Integer object2) {
                return object1.equals(object2);
            }
        });
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2).with(AbstractMutableSetTestCase.COLLISION_1, ((AbstractMutableSetTestCase.COLLISION_1) + 1000), ((AbstractMutableSetTestCase.COLLISION_1) + 2000), null);
        // Testing remove null from the end of the chain
        Assert.assertNull(integers.removeFromPool(null));
        Integer collision4000 = (AbstractMutableSetTestCase.COLLISION_1) + 4000;
        // Adding null back and creating a deep chain.
        integers.with(null, ((AbstractMutableSetTestCase.COLLISION_1) + 3000), collision4000, ((AbstractMutableSetTestCase.COLLISION_1) + 5000));
        // Removing null from the first position of a bucket in the deep chain
        Assert.assertNull(integers.removeFromPool(null));
        Verify.assertSize(6, integers);
        Assert.assertNull(integers.removeFromPool(null));
        Verify.assertSize(6, integers);
        // Removing from the end of the deep chain
        Assert.assertSame(collision4000, integers.removeFromPool(((AbstractMutableSetTestCase.COLLISION_1) + 4000)));
        // Removing from the first spot of the chain
        Assert.assertSame(AbstractMutableSetTestCase.COLLISION_1, integers.removeFromPool(AbstractMutableSetTestCase.COLLISION_1));
        Verify.assertSize(4, integers);
        // Testing removing an element that is not in a chained bucket
        Assert.assertSame(UnifiedSetWithHashingStrategyTest.JOHNSMITH, com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedSetWithHashingStrategyTest.JOHNSMITH).removeFromPool(UnifiedSetWithHashingStrategyTest.JOHNSMITH));
    }

    @Test
    public void serialization() {
        int size = AbstractMutableSetTestCase.COLLISIONS.size();
        for (int i = 1; i < size; i++) {
            MutableSet<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            Verify.assertPostSerializedEqualsAndHashCode(set);
            set.add(null);
            Verify.assertPostSerializedEqualsAndHashCode(set);
        }
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> nullBucketZero = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, null, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        Verify.assertPostSerializedEqualsAndHashCode(nullBucketZero);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> simpleSetWithNull = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, null, 1, 2);
        Verify.assertPostSerializedEqualsAndHashCode(simpleSetWithNull);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> people = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, UnifiedSetWithHashingStrategyTest.PEOPLE);
        Verify.assertPostSerializedEqualsAndHashCode(people);
        // Testing the hashingStrategy is serialized correctly by making sure it is still hashing by last name
        Verify.assertSetsEqual(UnifiedSetWithHashingStrategyTest.LAST_NAME_HASHED_SET.castToSet(), people.withAll(UnifiedSetWithHashingStrategyTest.PEOPLE.castToList()));
    }

    @Test
    public void null_behavior() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> unifiedSet = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 8).withAll(AbstractMutableSetTestCase.MORE_COLLISIONS);
        AbstractMutableSetTestCase.MORE_COLLISIONS.clone().reverseForEach(( each) -> {
            Assert.assertTrue(unifiedSet.add(null));
            Assert.assertFalse(unifiedSet.add(null));
            Verify.assertContains(null, unifiedSet);
            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);
            Assert.assertTrue(unifiedSet.remove(null));
            Assert.assertFalse(unifiedSet.remove(null));
            Verify.assertNotContains(null, unifiedSet);
            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);
            Assert.assertNull(unifiedSet.put(null));
            Assert.assertNull(unifiedSet.put(null));
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Assert.assertNull(unifiedSet.removeFromPool(null));
            Verify.assertContains(each, unifiedSet);
            Assert.assertTrue(unifiedSet.remove(each));
            Assert.assertFalse(unifiedSet.remove(each));
            Verify.assertNotContains(each, unifiedSet);
        });
    }

    @Override
    @Test
    public void equalsAndHashCode() {
        super.equalsAndHashCode();
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> singleCollisionBucket = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        singleCollisionBucket.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertEquals(singleCollisionBucket, com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, null, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, null, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, null, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, null, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, null, AbstractMutableSetTestCase.COLLISION_3), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, null, AbstractMutableSetTestCase.COLLISION_3));
        Verify.assertEqualsAndHashCode(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, null), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, null));
    }

    @Test
    public void equals_with_hashingStrategy() {
        HashingStrategy<Person> personHashingStrategy = HashingStrategies.fromFunction(Person.TO_LAST);
        HashingStrategy<Person> personHashingStrategyCopy = HashingStrategies.fromFunction(Person.TO_LAST);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> setA = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(personHashingStrategy, UnifiedSetWithHashingStrategyTest.PEOPLE);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Person> setB = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(personHashingStrategyCopy, UnifiedSetWithHashingStrategyTest.PEOPLE);
        // Test sets with different instances of the same hashing strategy are equal symmetrically
        Verify.assertEqualsAndHashCode(setA, setB);
        // Checking that a hashing set is symmetrically equal to an identical JDK set
        HashSet<Person> hashSet = new HashSet(setA);
        Assert.assertTrue(((hashSet.equals(setA)) && (setA.equals(hashSet))));
        // Checking that a hash set is symmetrically equal to an identical GS Collections set
        UnifiedSet<Person> unifiedSet = UnifiedSet.newSet(setA);
        Assert.assertTrue(((unifiedSet.equals(setA)) && (setA.equals(unifiedSet))));
        // Testing the asymmetry of equals
        HashingStrategy<String> firstLetterHashingStrategy = new HashingStrategy<String>() {
            public int computeHashCode(String object) {
                return Character.valueOf(object.charAt(0));
            }

            public boolean equals(String object1, String object2) {
                return (object1.charAt(0)) == (object2.charAt(0));
            }
        };
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<String> hashedString = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(firstLetterHashingStrategy, "apple", "banana", "cheese");
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<String> anotherHashedString = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(firstLetterHashingStrategy, "a", "b", "c");
        UnifiedSet<String> normalString = UnifiedSet.newSetWith("alpha", "bravo", "charlie");
        // Testing hashedString equals normalString
        Assert.assertTrue(((hashedString.equals(normalString)) && (hashedString.equals(hashedString))));
        // Testing normalString does not equal a hashedString, note cannot use Assert.notEquals because it assumes symmetric equals behavior
        Assert.assertFalse(((normalString.equals(hashedString)) && (hashedString.equals(normalString))));
        // Testing 2 sets with same hashing strategies must obey object equals definition
        Verify.assertEqualsAndHashCode(hashedString, anotherHashedString);
        // Testing set size matters
        Assert.assertNotEquals(hashedString, normalString.remove("alpha"));
    }

    @Test
    public void constructor_from_UnifiedSet() {
        Verify.assertEqualsAndHashCode(new HashSet(AbstractMutableSetTestCase.MORE_COLLISIONS), com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.MORE_COLLISIONS));
    }

    @Test
    public void copyConstructor() {
        // test copying a chained bucket
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Verify.assertEqualsAndHashCode(set, com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, set));
    }

    @Test(expected = NullPointerException.class)
    public void newSet_null() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(((com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Object>) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newSet_null_hashingStrategy() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(((HashingStrategy<Object>) (null)));
    }

    @Test
    public void batchForEach() {
        // Testing batch size of 1 to 16 with no chains
        UnifiedSet<Integer> set = UnifiedSet.<Integer>newSet(10).with(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount) {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex) {
                set.batchForEach(new SumProcedure(sum), sectionIndex, sectionCount);
            }
            Assert.assertEquals(55, sum.getValue());
        }
        // Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set2 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 3).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, 1, 2);
        int numBatches = set2.getBatchCount(100);
        for (int i = 0; i < numBatches; ++i) {
            set2.batchForEach(new SumProcedure(sum2), i, numBatches);
        }
        Assert.assertEquals(1, numBatches);
        Assert.assertEquals(54, sum2.getValue());
        // Testing batch size of 3 with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set3 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 4, 1.0F).with(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, 1, 2, 3, 4, 5);
        int numBatches2 = set3.getBatchCount(3);
        for (int i = 0; i < numBatches2; ++i) {
            set3.batchForEach(new SumProcedure(sum3), i, numBatches2);
        }
        Assert.assertEquals(32, sum3.getValue());
        // Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum4 = new IntegerSum(0);
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set4 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY);
        set4.batchForEach(new SumProcedure(sum4), 0, set4.getBatchCount(1));
        Assert.assertEquals(0, sum4.getValue());
    }

    @Override
    @Test
    public void toArray() {
        super.toArray();
        int size = AbstractMutableSetTestCase.COLLISIONS.size();
        for (int i = 1; i < size; i++) {
            MutableSet<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.COLLISIONS.subList(0, i));
            Object[] objects = set.toArray();
            Assert.assertEquals(set, UnifiedSet.newSetWith(objects));
        }
        MutableSet<Integer> deepChain = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6);
        Assert.assertArrayEquals(new Integer[]{ AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6 }, deepChain.toArray());
        MutableSet<Integer> minimumChain = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2);
        minimumChain.remove(AbstractMutableSetTestCase.COLLISION_2);
        Assert.assertArrayEquals(new Integer[]{ AbstractMutableSetTestCase.COLLISION_1 }, minimumChain.toArray());
        MutableSet<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        Integer[] target = new Integer[]{ Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1) };
        Integer[] actual = set.toArray(target);
        ArrayIterate.sort(actual, actual.length, Comparators.safeNullsHigh(Integer::compareTo));
        Assert.assertArrayEquals(new Integer[]{ AbstractMutableSetTestCase.COLLISION_1, 1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, null }, actual);
    }

    @Test
    public void iterator_remove() {
        int size = AbstractMutableSetTestCase.MORE_COLLISIONS.size();
        for (int i = 0; i < size; i++) {
            MutableSet<Integer> actual = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.SIZE).withAll(AbstractMutableSetTestCase.MORE_COLLISIONS);
            Iterator<Integer> iterator = actual.iterator();
            for (int j = 0; j <= i; j++) {
                Assert.assertTrue(iterator.hasNext());
                iterator.next();
            }
            iterator.remove();
            MutableSet<Integer> expected = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.MORE_COLLISIONS);
            expected.remove(AbstractMutableSetTestCase.MORE_COLLISIONS.get(i));
            Assert.assertEquals(expected, actual);
        }
        // remove the last element from within a 2-level long chain that is fully populated
        MutableSet<Integer> set = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6, AbstractMutableSetTestCase.COLLISION_7);
        Iterator<Integer> iterator1 = set.iterator();
        for (int i = 0; i < 7; i++) {
            iterator1.next();
        }
        iterator1.remove();
        Assert.assertEquals(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5, AbstractMutableSetTestCase.COLLISION_6), set);
        // remove the second-to-last element from a 2-level long chain that that has one empty slot
        Iterator<Integer> iterator2 = set.iterator();
        for (int i = 0; i < 6; i++) {
            iterator2.next();
        }
        iterator2.remove();
        Assert.assertEquals(com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4, AbstractMutableSetTestCase.COLLISION_5), set);
        // Testing removing the last element in a fully populated chained bucket
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> set2 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSetWith(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3, AbstractMutableSetTestCase.COLLISION_4);
        Iterator<Integer> iterator3 = set2.iterator();
        for (int i = 0; i < 3; ++i) {
            iterator3.next();
        }
        iterator3.next();
        iterator3.remove();
        Verify.assertSetsEqual(UnifiedSet.newSetWith(AbstractMutableSetTestCase.COLLISION_1, AbstractMutableSetTestCase.COLLISION_2, AbstractMutableSetTestCase.COLLISION_3), set2);
    }

    @Test
    public void setKeyPreservation() {
        Key key = new Key("key");
        Key duplicateKey1 = new Key("key");
        MutableSet<Key> set1 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(HashingStrategies.<Key>defaultStrategy()).with(key, duplicateKey1);
        Verify.assertSize(1, set1);
        Verify.assertContains(key, set1);
        Assert.assertSame(key, set1.getFirst());
        Key duplicateKey2 = new Key("key");
        MutableSet<Key> set2 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(HashingStrategies.<Key>defaultStrategy()).with(key, duplicateKey1, duplicateKey2);
        Verify.assertSize(1, set2);
        Verify.assertContains(key, set2);
        Assert.assertSame(key, set2.getFirst());
        Key duplicateKey3 = new Key("key");
        MutableSet<Key> set3 = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(HashingStrategies.<Key>defaultStrategy()).with(key, new Key("not a dupe"), duplicateKey3);
        Verify.assertSize(2, set3);
        Verify.assertContainsAll(set3, key, new Key("not a dupe"));
        Assert.assertSame(key, set3.detect(key::equals));
    }

    @Test
    public void withSameIfNotModified() {
        com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy<Integer> integers = com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy.newSet(UnifiedSetWithHashingStrategyTest.INTEGER_HASHING_STRATEGY);
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2), integers.with(1, 2));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), integers.with(2, 3, 4));
        Assert.assertSame(integers, integers.with(5, 6, 7));
    }

    @Override
    @Test
    public void retainAll() {
        super.retainAll();
        MutableSet<Object> setWithNull = this.newWith(((Object) (null)));
        Assert.assertFalse(setWithNull.retainAll(FastList.newListWith(((Object) (null)))));
        Assert.assertEquals(UnifiedSet.newSetWith(((Object) (null))), setWithNull);
    }
}

