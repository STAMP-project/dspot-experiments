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
package com.gs.collections.impl.multimap;


import Lists.immutable;
import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.multimap.bag.MutableBagMultimap;
import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.multimap.set.AbstractMutableSetMultimapTestCase;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.multimap.set.strategy.UnifiedSetWithHashingStrategyMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of {@link UnifiedSetWithHashingStrategyMultimap}.
 */
public class UnifiedSetWithHashingStrategyMultimapTest extends AbstractMutableSetMultimapTestCase {
    private static final HashingStrategy<Person> LAST_NAME_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final HashingStrategy<Person> FIRST_NAME_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);

    private static final Person JOHNSMITH = new Person("John", "Smith");

    private static final Person JANESMITH = new Person("Jane", "Smith");

    private static final Person JOHNDOE = new Person("John", "Doe");

    private static final Person JANEDOE = new Person("Jane", "Doe");

    private static final ImmutableList<Person> PEOPLE = immutable.of(UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);

    private static final ImmutableSet<Person> LAST_NAME_HASHED_SET = Sets.immutable.of(UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);

    @Override
    @Test
    public void testClear() {
        UnifiedSetWithHashingStrategyMultimap<Integer, String> map = this.newMultimapWithKeysValues(1, "1", 1, "One", 2, "2", 2, "Two");
        map.clear();
        Verify.assertEmpty(map);
    }

    @Test
    public void testHashingStrategyConstructors() {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> peopleMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(HashingStrategies.<Person>defaultStrategy());
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> lastNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> firstNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        peopleMap.putAll(1, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
        lastNameMap.putAll(1, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
        firstNameMap.putAll(1, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
        Verify.assertSetsEqual(UnifiedSetWithHashingStrategyMultimapTest.PEOPLE.toSet(), peopleMap.get(1));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH), firstNameMap.get(1));
        Verify.assertSetsEqual(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_HASHED_SET.castToSet(), lastNameMap.get(1));
    }

    @Test
    public void testMultimapConstructor() {
        MutableSetMultimap<Integer, Person> map = UnifiedSetMultimap.newMultimap();
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> map2 = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY);
        for (Person person : UnifiedSetWithHashingStrategyMultimapTest.PEOPLE) {
            map.put(1, person);
            map2.put(1, person);
        }
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> hashingMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY, map);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> hashingMap2 = UnifiedSetWithHashingStrategyMultimap.newMultimap(map2);
        Verify.assertSetsEqual(hashingMap.get(1), hashingMap2.get(1));
        Assert.assertSame(hashingMap.getValueHashingStrategy(), hashingMap2.getValueHashingStrategy());
    }

    @Test
    public void testNewEmpty() {
        UnifiedMap<Integer, MutableSet<Person>> expected = UnifiedMap.newMap();
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> lastNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> newEmptyMap = lastNameMap.newEmpty();
        for (int i = 1; i < 4; ++i) {
            expected.put(i, UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_HASHED_SET.toSet());
            lastNameMap.putAll(i, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
            newEmptyMap.putAll(i, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
        }
        Verify.assertMapsEqual(expected, lastNameMap.getMap());
        Verify.assertMapsEqual(expected, newEmptyMap.getMap());
        Assert.assertSame(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY, lastNameMap.getValueHashingStrategy());
        Assert.assertSame(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY, newEmptyMap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void serialization() {
        super.serialization();
        UnifiedSetWithHashingStrategyMultimap<Object, Person> lastNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_STRATEGY);
        lastNameMap.putAll(1, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
        lastNameMap.putAll(2, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE.toList().reverseThis());
        Verify.assertPostSerializedEqualsAndHashCode(lastNameMap);
        UnifiedSetWithHashingStrategyMultimap<Object, Person> deserialized = SerializeTestHelper.serializeDeserialize(lastNameMap);
        Verify.assertSetsEqual(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_HASHED_SET.castToSet(), deserialized.get(1));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(UnifiedSetWithHashingStrategyMultimapTest.JANEDOE, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH), deserialized.get(2));
        deserialized.putAll(3, UnifiedSetWithHashingStrategyMultimapTest.PEOPLE);
        Verify.assertSetsEqual(UnifiedSetWithHashingStrategyMultimapTest.LAST_NAME_HASHED_SET.castToSet(), deserialized.get(3));
    }

    @Override
    @Test
    public void selectKeysValues() {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> selectedMultimap = multimap.selectKeysValues(( key, value) -> ((key % 2) == 0) && ("Jane".equals(value.getFirstName())));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        expectedMultimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        expectedMultimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        Assert.assertEquals(expectedMultimap, selectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), selectedMultimap.getMap());
        Assert.assertSame(expectedMultimap.getValueHashingStrategy(), selectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void rejectKeysValues() {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> rejectedMultimap = multimap.rejectKeysValues(( key, value) -> ((key % 2) == 0) || ("Jane".equals(value.getFirstName())));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        expectedMultimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        expectedMultimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        Assert.assertEquals(expectedMultimap, rejectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), rejectedMultimap.getMap());
        Assert.assertSame(expectedMultimap.getValueHashingStrategy(), rejectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void selectKeysMultiValues() {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(4, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(4, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> selectedMultimap = multimap.selectKeysMultiValues(( key, values) -> ((key % 2) == 0) && (Iterate.contains(values, JANEDOE)));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        expectedMultimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        expectedMultimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        expectedMultimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        Assert.assertEquals(expectedMultimap, selectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), selectedMultimap.getMap());
        Assert.assertSame(expectedMultimap.getValueHashingStrategy(), selectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void rejectKeysMultiValues() {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(4, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(4, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> rejectedMultimap = multimap.rejectKeysMultiValues(( key, values) -> ((key % 2) == 0) || (Iterate.contains(values, JANEDOE)));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        expectedMultimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        expectedMultimap.put(3, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        Assert.assertEquals(expectedMultimap, rejectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), rejectedMultimap.getMap());
        Assert.assertSame(expectedMultimap.getValueHashingStrategy(), rejectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void collectKeysValues() {
        super.collectKeysValues();
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        MutableBagMultimap<String, Integer> collectedMultimap1 = multimap.collectKeysValues(( key, value) -> Tuples.pair(key.toString(), (key * (value.getAge()))));
        MutableBagMultimap<String, Integer> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.put("1", 100);
        expectedMultimap1.put("1", 100);
        expectedMultimap1.put("2", 200);
        expectedMultimap1.put("2", 200);
        Assert.assertEquals(expectedMultimap1, collectedMultimap1);
        MutableBagMultimap<String, Integer> collectedMultimap2 = multimap.collectKeysValues(( key, value) -> Tuples.pair("1", (key * (value.getAge()))));
        MutableBagMultimap<String, Integer> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.put("1", 100);
        expectedMultimap2.put("1", 100);
        expectedMultimap2.put("1", 200);
        expectedMultimap2.put("1", 200);
        Assert.assertEquals(expectedMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectValues() {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(UnifiedSetWithHashingStrategyMultimapTest.FIRST_NAME_STRATEGY);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(1, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANESMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNSMITH);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JOHNDOE);
        multimap.put(2, UnifiedSetWithHashingStrategyMultimapTest.JANEDOE);
        MutableBagMultimap<Integer, Integer> collectedMultimap = multimap.collectValues(Person::getAge);
        MutableBagMultimap<Integer, Integer> expectedMultimap = HashBagMultimap.newMultimap();
        expectedMultimap.put(1, 100);
        expectedMultimap.put(2, 100);
        Assert.assertEquals(expectedMultimap, collectedMultimap);
    }
}

