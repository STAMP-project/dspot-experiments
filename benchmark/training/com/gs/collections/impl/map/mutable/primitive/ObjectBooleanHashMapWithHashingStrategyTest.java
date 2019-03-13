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
package com.gs.collections.impl.map.mutable.primitive;


import ObjectBooleanMaps.mutable;
import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.primitive.ObjectBooleanMap;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import org.junit.Assert;
import org.junit.Test;


public class ObjectBooleanHashMapWithHashingStrategyTest extends ObjectBooleanHashMapTestCase {
    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>() {
        public int computeHashCode(String object) {
            return object.hashCode();
        }

        public boolean equals(String object1, String object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<Integer> INTEGER_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>() {
        public int computeHashCode(Integer object) {
            return object.hashCode();
        }

        public boolean equals(Integer object1, Integer object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);

    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final HashingStrategy<Person> CONSTANT_HASHCODE_STRATEGY = new HashingStrategy<Person>() {
        @Override
        public int computeHashCode(Person person) {
            return 0;
        }

        @Override
        public boolean equals(Person person1, Person person2) {
            return person1.getLastName().equals(person2.getLastName());
        }
    };

    private static final Person JOHNSMITH = new Person("John", "Smith");

    private static final Person JANESMITH = new Person("Jane", "Smith");

    private static final Person JOHNDOE = new Person("John", "Doe");

    private static final Person JANEDOE = new Person("Jane", "Doe");

    @Override
    @Test
    public void select() {
        super.select();
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, false), map.select(( argument1, argument2) -> "Doe".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void reject() {
        super.reject();
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        Assert.assertEquals(ObjectBooleanHashMap.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, false), map.reject(( argument1, argument2) -> "Smith".equals(argument1.getLastName())));
    }

    @Override
    @Test
    public void collect() {
        super.collect();
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        BooleanToObjectFunction f = ( argument1) -> argument1;
        Assert.assertEquals(FastList.newListWith(false, false), map.collect(f));
    }

    @Test
    public void contains_with_hashing_strategy() {
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        Assert.assertTrue(map.containsKey(ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE));
        Assert.assertTrue(map.containsKey(ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH));
        Assert.assertTrue(map.containsKey(ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE));
        Assert.assertTrue(map.containsKey(ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH));
        Assert.assertTrue(map.containsValue(false));
        Assert.assertFalse(map.containsValue(true));
    }

    @Test
    public void remove_with_hashing_strategy() {
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        map.remove(ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE);
        Assert.assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, false), map);
        map.remove(ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH);
        Verify.assertEmpty(map);
        MutableList<String> collidingKeys = AbstractMutableObjectBooleanMapTestCase.generateCollisions();
        ObjectBooleanHashMapWithHashingStrategy<String> map2 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, collidingKeys.get(0), true, collidingKeys.get(1), false, collidingKeys.get(2), true, collidingKeys.get(3), false);
        map2.remove(collidingKeys.get(3));
        Assert.assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, collidingKeys.get(0), true, collidingKeys.get(1), false, collidingKeys.get(2), true), map2);
        map2.remove(collidingKeys.get(0));
        Assert.assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.STRING_HASHING_STRATEGY, collidingKeys.get(1), false, collidingKeys.get(2), true), map2);
        Verify.assertSize(2, map2);
        ObjectBooleanHashMapWithHashingStrategy<Integer> map3 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, true, null, false, 3, true);
        map3.remove(null);
        Assert.assertEquals(ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.INTEGER_HASHING_STRATEGY, 1, true, 3, true), map3);
    }

    @Test
    public void equals_with_hashing_strategy() {
        ObjectBooleanHashMapWithHashingStrategy<Person> map1 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, true);
        ObjectBooleanHashMapWithHashingStrategy<Person> map2 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.FIRST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, true);
        ObjectBooleanHashMapWithHashingStrategy<Person> mapWithConstantHashcodeStrategy = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.CONSTANT_HASHCODE_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, true);
        Assert.assertEquals(map1, map2);
        Assert.assertEquals(map2, map1);
        Assert.assertEquals(mapWithConstantHashcodeStrategy, map2);
        Assert.assertEquals(map2, mapWithConstantHashcodeStrategy);
        Assert.assertNotEquals(map1.hashCode(), map2.hashCode());
        Assert.assertNotEquals(map1.hashCode(), mapWithConstantHashcodeStrategy.hashCode());
        Assert.assertNotEquals(map2.hashCode(), mapWithConstantHashcodeStrategy.hashCode());
        ObjectBooleanHashMapWithHashingStrategy<Person> map3 = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        ObjectBooleanHashMapWithHashingStrategy<Person> map4 = ObjectBooleanHashMapWithHashingStrategy.newMap(map3);
        ObjectBooleanMap<Person> hashMap = mutable.withAll(map3);
        Verify.assertEqualsAndHashCode(map3, map4);
        Assert.assertTrue((((map3.equals(hashMap)) && (hashMap.equals(map3))) && ((map3.hashCode()) != (hashMap.hashCode()))));
        ObjectBooleanHashMap<Person> objectMap = ObjectBooleanHashMap.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        ObjectBooleanHashMapWithHashingStrategy<Person> map5 = ObjectBooleanHashMapWithHashingStrategy.newMap(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY, objectMap);
        Assert.assertNotEquals(map5, objectMap);
    }

    @Test
    public void put_get_with_hashing_strategy() {
        ObjectBooleanHashMapWithHashingStrategy<String> map = this.classUnderTest();
        map.put(null, true);
        // Testing getting values from no chains
        Assert.assertEquals(true, map.get("1"));
        Assert.assertEquals(false, map.get("2"));
        Assert.assertEquals(true, map.get(null));
        ObjectBooleanHashMapWithHashingStrategy<Person> map2 = ObjectBooleanHashMapWithHashingStrategy.newMap(ObjectBooleanHashMapWithHashingStrategyTest.LAST_NAME_HASHING_STRATEGY);
        map2.put(ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH, true);
        Assert.assertEquals(true, map2.get(ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH));
        map2.put(ObjectBooleanHashMapWithHashingStrategyTest.JANESMITH, false);
        Assert.assertEquals(false, map2.get(ObjectBooleanHashMapWithHashingStrategyTest.JOHNSMITH));
    }
}

