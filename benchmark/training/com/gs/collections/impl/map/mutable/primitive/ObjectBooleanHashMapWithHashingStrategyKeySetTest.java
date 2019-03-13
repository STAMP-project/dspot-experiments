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


import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test for {@link ObjectBooleanHashMapWithHashingStrategy#keySet()}.
 */
public class ObjectBooleanHashMapWithHashingStrategyKeySetTest extends ObjectBooleanHashMapKeySetTestCase {
    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>() {
        public int computeHashCode(String object) {
            return object.hashCode();
        }

        public boolean equals(String object1, String object2) {
            return object1.equals(object2);
        }
    });

    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final Person JOHNSMITH = new Person("John", "Smith");

    private static final Person JANESMITH = new Person("Jane", "Smith");

    private static final Person JOHNDOE = new Person("John", "Doe");

    private static final Person JANEDOE = new Person("Jane", "Doe");

    @Override
    @Test
    public void contains() {
        super.contains();
        Set<Person> people = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyKeySetTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JANESMITH, false).keySet();
        Verify.assertSize(2, people);
        Verify.assertContains(ObjectBooleanHashMapWithHashingStrategyKeySetTest.JANEDOE, people);
        Verify.assertContains(ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNDOE, people);
        Verify.assertContains(ObjectBooleanHashMapWithHashingStrategyKeySetTest.JANESMITH, people);
        Verify.assertContains(ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNSMITH, people);
    }

    @Override
    @Test
    public void removeFromKeySet() {
        super.removeFromKeySet();
        ObjectBooleanHashMapWithHashingStrategy<Person> map = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyKeySetTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNDOE, true, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JANEDOE, false, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNSMITH, true, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JANESMITH, false);
        Set<Person> people = map.keySet();
        people.remove(ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNDOE);
        Assert.assertEquals(map, ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(ObjectBooleanHashMapWithHashingStrategyKeySetTest.LAST_NAME_HASHING_STRATEGY, ObjectBooleanHashMapWithHashingStrategyKeySetTest.JOHNSMITH, false));
    }
}

